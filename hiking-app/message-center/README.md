# message-center 消息中心模块

徒步App的消息服务，基于 Kafka 实现异步消息的发送与消费。

## 基本信息

| 项目 | 值 |
|------|-----|
| 端口 | 8083 |
| 启动类 | `com.hiking.app.message.MessageCenterApplication` |

## 依赖中间件

| 中间件 | 地址 | 用途 |
|--------|------|------|
| Kafka | localhost:9092 | 消息队列（KRaft 模式） |
| Redis | localhost:6379 | 缓存（通过 common 模块提供） |

## Kafka 配置

| 配置项 | 值 |
|--------|-----|
| bootstrap-servers | localhost:9092 |
| producer.key-serializer | StringSerializer |
| producer.value-serializer | StringSerializer |
| consumer.group-id | hiking-group |
| consumer.key-deserializer | StringDeserializer |
| consumer.value-deserializer | StringDeserializer |
| consumer.auto-offset-reset | earliest |

## 默认消费 Topic

| Topic | 消费组 | 说明 |
|-------|--------|------|
| `hiking-notification` | hiking-group | 通知消息消费 |

## API 接口

### 发送消息

```
POST /api/message/send?topic=hiking-notification&message=Hello
```

成功响应：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

## 核心组件

| 类名 | 说明 |
|------|------|
| `MessageProducer` | Kafka 消息生产者，封装 `KafkaTemplate.send(topic, message)` |
| `MessageConsumer` | Kafka 消息消费者，监听 `hiking-notification` Topic |
| `MessageController` | REST 接口，对外提供消息发送能力 |

## 项目结构

```
message-center/
└── src/main/java/com/hiking/app/message/
    ├── MessageCenterApplication.java  # 启动类
    ├── producer/MessageProducer.java  # Kafka 生产者
    ├── consumer/MessageConsumer.java  # Kafka 消费者
    └── controller/MessageController.java  # REST 控制器
```

## 启动方式

1. 确保 Kafka 已启动（KRaft 模式，端口 9092）
2. 确保 Redis 已启动
3. 运行 `MessageCenterApplication.main()`
4. 调用 `POST http://localhost:8083/api/message/send?topic=hiking-notification&message=test` 验证服务

## 扩展说明

- 新增消费 Topic：在 `MessageConsumer` 中添加 `@KafkaListener(topics = "新topic", groupId = "hiking-group")` 方法
- 消息格式升级：当前使用纯字符串，如需 JSON 消息可修改序列化器为 `JsonSerializer` / `JsonDeserializer`