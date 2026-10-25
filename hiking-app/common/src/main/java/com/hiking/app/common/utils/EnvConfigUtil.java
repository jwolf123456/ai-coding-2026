package com.hiking.app.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EnvConfigUtil implements ApplicationContextAware {

    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        environment = applicationContext.getEnvironment();
    }

    public static String getProperty(String key) {
        checkEnvironment();
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        checkEnvironment();
        return environment.getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        checkEnvironment();
        return environment.getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        checkEnvironment();
        return environment.getProperty(key, targetType, defaultValue);
    }

    public static String getRequiredProperty(String key) {
        checkEnvironment();
        return environment.getRequiredProperty(key);
    }

    public static boolean containsProperty(String key) {
        checkEnvironment();
        return environment.containsProperty(key);
    }

    public static String[] getActiveProfiles() {
        checkEnvironment();
        return environment.getActiveProfiles();
    }

    private static void checkEnvironment() {
        Objects.requireNonNull(environment,
                "Environment 尚未初始化，请确认 EnvConfigUtil 已被 Spring 扫描并注册为 Bean");
    }

    private EnvConfigUtil() {
    }
}