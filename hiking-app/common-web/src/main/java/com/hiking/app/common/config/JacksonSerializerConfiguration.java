package com.hiking.app.common.config;

import com.hiking.app.common.utils.JsonUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 后端->前端全局序列化设置：
 * 全局Long转String,LocalDateTime LocalDate LocalTime  java.util.Date序列化与反序列化
 * 可以通过实现WebMvcConfigurer接口
 * @author majun
 * @since 2020-08-21
 */
@Configuration
//@Primary
public class JacksonSerializerConfiguration implements WebMvcConfigurer {
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> cvt : converters) {
            if (cvt instanceof  MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter= (MappingJackson2HttpMessageConverter) cvt;
                enhanceConvertor(converter);
            }

        }
        
    }

    private void enhanceConvertor(MappingJackson2HttpMessageConverter converter) {
        converter.setObjectMapper(JsonUtil.getObjectMapper());
    }

}