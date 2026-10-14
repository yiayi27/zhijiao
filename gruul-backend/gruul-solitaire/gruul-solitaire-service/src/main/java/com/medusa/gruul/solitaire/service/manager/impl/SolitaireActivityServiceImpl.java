package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.solitaire.api.constant.SolitaireConstant;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivityProduct;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityDto;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityProductDto;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireRecordVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.ProductShowVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityProductVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivitySimpleVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import com.medusa.gruul.solitaire.mapper.api.ApiSolitaireRecordMapper;
import com.medusa.gruul.solitaire.mapper.manager.SolitaireActivityMapper;
import com.medusa.gruul.solitaire.mapper.manager.SolitaireActivityProductMapper;
import com.medusa.gruul.solitaire.service.manager.ISolitaireActivityService;
import com.medusa.gruul.solitaire.web.enums.ActivityProductTypeEnum;
import com.medusa.gruul.solitaire.web.enums.ActivityStatusEnum;
import com.medusa.gruul.solitaire.web.utils.SortUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * <p>
 * 接龙活动信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Service
public class SolitaireActivityServiceImpl extends ServiceImpl<SolitaireActivityMapper, SolitaireActivity> implements ISolitaireActivityService {

    @Autowired
    private SolitaireActivityProductMapper activityProductMapper;

    @Resource
    private RemoteGoodsService remoteGoodsService;

    @Autowired
    private ApiSolitaireRecordMapper apiSolitaireRecordMapper;

    /**
     * 获取接龙活动分页信息
     *
     * @param solitaireActivityParam 接龙活动查询参数
     * @return 接龙活动信息分页对象
     */
    @Override
    public IPage<SolitaireActivityVo> getActivityList(SolitaireActivityParam solitaireActivityParam) {
        IPage<SolitaireActivityVo> page = new Page<>(solitaireActivityParam.getCurrent(), solitaireActivityParam.getSize());
        List<SolitaireActivityVo> solitaireActivityVos = this.baseMapper.queryActivityList(page, solitaireActivityParam);
        Integer productNumber = remoteGoodsService.findProductCount();
        //新增接龙服务参与的商品数量赋值
        if (CollectionUtil.isNotEmpty(solitaireActivityVos)) {
            solitaireActivityVos.forEach(solitaireActivityVo -> {
                Integer number = CommonConstants.NUMBER_ZERO;
                List<SolitaireActivityProductVo> solitaireActivityProductVos = solitaireActivityVo.getActivityProducts();
                if (CollectionUtil.isNotEmpty(solitaireActivityProductVos)) {
                    number = solitaireActivityProductVos.size();
                }
                if (ActivityProductTypeEnum.ALL_JOIN.getStatus() == solitaireActivityVo.getProductType()) {
                    solitaireActivityVo.setProductNumber(productNumber);
                } else if (ActivityProductTypeEnum.SOME_JOIN.getStatus() == solitaireActivityVo.getProductType()) {
                    solitaireActivityVo.setProductNumber(number);
                } else if (ActivityProductTypeEnum.SOME_NOT_JOIN.getStatus() == solitaireActivityVo.getProductType()) {
                    if (productNumber < number) {
                        solitaireActivityVo.setProductNumber(CommonConstants.NUMBER_ZERO);
                    } else {
                        solitaireActivityVo.setProductNumber(productNumber - number);
                    }
                }
                //查询接龙活动的参加人数、支付单数、应收金额
                List<ApiSolitaireRecordVo> apiSolitaireRecordVos = apiSolitaireRecordMapper
                        .getSolitaireRecord(solitaireActivityVo.getId());
                if (CollectionUtil.isNotEmpty(apiSolitaireRecordVos)) {
                    //去重获取接龙活动购买人数
                    List<ApiSolitaireRecordVo> orderDistinct = apiSolitaireRecordVos.stream()
                            .filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getOrderId)).toList();
                    BigDecimal totalAmount = orderDistinct.stream()
                            .map(ApiSolitaireRecordVo::getPayAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<ApiSolitaireRecordVo> userDistinct = apiSolitaireRecordVos.stream()
                            .filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getBuyerId)).toList();
                    solitaireActivityVo.setUserNumber(userDistinct.size());
                    solitaireActivityVo.setOrderNumber(orderDistinct.size());
                    solitaireActivityVo.setTotalAmount(totalAmount);
                } else {
                    solitaireActivityVo.setUserNumber(CommonConstants.NUMBER_ZERO);
                    solitaireActivityVo.setOrderNumber(CommonConstants.NUMBER_ZERO);
                    solitaireActivityVo.setTotalAmount(new BigDecimal(SolitaireConstant.PRICE_NORMAL));
                }
            });
        }
        return page.setRecords(solitaireActivityVos);
    }

    /**
     * 获取接龙活动信息明细
     *
     * @param id 接龙活动id
     * @return 接龙活动信息对象
     */
    @Override
    public SolitaireActivitySimpleVo getActivityById(Long id) {
        SolitaireActivitySimpleVo solitaireActivitySimpleVo = new SolitaireActivitySimpleVo();
        SolitaireActivityVo solitaireActivityVo = this.baseMapper.queryActivityById(id);
        if (BeanUtil.isEmpty(solitaireActivityVo)) {
            throw new ServiceException("活动信息不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        if (CollectionUtil.isNotEmpty(solitaireActivityVo.getActivityProducts())) {
            List<Long> productIds =
                    solitaireActivityVo.getActivityProducts().stream().map(SolitaireActivityProductVo::getProductId).toList();
            List<DiscountProductVo> discountProducts = remoteGoodsService.findDiscountProductTypeList(productIds);
            List<ProductShowVo> productShowVos = CollUtil.emptyIfNull(discountProducts)
                    .stream()
                    .map((entity) -> {
                        ProductShowVo productShowVo = new ProductShowVo();
                        BeanUtil.copyProperties(entity, productShowVo);
                        return productShowVo;
                    }).toList();
            solitaireActivitySimpleVo.setProductVos(productShowVos);
        }
        solitaireActivitySimpleVo.setSolitaireActivityVo(solitaireActivityVo);
        return solitaireActivitySimpleVo;
    }

    /**
     * 接龙活动信息新增
     * 新增接龙活动信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需插入商品信息数据
     *
     * @param solitaireActivityDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addActivity(SolitaireActivityDto solitaireActivityDto) {
        //判断活动时间是否与其他活动时间冲突(与有效活动有交集则无法添加)
        checkActivityTime(solitaireActivityDto);
        //判断活动开始时间不能小于当前时间超过一分钟
        if (solitaireActivityDto.getStartTime().getTime() + SolitaireConstant.ONE_MINUTE_MILLISECOND < System.currentTimeMillis()) {
            throw new ServiceException("活动开始时间不能小于当前时间!", SystemCode.PARAM_VALID_ERROR_CODE);
        }
        //上面已经判断开始时间不能小于当前时间超过一分钟 这里只需判断开始时间与当前时间的大小 活动开始时间小于当前时间都把活动状态改成进行中 否则状态为未开始
        int activityStatus = solitaireActivityDto.getStartTime().getTime() < System.currentTimeMillis() ? ActivityStatusEnum.ON_GOING.getStatus() : ActivityStatusEnum.BE_GOING.getStatus();
        solitaireActivityDto.setStatus(activityStatus);
        SolitaireActivity solitaireActivity = solitaireActivityDto.coverActivity();
        //接龙活动信息信息新增
        int insert = this.baseMapper.insert(solitaireActivity);
        if (insert == 0) {
            throw new ServiceException("接龙活动信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
        //判断适用商品类型
        addActivityProductList(solitaireActivityDto, solitaireActivity.getId());
    }

    /**
     * 接龙活动关联商品批量新增
     *
     * @param solitaireActivityDto 接龙信息
     * @param id 活动id
     */
    private void addActivityProductList(SolitaireActivityDto solitaireActivityDto, Long id) {
        if (ActivityProductTypeEnum.SOME_JOIN.getStatus() == solitaireActivityDto.getProductType() || ActivityProductTypeEnum.SOME_NOT_JOIN.getStatus() == solitaireActivityDto.getProductType()) {
            //适用商品信息新增
            List<SolitaireActivityProductDto> activityProductDtos = solitaireActivityDto.getActivityProducts();
            if (CollectionUtil.isNotEmpty(activityProductDtos)) {
                activityProductDtos.forEach(bean -> {
                    SolitaireActivityProduct solitaireActivityProduct = bean.coverActivityProduct();
                    solitaireActivityProduct.setActivityId(id);
                    int insertProductAttribute = activityProductMapper.insert(solitaireActivityProduct);
                    if (insertProductAttribute == 0) {
                        throw new ServiceException("接龙活动适用商品新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                    }
                });
            } else {
                throw new ServiceException("商品信息不能为空！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }

    }

    /**
     * 接龙活动信息修改
     * 修改接龙活动信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需更新优惠劵商品信息数据
     *
     * @param solitaireActivityDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(SolitaireActivityDto solitaireActivityDto) {
        //判断接龙活动是否存在
        SolitaireActivity solitaireActivitySearch = this.baseMapper.selectById(solitaireActivityDto.getId());
        if (BeanUtil.isEmpty(solitaireActivitySearch)) {
            throw new ServiceException("接龙活动不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //判断活动时间是否与其他活动时间冲突(与有效活动有交集则无法添加)
        checkActivityTime(solitaireActivityDto);
        //判断活动结束时间不能小于当前时间 防止页面长时间不动再提交的时间差问题 主要用于活动编辑的时候可能结束时间小于当前时间
        if (solitaireActivityDto.getEndTime().getTime() < System.currentTimeMillis()) {
            throw new ServiceException("活动结束时间不能小于当前时间!", SystemCode.PARAM_VALID_ERROR_CODE);
        }
        //先判断修改活动中的数据还是其他活动状态的数据
        if (ActivityStatusEnum.ON_GOING.getStatus() != solitaireActivitySearch.getStatus()) {
            //除进行中的活动其他活动编辑的时候要判断活动开始时间不能小于当前时间超过一分钟
            if (solitaireActivityDto.getStartTime().getTime() + SolitaireConstant.ONE_MINUTE_MILLISECOND < System.currentTimeMillis()) {
                throw new ServiceException("活动开始时间不能小于当前时间!", SystemCode.PARAM_VALID_ERROR_CODE);
            }
            //上面已经判断开始时间不能小于当前时间超过一分钟 这里只需判断开始时间与当前时间的大小 活动开始时间小于当前时间都把活动状态改成进行中 否则状态为未开始
            int activityStatus = solitaireActivityDto.getStartTime().getTime() < System.currentTimeMillis() ? ActivityStatusEnum.ON_GOING.getStatus() : ActivityStatusEnum.BE_GOING.getStatus();
            solitaireActivityDto.setStatus(activityStatus);
        }
        SolitaireActivity solitaireActivity = solitaireActivityDto.coverActivity();
        //基础信息修改
        int update = this.baseMapper.updateById(solitaireActivity);
        if (update == 0) {
            throw new ServiceException("接龙活动信息修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //删除原先关联商品信息
        activityProductMapper.delete(new LambdaQueryWrapper<SolitaireActivityProduct>().eq(SolitaireActivityProduct::getActivityId, solitaireActivity.getId()));
        //判断适用商品类型
        addActivityProductList(solitaireActivityDto, solitaireActivity.getId());
    }

    /**
     * 检查活动时间是否可以设置为有效时间
     * 拿当前活动的开始结束时间去与查询出来的有效活动的开始结束时间相互计算区间 如有交集则视为活动时间重叠 不可保存
     */
    private void checkActivityTime(SolitaireActivityDto solitaireActivityDto) {
        Long id = CommonConstants.LONG_ZERO;
        if (solitaireActivityDto.getId() != null) {
            id = solitaireActivityDto.getId();
        }
        //判断活动结束时间不能小于开始时间时间
        if (solitaireActivityDto.getEndTime().getTime() < solitaireActivityDto.getStartTime().getTime()) {
            throw new ServiceException("活动结束时间不能小于开始时间!", SystemCode.PARAM_VALID_ERROR_CODE);
        }
        List<SolitaireActivityVo> solitaireActivityVos = this.baseMapper.queryValidActivity(id);
        if (CollectionUtil.isNotEmpty(solitaireActivityVos)) {
            solitaireActivityVos.forEach(entity -> {
                if (isEffectiveDate(solitaireActivityDto.getStartTime(), entity.getStartTime(), entity.getEndTime())) {
                    throw new ServiceException(String.format("活动开始时间已有活动存在! 活动id : %s", entity.getId()), SystemCode.DATA_EXISTED_CODE);
                }
                if (isEffectiveDate(solitaireActivityDto.getEndTime(), entity.getStartTime(), entity.getEndTime())) {
                    throw new ServiceException(String.format("活动结束时间已有活动存在! 活动id : %s", entity.getId()), SystemCode.DATA_EXISTED_CODE);
                }
                if (isEffectiveDate(entity.getStartTime(), solitaireActivityDto.getStartTime(), solitaireActivityDto.getEndTime())) {
                    throw new ServiceException(String.format("活动开始时间已有活动存在! 活动id : %s", entity.getId()), SystemCode.DATA_EXISTED_CODE);
                }
                if (isEffectiveDate(entity.getEndTime(), solitaireActivityDto.getStartTime(), solitaireActivityDto.getEndTime())) {
                    throw new ServiceException(String.format("活动开始时间已有活动存在! 活动id : %s", entity.getId()), SystemCode.DATA_EXISTED_CODE);
                }
            });
        }
    }

    /**
     * 判断当前活动时间是否在其他活动区间内
     */
    private boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
            return Boolean.TRUE;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        return date.after(begin) && date.before(end);
    }

    /**
     * 接龙活动的关闭
     *
     * @param id 接龙活动id
     */
    @Override
    public void changeActivityStatus(Long id) {
        SolitaireActivity solitaireActivity = this.baseMapper.selectById(id);
        if (BeanUtil.isEmpty(solitaireActivity)) {
            throw new ServiceException("活动信息不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        solitaireActivity.setStatus(ActivityStatusEnum.OVER.getStatus());
        //如果是关闭活动 活动结束时间设置为当前时间
        solitaireActivity.setEndTime(new Date());
        int update = this.baseMapper.updateById(solitaireActivity);
        if (update == 0) {
            throw new ServiceException("接龙活动关闭失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }


    /**
     * 接龙活动的删除  只有未开始与已结束的接龙活动才可以删除
     *
     * @param id 接龙活动id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        SolitaireActivity solitaireActivityVo = this.baseMapper.selectById(id);
        if (BeanUtil.isEmpty(solitaireActivityVo)) {
            throw new ServiceException("活动信息不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        if (ActivityStatusEnum.ON_GOING.getStatus() == solitaireActivityVo.getStatus()) {
            throw new ServiceException("活动进行中，无法删除！", SystemCode.DATA_EXISTED.getCode());
        }
        //删除活动信息
        int delete = this.baseMapper.deleteById(id);
        if (delete == 0) {
            throw new ServiceException("活动信息删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
        }
        //删除关联商品信息
        activityProductMapper.delete(new LambdaQueryWrapper<SolitaireActivityProduct>().eq(SolitaireActivityProduct::getActivityId, id));
    }

    /**
     * 定时任务查询可未开始与进行中的接龙活动
     *
     * @return List<FullScaleVo>
     */
    @Override
    public List<SolitaireActivityVo> findActivityListForTask() {
        return this.baseMapper.queryActivityListForTask();
    }

    /**
     * 定时任务查更新接龙活动状态
     *
     * @param solitaireActivityVo 接龙活动信息
     */
    @Override
    public void updateActivityForTask(SolitaireActivityVo solitaireActivityVo) {
        this.baseMapper.updateActivityForTask(solitaireActivityVo);
    }

}
