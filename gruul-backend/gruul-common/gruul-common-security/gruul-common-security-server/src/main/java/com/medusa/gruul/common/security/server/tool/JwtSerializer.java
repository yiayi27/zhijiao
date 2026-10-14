package com.medusa.gruul.common.security.server.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.AbstractSerializer;

import java.io.OutputStream;

/**
 * jwt 序列化器
 *
 * @author 张治保
 * @since 2023/11/28
 */
public class JwtSerializer<T> extends AbstractSerializer<T> {
    @Override
    protected void doSerialize(T data, OutputStream out) throws Exception {
        try (out) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(out, data);
        }
    }
}
