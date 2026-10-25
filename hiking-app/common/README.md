# common 通用模块

徒步App的公共基础模块，提供各业务模块复用的通用能力。

## 模块定位

本模块不独立启动，作为 `user-center`、`line-center`、`message-center` 的公共依赖。

## 提供的能力

| 包路径 | 类名 | 说明 |
|--------|------|------|
| `common.result` | `R<T>` | 统一响应包装，支持 `R.ok()` / `R.fail()` / `R.fail(code, msg)` |
| `common.result` | `ResultCode` | 响应状态码枚举（200/400/401/403/404/500） |
| `common.exception` | `BusinessException` | 业务异常，携带 code + msg |
| `common.exception` | `GlobalExceptionHandler` | 全局异常拦截，自动将异常转为 `R` 响应 |
| `common.entity` | `BaseEntity` | 基础实体，含 `id`、`createTime`、`updateTime`、`deleted` 字段及自动填充 |
| `common.config` | `MybatisPlusConfig` | MyBatis-Plus 分页插件 + 字段自动填充（createTime/updateTime/deleted） |
| `common.config` | `RedisConfig` | RedisTemplate 序列化配置（Key 用 String，Value 用 Jackson JSON） |
| `common.utils` | `RedisUtils` | Redis 工具类，封装 set/get/delete/hasKey/expire |

## 依赖

| 依赖 | 用途 |
|------|------|
| `spring-boot-starter-web` | Web 基础能力 |
| `spring-boot-starter-data-redis` | Redis 操作 |
| `spring-kafka` | Kafka 消息队列 |
| `mybatis-plus-spring-boot3-starter` | MyBatis-Plus ORM |
| `mybatis-plus-jsqlparser` | 分页插件 SQL 解析 |
| `mysql-connector-j` | MySQL 驱动 |
| `lombok` | 简化 POJO |
| `jackson-databind` | JSON 序列化 |

## 统一响应格式

所有接口返回统一的 JSON 结构：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

- 成功：`R.ok(data)` → `{"code":200, "msg":"操作成功", "data": ...}`
- 失败：`R.fail("用户不存在")` → `{"code":500, "msg":"用户不存在", "data":null}`
- 业务异常：抛出 `BusinessException` 后由 `GlobalExceptionHandler` 自动捕获并转为上述格式