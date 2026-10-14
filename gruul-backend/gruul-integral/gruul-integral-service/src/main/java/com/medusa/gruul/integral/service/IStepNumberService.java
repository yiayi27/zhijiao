package com.medusa.gruul.integral.service;

import com.medusa.gruul.integral.api.entity.StepNumber;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.web.model.dto.manager.DecryptRunData;
import com.medusa.gruul.integral.web.model.dto.manager.StepNumberDto;
import com.medusa.gruul.integral.web.model.dto.manager.UserStepNumberDto;

import java.util.List;

/**
 * <p>
 * 步数表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IStepNumberService extends IService<StepNumber> {

	/**
	 * 获取微信用户的步数(数据表里存储的今日步数兑换信息) 及用户信息
	 *
	 * @param decryptRunData 解密数据
	 * @return
	 */
	String getUserStepNumber(DecryptRunData decryptRunData);

	/**
	 * 用户进行步数兑换
	 *
	 * @param todayStep 当前步数
	 * @param targetStep 目前步数
	 * @return 剩余步数
	 */
	String userStepNumberConversion(Integer todayStep, Integer targetStep);

	/**
	 * 	 用户步数兑换相关信息获取
	 *
	 *   @param userId 用户id
	 * 	 @param theCurrentStep 当前步数
	 * @return
	 */
	UserStepNumberDto getUserStepNumberInfo(String userId, Integer theCurrentStep);

	/**
	 *  获取所有已兑换步数大于0的用户信息
	 *
	 *
	 * @return
	 */
	List<StepNumber> selectByHasChange();

	/**
	 * 定时任务 修改用户步数为0
	 *
	 * @param sn
	 */
	void updateStepNumber(StepNumber sn);
}
