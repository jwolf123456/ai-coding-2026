package com.hiking.app.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class NacosLogLevelRefresher {

    private static final String LOGGING_LEVEL_PREFIX = "logging.level.";

    private final LoggingSystem loggingSystem;
    private final Environment environment;

    public NacosLogLevelRefresher(LoggingSystem loggingSystem, Environment environment) {
        this.loggingSystem = loggingSystem;
        this.environment = environment;
    }

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        Map<String, String> levels = extractLogLevels();
        if (levels.isEmpty()) {
            log.debug("未找到 logging.level.* 配置，跳过日志级别刷新");
            return;
        }
        for (Map.Entry<String, String> entry : levels.entrySet()) {
            String loggerName = entry.getKey();
            String level = entry.getValue();
            try {
                loggingSystem.setLogLevel(loggerName, LogLevel.valueOf(level.toUpperCase()));
                log.info("动态调整日志级别: logger={}, level={}", loggerName, level);
            } catch (Exception e) {
                log.error("动态调整日志级别失败: logger={}, level={}", loggerName, level, e);
            }
        }
    }

    private Map<String, String> extractLogLevels() {
        Map<String, String> levels = new HashMap<>();
        if (environment instanceof AbstractEnvironment abstractEnv) {
            for (PropertySource<?> ps : abstractEnv.getPropertySources()) {
                if (ps instanceof EnumerablePropertySource<?> enumerablePs) {
                    for (String name : enumerablePs.getPropertyNames()) {
                        if (name.startsWith(LOGGING_LEVEL_PREFIX)) {
                            String loggerName = name.substring(LOGGING_LEVEL_PREFIX.length());
                            String value = environment.getProperty(name);
                            if (value != null) {
                                levels.put(loggerName, value);
                            }
                        }
                    }
                }
            }
        }
        return levels;
    }
}