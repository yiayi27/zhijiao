package com.medusa.gruul.live.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * The type Json live info vo.
 * @author alan
 */
@Data
@Accessors(chain = true)
public class JsonLiveInfoVo {
    /**
     * 错误代码.
     */
   private int errcode;
    /**
     * 错误信息.
     */
    private String errmsg;
    /**
     * 房间信息.
     */
    private List<RoomInfoVo> room_info;
    /**
     * 总数
     */
    private int total;
}
