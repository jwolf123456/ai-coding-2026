package com.hiking.app.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    /**
     * 按类型获取 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        checkContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 按名称获取 Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 按名称和类型获取 Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 判断容器中是否存在指定类型的 Bean
     */
    public static boolean containsBean(Class<?> clazz) {
        checkContext();
        return applicationContext.containsBeanDefinition(
                getBeanName(clazz)
        );
    }

    /**
     * 判断容器中是否存在指定名称的 Bean
     */
    public static boolean containsBean(String name) {
        checkContext();
        return applicationContext.containsBean(name);
    }

    /**
     * 获取所有指定类型的 Bean
     */
    public static <T> java.util.Map<String, T> getBeansOfType(Class<T> clazz) {
        checkContext();
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 获取应用名称
     */
    public static String getApplicationName() {
        checkContext();
        return applicationContext.getApplicationName();
    }

    /**
     * 获取环境对象
     */
    public static org.springframework.core.env.Environment getEnvironment() {
        checkContext();
        return applicationContext.getEnvironment();
    }

    private static void checkContext() {
        Objects.requireNonNull(applicationContext,
                "ApplicationContext 尚未初始化，请确认 SpringContextUtil 已被 Spring 扫描并注册为 Bean");
    }

    /**
     * 根据类名生成默认 Bean 名称
     */
    private static String getBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private SpringContextUtil() {
    }
}