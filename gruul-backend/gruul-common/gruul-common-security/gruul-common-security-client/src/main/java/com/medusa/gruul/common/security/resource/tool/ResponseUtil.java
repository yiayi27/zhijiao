package com.medusa.gruul.common.security.resource.tool;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.io.IOException;
import java.util.List;

/**
 * 响应处理 工具类
 *
 * @author 张治保
 * date 2022/2/28
 */
@Component
@RequiredArgsConstructor
public class ResponseUtil implements InitializingBean {

    private static List<HttpMessageConverter<?>> messageConverters;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * 响应数据
     *
     * @param response 响应
     * @throws IOException IO 异常
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void response(HttpServletResponse response, Object value) throws IOException {
        MediaType selectedMediaType = MediaType.APPLICATION_JSON;
        Class<?> valueType = value.getClass();
        //优先尝试使用 HttpMessageConverter
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter.canWrite(valueType, selectedMediaType)) {
                ((HttpMessageConverter) converter).write(value, selectedMediaType, new ServletServerHttpResponse(response));
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "未能找到合适的序列化方式");
    }


    @Override
    public void afterPropertiesSet() {
        ResponseUtil.messageConverters = requestMappingHandlerAdapter.getMessageConverters();
    }
}
