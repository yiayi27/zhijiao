package com.medusa.gruul.datasets.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.datasets.service.IDcShopRankingService;
import com.medusa.gruul.datasets.web.model.RankingsGMVVo;
import com.medusa.gruul.datasets.web.model.RankingsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 交易统计表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@RestController
@RequestMapping("/dc-shop-ranking")
public class DcShopRankingController {

	@Autowired
	private IDcShopRankingService dcShopRankingService;

	/**
	 * 获取交易量排行榜信息
	 *
	 * @return 交易量排行榜信息
	 */
	@GetMapping("get/rankingsInfo")
	public Result<RankingsVo> getRankingsInfo() {
		RankingsVo rankingsInfo = dcShopRankingService.getRankingsInfos();
		return Result.ok(rankingsInfo);
	}


	/**
	 * 获取交易额排行榜信息
	 *
	 * @return 交易额排行榜信息
	 */
	@GetMapping("get/rankingsGVMInfo")
	public Result<RankingsGMVVo> getRankingsGvmInfo() {
		return Result.ok(dcShopRankingService.getRankingsGvmInfos());
	}


}
