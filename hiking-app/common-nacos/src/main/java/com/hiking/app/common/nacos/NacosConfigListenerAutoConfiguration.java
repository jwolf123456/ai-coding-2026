package com.hiking.app.common.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@AutoConfiguration
public class NacosConfigListenerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigListenerAutoConfiguration.class);

    @Bean
    public ConfigService nacosConfigService(
            @Value("${spring.cloud.nacos.server-addr:localhost:8848}") String serverAddr,
            @Value("${spring.cloud.nacos.config.namespace:public}") String namespace,
            @Value("${spring.cloud.nacos.config.username:}") String username,
            @Value("${spring.cloud.nacos.config.password:}") String password
    ) throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        if (namespace != null && !namespace.isEmpty()) {
            properties.put("namespace", namespace);
        }
        if (username != null && !username.isEmpty()) {
            properties.put("username", username);
        }
        if (password != null && !password.isEmpty()) {
            properties.put("password", password);
        }
        ConfigService configService = NacosFactory.createConfigService(properties);
        log.info("Nacos ConfigService initialized, serverAddr: {}", serverAddr);
        return configService;
    }

    @Bean
    public NacosConfigListener nacosConfigListener(
            ConfigService configService,
            @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}") String group
    ) {
        return new NacosConfigListener(configService, group);
    }

    @Bean
    public NacosConfigInitializer nacosConfigInitializer(
            ConfigService configService,
            @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}") String group,
            @Value("${spring.cloud.nacos.config.namespace:public}") String namespace
    ) {
        return new NacosConfigInitializer(configService, group, namespace);
    }
}