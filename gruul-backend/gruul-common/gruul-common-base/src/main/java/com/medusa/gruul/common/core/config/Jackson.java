package com.medusa.gruul.common.core.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Description: JacksonConfig
 *
 * @author : alan
 * Date: 2023/7/13 19:20
 * modify by 张治保
 * date 2024-12-01
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class Jackson {

    /**
     * 字符串反序列化
     * 1. 去除前后空格
     * 2. 空字符串转为null
     */
    private static final JsonDeserializer<String> STRING_DESERIALIZER = new JsonDeserializer<>() {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String valueAsString = p.getValueAsString();
            String result;
            if (valueAsString == null || (result = valueAsString.trim()).isEmpty()) {
                return null;
            }
            return result;
        }
    };
    public static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setLocale(Locale.CHINA);
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //空字符串 to null
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(ObjectMapperModule.INSTANCE);
    }

    /**
     * 序列化
     *
     * @param value 对象
     * @return 序列化后的字符串
     */
    public static String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ServiceException(SystemCode.PARAM_BIND_ERROR);
        }
    }

    /**
     * 反序列化
     *
     * @param <T>       对象类型
     * @param content   内容
     * @param valueType 目标类型
     * @return 转换后的对象
     */
    public static <T> T readValue(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (IOException exception) {
            throw new ServiceException(SystemCode.PARAM_BIND_ERROR);
        }
    }

    /**
     * 反序列化
     *
     * @param <T>       对象类型
     * @param content   内容
     * @param reference 类型引用
     * @return 转换后的对象
     */
    public static <T> T readValue(String content, TypeReference<T> reference) {
        try {
            return mapper.readValue(content, reference);
        } catch (IOException exception) {
            throw new ServiceException(SystemCode.PARAM_BIND_ERROR);
        }
    }

    /**
     * 将对象转换为指定类型
     *
     * @param <T>       对象类型
     * @param value     对象
     * @param valueType 目标类型
     * @return 转换后的对象
     */
    public static <T> T convertValue(Object value, Class<T> valueType) {
        return mapper.convertValue(value, valueType);
    }

    /**
     * 将对象转换为指定类型
     *
     * @param <T>          对象类型
     * @param value        对象
     * @param valueTypeRef 目标类型
     * @return 转换后的对象
     */
    public static <T> T convertValue(Object value, TypeReference<T> valueTypeRef) {
        return mapper.convertValue(value, valueTypeRef);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            //空字符串 to null
            builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.modules(ObjectMapperModule.INSTANCE);
        };
    }

    /**
     * java 8 时间默认序列化
     *
     * @author L.cm
     * @author lishanbu
     */
    private static class ObjectMapperModule {
        public static com.fasterxml.jackson.datatype.jsr310.JavaTimeModule INSTANCE = new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule();

        static {
            INSTANCE.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            INSTANCE.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            INSTANCE.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
            INSTANCE.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            INSTANCE.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            INSTANCE.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
            //long 类型转为字符串
            INSTANCE.addSerializer(Long.class, ToStringSerializer.instance);
            //字符串去除前后空格
            INSTANCE.addDeserializer(String.class, STRING_DESERIALIZER);
        }
    }
}
