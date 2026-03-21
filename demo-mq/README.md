# Demo-MQ 消息中间件抽象框架

## 一、框架概述

Demo-MQ 是一个轻量级的消息中间件抽象框架，通过注解驱动的方式实现消息的发布与消费，让业务代码与底层消息中间件解耦。

### 核心特性

- **注解驱动**：使用 `@EventListener` 注解自动注册消费者
- **统一 API**：通过 `MessageTemplate` 统一发送消息
- **多中间件支持**：通过配置动态切换 Kafka、RabbitMQ
- **高可靠**：支持重试、死信队列、事务消息

---

## 二、快速开始

### 2.1 添加依赖

```xml
<dependency>
    <groupId>com.ricky</groupId>
    <artifactId>demo-mq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2.2 定义事件

```java
@Data
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String productName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
```

### 2.3 发布消息

```java
@Autowired
private MessageTemplate messageTemplate;

// 发布消息
messageTemplate.send("order-created", orderEvent);

// 异步发布
messageTemplate.sendAsync("order-created", orderEvent);
```

### 2.4 消费消息

```java
@Component
@EventListener(topic = "order-created")
public class OrderCreatedListener implements MessageConsumer<OrderCreatedEvent> {

    @Override
    public void onMessage(Message<OrderCreatedEvent> message) throws Exception {
        OrderCreatedEvent event = message.getPayload();
        log.info("Received: orderId={}, amount={}", event.getOrderId(), event.getAmount());
    }
}
```

---

## 三、配置说明

### 3.1 application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: ricky-consumer-group
      auto-offset-reset: earliest

ricky:
  messaging:
    broker: kafka  # 可选: kafka, rabbitmq
    kafka:
      topics:
        - order-created
        - order-paid
        - order-shipped
      consumer-group: ricky-consumer-group
      concurrency: 3
```

### 3.2 配置属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ricky.messaging.broker` | String | `kafka` | 消息中间件类型 |
| `ricky.messaging.kafka.topics` | List | `[]` | Kafka Topic 列表 |
| `ricky.messaging.kafka.consumer-group` | String | `ricky-consumer-group` | 消费者组 |
| `ricky.messaging.kafka.concurrency` | int | `3` | 并发消费者数 |

---

## 四、核心组件

### 4.1 MessageTemplate

对外暴露的唯一消息发送接口：

```java
public interface MessageTemplate {
    // 同步发送
    <T> void send(String topic, T payload);
    
    // 异步发送
    <T> CompletableFuture<SendResult> sendAsync(String topic, T payload);
    
    // 事务内发送（消息在事务提交后发送）
    <T> void sendInTransaction(String topic, T payload, Runnable transactionCallback);
}
```

### 4.2 @EventListener 注解

用于标注消息消费者类：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    String topic();  // 监听的Topic
    String group() default "";  // 消费者组（可选）
}
```

### 4.3 MessageConsumer 接口

消费者需要实现的接口：

```java
public interface MessageConsumer<T> {
    void onMessage(Message<T> message) throws Exception;
}
```

### 4.4 Message 模型

```java
public class Message<T> {
    private String topic;    // Topic
    private String key;      // 消息键
    private T payload;       // 消息载荷
    private Map<String, String> headers;  // 元数据
}
```

---

## 五、内部架构

### 5.1 组件交互

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│                                                             │
│  ┌─────────────────────┐     ┌───────────────────────────┐  │
│  │  @EventListener     │     │   MessageTemplate       │  │
│  │  (消费者)           │     │   (消息发送)             │  │
│  └──────────┬──────────┘     └───────────┬───────────┘  │
│             │                               │              │
│             │    ┌──────────────────────────┘              │
│             │    │                                        │
│             ▼    ▼                                        │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           EventListenerScanner                     │   │
│  │       (注解扫描 + 自动注册)                         │   │
│  └──────────────────────┬──────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────────┐
│              MessageListenerRegistry                         │
│                 (消费者注册表)                               │
└─────────────────────────┬───────────────────────────────────┘
                          │
         ┌────────────────┼────────────────┐
         ▼                ▼                ▼
   ┌──────────┐     ┌──────────┐     ┌──────────┐
   │  Kafka   │     │  Rabbit  │     │   ...    │
   │ Adapter  │     │ Adapter  │     │ Adapter  │
   └──────────┘     └──────────┘     └──────────┘
```

### 5.2 启动流程

1. **Spring 容器初始化**：`EventListenerBootstrap` 监听 `ContextRefreshedEvent`
2. **扫描注解**：`EventListenerScanner` 扫描带有 `@EventListener` 的类
3. **自动注册**：将消费者注册到 `MessageListenerRegistry`
4. **消息消费**：`KafkaListenerAdapter` 消费消息并分发给对应的消费者

---

## 六、高级特性

### 6.1 事务消息

```java
// Kafka 事务发送
messageProducer.sendInTransaction(message);
```

### 6.2 重试与死信

框架配置了默认的重试策略和死信队列：

```java
// 重试：3次，每次间隔1秒
FixedBackOff backOff = new FixedBackOff(1000L, 3);

// 死信队列：发送到 DLT Topic
DeadLetterPublishingRecoverer recoverer = ...
```

### 6.3 异步发送

```java
CompletableFuture<SendResult> future = messageTemplate.sendAsync("topic", event);
future.thenAccept(result -> {
    if (result.isSuccess()) {
        log.info("Message sent: {}", result.getMessageId());
    }
});
```

### 6.3 事务内发送

确保数据库操作与消息发送的原子性，消息仅在事务提交成功后发送：

```java
@Autowired
private MessageTemplate messageTemplate;

// 事务内发送消息
messageTemplate.sendInTransaction("order-created", orderEvent, () -> {
    // 业务逻辑：创建订单（与消息发送在同一事务中）
    orderService.create(order);
});
```

**特点**：
- 事务成功后消息才发送
- 事务回滚时消息不会发送
- 避免数据不一致问题

---

## 七、Docker 部署

### 启动 Kafka

```bash
cd demo/docker
sudo docker-compose up -d
```

### 验证服务

- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:8081`

---

## 八、测试

### 运行单元测试

```bash
cd demo-mq
mvn test
```

### 运行集成测试

```bash
# 确保 Kafka 已启动
mvn test -Dtest=OrderEventIntegrationTest
```

---

## 九、使用示例

完整的订单事件示例：

```java
// 1. 定义事件
public class OrderCreatedEvent { ... }
public class OrderPaidEvent { ... }
public class OrderShippedEvent { void }

// 2. 消费者
@Component
@EventListener(topic = "order-created")
public class OrderCreatedListener implements MessageConsumer<OrderCreatedEvent> {
    public void onMessage(Message<OrderCreatedEvent> m) { ... }
}

@Component
@EventListener(topic = "order-paid")
public class OrderPaidListener implements MessageConsumer<OrderPaidEvent> {
    public void onMessage(Message<OrderPaidEvent> m) { ... }
}

// 3. 发布者
@RestController
public class OrderController {
    @Autowired
    private MessageTemplate messageTemplate;
    
    @PostMapping("/order")
    public void createOrder(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(order.getId())
            .amount(order.getAmount())
            .build();
        messageTemplate.send("order-created", event);
    }
}
```

---

## 十、注意事项

1. **消费者类必须实现 `MessageConsumer<T>` 接口**
2. **每个消费者类需要标注 `@EventListener` 注解并指定 topic**
3. **消息载荷类型通过泛型确定**
4. **生产环境建议 `acks=all` 保证高可靠**