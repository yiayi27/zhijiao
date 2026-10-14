package com.medusa.gruul.datasets.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description: 排行榜vo
 * @Author: xiaoq
 * @Date: 2024/5/9 15:30
 */
@Data
@Schema(title = "排行榜详情")
public class RankingsVo {


    /**
     * 供应商交易量排行榜信息
     */
    private List<RankingsDto> supplierVBRanking;



    /**
     * 商品交易量排行榜信息
     */
    private List<RankingsDto> productVBRanking;



}
