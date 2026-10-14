package com.medusa.gruul.live.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The type Json we chat code vo.
 * @author alan
 */
@Data
@Accessors(chain = true)
public class JsonWeChatCodeVo {
    /**
     * 错误代码.
     */
    private int errcode;
    /**
     * 错误信息.
     */
    private String errmsg;
    /**
     * 文本格式.
     */
    private String contentType;
    /**
     * 文本内容.
     */
    private String buffer;
}
