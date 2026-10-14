package com.medusa.gruul.datasets.service;

import com.medusa.gruul.datasets.api.entity.DcShopActiveAnalyse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.datasets.api.model.CallerDto;
import com.medusa.gruul.datasets.web.model.CallerInfoVo;

/**
 * <p>
 * 访客数据统计表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
public interface IDcShopActiveAnalyseService extends IService<DcShopActiveAnalyse> {


	/**
	 * 新增访客数据信息
	 */
	void addCallerInfo();

	/**
	 * mq消息 新用户访问小程序 增加访问量
	 *
	 * @param shopId  商铺id
	 */
	void saveCallerInfo(String shopId);

	/**
	 * 根据时间筛选出 浏览量
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return CallerDto
	 */
	CallerDto getCallerInfo(Integer startTime, Integer endTime);


	/**
	 * 获取当前商铺的返回信息
	 *
	 * @return 访客信息的数据及统计
	 */
	CallerInfoVo getShopCallerInfo();
}
