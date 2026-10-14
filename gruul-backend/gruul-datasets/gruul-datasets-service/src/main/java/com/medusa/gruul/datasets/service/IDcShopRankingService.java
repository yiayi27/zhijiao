package com.medusa.gruul.datasets.service;

import com.medusa.gruul.datasets.api.entity.DcShopRanking;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.datasets.web.model.RankingsGMVVo;
import com.medusa.gruul.datasets.web.model.RankingsVo;
import com.medusa.gruul.order.api.entity.OrderItem;
import com.medusa.gruul.order.api.model.OrderVo;

/**
 * <p>
 * 交易统计表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
public interface IDcShopRankingService extends IService<DcShopRanking> {

	/**
	 * 订阅已支付 完成的订单(插入信息)
	 *
	 * @param orderVo 订单vo
	 */
	void orderPayFinish(OrderVo orderVo);

	/**
	 * 获取排行榜交易量信息
	 *
	 * @return 排行榜信息
	 */
	RankingsVo getRankingsInfos();

	/**
	 * 获取排行榜交易额信息
	 *
	 * @return RankingsGMVVo
	 */
	RankingsGMVVo getRankingsGvmInfos();

}
