package com.hiking.app.message.controller;

import com.hiking.app.common.result.R;
import com.hiking.app.message.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping("/send")
    public R<Void> send(@RequestParam String topic, @RequestParam String message) {
        messageProducer.send(topic, message);
        return R.ok();
    }
}