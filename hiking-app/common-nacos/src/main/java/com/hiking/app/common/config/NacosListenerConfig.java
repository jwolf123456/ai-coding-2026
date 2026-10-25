package com.hiking.app.common.config;

import com.hiking.app.common.nacos.NacosConfigListener;
import com.hiking.app.common.utils.EnvConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NacosListenerConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NacosListenerConfig.class);

    @Autowired
    private NacosConfigListener nacosConfigListener;

    @Override
    public void run(ApplicationArguments args){

        nacosConfigListener.addListener("common.yml", (dataId, configInfo) -> {
            log.info("common.yml config changed, refreshing...");
        });
        String profile = EnvConfigUtil.getProperty("spring.profiles.active");
        String applicationName = EnvConfigUtil.getProperty("spring.application.name");
        String appDataId=String.format("%s-%s.yml",applicationName,profile);
        nacosConfigListener.addListener(appDataId, (dataId, configInfo) -> {
            log.info("%s config changed, refreshing...",appDataId);
        });
    }
}