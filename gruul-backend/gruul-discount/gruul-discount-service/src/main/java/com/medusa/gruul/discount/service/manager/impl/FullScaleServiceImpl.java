package com.medusa.gruul.discount.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.medusa.gruul.discount.api.entity.FullScaleProduct;
import com.medusa.gruul.discount.api.entity.FullScaleRule;
import com.medusa.gruul.discount.api.model.dto.manager.FullScaleDto;
import com.medusa.gruul.discount.api.model.dto.manager.FullScaleProductDto;
import com.medusa.gruul.discount.api.model.dto.manager.FullScaleRuleDto;
import com.medusa.gruul.discount.api.model.param.manager.FullScaleParam;
import com.medusa.gruul.discount.api.model.vo.manager.DiscountProductShowVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleProductVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleSimpleVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.mapper.manager.FullScaleMapper;
import com.medusa.gruul.discount.mapper.manager.FullScaleProductMapper;
import com.medusa.gruul.discount.mapper.manager.FullScaleRuleMapper;
import com.medusa.gruul.discount.service.manager.IFullScaleService;
import com.medusa.gruul.discount.web.enums.FullScaleProductTypeEnum;
import com.medusa.gruul.discount.web.enums.FullScaleStatusEnum;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.ActivityStatisticsVo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 满减活动 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class FullScaleServiceImpl extends ServiceImpl<FullScaleMapper, FullScale> implements IFullScaleService {

    @Autowired
    private FullScaleProductMapper fullScaleProductMapper;

    @Autowired
    private FullScaleRuleMapper fullScaleRuleMapper;

    @Resource
    private RemoteGoodsService remoteGoodsService;

    @Resource
    private RemoteOrderService remoteOrderService;

    /**
     * 获取满减活动分页信息
     *
     * @param fullScaleParam 满减活动参数
     * @return 满减活动分页对象
     */
    @Override
    public IPage<FullScaleVo> getFullScaleList(FullScaleParam fullScaleParam) {
        IPage<FullScaleVo> page = new Page<>(fullScaleParam.getCurrent(), fullScaleParam.getSize());
        List<FullScaleVo> fullScaleVos = this.baseMapper.queryFullScaleList(page, fullScaleParam);
        Integer productNumber = remoteGoodsService.findProductCount();
        //新增满减服务参与的商品数量赋值
        if (CollectionUtil.isNotEmpty(fullScaleVos)) {
            Long[] ids = ArrayUtil.toArray(fullScaleVos.stream().map(FullScaleVo::getId).toList(), Long.class);
            List<ActivityStatisticsVo> activityStatisticsVos = remoteOrderService.fullScaleStatistics(ids);
            fullScaleVos.forEach(fullScaleVo -> {
                Integer number = CommonConstants.NUMBER_ZERO;
                List<FullScaleProductVo> fullScaleProductVos = fullScaleVo.getFullScaleProducts();
                if (CollectionUtil.isNotEmpty(fullScaleProductVos)) {
                    number = fullScaleProductVos.size();
                }
                if (FullScaleProductTypeEnum.ALL_JOIN.getStatus() == fullScaleVo.getProductType()) {
                    fullScaleVo.setProductNumber(productNumber);
                } else if (FullScaleProductTypeEnum.SOME_JOIN.getStatus() == fullScaleVo.getProductType()) {
                    fullScaleVo.setProductNumber(number);
                } else if (FullScaleProductTypeEnum.SOME_NOT_JOIN.getStatus() == fullScaleVo.getProductType()) {
                    if (productNumber < number) {
                        fullScaleVo.setProductNumber(CommonConstants.NUMBER_ZERO);
                    } else {
                        fullScaleVo.setProductNumber(productNumber - number);
                    }
                }
                //订单部分参与人数、支付订单数、金额总数赋值
                if (CollectionUtil.isNotEmpty(activityStatisticsVos)) {
                    for (ActivityStatisticsVo activityStatisticsVo : activityStatisticsVos) {
                        if (activityStatisticsVo.getActivityId().equals(fullScaleVo.getId())) {
                            fullScaleVo.setPeopleNum(activityStatisticsVo.getPeopleNum());
                            fullScaleVo.setPayOrder(activityStatisticsVo.getPayOrder());
                            fullScaleVo.setGaapTotal(activityStatisticsVo.getGaapTotal());
                        }
                    }
                }
            });
        }
        return page.setRecords(fullScaleVos);
    }

    /**
     * 获取单张满减活动信息
     *
     * @param id 满减活动id
     * @return 满减活动信息对象
     */
    @Override
    public FullScaleSimpleVo getSimpleFullScale(Long id) {
        FullScaleSimpleVo fullScaleSimpleVo = new FullScaleSimpleVo();
        FullScaleVo fullScaleVo = this.baseMapper.queryFullScaleById(id);
        if (BeanUtil.isEmpty(fullScaleVo)) {
            throw new ServiceException("满减活动不存在！", SystemCode.DATA_NOT_EXIST.getCode());
        }
        if (CollectionUtil.isNotEmpty(fullScaleVo.getFullScaleProducts())) {
            List<Long> productIds = fullScaleVo.getFullScaleProducts()
                    .stream()
                    .map(FullScaleProductVo::getProductId)
                    .toList();
            List<DiscountProductVo> discountProducts = remoteGoodsService.findDiscountProductTypeList(productIds);
            if (CollectionUtil.isNotEmpty(discountProducts)) {
                List<DiscountProductShowVo> discountProductShowVos = CollUtil.emptyIfNull(discountProducts)
                        .stream()
                        .map((entity) -> {
                            DiscountProductShowVo discountProductShowVo = new DiscountProductShowVo();
                            BeanUtil.copyProperties(entity, discountProductShowVo);
                            return discountProductShowVo;
                        }).toList();
                fullScaleSimpleVo.setDiscountProductVos(discountProductShowVos);
            }
        }
        fullScaleSimpleVo.setFullScaleVo(fullScaleVo);
        return fullScaleSimpleVo;
    }

    /**
     * 获取单张满减活动信息接口
     *
     * @param id 满减活动id
     * @return 满减活动信息对象
     */
    @Override
    public FullScaleVo getFullScaleById(Long id) {
        return this.baseMapper.queryFullScaleById(id);
    }

    /**
     * 满减活动信息新增
     * 新增满减活动信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需插入满减活动商品信息数据
     *
     * @param fullScaleDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFullScale(FullScaleDto fullScaleDto) {
        //判断时间是否在有效期内  在的话满减活动状态为活动中
        refreshStatus(fullScaleDto);

        FullScale fullScale = fullScaleDto.coverFullScale();
        //商品基础信息新增
        int insert = this.baseMapper.insert(fullScale);
        if (insert == 0) {
            throw new ServiceException("满减活动新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }

        //满减活动规则新增
        List<FullScaleRuleDto> fullScaleRuleDtos = fullScaleDto.getFullScaleRules();
        if (CollectionUtil.isNotEmpty(fullScaleRuleDtos)) {
            addFullScaleRuleList(fullScaleRuleDtos, fullScale.getId());
        }

        //判断适用商品类型
        if (FullScaleProductTypeEnum.SOME_JOIN.getStatus() == fullScaleDto.getProductType() || FullScaleProductTypeEnum.SOME_NOT_JOIN.getStatus() == fullScaleDto.getProductType()) {
            //适用商品信息新增
            List<FullScaleProductDto> fullScaleProductDtos = fullScaleDto.getFullScaleProducts();
            if (CollectionUtil.isNotEmpty(fullScaleProductDtos)) {
                addFullScaleProductList(fullScaleProductDtos, fullScale.getId());
            }
        }
    }

    /**
     * 满减活动信息修改
     * 修改满减活动信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需更新满减活动商品信息数据
     *
     * @param fullScaleDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFullScale(FullScaleDto fullScaleDto) {
        //判断满减活动是否处于活动中状态
        FullScale fullScaleSearch = this.baseMapper.selectById(fullScaleDto.getId());
        if (BeanUtil.isEmpty(fullScaleSearch)) {
            throw new ServiceException("满减活动不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        if (FullScaleStatusEnum.OVER.getStatus() == fullScaleSearch.getStatus()) {
            throw new ServiceException("已关闭的满减活动活动不能修改！", SystemCode.DATA_EXISTED.getCode());
        }
        //判断时间是否在有效期内  在的话满减活动状态为活动中
        refreshStatus(fullScaleDto);

        FullScale fullScale = fullScaleDto.coverFullScale();
        //商品基础信息修改
        int update = this.baseMapper.updateById(fullScale);
        if (update == 0) {
            throw new ServiceException("满减活动修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }

        //删除原先满减活动规则
        fullScaleRuleMapper.delete(new LambdaQueryWrapper<FullScaleRule>().eq(FullScaleRule::getFullScaleId, fullScale.getId()));
        //满减活动规则新增
        List<FullScaleRuleDto> fullScaleRuleDtos = fullScaleDto.getFullScaleRules();
        if (CollectionUtil.isNotEmpty(fullScaleRuleDtos)) {
            addFullScaleRuleList(fullScaleRuleDtos, fullScale.getId());
        }

        //删除原先关联商品信息
        fullScaleProductMapper.delete(new LambdaQueryWrapper<FullScaleProduct>().eq(FullScaleProduct::getFullScaleId, fullScale.getId()));
        //判断适用商品类型
        if (FullScaleProductTypeEnum.SOME_JOIN.getStatus() == fullScaleDto.getProductType() || FullScaleProductTypeEnum.SOME_NOT_JOIN.getStatus() == fullScaleDto.getProductType()) {
            //适用商品信息新增
            List<FullScaleProductDto> fullScaleProductDtos = fullScaleDto.getFullScaleProducts();
            if (CollectionUtil.isNotEmpty(fullScaleProductDtos)) {
                addFullScaleProductList(fullScaleProductDtos, fullScale.getId());
            }
        }
    }

    /**
     * 关闭/启用满减活动
     *
     * @param ids 满减活动ids
     * @param status 满减活动状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeFullScaleStatus(Long[] ids, Integer status) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            FullScale fullScaleSearch = this.baseMapper.selectById(id);
            if (BeanUtil.isEmpty(fullScaleSearch)) {
                throw new ServiceException("满减活动不存在！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            //如果是关闭活动 活动结束时间设置为当前时间
            if (CommonConstants.NUMBER_TWO.equals(status)) {
                fullScaleSearch.setEndTime(new Date());
            }
            fullScaleSearch.setStatus(status);
            int update = this.baseMapper.updateById(fullScaleSearch);
            if (update == 0) {
                throw new ServiceException("满减活动状态变更失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }


    /**
     * 满减活动的删除  只有未开始与已结束的满减活动才可以删除
     *
     * @param ids 删除的满减活动对象集
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFullScaleList(Long[] ids) {
        //判断商品是否处于下架状态
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            FullScale fullScaleSearch = this.baseMapper.selectById(id);
            if (BeanUtil.isEmpty(fullScaleSearch)) {
                throw new ServiceException("满减活动不存在！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            if (FullScaleStatusEnum.ON_GOING.getStatus() == fullScaleSearch.getStatus()) {
                throw new ServiceException("满减活动状态为活动中，无法删除！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            int delete = this.baseMapper.deleteById(id);
            if (delete == 0) {
                throw new ServiceException("满减活动删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
        });
        //删除满减活动关联商品信息
        fullScaleProductMapper.delete(new LambdaQueryWrapper<FullScaleProduct>().in(FullScaleProduct::getFullScaleId, list));
        //删除满减活动规则信息
        fullScaleRuleMapper.delete(new LambdaQueryWrapper<FullScaleRule>().in(FullScaleRule::getFullScaleId, list));
    }

    /**
     * 满减活动关联商品批量新增
     *
     * @param fullScaleProductDtos 满减活动关联商品信息
     * @param id 满减活动id
     */
    private void addFullScaleProductList(List<FullScaleProductDto> fullScaleProductDtos, Long id) {
        fullScaleProductDtos.forEach(bean -> {
            FullScaleProduct fullScaleProduct = bean.coverFullScaleProduct();
            fullScaleProduct.setFullScaleId(id);
            int insertProductAttribute = fullScaleProductMapper.insert(fullScaleProduct);
            if (insertProductAttribute == 0) {
                throw new ServiceException("满减活动适用商品新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 满减活动规则信息批量新增
     *
     * @param fullScaleRuleDtos 满减活动关联商品信息
     * @param id 满减活动id
     */
    private void addFullScaleRuleList(List<FullScaleRuleDto> fullScaleRuleDtos, Long id) {
        fullScaleRuleDtos.forEach(bean -> {
            FullScaleRule fullScaleRule = bean.coverFullScaleRule();
            fullScaleRule.setFullScaleId(id);
            int insertProductAttribute = fullScaleRuleMapper.insert(fullScaleRule);
            if (insertProductAttribute == 0) {
                throw new ServiceException("满减活动规则新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 判断时间是否在有效期内 小于开始时间为未开始 在时间段内的话满减活动状态为活动中 否则为未开始
     *
     * @param fullScaleDto 满减活动信息
     */
    private void refreshStatus(FullScaleDto fullScaleDto) {
        //判断时间是否在有效期内  在的话满减状态为活动中
        long startTime = fullScaleDto.getStartTime().getTime();
        long endTime = fullScaleDto.getEndTime().getTime();
        long nowTime = System.currentTimeMillis();
        if (nowTime < startTime) {
            fullScaleDto.setStatus(FullScaleStatusEnum.BE_GOING.getStatus());
        } else if (nowTime > startTime && nowTime < endTime) {
            fullScaleDto.setStatus(FullScaleStatusEnum.ON_GOING.getStatus());
        } else {
            fullScaleDto.setStatus(FullScaleStatusEnum.OVER.getStatus());
        }
    }

    /**
     * 查询可使用满减活动
     *
     * @param productIds 商品id集合
     * @return List<FullScaleVo>
     */
    @Override
    public List<FullScaleVo> findFullScaleListForOrder(Collection<Long> productIds) {
        List<FullScaleVo> fullScaleVos = this.baseMapper.queryGoingFullScaleList();
        List<FullScaleVo> fullScaleVoList = new ArrayList<>(fullScaleVos.size());
        fullScaleVos.forEach(fullScaleVo -> {
            if (FullScaleProductTypeEnum.ALL_JOIN.getStatus() == fullScaleVo.getProductType()) {
                fullScaleVoList.add(fullScaleVo);
            } else if (FullScaleProductTypeEnum.SOME_JOIN.getStatus() == fullScaleVo.getProductType()) {
                List<Long> productList =
                        fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).toList();
                if (CollectionUtil.isNotEmpty(productList) && CollectionUtil.containsAny(productList, productIds)) {
                    fullScaleVoList.add(fullScaleVo);
                }
            } else if (FullScaleProductTypeEnum.SOME_NOT_JOIN.getStatus() == fullScaleVo.getProductType()) {
                List<Long> productList =
                        fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).toList();
                if (CollectionUtil.isNotEmpty(productList) && !CollectionUtil.containsAll(productList, productIds)) {
                    fullScaleVoList.add(fullScaleVo);
                }
            }
        });
        return fullScaleVoList;
    }

    /**
     * 定时任务查询可未开始与进行中的满减活动
     *
     * @return List<FullScaleVo>
     */
    @Override
    public List<FullScaleVo> findFullScaleListForTask() {
        return this.baseMapper.queryFullScaleListForTask();
    }

    /**
     * 定时任务查更新满减活动状态
     *
     * @param fullScaleVo 满减活动信息
     */
    @Override
    public void updateFullScaleForTask(FullScaleVo fullScaleVo) {
        this.baseMapper.updateFullScaleForTask(fullScaleVo);
    }

    /**
     * 查询单个商品可使用满减信息
     *
     * @param productId 商品id
     * @return List<FullScaleVo>
     */
    @Override
    public List<FullScaleVo> findFullScaleListForProduct(Long productId) {
        List<FullScaleVo> fullScaleVos = this.baseMapper.queryGoingFullScaleList();
        List<FullScaleVo> fullScaleVoList = new ArrayList<>(fullScaleVos.size());
        fullScaleVos.forEach(fullScaleVo -> {
            if (FullScaleProductTypeEnum.ALL_JOIN.getStatus() == fullScaleVo.getProductType()) {
                fullScaleVoList.add(fullScaleVo);
            } else if (FullScaleProductTypeEnum.SOME_JOIN.getStatus() == fullScaleVo.getProductType()) {
                List<Long> productList =
                        fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).toList();
                if (CollectionUtil.isNotEmpty(productList) && CollectionUtil.contains(productList, productId)) {
                    fullScaleVoList.add(fullScaleVo);
                }
            } else if (FullScaleProductTypeEnum.SOME_NOT_JOIN.getStatus() == fullScaleVo.getProductType()) {
                List<Long> productList =
                        fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).toList();
                if (CollectionUtil.isNotEmpty(productList) && !CollectionUtil.contains(productList, productId)) {
                    fullScaleVoList.add(fullScaleVo);
                }
            }
        });
        return fullScaleVoList;
    }
}
