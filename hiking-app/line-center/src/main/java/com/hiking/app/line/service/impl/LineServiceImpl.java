package com.hiking.app.line.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hiking.app.common.result.R;
import com.hiking.app.line.entity.Line;
import com.hiking.app.common.feign.MessageFeignClient;
import com.hiking.app.line.mapper.LineMapper;
import com.hiking.app.line.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineServiceImpl extends ServiceImpl<LineMapper, Line> implements LineService {

    private final MessageFeignClient messageFeignClient;

    @Override
    public boolean save(Line entity) {
        boolean result = super.save(entity);
        if (result) {
            try {
                R<Void> r = messageFeignClient.send("line-created", "线路创建成功: " + entity.getTitle());
                log.info("通知消息中心结果: code={}, msg={}", r.getCode(), r.getMsg());
            } catch (Exception e) {
                log.warn("通知消息中心失败: {}", e.getMessage());
            }
        }
        return result;
    }
}