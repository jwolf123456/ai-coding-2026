# line-center 线路中心模块

徒步App的线路管理服务，提供徒步线路的增删改查能力。

## 基本信息

| 项目 | 值 |
|------|-----|
| 端口 | 8082 |
| 数据库 | `hiking_line` |
| 启动类 | `com.hiking.app.line.LineCenterApplication` |

## 依赖中间件

| 中间件 | 地址 | 用途 |
|--------|------|------|
| MySQL | localhost:3306 | 线路数据持久化 |
| Redis | localhost:6379 | 缓存（通过 common 模块提供） |

## 数据库表

### t_line 线路表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(100) | 线路标题 |
| description | TEXT | 线路描述 |
| distance | DECIMAL(10,2) | 距离（公里） |
| difficulty | INT | 难度：1-简单，2-中等，3-困难，4-极难 |
| start_point | VARCHAR(255) | 起点 |
| end_point | VARCHAR(255) | 终点 |
| cover_image | VARCHAR(255) | 封面图URL |
| status | INT | 状态：1-上线，0-下线 |
| create_time | DATETIME | 创建时间（自动填充） |
| update_time | DATETIME | 更新时间（自动填充） |
| deleted | INT | 逻辑删除：0-未删除，1-已删除 |

## API 接口

### 创建线路

```
POST /api/line
Content-Type: application/json

{
  "title": "西湖环湖线",
  "description": "环绕西湖的经典徒步线路",
  "distance": 15.5,
  "difficulty": 2,
  "startPoint": "断桥",
  "endPoint": "断桥",
  "coverImage": "https://example.com/xihu.jpg"
}
```

### 删除线路

```
DELETE /api/line/{id}
```

### 更新线路

```
PUT /api/line
Content-Type: application/json

{
  "id": 1,
  "title": "西湖环湖线（升级版）",
  "difficulty": 3
}
```

### 查询单个线路

```
GET /api/line/{id}
```

### 分页查询线路

```
GET /api/line/page?current=1&size=10
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [...],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

## 项目结构

```
line-center/
└── src/main/java/com/hiking/app/line/
    ├── LineCenterApplication.java    # 启动类
    ├── entity/Line.java              # 线路实体
    ├── mapper/LineMapper.java        # MyBatis-Plus Mapper
    ├── service/LineService.java      # 服务接口
    ├── service/impl/LineServiceImpl.java  # 服务实现
    └── controller/LineController.java    # REST 控制器
```

## 启动方式

1. 确保 MySQL 已启动且 `hiking_line` 库已初始化（执行 `sql/init.sql`）
2. 确保 Redis 已启动
3. 运行 `LineCenterApplication.main()`
4. 访问 `http://localhost:8082/api/line/page` 验证服务