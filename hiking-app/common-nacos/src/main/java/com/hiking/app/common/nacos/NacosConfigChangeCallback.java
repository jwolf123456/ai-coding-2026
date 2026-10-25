package com.hiking.app.common.nacos;

@FunctionalInterface
public interface NacosConfigChangeCallback {

    void onChange(String dataId, String configInfo);
}