package com.medusa.gruul.datasets.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description: 排行榜交易额 vo
 * @Author: xiaoq
 * @Date: 2024/5/12 10:10
 */
@Data
@Schema(title = "排行榜交易额详情")
public class RankingsGMVVo {

    /**
     * 供应商交易额排行榜信息
     */
    private List<RankingsDto> supplierGMVRanking;


    /**
     * 商品交易额排行榜信息
     */
    private List<RankingsDto> productGMVRanking;
}
