package com.medusa.gruul.integral.service;

import com.medusa.gruul.integral.api.entity.IntegralClock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralClockDto;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 积分签到表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralClockService extends IService<IntegralClock> {




	/**
	 * 根据商铺id 获取商家设置的积分签到规则 天数
	 *
	 * @param shopId 商铺id
	 * @return 每天对应的积分值
	 */
	HashMap<Integer, String> getSingInIntegralValue(String shopId);

	/**
	 * 获取用户签到信息
	 *
	 * @param userId 用户id
	 * @return 用户签到信息
	 */
	IntegralClockDto userSingInInfo(String userId);

    /**
     * 用户签到
     * @param userId
     * @return 获得积分值
     */
	Integer userSingIn(String userId);

	/**
	 *
	 * 获取所有当日签到信息为false的用户 修改用户连续签到天数为0
	 * @return
	 */
	List<IntegralClock> selectStatusAll();

	/**
	 *
	 * 生成用户签到信息默认值
	 *
	 */
	void addSingInDefault();


	/**
	 * 查询所有用户
	 * @return
	 */
	List<IntegralClock> selectAll();


	/**
	 * 修改 未签到的用户连续签到天数为1L
	 *
	 * @param integralClock
	 */
	void updateRunningDays(IntegralClock integralClock);


	/**
	 * 定时任务 修改签到状态为未签到
	 *
	 * @param integralClock
	 */
	void updateSignIn(IntegralClock integralClock);


	/**
	 * 获取该商铺下所有的用户签到信息
	 *
	 * @param shopId 商铺id
	 * @return
	 */
	List<IntegralClock> selectShopAll(String shopId);


//---------------------------------------------------废弃接口---------------------------------------------------
	/**
	 *
	 * 根据调用接口时间，创建默认签到信息
	 * @param shopId  店铺Id
	 * @param userId  用户id
	 * @param date    调用接口当前时间
	 */
//	void saveSingInMessage(String shopId, String userId, String date);

}
