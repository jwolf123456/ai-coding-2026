# user-center 用户中心模块

徒步App的用户管理服务，提供用户的增删改查能力。

## 基本信息

| 项目 | 值 |
|------|-----|
| 端口 | 8081 |
| 数据库 | `hiking_user` |
| 启动类 | `com.hiking.app.user.UserCenterApplication` |

## 依赖中间件

| 中间件 | 地址 | 用途 |
|--------|------|------|
| MySQL | localhost:3306 | 用户数据持久化 |
| Redis | localhost:6379 | 缓存（通过 common 模块提供） |

## 数据库表

### t_user 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(100) | 密码 |
| nickname | VARCHAR(50) | 昵称 |
| avatar | VARCHAR(255) | 头像URL |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| status | INT | 状态：1-正常，0-禁用 |
| create_time | DATETIME | 创建时间（自动填充） |
| update_time | DATETIME | 更新时间（自动填充） |
| deleted | INT | 逻辑删除：0-未删除，1-已删除 |

## API 接口

### 创建用户

```
POST /api/user
Content-Type: application/json

{
  "username": "hiker01",
  "password": "123456",
  "nickname": "徒步达人",
  "phone": "13800138000"
}
```

### 删除用户

```
DELETE /api/user/{id}
```

### 更新用户

```
PUT /api/user
Content-Type: application/json

{
  "id": 1,
  "nickname": "新昵称",
  "avatar": "https://example.com/avatar.jpg"
}
```

### 查询单个用户

```
GET /api/user/{id}
```

### 分页查询用户

```
GET /api/user/page?current=1&size=10
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 项目结构

```
user-center/
└── src/main/java/com/hiking/app/user/
    ├── UserCenterApplication.java    # 启动类
    ├── entity/User.java              # 用户实体
    ├── mapper/UserMapper.java        # MyBatis-Plus Mapper
    ├── service/UserService.java      # 服务接口
    ├── service/impl/UserServiceImpl.java  # 服务实现
    └── controller/UserController.java    # REST 控制器
```

## 启动方式

1. 确保 MySQL 已启动且 `hiking_user` 库已初始化（执行 `sql/init.sql`）
2. 确保 Redis 已启动
3. 运行 `UserCenterApplication.main()`
4. 访问 `http://localhost:8081/api/user/page` 验证服务