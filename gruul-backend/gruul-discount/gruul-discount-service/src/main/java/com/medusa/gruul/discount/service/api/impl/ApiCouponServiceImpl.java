package com.medusa.gruul.discount.service.api.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponProductVo;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.mapper.api.ApiCouponMapper;
import com.medusa.gruul.discount.service.api.IApiCouponService;
import com.medusa.gruul.discount.util.SortUtil;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponProductTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * <p>
 * 优惠劵 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class ApiCouponServiceImpl extends ServiceImpl<ApiCouponMapper, Coupon> implements IApiCouponService {

    /**
     * 获取优惠劵信息
     * 查询出来活动中的优惠劵 再判断所有用户的总领取数量是否小于发行数量 最后再判断客户已领取数量是否小于限领数量
     *
     * @return 优惠劵对象list
     */
    @Override
    public List<ApiCouponVo> getApiCouponList() {
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return this.baseMapper.queryApiCouponListNoLogin();
        }
        SecureUser<Object> secureUser = userOpt.get();
        Long start=System.currentTimeMillis();
        List<ApiCouponVo> apiCouponVos = this.baseMapper.queryApiCouponList(secureUser.getId());
        Long end=System.currentTimeMillis();
        System.out.println("查询优惠劵耗时："+(end-start));
        List<ApiCouponVo> apiCouponVoList = new ArrayList<>(apiCouponVos.size());
        if (CollectionUtil.isNotEmpty(apiCouponVos)) {
            apiCouponVos.forEach(bean -> {
                //判断剩余数量与可领取数量是否大于0
                if (bean.getTotalAvailable() > CommonConstants.NUMBER_ZERO && bean.getAvailable() > CommonConstants.NUMBER_ZERO) {
                    //领劵立即生效 取创建时间与有效天数后的时间  结束时间是按天计算不是按24小时 取createTime记得取当天的0点时间
                    if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == bean.getEffectiveType()) {
                        coverStartAndEndDate(bean);
                    }
                    apiCouponVoList.add(bean);
                }
            });
        }
        //按开始时间降序排列
        SortUtil.sortByMethod(apiCouponVoList, "getStartTime", true);
        return apiCouponVoList;
    }

    /**
     * 优惠劵组件信息列表
     * 查询出来活动中的优惠劵 再判断所有用户的总领取数量是否小于发行数量 最后再判断客户已领取数量是否小于限领数量
     *
     * @return 优惠劵对象list
     */
    @Override
    public List<ApiCouponVo> getApiCouponComponentList() {
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return this.baseMapper.queryApiCouponListNoLogin();
        }
        SecureUser<Object> secureUser = userOpt.get();
        List<ApiCouponVo> apiCouponVos = this.baseMapper.queryApiCouponList(secureUser.getId());
        List<ApiCouponVo> apiCouponVoList = new ArrayList<>(apiCouponVos.size());
        if (CollectionUtil.isNotEmpty(apiCouponVos)) {
            apiCouponVos.forEach(bean -> {
                if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == bean.getEffectiveType()) {
                    coverStartAndEndDate(bean);
                }
                apiCouponVoList.add(bean);
            });
        }
        //按开始时间降序排列
        SortUtil.sortByMethod(apiCouponVoList, "getStartTime", true);
        return apiCouponVoList;
    }

    /**
     * 获取购物车优惠劵信息
     * 查询出来活动中的优惠劵
     *
     * @return 优惠劵对象list
     */
    @Override
    public List<ApiCouponVo> getApiShoppingCartCouponList() {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        return this.baseMapper.queryApiCouponList(userId);
    }

    /**
     * 查询单个商品可使用优惠劵信息
     * 先查询出活动中的优惠劵信息，再匹配符合这个商品的优惠劵条件，最后再匹配是否发行数量
     *
     * @param userId 用户id
     * @param productId 商品id
     * @return List<ApiCouponVo>
     */
    @Override
    public List<ApiCouponVo> findCouponListForProduct(String userId, Long productId) {
        List<ApiCouponVo> apiCouponVos = this.baseMapper.queryApiCouponList(userId);
        List<ApiCouponVo> apiCouponVoList = new ArrayList<>(apiCouponVos.size());
        if (CollectionUtil.isNotEmpty(apiCouponVos)) {
            apiCouponVos.forEach(bean -> {
                //领劵立即生效 取创建时间与有效天数后的时间  结束时间是按天计算不是按24小时 取createTime记得取当天的0点时间
                if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == bean.getEffectiveType()) {
                    coverStartAndEndDate(bean);
                }
                //全部参与则直接添加
                if (CouponProductTypeEnum.ALL_JOIN.getStatus() == bean.getProductType()) {
                    apiCouponVoList.add(bean);
                } else if (CouponProductTypeEnum.SOME_JOIN.getStatus() == bean.getProductType()) {
                    //部分参与则过滤取参与中的商品
                    if (CollectionUtil.isNotEmpty(bean.getCouponProducts())) {
                        List<Long> productList =
                                bean.getCouponProducts().stream().map(ApiCouponProductVo::getProductId).toList();
                        if (CollectionUtil.contains(productList, productId)) {
                            apiCouponVoList.add(bean);
                        }
                    }
                } else if (CouponProductTypeEnum.SOME_NOT_JOIN.getStatus() == bean.getProductType()) {
                    //部分不参与则过滤掉不参与的商品
                    if (CollectionUtil.isNotEmpty(bean.getCouponProducts())) {
                        List<Long> productList =
                                bean.getCouponProducts().stream().map(ApiCouponProductVo::getProductId).toList();
                        if (!CollectionUtil.contains(productList, productId)) {
                            apiCouponVoList.add(bean);
                        }
                    }
                }
            });
        }
        return apiCouponVoList;
    }

    /**
     * 领劵立即生效开始与结束时间数据格式调整 统一赋值到开始与结束时间里面
     * 注：领劵后1天有效格式为2024-02-15至2024-02-15
     *
     * @param apiCouponVo 优惠劵信息
     */
    public void coverStartAndEndDate(ApiCouponVo apiCouponVo) {
        //显示规则为：当前时间-设置的第几天后的日期
        Date startDate = new Date(System.currentTimeMillis());
        long dayTime = (long) CommonConstants.SECOND_OF_HOUR * CommonConstants.HOUR_OF_DAY;
        LocalDateTime createTime = LocalDateTimeUtils.getDayStart(LocalDateTimeUtils.convertDateToLdt(startDate));
        long zero = createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / dayTime * dayTime - TimeZone.getDefault().getRawOffset();
        long twelve = zero + dayTime - CommonConstants.NUMBER_ONE;
        long endTime = twelve + apiCouponVo.getEffectiveDays() * dayTime;
        Date endDate = new Date();
        endDate.setTime(endTime);
        apiCouponVo.setStartTime(startDate);
        apiCouponVo.setEndTime(endDate);
    }

    /**
     * 获取积分优惠劵信息
     * 查询出来活动中的优惠劵 再判断所有用户的总领取数量是否小于发行数量 最后再判断客户已领取数量是否小于限领数量
     *
     * @return 积分优惠劵对象list
     */
    @Override
    public List<ApiCouponVo> getApiIntegralCouponList() {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        return this.baseMapper.queryApiIntegralCouponList(userId);
    }
}
