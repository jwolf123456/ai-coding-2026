
# 通过一个注解将普通 Spring Boot 项目的API暴露成 MCP 工具，自动注册到 Nacos MCP Registry
# 公司使用springboot ai或~~langchain4j~~自定义Agent项目可以通过接入nacos,来发现和调用这些MCP工具

下面给一套 **Spring AI Alibaba + Nacos 3.x MCP Registry** 的最小示例：先把一个普通 Spring Boot 服务暴露成 MCP 工具，启动后自动注册到 Nacos；再让 Agent 从 Nacos 发现它并调用。



### 1. MCP Server：把 HTTP 接口注册成 MCP 工具

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-mcp-registry</artifactId>
    <version>1.0.0.3</version>
</dependency>

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
@Service
public class WeatherService {

    @Tool(description = "根据城市名称查询天气")
    public String getWeather(@ToolParam(description = "城市名称，例如 成都") String cityName) {
        return cityName + " 天气：晴，25℃";
    }
}
```

```java
@SpringBootApplication
public class WeatherMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherMcpApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }
}
```

```yaml
spring:
  application:
    name: weather-mcp-server
  ai:
    mcp:
      server:
        name: weather-mcp-server
        version: 1.0.0
        type: SYNC
        instructions: "提供天气查询 MCP 工具"
      alibaba:
        mcp:
          nacos:
            server-addr: 127.0.0.1:8848
            namespace: public
            username: nacos
            password: nacos
            register:
              enabled: true
```

服务启动后，`getWeather` 会被注册成 MCP 工具，并自动出现在 Nacos MCP Registry 里。

### 2. Agent 侧：从 Nacos 发现 MCP 工具并调用

```yaml
spring:
  ai:
    mcp:
      client:
        nacos:
          enabled: true
          server-addr: 127.0.0.1:8848
          namespace: public
          username: nacos
          password: nacos
          service-name: weather-mcp-server
```

```java
@RestController
@RequestMapping("/agent")
public class AgentController {

    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder builder,
                           ToolCallbackProvider mcpTools) {
        this.chatClient = builder
                .defaultTools(mcpTools)
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }
}
```

当 Agent 收到类似 **“成都今天天气怎么样？”** 的请求时，它会从 Nacos 发现 `weather-mcp-server`，拿到 `getWeather` 工具描述，然后自动调用这个 MCP 工具。

### 3. 如果不用 Spring AI，也可以用 AgentScope Java 客户端

```java
NacosRegistry nacosRegistry = new NacosRegistry(
        "127.0.0.1:8848",
        "public",
        "nacos",
        "nacos"
);

McpClient mcpClient = McpClientBuilder.create("weather-mcp-server")
        .nacosRegistry(nacosRegistry)
        .streamableHttpTransport("http://weather-mcp-server/mcp")
        .buildAsync();
```

这种方式适合在 Agent 运行时里动态发现 MCP Server，而不是把地址写死。

### 4. 本地跑一下

1. 启动 Nacos 3.x。
2. 启动 `weather-mcp-server`。
3. 在 Nacos 控制台 MCP 管理里确认 `weather-mcp-server` 已注册。
4. 启动 Agent 应用，调用 `/agent/chat?question=成都今天天气怎么样`。
5. 观察 Agent 是否自动发现并调用 `getWeather`。

实际落地时，建议把 `server-addr`、`namespace`、`username`、`password` 抽到环境变量或配置中心里，不要直接写死。

---
需要我帮你把 Nacos 配置信息抽到环境变量，并给出对应的配置类写法吗？
