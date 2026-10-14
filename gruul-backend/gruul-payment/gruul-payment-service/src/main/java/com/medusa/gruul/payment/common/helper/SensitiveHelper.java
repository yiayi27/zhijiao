package com.medusa.gruul.payment.common.helper;

import cn.hutool.core.util.StrUtil;

/**
 * 脱敏辅助类
 * @author 张治保
 * date 2024/12/20
 */
public class SensitiveHelper {

    /**
     * 前两个和后三个字符正常显示
     */
    public static String desensitive(String str){
        return SensitiveHelper.desensitive(str,2,3);
    }
    /**
     * 替换指定字符串的指定区间内字符为"*"
     * 前pre个字符正常显示
     * 后suffix个字符正常显示
     * desensitive("12334", 1, 1) ==> 1****4
     * desensitive("12334", 1, 2) ==> 1****34
     */
    public static String desensitive(String str,int pre,int suffix){
        if (StrUtil.isBlank(str)){
            return null;
        }
        return StrUtil.sub(str, 0, pre)
            .concat("****")
            .concat(  StrUtil.sub(str, str.length() - suffix, str.length()) );
    }

}
