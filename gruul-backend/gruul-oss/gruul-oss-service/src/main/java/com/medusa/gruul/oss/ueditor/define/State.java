package com.medusa.gruul.oss.ueditor.define;

/**
 * 处理状态接口
 *
 * @author hancong03@baidu.com
 */
public interface State {

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    boolean isSuccess();

    /**
     * 传值
     *
     * @param name 名称
     * @param val 值
     */
    void putInfo(String name, String val);

    /**
     * 传值
     *
     * @param name 名称
     * @param val 值
     */
    void putInfo(String name, long val);

    /**
     * 转json格式字符串
     *
     * @return 状态信息
     */
    String toJsonString();

}
