package com.medusa.gruul.datasets.mapper;

import com.medusa.gruul.datasets.api.entity.DcShopActiveAnalyse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 访客数据统计表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
public interface DcShopActiveAnalyseMapper extends BaseMapper<DcShopActiveAnalyse> {


	/**
	 * 根据userid获取访客信息
	 *
	 * @param userId 用户id
	 * @return 访客信息
	 */
	DcShopActiveAnalyse getCallerInfoByUserId(@Param("userId") String userId);

	/**
	 * 获取用户当日访客数据信息
	 *
	 * @return
	 */
	DcShopActiveAnalyse getCallerInfoByToday();

	/**
	 * 获取用户当日访客数据信息
	 * @param shopId 商铺id
	 * @return
	 */
	DcShopActiveAnalyse getCallerInfoByShopId(@Param("shopId") String shopId);

	/**
	 * 获取用户昨日日访客数据信息
	 *
	 * @return
	 */
	DcShopActiveAnalyse getCallerInfoByYesterday();

	/**
	 * 根据时间获取该时间端的所有浏览量
	 *
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	Integer getCallerInfoByTime(@Param("startTime") Integer startTime, @Param("endTime") Integer endTime);

	/**
	 * 获取这个商铺总的 访客人数
	 *
	 * @return
	 */
	Integer getAllCallerInfoByShopId();

	/**
	 *  获取这个商铺总的 浏览量
	 * @return
	 */
	Integer getAllPageViewInfoByShopId();
}
