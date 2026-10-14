package com.medusa.gruul.recycling.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/4/30
 */
public class RecyclingOrderNewDto {

    /**
     * 物品图片
     */
    @Schema(title = "物品图片")
    private String articleImgs;


}
