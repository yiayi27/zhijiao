package com.medusa.gruul.payment.common.context;

/**
 * 商户配置id
 * @author 张治保
 * date 2024/12/23
 */
public class DetailsIdContextHolder {

    private static final ThreadLocal<String> LOCAL = new ThreadLocal<>();

    public static void set(String detailsId){
        LOCAL.set(detailsId);
    }

    public static String get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
