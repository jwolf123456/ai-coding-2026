package com.hiking.app.common.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NacosConfigListener {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigListener.class);

    private final ConfigService configService;
    private final String group;

    public NacosConfigListener(ConfigService configService, String group) {
        this.configService = configService;
        this.group = group;
    }

    public NacosConfigListener(ConfigService configService) {
        this(configService, "DEFAULT_GROUP");
    }

    public void addListener(String dataId, NacosConfigChangeCallback callback) {
        try {
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return Executors.newSingleThreadExecutor(r -> {
                        Thread t = new Thread(r, "nacos-config-listener-" + dataId);
                        t.setDaemon(true);
                        return t;
                    });
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("Nacos config changed - dataId: {}, group: {}", dataId, group);
                    callback.onChange(dataId, configInfo);
                }
            });
            log.info("Registered Nacos config listener for dataId: {}, group: {}", dataId, group);
        } catch (NacosException e) {
            log.error("Failed to add Nacos config listener for dataId: {}, group: {}", dataId, group, e);
        }
    }
}