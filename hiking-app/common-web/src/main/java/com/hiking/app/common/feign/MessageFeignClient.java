package com.hiking.app.common.feign;

import com.hiking.app.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "message-center", path = "/api/message")
public interface MessageFeignClient {

    @PostMapping("/send")
    R<Void> send(@RequestParam("topic") String topic, @RequestParam("message") String message);
}