package com.medusa.gruul.integral.service;

import com.medusa.gruul.common.core.util.Result;

/**
 * @author zhaozheng
 */
public interface IGaoDeService {
    /**
     * 高德api, 具体前往网址查看: https://lbs.amap.com/api/webservice/guide/api/district
     * @param keywords
     * @param subdistrict
     * @param page
     * @param offset
     * @param extensions
     * @param filter
     * @param callback
     * @param output
     * @return
     */
    Result configDistrict(String keywords, String subdistrict, Integer page, Integer offset, String extensions, String filter, String callback, String output);

    /**
     * 高德api, 具体前往网址查看: https://lbs.amap.com/api/webservice/guide/api/search
     * @param location
     * @param keywords
     * @param types
     * @param city
     * @param radius
     * @param sortrule
     * @param offset
     * @param page
     * @param extensions
     * @param sig
     * @param output
     * @param callback
     * @return
     */
    Result apiSearch(String location,String keywords, String types, String city, String radius, String sortrule, Integer offset, Integer page, String extensions, String sig, String output, String callback);
}
