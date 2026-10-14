package com.medusa.gruul.payment.api.util;


import cn.hutool.crypto.SecureUtil;


/**
 * @author create by zq
 * @date created in 2024/03/11
 */
public class ParamMd5SignUtils {


    static String MD5_KEY = "ParamMd5SignUtils";


    /**
     * MD5加密
     *
     * @param obj 明文
     * @return value
     */
    public static String md5(Object obj) {
        return SecureUtil.md5(obj.toString() + MD5_KEY);
    }


    /**
     * MD5验证
     *
     * @param md5 密文
     * @return status
     */
    public static boolean checkParam(Object obj, String md5) {
        return md5(obj).equalsIgnoreCase(md5);
    }

}
