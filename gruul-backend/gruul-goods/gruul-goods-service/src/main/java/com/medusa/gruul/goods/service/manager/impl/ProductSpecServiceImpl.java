package com.medusa.gruul.goods.service.manager.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.constant.GoodsProductSpecConst;
import com.medusa.gruul.goods.api.constant.SaveOrUpdateEnum;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecsAndSkusVo;
import com.medusa.gruul.goods.api.model.vo.manager.SkuStockVo;
import com.medusa.gruul.goods.mapper.manager.ProductMapper;
import com.medusa.gruul.goods.mapper.manager.ProductSpecMapper;
import com.medusa.gruul.goods.service.manager.IProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品规格查询service实现类
 *
 * @author 张治保
 */
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {


    @Autowired
    @Lazy
    private IProductSpecService productSpecService;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductSpecVo> getProductSpecs(Long productId, Long parentId) {
                return baseMapper.queryProductSpecsByProductId(productId, parentId);
    }

    /**
     * 保存商品规格
     *
     * @param productId 商品id
     * @param parentId  父规格id 传0则为规格组
     * @param specs     商品id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProductSpecs(Long productId, Long parentId, List<ProductSpec> specs) {
        if (specs == null) {
            return;
        }
        RedisUtil.doubleDeletion(
                () -> this.saveOrUpdate(SaveOrUpdateEnum.SAVE, productId, parentId, specs),
                GoodsProductSpecConst.CACHE_SPECS_KEY, productId
        );
    }

    @Override
    public void updateProductSpecs(Long productId, Long parentId, List<ProductSpec> specs) {
        if (specs == null) {
            return;
        }
        RedisUtil.doubleDeletion(
                () -> {
                    List<Long> specIds = getAllSpecIds(specs);
                    /*
                     * 移除已经不存在的规格组/规格
                     */
                    LambdaQueryWrapper<ProductSpec> queryWrapper = Wrappers.<ProductSpec>lambdaQuery()
                            .eq(ProductSpec::getProductId, productId);
                    if (CollUtil.isNotEmpty(specIds)) {
                        queryWrapper.notIn(ProductSpec::getId, specIds);
                    }
                    this.remove(queryWrapper);
                    /*
                     * 更新或保存剩余数据
                     */
                    this.saveOrUpdate(SaveOrUpdateEnum.UPDATE, productId, parentId, specs);
                },
                GoodsProductSpecConst.CACHE_SPECS_KEY, productId

        );

    }

    @Override
    public ProductSpecsAndSkusVo getSpecsAndSkusByProductId(Long productId) {
        List<ProductSpecVo> specs = productSpecService.getProductSpecs(productId, GoodsProductSpecConst.SPEC_GROUP_PARENT_ID);
        List<SkuStockVo> skus = productMapper.querySkuStock(productId);
        return new ProductSpecsAndSkusVo().setSpecs(specs).setSkus(skus);
    }


    /**
     * 新增或更新
     */
    private void saveOrUpdate(SaveOrUpdateEnum saveOrUpdate, Long productId, Long parentId, List<ProductSpec> specs) {
        if (CollUtil.isEmpty(specs)) {
            return;
        }
        /*
         * 设置参数
         */
        boolean isSave = SaveOrUpdateEnum.SAVE == saveOrUpdate;
        specs.forEach(
                spec -> {
                    spec.setProductId(productId)
                            .setParentId(parentId)
                            .setShopId(ISecurity.userMust().getShopId());
                    if (spec.getVersion() == null) {
                        spec.setVersion(CommonConstants.NUMBER_ZERO);
                    }
                }
        );
        boolean success = isSave ? super.saveBatch(specs) : super.saveOrUpdateBatch(specs);
        if (!success) {
            throw new ServiceException("保存/更新数据失败", SystemCode.DATA_ADD_FAILED.getCode());
        }

        specs.forEach(
                spec -> {
                    if (CollUtil.isNotEmpty(spec.getChildren())) {
                        saveOrUpdate(saveOrUpdate, productId, spec.getId(), spec.getChildren());
                    }
                }
        );
    }

    /**
     * 获取所有的规格id
     */
    private List<Long> getAllSpecIds(List<ProductSpec> specs) {
        if (CollUtil.isEmpty(specs)) {
            return Collections.emptyList();
        }
        List<Long> ids = new ArrayList<>();
        for (ProductSpec spec : specs) {
            Long id = spec.getId();
            if (id != null) {
                ids.add(id);
                ids.addAll(getAllSpecIds(spec.getChildren()));
            }
        }
        return ids;
    }


}




