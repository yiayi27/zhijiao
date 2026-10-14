package com.medusa.gruul.discount.service.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.entity.CouponProduct;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiMemberCouponVo;
import com.medusa.gruul.discount.mapper.api.ApiCouponProductMapper;
import com.medusa.gruul.discount.mapper.api.ApiMemberCouponMapper;
import com.medusa.gruul.discount.mapper.manager.CouponMapper;
import com.medusa.gruul.discount.service.api.IApiMemberCouponService;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponStatusEnum;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.integral.api.model.IntegralCouponDto;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

/**
 * <p>
 * 客户优惠劵 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class ApiMemberCouponServiceImpl extends ServiceImpl<ApiMemberCouponMapper, MemberCoupon> implements IApiMemberCouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private ApiCouponProductMapper apiCouponProductMapper;

    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;

    @Resource
    private RemoteIntegralService remoteIntegralService;

    @Resource
    private RemoteGoodsService remoteGoodsService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 查询用户优惠劵信息
     *
     * @return 用户优惠劵list信息
     */
    @Override
    public List<ApiMemberCouponVo> getMemberCouponListByUserId() {
        SecureUser secureUser = ISecurity.userMust();
        List<ApiMemberCouponVo> apiMemberCouponVos = this.baseMapper.queryMemberCouponListByUserId(secureUser.getId());
        apiMemberCouponVos.forEach(bean -> {
            //领劵立即生效 取创建时间与有效天数后的时间  结束时间是按天计算不是按24小时 取createTime记得取当天的0点时间
            if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == bean.getEffectiveType()) {
                Date startDate = LocalDateTimeUtils.convertLdtToDate(bean.getCreateTime());
                long dayTime = (long) CommonConstants.SECOND_OF_HOUR * CommonConstants.HOUR_OF_DAY;
                LocalDateTime createTime = LocalDateTimeUtils.getDayStart(bean.getCreateTime());
                long zero = createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / dayTime * dayTime - TimeZone.getDefault().getRawOffset();
                long twelve = zero + dayTime - CommonConstants.NUMBER_ONE;
                long endTime = twelve + bean.getEffectiveDays() * dayTime;
                Date endDate = new Date();
                endDate.setTime(endTime);
                bean.setStartTime(startDate);
                bean.setEndTime(endDate);
            }
        });
        return apiMemberCouponVos;
    }

    /**
     * 领取优惠劵
     * 判断优惠劵是否达到限领次数上限以及是否超过发行数量
     *
     * @param couponId 优惠劵id
     */
    @Override
    public Result<Void> receiveCoupon(Long couponId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        Coupon coupon = couponMapper.selectById(couponId);
        //先验证优惠劵是否存在或者已被关闭
        if (BeanUtil.isEmpty(coupon)) {
            return Result.failed(SystemCode.DATA_ADD_FAILED.getCode(), "优惠劵不存在！");
        } else {
            if (CouponStatusEnum.OVER.getStatus() == coupon.getStatus()) {
                return Result.failed(SystemCode.DATA_ADD_FAILED.getCode(), "优惠劵已关闭，不可领取！");
            }
        }
        //获取该优惠券的唯一锁标识
        RLock rLock = redissonClient.getLock(String.valueOf(couponId));
        try {
            rLock.lock();
            //总领取数量
            Long totalLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId));
            //发行数量
            int issueQuantity = coupon.getIssueQuantity();
            if (issueQuantity > totalLimitQuantity) {
                //客户已领取数量
                Long hasLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId).eq(MemberCoupon::getUserId, userId));
                //客户限领次数
                int limitQuantity = coupon.getLimitQuantity();
                if (limitQuantity > hasLimitQuantity) {
                    //客户优惠劵新增
                    MemberCoupon memberCoupon = new MemberCoupon();
                    BeanUtil.copyProperties(coupon, memberCoupon);
                    memberCoupon.setId(null);
                    memberCoupon.setUserId(userId);
                    memberCoupon.setCouponId(coupon.getId());
                    int receive = this.baseMapper.insert(memberCoupon);
                    if (receive == 0) {
                        return Result.failed(SystemCode.DATA_ADD_FAILED.getCode(), "优惠劵领取失败！");
                    }
                } else {
                    return Result.failed(SystemCode.ITEM_SOLD_OUT.getCode(), "该张优惠劵领取次数超过限领数量！");
                }
            } else {
                return Result.failed(SystemCode.ITEM_SOLD_OUT.getCode(), "优惠劵已领完！");
            }
        } catch (Exception e) {
            log.error(STR."优惠劵id为\{couponId}", e);
            throw new ServiceException("领取失败");
        } finally {
            rLock.unlock();
        }
        return Result.ok();
    }

    /**
     * 一键领取优惠劵
     * 循环时每张优惠劵要判断还能领取几张
     *
     * @param ids 优惠券id数组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveSomeCoupon(Long[] ids) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            //优惠劵表取出限领次数与客户优惠劵中已领取的张数对比还能领取几张
            Coupon coupon = couponMapper.selectById(id);
            //先验证优惠劵是否存在或者已被关闭
            if (!BeanUtil.isEmpty(coupon) && CouponStatusEnum.OVER.getStatus() != coupon.getStatus()) {
                //获取该优惠券的唯一锁标识
                RLock rLock = redissonClient.getLock(String.valueOf(id));
                try {
                    rLock.lock();
                    //总领取数量
                    Long totalLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, id));
                    //发行数量
                    int issueQuantity = coupon.getIssueQuantity();
                    if (issueQuantity > totalLimitQuantity) {
                        //客户已领取数量
                        Long hasLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, id).eq(MemberCoupon::getUserId, userId));
                        //客户限领次数
                        int limitQuantity = coupon.getLimitQuantity();
                        //客户剩余可领取的次数
                        long count = limitQuantity - hasLimitQuantity;
                        //一键领取要判断剩余可领张数是否大于优惠券剩余张数
                        if (count > 0) {
                            //优惠券剩余可领取数量
                            long canLimitQuantity = issueQuantity - totalLimitQuantity;
                            //计算要领取的优惠券张数 两者择低领取
                            long receiveCount = Math.min(count, canLimitQuantity);
                            //批量新增客户优惠劵
                            for (int i = 0; i < receiveCount; i++) {
                                //客户优惠劵新增
                                MemberCoupon memberCoupon = new MemberCoupon();
                                BeanUtil.copyProperties(coupon, memberCoupon);
                                memberCoupon.setUserId(userId);
                                memberCoupon.setCouponId(coupon.getId());
                                int receive = this.baseMapper.insert(memberCoupon);
                                if (receive == 0) {
                                    log.error("批量领取优惠劵领取失败!");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(STR."优惠劵id为\{id}", e);
                } finally {
                    rLock.unlock();
                }
            }
        });
    }

    /**
     * 积分优惠劵兑换
     * 先判断优惠劵是否达到限领次数上限以及是否超过发行数量
     * 再判断积分是否充足
     *
     * @param couponId 优惠券id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangeCoupon(Long couponId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        Coupon coupon = couponMapper.selectById(couponId);
        //先验证优惠劵是否存在或者已被关闭
        if (BeanUtil.isEmpty(coupon)) {
            throw new ServiceException("优惠劵不存在！", SystemCode.DATA_ADD_FAILED.getCode());
        } else {
            if (CouponStatusEnum.OVER.getStatus() == coupon.getStatus()) {
                throw new ServiceException("优惠劵已关闭，不可领取！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
        //总领取数量
        long totalLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId));
        //发行数量
        int issueQuantity = coupon.getIssueQuantity();
        if (issueQuantity > totalLimitQuantity) {
            //客户已领取数量
            long hasLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId).eq(MemberCoupon::getUserId, userId));
            //客户限领次数
            int limitQuantity = coupon.getLimitQuantity();
            if (limitQuantity > hasLimitQuantity) {
                //用户与积分服务接口调用后新增客户优惠劵
                checkBeforeExchangeCoupon(coupon, userId);
            } else {
                throw new ServiceException("该张优惠劵领取次数超过限领数量！", SystemCode.ITEM_SOLD_OUT.getCode());
            }
        } else {
            throw new ServiceException("优惠劵已领完！", SystemCode.ITEM_SOLD_OUT.getCode());
        }
    }

    /**
     * 判断积分是否充足
     * 再新增优惠劵信息
     * 最后再调用积分扣除接口 注意顺序 保持在一个事务里面
     *
     * @param coupon 优惠券信息
     * @param userId 用户id
     */
    public void checkBeforeExchangeCoupon(Coupon coupon, String userId) {
        BigDecimal value = BigDecimal.valueOf(coupon.getIntegral());
        Boolean isDeduct = false;
        //判断积分是否充足
        if (remoteMiniAccountService.integralSubtractAffirm(userId, value, isDeduct,ISecurity.userMust().getShopId())) {
            //先往客户优惠劵新增
            MemberCoupon memberCoupon = new MemberCoupon();
            BeanUtil.copyProperties(coupon, memberCoupon);
            memberCoupon.setId(null);
            memberCoupon.setUserId(userId);
            memberCoupon.setCouponId(coupon.getId());
            int receive = this.baseMapper.insert(memberCoupon);
            if (receive == 0) {
                throw new ServiceException("优惠劵领取失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
            //拼装数据给积分扣除接口
            String couponValidity = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            //判断有效时间类型
            if (CouponEffectiveTypeEnum.TIME_BEGIN.getStatus() == coupon.getEffectiveType()) {
                //固定时间 取开始时间与结束时间
                String startTime = formatter.format(coupon.getStartTime());
                String endTime = formatter.format(coupon.getEndTime());
                couponValidity = startTime + "-" + endTime;
            } else if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == coupon.getEffectiveType()) {
                //领劵立即生效 取创建时间与有效天数后的时间  结束时间是按天计算不是按24小时 取createTime记得取当天的0点时间
                String startTime = formatter.format(Date.from(coupon.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
                long dayTime = (long) CommonConstants.SECOND_OF_HOUR * CommonConstants.HOUR_OF_DAY;
                LocalDateTime createTime = LocalDateTimeUtils.getDayStart(coupon.getCreateTime());
                long zero = createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / dayTime * dayTime - TimeZone.getDefault().getRawOffset();
                long twelve = zero + dayTime - CommonConstants.NUMBER_ONE;
                long endTime = twelve + coupon.getEffectiveDays() * dayTime;
                Date date = new Date();
                date.setTime(endTime);
                String endDate = formatter.format(date);
                couponValidity = startTime + "-" + endDate;
            }
            IntegralCouponDto integralCouponDto = new IntegralCouponDto();
            integralCouponDto.setIntegralValue(value);
            integralCouponDto.setCouponType(coupon.getCouponType());
            integralCouponDto.setDiscount(coupon.getDiscount());
            integralCouponDto.setDiscountAmount(coupon.getDiscountAmount());
            integralCouponDto.setConditionAmount(coupon.getConditionAmount());
            integralCouponDto.setCouponValidity(couponValidity);
            integralCouponDto.setUserId(userId);
            integralCouponDto.setEffectiveType(coupon.getEffectiveType());
            integralCouponDto.setEffectiveDays(coupon.getEffectiveDays());
            //判断是否扣除成功
            Result<Integer> result = remoteIntegralService.conversionIntegralCoupon(integralCouponDto);
            if (result.getCode() == CommonConstants.FAIL) {
                throw new ServiceException(result.getMsg(), SystemCode.DATA_ADD_FAILED.getCode());
            }
        } else {
            throw new ServiceException("积分不足，无法领取！", SystemCode.DATA_ADD_FAILED.getCode());

        }
    }

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     *
     * @param id              客户优惠券id
     * @param apiProductParam 商品参数
     * @return PageUtils
     */
    @Override
    public IPage<ApiAliveProductVo> getProductPageBySingleCoupon(Long id, ApiProductParam apiProductParam) {
        MemberCoupon memberCoupon = this.baseMapper.selectById(id);
        if (BeanUtil.isEmpty(memberCoupon) || CouponStatusEnum.OVER.getStatus() == memberCoupon.getStatus()) {
            throw new ServiceException("优惠券不存在或已失效!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        List<CouponProduct> couponProducts = apiCouponProductMapper.selectList(new QueryWrapper<CouponProduct>().eq("coupon_id", memberCoupon.getCouponId()));
        List<Long> productIds = new ArrayList<>(couponProducts.size());
        if (CollectionUtil.isNotEmpty(couponProducts)) {
            productIds = couponProducts.stream().map(CouponProduct::getProductId).toList();
        }
        IPage<ApiAliveProductVo> apiAliveProductVoIPage = new Page<>(apiProductParam.getCurrent(), apiProductParam.getSize());
        List<ApiAliveProductVo> aliveProductVos = remoteGoodsService.findAliveProductByMemberCoupon(memberCoupon.getProductType(), productIds, Jackson.writeValueAsString(apiProductParam));
        if (BeanUtil.isNotEmpty(aliveProductVos)) {
            return apiAliveProductVoIPage.setRecords(aliveProductVos);
        } else {
            return new Page<>(apiProductParam.getCurrent(), apiProductParam.getSize());
        }
    }
}
