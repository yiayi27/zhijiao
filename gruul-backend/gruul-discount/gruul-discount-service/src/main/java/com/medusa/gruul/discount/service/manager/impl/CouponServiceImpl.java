package com.medusa.gruul.discount.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.discount.api.constant.DiscountConstant;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.entity.CouponProduct;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.dto.manager.CouponDto;
import com.medusa.gruul.discount.api.model.dto.manager.CouponProductDto;
import com.medusa.gruul.discount.api.model.param.manager.CouponParam;
import com.medusa.gruul.discount.api.model.vo.manager.*;
import com.medusa.gruul.discount.mapper.api.ApiMemberCouponMapper;
import com.medusa.gruul.discount.mapper.manager.CouponMapper;
import com.medusa.gruul.discount.mapper.manager.CouponProductMapper;
import com.medusa.gruul.discount.mapper.manager.MemberCouponMapper;
import com.medusa.gruul.discount.service.manager.ICouponService;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponProductTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponStatusEnum;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 优惠劵 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Autowired
    private CouponProductMapper couponProductMapper;

    @Autowired
    private MemberCouponMapper memberCouponMapper;

    @Autowired
    private ApiMemberCouponMapper apiMemberCouponMapper;

    @Resource
    private RemoteGoodsService remoteGoodsService;

    /**
     * 获取优惠劵分页信息
     *
     * @param couponParam 优惠劵查询信息
     * @return 优惠劵分页对象
     */
    @Override
    public IPage<CouponVo> getCouponList(CouponParam couponParam) {
        IPage<CouponVo> page = new Page<>(couponParam.getCurrent(), couponParam.getSize());
        return page.setRecords(this.baseMapper.queryCouponList(page, couponParam));
    }

    /**
     * 回收券列表
     * @return  List<RecycleCouponVo>
     */
    @Override
    public List<RecycleCouponVo> getRecycleList() {
        //获取回收券列表
        List<Coupon> coupons = this.baseMapper.selectList(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getRecycleActivity, CommonConstants.NUMBER_ONE)
                .ne(Coupon::getStatus, CommonConstants.NUMBER_TWO));
        //过滤已经领取完的优惠券 cop到RecycleCouponVo
        return coupons.stream().filter((e) -> {
            //总领取数量
            long totalLimitQuantity = apiMemberCouponMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, e.getId()));
            //发行数量跟领取量对比
            return e.getIssueQuantity() > totalLimitQuantity;
        }).map(e -> {
            //cop到recycleCouponVo进行返回
            RecycleCouponVo recycleCouponVo = new RecycleCouponVo();
            BeanUtil.copyProperties(e, recycleCouponVo);
            return recycleCouponVo;
        }).toList();
    }

    /**
     * 获取积分优惠劵分页信息
     *
     * @param couponParam 优惠劵查询参数
     * @return 积分优惠劵分页对象
     */
    @Override
    public IPage<CouponVo> getIntegralCouponList(CouponParam couponParam) {
        IPage<CouponVo> page = new Page<>(couponParam.getCurrent(), couponParam.getSize());
        return page.setRecords(this.baseMapper.queryIntegralCouponList(page, couponParam));
    }

    /**
     * 获取单张优惠劵信息
     *
     * @param id 优惠劵id
     * @return 优惠劵信息对象
     */
    @Override
    public CouponSimpleVo getSimpleCoupon(Long id) {
        CouponSimpleVo couponSimpleVo = new CouponSimpleVo();
        CouponVo couponVo = this.baseMapper.queryCouponById(id);
        if (CollectionUtil.isNotEmpty(couponVo.getCouponProducts())) {
            List<Long> productIds = couponVo.getCouponProducts()
                    .stream()
                    .map(CouponProductVo::getProductId)
                    .toList();
            List<DiscountProductVo> discountProducts = remoteGoodsService.findDiscountProductTypeList(productIds);
            List<DiscountProductShowVo> discountProductShowVos = CollUtil.emptyIfNull(discountProducts)
                    .stream()
                    .map((entity) -> {
                        DiscountProductShowVo discountProductShowVo = new DiscountProductShowVo();
                        BeanUtil.copyProperties(entity, discountProductShowVo);
                        return discountProductShowVo;
                    }).toList();
            couponSimpleVo.setDiscountProductVos(discountProductShowVos);
        }
        couponSimpleVo.setCouponVo(couponVo);
        return couponSimpleVo;
    }

    /**
     * 赠送优惠劵
     * 如果适用商品类型为指定商品参与或指定商品不参与，则还需插入优惠劵商品信息数据
     * 注：多用户赠送多张优惠劵情况
     * 同时往优惠劵信息表与客户优惠劵表中插入数据
     *
     * @param couponDtos 优惠券信息
     * @param ids 优惠券id数组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void giveCoupon(List<CouponDto> couponDtos, String[] ids) {
        couponDtos.forEach(couponDto -> {
            //更新优惠劵状态
            refreshStatus(couponDto, null);
            //生成后续查询用到的id
            long name;
            do {
                //生成一个16位的商品id
                name = RandomUtil.randomLong(DiscountConstant.MIN_ID, DiscountConstant.MAX_ID);
                String nameString = String.valueOf(name);
                //验证改id是否已经被使用
                Coupon productSearch = this.baseMapper.selectOne(new LambdaQueryWrapper<Coupon>().eq(Coupon::getName, nameString));
                if (BeanUtil.isEmpty(productSearch)) {
                    break;
                }
            } while (true);
            couponDto.setName(String.valueOf(name));
            couponDto.setGiveType(CommonConstants.NUMBER_ONE);
            MemberCoupon memberCoupon = new MemberCoupon();
            BeanUtil.copyProperties(couponDto, memberCoupon);
            Coupon coupon = couponDto.coverCoupon();
            //优惠劵基础信息新增
            int insert = this.baseMapper.insert(coupon);
            if (insert == 0) {
                throw new ServiceException("优惠劵新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }

            //判断适用商品类型
            if (CouponProductTypeEnum.SOME_JOIN.getStatus() == couponDto.getProductType() || CouponProductTypeEnum.SOME_NOT_JOIN.getStatus() == couponDto.getProductType()) {
                //适用商品信息新增
                List<CouponProductDto> couponProductDtos = couponDto.getCouponProducts();
                if (CollectionUtil.isNotEmpty(couponProductDtos)) {
                    addCouponProductList(couponProductDtos, coupon.getId());
                }
            }

            //批量往客户优惠劵表中新增一条优惠劵信息
            List<String> list = Arrays.asList(ids);
            list.forEach(id -> {
                memberCoupon.setUserId(id);
                memberCoupon.setCouponId(coupon.getId());
                int memberCouponInsert = memberCouponMapper.insert(memberCoupon);
                if (memberCouponInsert == 0) {
                    throw new ServiceException("客户优惠劵新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                }
            });
        });
    }

    /**
     * 优惠劵信息新增
     * 新增优惠劵信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需插入优惠劵商品信息数据
     * 根据前端传过来的giveType来区分优惠劵的赠送类型：  0-全民活动  1-赠予客户  2-积分优惠劵
     *
     * @param couponDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCoupon(CouponDto couponDto) {
        if (couponDto.getEffectiveDays() != null) {
            if (couponDto.getEffectiveDays() < CommonConstants.NUMBER_ONE) {
                throw new ServiceException("有效时间不能小于1");
            }
        }
        if (couponDto.getDiscountAmount() != null) {
            if (couponDto.getDiscountAmount().compareTo(BigDecimal.ZERO) != CommonConstants.NUMBER_ONE) {
                throw new ServiceException("优惠金额不能小于1");
            }
        }
        if (!couponDto.getGiveType().equals(CommonConstants.NUMBER_TWO)) {
            if (couponDto.getRecycleActivity().equals(CommonConstants.NUMBER_ONE) && !CommonConstants.NUMBER_ZERO.equals(couponDto.getCouponType())) {
                throw new ServiceException("优惠券类型错误,不能参与回收券");
            }
            if (couponDto.getRecycleActivity().equals(CommonConstants.NUMBER_ONE) && StringUtils.isEmpty(couponDto.getRecycleImg())) {
                couponDto.setRecycleImg("https://oss-tencent.bgniao.cn/api/recycle_discount_coupon.png");
            }
        }
        //更新优惠劵状态
        refreshStatus(couponDto, null);
        //判断名称是否为空(用于非普通优惠劵)
        if (couponDto.getName() == null || StringUtils.isEmpty(couponDto.getName())) {
            long name;
            do {
                //生成一个16位的商品id
                name = RandomUtil.randomLong(DiscountConstant.MIN_ID, DiscountConstant.MAX_ID);
                //验证改id是否已经被使用
                Coupon productSearch = this.baseMapper.selectOne(new LambdaQueryWrapper<Coupon>().eq(Coupon::getName, name));
                if (BeanUtil.isEmpty(productSearch)) {
                    break;
                }
            } while (true);
            couponDto.setName(String.valueOf(name));
        }
        Coupon coupon = couponDto.coverCoupon();
        //优惠劵基础信息新增
        int insert = this.baseMapper.insert(coupon);
        if (insert == 0) {
            throw new ServiceException("优惠劵新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }

        //判断适用商品类型
        if (CouponProductTypeEnum.SOME_JOIN.getStatus() == couponDto.getProductType() || CouponProductTypeEnum.SOME_NOT_JOIN.getStatus() == couponDto.getProductType()) {
            //适用商品信息新增
            List<CouponProductDto> couponProductDtos = couponDto.getCouponProducts();
            if (CollectionUtil.isNotEmpty(couponProductDtos)) {
                addCouponProductList(couponProductDtos, coupon.getId());
            }
        }

    }

    /**
     * 优惠劵信息修改
     * 修改优惠劵信息，如果适用商品类型为指定商品参与或指定商品不参与，则还需更新优惠劵商品信息数据
     *
     * @param couponDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoupon(CouponDto couponDto) {
        if (couponDto.getRecycleActivity() != null && couponDto.getRecycleActivity().equals(CommonConstants.NUMBER_ONE)) {
            if (!CommonConstants.NUMBER_ZERO.equals(couponDto.getCouponType())) {
                throw new ServiceException("优惠券类型错误，不能参与回收券！");
            }
            if (StringUtils.isEmpty(couponDto.getRecycleImg())) {
                couponDto.setRecycleImg("https://oss-tencent.bgniao.cn/api/recycle_discount_coupon.png");
            }
        }

        //判断优惠劵是否处于活动中状态
        Coupon couponSearch = this.baseMapper.selectById(couponDto.getId());
        if (BeanUtil.isEmpty(couponSearch)) {
            throw new ServiceException("优惠劵不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //普通优惠劵下架的无法修改 积分优惠劵可以
        if (CommonConstants.NUMBER_ZERO.equals(couponSearch.getGiveType())) {
            if (CouponStatusEnum.OVER.getStatus() == couponSearch.getStatus()) {
                throw new ServiceException("已关闭的优惠劵活动不能修改！", SystemCode.DATA_EXISTED.getCode());
            }
        }

        //更新优惠劵状态
        refreshStatus(couponDto, couponSearch.getCreateTime());

        Coupon coupon = couponDto.coverCoupon();
        //基础信息修改
        int update = this.baseMapper.updateById(coupon);
        if (update == 0) {
            throw new ServiceException("优惠劵修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //判断适用商品类型
        if (CouponProductTypeEnum.SOME_JOIN.getStatus() == couponDto.getProductType() || CouponProductTypeEnum.SOME_NOT_JOIN.getStatus() == couponDto.getProductType()) {
            //删除原先关联商品信息
            couponProductMapper.delete(new LambdaQueryWrapper<CouponProduct>().eq(CouponProduct::getCouponId, coupon.getId()));
            //适用商品信息新增
            List<CouponProductDto> couponProductDtos = couponDto.getCouponProducts();
            if (CollectionUtil.isNotEmpty(couponProductDtos)) {
                addCouponProductList(couponProductDtos, coupon.getId());
            }
        }
    }

    /**
     * 关闭/启用优惠劵
     *
     * @param ids 优惠券id数组
     * @param status 优惠券状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeCouponStatus(Long[] ids, Integer status) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            Coupon couponSearch = this.baseMapper.selectById(id);
            if (BeanUtil.isEmpty(couponSearch)) {
                throw new ServiceException("优惠劵不存在！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            //如果是关闭活动 活动结束时间设置为当前时间
            if (CommonConstants.NUMBER_TWO.equals(status)) {
                couponSearch.setStatus(status);
                if (!CommonConstants.NUMBER_TWO.equals(couponSearch.getGiveType())) {
                    couponSearch.setEndTime(new Date());
                }
            } else {
                if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == couponSearch.getEffectiveType()) {
                    couponSearch.setStatus(status);
                } else {
                    CouponDto couponDto = new CouponDto();
                    BeanUtil.copyProperties(couponSearch, couponDto);
                    refreshStatus(couponDto, couponSearch.getCreateTime());
                    couponSearch.setStatus(couponDto.getStatus());
                }
            }
            int update = this.baseMapper.updateById(couponSearch);
            if (update == 0) {
                throw new ServiceException("优惠劵状态变更失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 优惠劵的删除  只有未开始与已结束的优惠劵才可以删除
     *
     * @param ids 删除的优惠劵对象集
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCouponList(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            Coupon couponSearch = this.baseMapper.selectById(id);
            if (BeanUtil.isEmpty(couponSearch)) {
                throw new ServiceException("优惠劵不存在！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            //判断非积分优惠劵商品是否处于活动中状态
            if (!CommonConstants.NUMBER_TWO.equals(couponSearch.getGiveType())) {
                if (CouponStatusEnum.ON_GOING.getStatus() == couponSearch.getStatus()) {
                    throw new ServiceException("进行中优惠券无法删除，请先关闭！", SystemCode.DATA_DELETE_FAILED.getCode());
                }
            }
            //删除原先关联商品信息
            int delete = this.baseMapper.deleteById(id);
            if (delete == 0) {
                throw new ServiceException("优惠劵删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
        });
        //删除优惠劵关联商品信息
        couponProductMapper.delete(new LambdaQueryWrapper<CouponProduct>().in(CouponProduct::getCouponId, list));
    }

    /**
     * 优惠劵关联商品批量新增
     *
     * @param couponProductDtos 优惠券信息
     * @param id 优惠劵id
     */
    private void addCouponProductList(List<CouponProductDto> couponProductDtos, Long id) {
        couponProductDtos.forEach(bean -> {
            CouponProduct couponProduct = bean.coverCouponProduct();
            couponProduct.setCouponId(id);
            int insertProductAttribute = couponProductMapper.insert(couponProduct);
            if (insertProductAttribute == 0) {
                throw new ServiceException("优惠劵适用商品新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 判断时间是否在有效期内  在的话优惠劵活动状态为活动中 否则为未开始
     * 领劵立即生效 判断当前时间是否大于活动结束时间
     *
     * @param couponDto 优惠券信息
     */
    private void refreshStatus(CouponDto couponDto, LocalDateTime startDate) {
        long nowTime = System.currentTimeMillis();
        //判断有效时间类型
        if (CouponEffectiveTypeEnum.TIME_BEGIN.getStatus() == couponDto.getEffectiveType()) {
            //判断时间是否在有效期内  在的话优惠劵状态为活动中
            long startTime = couponDto.getStartTime().getTime();
            long endTime = couponDto.getEndTime().getTime();
            if (nowTime < startTime) {
                couponDto.setStatus(CouponStatusEnum.BE_GOING.getStatus());
            } else if (nowTime > startTime && nowTime < endTime) {
                couponDto.setStatus(CouponStatusEnum.ON_GOING.getStatus());
            } else {
                couponDto.setStatus(CouponStatusEnum.OVER.getStatus());
            }
        } else if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == couponDto.getEffectiveType()) {
            if (couponDto.getStatus() == null) {
                couponDto.setStatus(CouponStatusEnum.ON_GOING.getStatus());
            }
        } else {
            couponDto.setStatus(CouponStatusEnum.OVER.getStatus());
        }
    }

    /**
     * 定时任务查询可未开始与进行中的优惠劵活动
     *
     * @return List<CouponVo>
     */
    @Override
    public List<CouponVo> findCouponListForTask() {
        return this.baseMapper.queryCouponListForTask();
    }

    /**
     * 定时任务查更新优惠劵活动状态
     *
     * @param couponVo 优惠劵信息
     */
    @Override
    public void updateCouponForTask(CouponVo couponVo) {
        this.baseMapper.updateCouponForTask(couponVo);
    }
}
