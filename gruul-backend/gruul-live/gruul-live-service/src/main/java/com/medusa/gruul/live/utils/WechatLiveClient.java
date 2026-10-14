package com.medusa.gruul.live.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.live.api.model.vo.JsonLiveInfoVo;
import com.medusa.gruul.live.api.model.vo.JsonWeChatCodeVo;
import com.medusa.gruul.live.api.model.vo.RoomInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与微信后台的交互
 *
 * @author zhaokw
 * @date 2024/04/04
 */
@Component
@Slf4j
public class WechatLiveClient {
    /**
     * 请求超时时长，毫秒
     */
    private static final int SERVICE_TIMEOUT = 6000;

    /**
     * @param accessToken
     * @Description: 从微信后台获取直播列表
     * @Author: zhaokw
     * @Date: 2024/4/4 15:49
     * @return: boolean
     **/
    public  List<RoomInfoVo> getLiveInfo(String accessToken) {
        log.info("accessToken is {}.",accessToken );
        List<RoomInfoVo> roomInfoVos = new ArrayList<>();
        String url = "https://api.weixin.qq.com/wxa/business/getliveinfo?access_token=" + accessToken;
        int start = 0;
        int limit = 50;
        String resultString = null;
        Map<String, Object> paramMap = new HashMap<>(50);
        JsonLiveInfoVo liveInfoVo = null;
        for (int i = start; ; i += limit) {
            paramMap.put("start", i);
            paramMap.put("limit", limit);
            resultString = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap), SERVICE_TIMEOUT);
            log.info("resultString is {}.",resultString );

            liveInfoVo = JSONUtil.toBean(resultString, JsonLiveInfoVo.class);
            if (liveInfoVo.getRoom_info().size() <= i + limit) {
                break;
            }
        }
        log.info("[getLiveInfo] get to wechat url={} request={} response={}", url,
                JSONUtil.toJsonStr(paramMap), resultString);

        if ("0".equals(String.valueOf(liveInfoVo.getErrcode()))) {
            roomInfoVos = liveInfoVo.getRoom_info();
        } else if ("1".equals(String.valueOf(liveInfoVo.getErrcode()))) {
            log.info("getLiveInfo fail because:{}", "未创建直播列表" + liveInfoVo.getErrmsg());
        } else {
            log.info("getLiveInfo fail because:{}", liveInfoVo.getErrmsg());
        }
        return roomInfoVos;
    }

    /**
     * @param accessToken
     * @Description: 微信端获取小程序码
     * @Author: zhaokw
     * @Date: 2024/4/11 15:49
     * @return: boolean
     **/
    public String getWechatCode(String accessToken) {
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN" + accessToken;
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("scene", "a=1");
        String resultString = HttpUtil.post(url, paramMap, SERVICE_TIMEOUT);
        JsonWeChatCodeVo jsonWeChatCodeVo = JSONUtil.toBean(resultString, JsonWeChatCodeVo.class);
        log.info("[getLiveInfo] get to wechat url={} request={} response={}", url, JSONUtil.toJsonStr(paramMap),
                resultString);

        if ("45009".equals(String.valueOf(jsonWeChatCodeVo.getErrcode()))) {
            log.error("getWechatCode fail because:{}", "调用分钟频率受限(目前5000次/分钟，会调整)，如需大量小程序码，建议预生成。");
            return null;
        } else if ("41030".equals(String.valueOf(jsonWeChatCodeVo.getErrcode()))) {
            log.error("getWechatCode fail because:{}", "所传page页面不存在，或者小程序没有发布");
            return null;
        }
        return jsonWeChatCodeVo.getBuffer();

    }
    
}
