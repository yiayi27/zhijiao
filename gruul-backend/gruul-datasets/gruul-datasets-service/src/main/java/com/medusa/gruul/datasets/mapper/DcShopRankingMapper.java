package com.medusa.gruul.datasets.mapper;

import com.medusa.gruul.datasets.api.entity.DcShopRanking;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.datasets.web.model.RankingsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 交易统计表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
public interface DcShopRankingMapper extends BaseMapper<DcShopRanking> {


	/**
	 *  根据当前月份 排行榜code 获取 排行榜交易量信息
	 *
	 * @param code 排行榜区分  供应商 商品
	 * @param month 当前月份
	 * @return 本月交易量 排行榜前5 ( 供应商 商品)
	 */
	List<RankingsDto> selectVbByType(@Param("code") int code, @Param("month") Integer month);


	/**
	 * 根据当前月份 排行榜code 获取 排行榜交易额信息
	 *
	 * @param code 排行榜区分  供应商 商品
	 * @param month 当前月份
	 * @return 本月交易额 排行榜前5 ( 供应商 商品)
	 */
	List<RankingsDto> selectGvmByType(@Param("code") int code, @Param("month") Integer month);

}
