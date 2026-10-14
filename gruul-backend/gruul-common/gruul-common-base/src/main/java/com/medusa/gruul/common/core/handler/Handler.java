package com.medusa.gruul.common.core.handler;

/**
 * 处理器超类
 * @author 张治保
 * date 2024/11/29
 */
public interface Handler<T> {
    /**
     * 处理参数
     * @param params 参数
     * @return 返回内容 <T>返回数据类型
     */
    T handle(Object... params);

    /**
     * 校验参数
     * @param params 处理参数
     * @param classes 处理参数类型
     * @return 是否穿了错误参数  true是 false 否
     */
    default boolean hasErrorParam(Object[] params, Class<?>... classes) {
        if (classes == null || classes.length ==0){
            return false;
        }
        if (params == null || params.length == 0 || params.length != classes.length){
            return true;
        }
        for (int i = 0; i < classes.length; i++) {
            Class<?> aClass = classes[i];
            if (aClass == null || aClass.isAssignableFrom(params[i].getClass())){
                continue;
            }
            return true;
        }
        return false;
    }
    /**
     * 强转参数
     * @param value 被强转的对象,
     * @param type 强转后的类型
     * @return 强转后的值
     */
    default <E> E  cast(Object value,Class<E> type){
        return type.cast(value);
    }

}
