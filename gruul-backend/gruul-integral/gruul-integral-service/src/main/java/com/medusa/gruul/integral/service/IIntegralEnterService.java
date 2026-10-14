package com.medusa.gruul.integral.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.entity.IntegralLogIn;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 积分登入表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-12-31
 */
public interface IIntegralEnterService extends IService<IntegralLogIn> {

    /**
     * 用户登入获取积分
     *  @param userId 用户id
     * @return
     */
	BigDecimal userLogin(String userId);


	/**
	 * 获取所有登入状态为false的数据
	 *
	 * @return 用戶登入信息
	 */
	List<IntegralLogIn> selectStatusAll();

	/**
	 * 获取所有登入获取积分信息
	 *
	 * @return
	 */
	List<IntegralLogIn> selectAll();


	/**
	 * 定时任务 修改状态为未登入的用户连续签到前天数1L
	 *
	 * @param integralLogIn
	 */
	void updateEnterDays(IntegralLogIn integralLogIn);


	/**
	 * 定时任务 修改登入状态为未登入
	 *
	 * @param integralLogIn
	 */
	void updateEnterStatus(IntegralLogIn integralLogIn);



	/**
	 * 获取该商铺下所有的用户登入信息
	 *
	 * @param shopId
	 * @return
	 */
	List<IntegralLogIn> selectShopAll(String shopId);
}
