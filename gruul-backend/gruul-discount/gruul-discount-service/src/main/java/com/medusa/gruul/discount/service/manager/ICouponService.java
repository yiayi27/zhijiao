package com.medusa.gruul.discount.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.discount.api.model.dto.manager.CouponDto;
import com.medusa.gruul.discount.api.model.param.manager.CouponParam;
import com.medusa.gruul.discount.api.model.vo.manager.CouponSimpleVo;
import com.medusa.gruul.discount.api.model.vo.manager.CouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.RecycleCouponVo;

import java.util.List;

/**
 * <p>
 * 优惠劵 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface ICouponService extends IService<Coupon> {

    /**
     * 查询列表
     *
     * @param couponParam 查询参数
     * @return 分页对象
     */
    IPage<CouponVo> getCouponList(CouponParam couponParam);

    /**
     * 回收券列表
     * @return  List<RecycleCouponVo>
     */
    List<RecycleCouponVo> getRecycleList();


    /**
     * 积分优惠劵查询列表
     *
     * @param couponParam 优惠券查询参数
     * @return 分页对象
     */
    IPage<CouponVo> getIntegralCouponList(CouponParam couponParam);

    /**
     * 查询单张优惠劵信息
     *
     * @param id 优惠券id
     * @return CouponSimpleVo
     */
    CouponSimpleVo getSimpleCoupon(Long id);

    /**
     * 赠送优惠劵
     *
     * @param couponDtos 优惠券信息
     * @param ids 优惠券id数组
     */
    void giveCoupon(List<CouponDto> couponDtos, String[] ids);

    /**
     * 优惠劵新增
     *
     * @param couponDto 优惠券信息
     */
    void addCoupon(CouponDto couponDto);

    /**
     * 优惠劵修改
     *
     * @param couponDto 优惠券信息
     */
    void updateCoupon(CouponDto couponDto);

    /**
     * 关闭/启用优惠劵
     *
     * @param ids 优惠劵ids
     * @param status 优惠劵状态
     */
    void changeCouponStatus(Long[] ids, Integer status);

    /**
     * 优惠劵删除
     *
     * @param ids 优惠劵ids
     */
    void deleteCouponList(Long[] ids);

    /**
     * 定时任务查询可未开始与进行中的优惠劵活动
     *
     * @return List<CouponVo>
     */
    List<CouponVo> findCouponListForTask();

    /**
     * 定时任务查更新优惠劵活动状态
     *
     * @param couponVo 优惠劵信息
     */
    void updateCouponForTask(CouponVo couponVo);

}
