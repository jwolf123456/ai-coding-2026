package com.hiking.app.common.feign;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5, TimeUnit.SECONDS,
                5, TimeUnit.SECONDS,
                true
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, TimeUnit.MILLISECONDS.ordinal(), 2);
    }
}