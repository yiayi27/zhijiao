package com.medusa.gruul.integral.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.service.IGaoDeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
/**
 * @author zhaozheng
 */
public class GaoDeServiceImpl implements IGaoDeService {
    @Value("${geo.key}")
    private  String KEY = "689f39f8e88f66fd0df7abb8a88547b6";
    private static final String CONFIG_URL = "https://restapi.amap.com/v3/config/district";
    private static final String API_SEARCH = "https://restapi.amap.com/v3/place/around";


    @Override
    public Result<Object> configDistrict(String keywords, String subdistrict, Integer page, Integer offset, String extensions, String filter, String callback, String output) {
        StringBuilder url = new StringBuilder().append(CONFIG_URL).append("?key=").append(KEY);
        mosaic(url, "keywords", keywords);
        mosaic(url, "subdistrict", subdistrict);
        mosaic(url, "page", page == null ? null : page.toString());
        mosaic(url, "offset", offset == null ? null : offset.toString());
        mosaic(url, "extensions", extensions);
        mosaic(url, "filter", filter);
        mosaic(url, "callback", callback);
        mosaic(url, "output", output);
        String res = HttpUtil.get(url.toString());
        return Result.ok().setData(JSONUtil.parseObj(res));
    }

    @Override
    public Result<Object> apiSearch(String location, String keywords, String types, String city, String radius, String sortrule, Integer offset, Integer page, String extensions, String sig, String output, String callback) {
        StringBuilder url = new StringBuilder().append(API_SEARCH).append("?key=").append(KEY);
        mosaic(url, "location", location);
        mosaic(url, "keywords", keywords);
        mosaic(url, "types", types);
        mosaic(url, "city", city);
        mosaic(url, "radius", radius);
        mosaic(url, "sortrule", sortrule);
        mosaic(url, "offset", offset == null ? null : offset.toString());
        mosaic(url, "page", page == null ? null : page.toString());
        mosaic(url, "extensions", extensions);
        mosaic(url, "output", output);
        mosaic(url, "callback", callback);
        String res = HttpUtil.get(url.toString());

        return Result.ok().setData(JSONUtil.parseObj(res));
    }

    /**
     * url拼接参数方法
     */
    private void mosaic(StringBuilder builder, String key, String val) {
        if (StringUtils.isNotEmpty(val)) {
            builder.append("&").append(key).append("=").append(val);
        }
    }
}
