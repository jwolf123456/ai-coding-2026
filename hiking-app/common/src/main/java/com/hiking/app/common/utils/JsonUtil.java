package com.hiking.app.common.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Jackson JSON 工具类
 * <p>
 * 配置规则参考 JacksonSerializerConfiguration：
 * 1. 日期格式：yyyy-MM-dd HH:mm:ss
 * 2. 时区：GMT+8
 * 3. 序列化所有字段，关闭空 Bean 报错
 * 4. 反序列化忽略未知属性
 * 5. 支持 LocalDateTime / LocalDate / LocalTime / Date 自定义格式
 * 6. Long 类型长度 >= 18 时序列化为 String，避免前端精度丢失
 */
public final class JsonUtil {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String TIME_ZONE = "GMT+8";

    private static final ObjectMapper OBJECT_MAPPER;
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //关闭日期序列化为时间戳的功能
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //关闭序列化的时候没有为属性找到getter方法,报错
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //序列化的时候序列对象的所有属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //空值不序列化
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //序列化空对象不抛异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //关闭反序列化的时候，没有找到属性的setter报错
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        //反序列化未知属性不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //自定义序列化：key-序列化字段类型,value-序列化所用对象,支持自定义及Jackson自带序列化器
        SimpleModule module = new SimpleModule();
        String[] patternArr = DATE_TIME_PATTERN.split(" ");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(patternArr[0])));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(patternArr[1])));
        module.addSerializer(Date.class, new DateSerializer(false,new SimpleDateFormat(DATE_TIME_PATTERN)));
        //module.addSerializer(Long.class,ToStringSerializer.instance); // 会将mybatisplus 分页返回的total也转成字符串导致flutter问题
        // 自定义序列化：long长度超过18位则认为是雪花ID，转为string，否则为数字，
        module.addSerializer(Long.class, new JsonSerializer<Long>() {
            @Override
            public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                String str = String.valueOf(aLong);
                if (str.length()>=18) {
                    jsonGenerator.writeString(str);
                }else {
                    jsonGenerator.writeNumber(aLong);
                }
            }
        });
        //自定义反序列化：key-序列化字段类型,value-序列化所用对象,支持自定义及Jackson自带序列化器
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(patternArr[0])));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(patternArr[1])));
        module.addDeserializer(Date.class, new DateDeserializers.DateDeserializer(){
            @SneakyThrows
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt){
                String text = jsonParser.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
                return sdf.parse(text);
            }
        });
        objectMapper.registerModule(module);
        OBJECT_MAPPER = objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转 JSON 失败", e);
        }
    }

    /**
     * 对象转格式化 JSON 字符串
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转格式化 JSON 失败", e);
        }
    }

    /**
     * JSON 字符串转对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON 转对象失败, class=" + clazz.getName(), e);
        }
    }

    /**
     * JSON 字符串转 List
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (IOException e) {
            throw new RuntimeException("JSON 转 List 失败, elementClass=" + elementClass.getName(), e);
        }
    }

    /**
     * JSON 字符串转复杂类型，例如 Map、嵌套对象
     */
    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("JSON 转复杂对象失败", e);
        }
    }

    /**
     * JSON 字符串转 Map
     */
    public static Map<String, Object> toMap(String json) {
        return toObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 对象转 byte[]
     */
    public static byte[] toJsonBytes(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转 JSON byte[] 失败", e);
        }
    }

    /**
     * byte[] 转对象
     */
    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON byte[] 转对象失败, class=" + clazz.getName(), e);
        }
    }
}