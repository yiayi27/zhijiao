package com.medusa.gruul.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.model.IntegralCouponDto;
import com.medusa.gruul.integral.web.model.dto.IntegralDetailDto;
import com.medusa.gruul.integral.web.model.param.api.IntegralDetailParam;
import com.medusa.gruul.integral.web.model.vo.IntegralDetailVo;
import com.medusa.gruul.integral.api.model.UserInfoVo;
import com.medusa.gruul.order.api.model.OrderFailMessage;
import com.medusa.gruul.order.api.model.OrderVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 积分明细表
 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralDetailService extends IService<IntegralDetail> {

	/**
	 * 用户查询积分详情 明细
	 *
	 * @param integralDetailParam (api端)
	 * @return 分页对象
	 */
	IPage<IntegralDetailVo> selectIntegralDetail(IntegralDetailParam integralDetailParam);


	/**
	 * 删除积分明细记录
	 *
	 * @param integralDetailId 积分明细id
	 */
	void delDetail(Long integralDetailId);


	/**
	 * 用户分享获得积分
	 *
	 * @param shopId 商铺id
	 * @param userId 用户Id
	 * @return 用户获得积分值
	 */
	BigDecimal addIntegralDetail(String shopId, String userId);


	/**
	 * 生成积分明细记录  增加
	 *
	 * @param userId 用户Id
	 * @param integralValue 积分值
	 * @return 是否成功
	 */
	Boolean addIntegralDetailRecord(String userId, BigDecimal integralValue);


	/**
	 * 生成积分明细记录  扣除
	 *
	 * @param userId 用戶id
	 * @param integralValue  积分值
	 * @param shopId 店铺id
	 * @return 是否成功
	 */
	Boolean subtractIntegralDetailRecord(String userId, BigDecimal integralValue,String shopId);


	/**
	 * 商家对用户进行积分充值
	 *
	 * @param userIds 用户ids
	 * @param integral 积分值
	 */
	void rechargeIntegral(List<String> userIds, BigDecimal integral);

	/**
	 * 商家对用户进行积分扣除  批量扣除
	 *
	 * @param userIds 用户ids
	 * @param integral 积分值
	 */
	void deductIntegral(List<String> userIds, BigDecimal integral);

	/**
	 * 获取用户信息
	 *
	 * @param userId 用户od
	 * @return 用户信息
	 */
	UserInfoVo getUserInfo(String userId);


	/**
	 * 订单订阅 生成明细记录 通知用户
	 *
	 * @param orderVo 订单信息(普通订单)
	 */
	void orderFinish(OrderVo orderVo);


	/**
	 * 生成 兑换积分明细 (积分优惠券)
	 *
	 * @param integralCouponDto
	 * @return 插入 兑换积分优惠券信息是否成功
	 */
	Boolean addConversionIntegralDetail(IntegralCouponDto integralCouponDto);


	/**
	 * 获取需要清空的积分
	 *
	 * @param shopId 商铺id
	 * @param createTime 积分清空时间 (对积分获取时间小于该时间的积分进行清空)
	 * @return 需要清空的积分
	 */
	List<Map<String, Object>> getNeedClearIntegrals(String shopId, String createTime);


	/**
	 * 获取需要清空的用户 以及清空积分值
	 *
	 * @param shopId 商品id
	 * @return map<key                               ,                               用户id以及清空的积分值>
	 */
	List<Map<String, Object>> selectAllUser(String shopId);


	/**
	 * 生成积分新的积分明细
	 *
	 * @param userId 用户id
	 * @param integral 扣除积分值
	 * @param shopId 商品id
	 */
	void generateNowIntegralDetailService(String userId, BigDecimal integral, String shopId);


	/**
	 * 修改这个商铺中所有的积分明细信息为 已清空状态
	 *
	 * @param shopId 商铺id
	 */
	void updateAll(String shopId);


	/**
	 * 按条件获取用户积分明细 (pc端)
	 *
	 * @param integralDetailDto 查询条件
	 * @return 符合条件的数据
	 */
	IPage<IntegralDetailVo> getIntegralDetail(IntegralDetailDto integralDetailDto);


    /**
     * 创建订单失败订阅 生成明细记录 通知用户
     * @param orderFailMessage
     */
	void createOrderFail(OrderFailMessage orderFailMessage);


	/**
	 * 使用积分兑换会员卡
	 *
	 * @param userId 用户id
	 * @param integralValue 所需积分值
	 * @return 兑换会员卡 信息 如 成功 失败(黑名单 ,及积分不足)
	 */
	Result<Integer> useIntegralConversionClubCard(String userId, BigDecimal integralValue);

}
