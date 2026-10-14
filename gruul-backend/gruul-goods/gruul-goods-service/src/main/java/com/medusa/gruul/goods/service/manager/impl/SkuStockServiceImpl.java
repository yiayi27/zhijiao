package com.medusa.gruul.goods.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.constant.SaveOrUpdateEnum;
import com.medusa.gruul.goods.api.entity.MemberPrice;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.entity.SkuRebate;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.param.OperateStockDto;
import com.medusa.gruul.goods.mapper.manager.MemberPriceMapper;
import com.medusa.gruul.goods.mapper.manager.SkuRebateMapper;
import com.medusa.gruul.goods.mapper.manager.SkuStockMapper;
import com.medusa.gruul.goods.service.manager.ISkuStockService;
import com.medusa.gruul.goods.util.ProductSpecsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Service
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements ISkuStockService {

    @Autowired
    private MemberPriceMapper memberPriceMapper;

    @Autowired
    private SkuRebateMapper skuRebateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean subtractStock(OperateStockDto operateStockDto) {
        int numAttempts = 0;
        do {
            SkuStock skuStock = baseMapper.selectById(operateStockDto.getSkuId());
            if (skuStock.getStock() >= operateStockDto.getNumber()) {
                skuStock.setStock(skuStock.getStock() - operateStockDto.getNumber());
                skuStock.setSale(skuStock.getSale() + operateStockDto.getNumber());
                if (this.updateById(skuStock)) {
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStock.getId()), String.valueOf(skuStock.getStock()));
                    return true;
                } else {
                    numAttempts++;
                }
            }
        } while (numAttempts < CommonConstants.DEFAULT_MAX_RETRIES);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSubtractStock(List<OperateStockDto> operateStockDtoList) {
        List<Long> skuIdList =
                operateStockDtoList.stream().map(OperateStockDto::getSkuId).toList();
        List<SkuStock> skuStockList =
                baseMapper.selectByIds(skuIdList);
        Map<Long, SkuStock> skuStockMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getId, v -> v));
        skuStockList.clear();
        boolean re = false;
        for (OperateStockDto operateStockDto : operateStockDtoList) {
            SkuStock skuStock = skuStockMap.get(operateStockDto.getSkuId());
            if (skuStock.getStock() >= operateStockDto.getNumber()) {
                skuStock.setStock(skuStock.getStock() - operateStockDto.getNumber());
                skuStock.setSale(skuStock.getSale() + operateStockDto.getNumber());
                //更新数据库库存与销量 同时更新缓存里面的库存
                if (this.updateById(skuStock)) {
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStock.getId()), String.valueOf(skuStock.getStock()));
                    re = true;
                }
            }
        }
        return re;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchRevertStock(List<OperateStockDto> operateStockDtoList) {
        List<Long> skuIdList =
                operateStockDtoList.stream().map(OperateStockDto::getSkuId).toList();
        List<SkuStock> skuStockList =
                baseMapper.selectByIds(skuIdList);
        Map<Long, SkuStock> skuStockMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getId, v -> v));
        skuStockList.clear();
        for (OperateStockDto operateStockDto : operateStockDtoList) {
            SkuStock skuStock = skuStockMap.get(operateStockDto.getSkuId());
            if (BeanUtil.isNotEmpty(skuStock)) {
                skuStock.setStock(skuStock.getStock() + operateStockDto.getNumber());
                skuStock.setSale(skuStock.getSale() - operateStockDto.getNumber());
                //更新数据库库存与销量 同时更新缓存里面的库存
                if (this.updateById(skuStock)) {
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStock.getId()), String.valueOf(skuStock.getStock()));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存sku信息
     */
    @Override
    public void saveProductSkuStock(Long productId, List<SkuStockDto> skuStockDtos, List<ProductSpec> specs) {
        if (CollUtil.isEmpty(skuStockDtos)) {
            return;
        }
        this.saveOrUpdate(SaveOrUpdateEnum.SAVE, productId, skuStockDtos, specs);
    }

    /**
     * 更新sku信息
     *
     * @author 张治保
     */
    @Override
    public void updateProductSkuStock(Long productId, List<SkuStockDto> skuStockDtos, List<ProductSpec> specs) {
        if (CollUtil.isEmpty(skuStockDtos)) {
            return;
        }
        /*
         * 参数中的sku列表
         */
        List<Long> paramSkuIds = skuStockDtos.stream().filter(Objects::nonNull).map(SkuStockDto::getId).collect(toList());
        /*
         * 数据库中该商品的sku的id列表
         */
        List<Long> dbSkuIds = super.listObjs(
                Wrappers.<SkuStock>lambdaQuery()
                        .select(SkuStock::getId)
                        .eq(SkuStock::getProductId, productId)
                ,
                id -> Long.valueOf(String.valueOf(id))
        );
        /*
         * 需要删除的sku列表
         * 数据库中存在 参数中不存在
         */
        Collection<Long> deleteIds = dbSkuIds.stream().filter(dbSkuId -> !paramSkuIds.contains(dbSkuId)).collect(toList());
        this.removeSkuByIds(deleteIds);
        /*
         * 需要新增的sku列表
         * 参数中存在的 数据库中不存在的
         */
        Predicate<SkuStockDto> predicate = dto -> dbSkuIds.contains(dto.getId());
        List<SkuStockDto> saveBatch = skuStockDtos.stream().filter(predicate.negate()).collect(toList());
        this.saveProductSkuStock(productId, saveBatch, specs);
        /*
         * 需要更新的sku列表
         * 参数中存在的 数据库中存在的
         */
        List<SkuStockDto> updateBatch = skuStockDtos.stream().filter(predicate).collect(toList());
        this.saveOrUpdate(SaveOrUpdateEnum.UPDATE, productId, updateBatch, specs);
    }

    /**
     * 根据id列表删除对应sku信息
     *
     * @author 张治保
     */
    private void removeSkuByIds(Collection<Long> deleteIds) {
        if (CollUtil.isEmpty(deleteIds)) {
            return;
        }
        /*
         *删除sku信息先删除会员价关联sku信息
         */
        memberPriceMapper.delete(new LambdaQueryWrapper<MemberPrice>().in(MemberPrice::getSkuId, deleteIds));
        /*
         *删除sku信息先删除返利关联sku信息
         */
        skuRebateMapper.delete(new LambdaQueryWrapper<SkuRebate>().in(SkuRebate::getSkuId, deleteIds));
        boolean success = super.removeByIds(deleteIds);
        if (!success) {
            throw new ServiceException("商品sku信息删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
        }
        Set<String> cacheKeys = deleteIds.stream()
                .filter(Objects::nonNull)
                .map(id -> RedisUtil.key(Keys.GOODS_SKU_STOCK, id))
                .collect(Collectors.toSet());
        RedisUtil.delete(cacheKeys);
    }


    /**
     * 保存或更新数据
     */
    private void saveOrUpdate(SaveOrUpdateEnum saveOrUpdate, Long productId, List<SkuStockDto> skuStockDtos, List<ProductSpec> specs) {
        if (CollUtil.isEmpty(skuStockDtos)) {
            return;
        }
        boolean isSave = saveOrUpdate == SaveOrUpdateEnum.SAVE;

        String shopId = ISecurity.userMust().getShopId();
        skuStockDtos.forEach(
                sku -> {
                    sku.setSpecs(
                            CollUtil.isEmpty(sku.getProductSpecNames()) ? "" : sku.getProductSpecNames().toString()
                    );
                    sku.setProductId(productId);
                    SkuStock skuStock = sku.coverSkuStock();
                    skuStock.setShopId(shopId);
                    skuStock.setUpdateTime(LocalDateTime.now());
//                    if (skuStock.getVersion() == null) {
//                        skuStock.setVersion(0);
//                    }
                    skuStock.setProductSpecIds(
                            CollUtil.isEmpty(specs) ? null : ProductSpecsUtil.getSpecIdsBySpecNames(sku.getProductSpecNames(), specs)
                    );
                    boolean success = isSave ? super.save(skuStock) : super.updateById(skuStock);
                    if (!success) {
                        throw new ServiceException("商品sku信息新增/更新失败！", SystemCode.DATA_ADD_FAILED_CODE);
                    }
                    sku.setId(skuStock.getId());
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStock.getId()), String.valueOf(skuStock.getStock()));
                }
        );

    }

}
