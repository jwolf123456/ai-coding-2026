package com.hiking.app.common.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.hiking.app.common.utils.EnvConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NacosConfigInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigInitializer.class);

    private final ConfigService configService;
    private final String group;
    private final String namespace;

    public NacosConfigInitializer(ConfigService configService, String group, String namespace) {
        this.configService = configService;
        this.group = group;
        this.namespace = namespace;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            Resource[] commonResources = resolver.getResources("classpath*:common.yml");
            for (Resource resource : commonResources) {
                String filename = resource.getFilename();
                if (filename == null) {
                    continue;
                }
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                publishIfAbsent(filename, content);
            }
            String profile = EnvConfigUtil.getProperty("spring.profiles.active");
            String applicationName = EnvConfigUtil.getProperty("spring.application.name");
            String dataId=String.format("%s-%s.yml",applicationName,profile);
            Resource resource = resolver.getResource("classpath:"+dataId);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            publishIfAbsent(dataId, content);

        } catch (IOException e) {
            log.warn("Failed to scan classpath resources: {}", e.getMessage());
        }
    }

    private void publishIfAbsent(String dataId, String content) {
        try {
            String existing = configService.getConfig(dataId, group, 3000L);
            if (existing != null && !existing.isEmpty()) {
                log.info("Nacos config [{}] already exists in group [{}], skip publishing", dataId, group);
                return;
            }
            boolean success = configService.publishConfig(dataId, group, content, "yaml");
            if (success) {
                log.info("Nacos config [{}] published to group [{}] successfully", dataId, group);
            } else {
                log.warn("Nacos config [{}] publish failed", dataId);
            }
        } catch (NacosException e) {
            log.warn("Failed to check/publish Nacos config [{}]: {}", dataId, e.getMessage());
        }
    }
}