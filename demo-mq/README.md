# Demo-MQ 消息中间件抽象框架

## 一、框架概述

Demo-MQ 是一个轻量级的消息中间件抽象框架，旨在让应用程序在编写业务代码时只关注消息的数据结构定义、消息的发送和消费，而无需关心底层具体使用哪种消息中间件。

该框架的核心设计理念是**解耦业务逻辑与消息中间件实现**，通过统一的消息抽象层，实现：

- **高并发**：支持同步/异步发送，事务消息
- **高可靠**：消息确认机制、重试策略、死信队列
- **多适配**：通过配置动态切换 Kafka、RabbitMQ 等主流消息中间件

---

## 二、架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        Application Layer                        │
│                                                                  │
│   ┌─────────────────┐          ┌─────────────────────────────┐ │
│   │  Business Code  │          │  MessageListenerRegistry    │ │
│   │                 │          │  (Consumer Registration)    │ │
│   └────────┬────────┘          └──────────────┬──────────────┘ │
│            │                                │                  │
│   ┌────────┴───────────────────────────────┴────────────────┐  │
│   │              MessagingTemplateImpl                        │  │
│   │         (Unified Message Sending API)                     │  │
│   └────────────────────────┬────────────────────────────────┘  │
└─────────────────────────────┼──────────────────────────────────┘
                              │
┌─────────────────────────────┼──────────────────────────────────┐
│                    Message Broker Abstraction Layer             │
│                              │                                  │
│   ┌─────────────────────────┴─────────────────────────────┐    │
│   │              MessageProducer (Interface)                │    │
│   │   - send()                                             │    │
│   │   - sendAsync()                                        │    │
│   │   - sendInTransaction()                                │    │
│   └────────────┬───────────────────────────────┬────────────┘    │
│                │                               │                  │
│   ┌────────────┴────────────┐    ┌────────────┴──────────────┐  │
│   │  KafkaMessageProducer   │    │  RabbitMessageProducer    │  │
│   │      (Kafka Impl)       │    │     (RabbitMQ Impl)       │  │
│   └─────────────────────────┘    └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────┼──────────────────────────────────┐
│                 Actual Message Middleware                       │
│                         │                                        │
│   ┌─────────────────────┐ │    ┌─────────────────────────────┐  │
│   │        Kafka        │ │    │         RabbitMQ            │  │
│   │  (KafkaTemplate)    │ │    │     (RabbitTemplate)        │  │
│   └─────────────────────┘ │    └─────────────────────────────┘  │
└─────────────────────────────┴─────────────────────────────────────┘
```

### 2.2 核心组件

#### 2.2.1 消息模型 (Message)

```java
public class Message<T> {
    private String topic;           // 主题/队列名
    private String key;             // 消息键 (用于分区路由)
    private T payload;              // 消息载荷
    private Map<String, String> headers; // 元数据/Headers
}
```

**设计说明**：
- 使用泛型 `T` 支持任意类型的消息载荷
- `topic` 统一抽象：在 Kafka 中对应 Topic，在 RabbitMQ 中对应 Queue
- `key` 用于消息分区路由和消息追溯
- `headers` 用于传递元数据（如 traceId、时间戳等）

#### 2.2.2 消息发送模板 (MessageTemplate)

```java
public interface MessageTemplate {
    // 同步发送
    <T> void send(String topic, T payload);
    
    // 异步发送
    <T> CompletableFuture<SendResult> sendAsync(String topic, T payload);
}
```

**实现类**：`MessagingTemplateImpl`，内部依赖 `MessageProducer`

#### 2.2.3 消息生产者 (MessageProducer)

```java
public interface MessageProducer {
    // 同步发送
    <T> SendResult send(Message<T> message);
    
    // 异步发送
    <T> CompletableFuture<SendResult> sendAsync(Message<T> message);
    
    // 事务消息
    <T> SendResult sendInTransaction(Message<T> message);
}
```

**实现**：
- `KafkaMessageProducer`：基于 Spring Kafka `KafkaTemplate`
- `RabbitMessageProducer`：基于 Spring AMQP `RabbitTemplate`

#### 2.2.4 消息消费者 (MessageConsumer)

```java
public interface MessageConsumer<T> {
    void onMessage(Message<T> message) throws Exception;
}
```

**特点**：
- 泛型接口，支持类型安全
- 业务实现只需关注消息处理逻辑，无需关心底层消费机制

#### 2.2.5 消费者注册表 (MessageListenerRegistry)

```java
public interface MessageListenerRegistry {
    <T> void register(String topic, Class<T> type, MessageConsumer<T> consumer);
    ListenerWrapper<?> get(String topic);
}
```

**实现**：`DefaultMessageListenerRegistry`，使用 `ConcurrentHashMap` 存储消费者

#### 2.2.6 消息监听适配器

- **KafkaListenerAdapter**：基于 `@KafkaListener` 注解
- **RabbitListenerAdapter**：基于 `@RabbitListener` 注解

---

## 三、配置说明

### 3.1 消息中间件选择

通过 `application.yml` 中的 `ricky.messaging.broker` 配置选择消息中间件：

```yaml
ricky:
  messaging:
    broker: kafka  # 可选值: kafka, rabbitmq (默认: kafka)
```

### 3.2 完整配置示例

```yaml
spring:
  application:
    name: demo-mq

  # Kafka 配置
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all
      retries: 2147483647
      batch-size: 65536
      buffer-memory: 67108864
      compression-type: zstd
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        delivery.timeout.ms: 120000
        request.timeout.ms: 30000
    consumer:
      group-id: ricky-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

ricky:
  messaging:
    broker: kafka  # 消息中间件类型
    kafka:
      topics:
        - test-topic
        - order-topic
      consumer-group: ricky-consumer-group
      concurrency: 3
    rabbitmq:
      exchange: ricky.exchange
      queue-prefix: ricky.queue.
      routing-key-prefix: ricky.key.

server:
  port: 8080

logging:
  level:
    com.ricky.message: DEBUG
    org.springframework.kafka: INFO
```

### 3.3 配置属性说明

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ricky.messaging.broker` | String | `kafka` | 消息中间件类型 |
| `ricky.messaging.kafka.topics` | List | `[]` | Kafka Topic 列表 |
| `ricky.messaging.kafka.consumer-group` | String | `ricky-consumer-group` | 消费者组ID |
| `ricky.messaging.kafka.concurrency` | int | `3` | 并发消费者数 |
| `ricky.messaging.rabbitmq.exchange` | String | `ricky.exchange` | 交换机名称 |
| `ricky.messaging.rabbitmq.queue-prefix` | String | `ricky.queue.` | 队列前缀 |
| `ricky.messaging.rabbitmq.routing-key-prefix` | String | `ricky.key.` | 路由键前缀 |

---

## 四、使用指南

### 4.1 消息发送

#### 4.1.1 同步发送

```java
@RestController
@RequiredConstructor
public class OrderController {

    private final MessageTemplate messageTemplate;

    @PostMapping("/order")
    public void createOrder(@RequestBody Order order) {
        messageTemplate.send("order-topic", order);
    }
}
```

#### 4.1.2 异步发送

```java
@PostMapping("/order/async")
public CompletableFuture<Result> createOrderAsync(@RequestBody Order order) {
    return messageTemplate.sendAsync("order-topic", order)
            .thenApply(result -> {
                if (result.isSuccess()) {
                    return Result.success(result.getMessageId());
                }
                return Result.error("Message send failed: " + result.getError());
            });
}
```

#### 4.1.3 发送结果处理

```java
// SendResult 包含以下信息：
// - success: 是否发送成功
// - topic: 目标主题
// - messageId: 消息ID (Kafka: offset, RabbitMQ: null)
// - error: 错误信息
// - metadata: 中间件特定的元数据
```

### 4.2 消息消费

#### 4.2.1 注册消费者

```java
@Component
@RequiredConstructor
public class OrderMessageConsumer {

    private final MessageListenerRegistry registry;

    @PostConstruct
    public void init() {
        registry.register("order-topic", OrderMessage.class, this::handleOrder);
    }

    private void handleOrder(Message<OrderMessage> message) {
        OrderMessage payload = message.getPayload();
        log.info("Received order: {}", payload);
        // 业务处理逻辑
    }
}
```

#### 4.2.2 消费者代码

```java
public class OrderMessageConsumer implements MessageConsumer<OrderMessage> {
    
    @Override
    public void onMessage(Message<OrderMessage> message) throws Exception {
        OrderMessage payload = message.getPayload();
        log.info("Processing order: {}", payload.getOrderId());
        
        // 业务处理
        // 如果处理失败抛出异常，将触发重试机制
    }
}
```

---

## 五、高并发与高可靠特性

### 5.1 高并发支持

#### 5.1.1 异步发送

```java
// 异步非阻塞发送
CompletableFuture<SendResult> future = messageTemplate.sendAsync("topic", payload);
future.thenAccept(result -> {
    if (result.isSuccess()) {
        log.info("Message sent successfully: {}", result.getMessageId());
    }
});
```

#### 5.1.2 批量配置

```yaml
spring:
  kafka:
    producer:
      batch-size: 65536        # 批量大小
      buffer-memory: 67108864  # 缓冲区大小
      compression-type: zstd   # 压缩类型
```

#### 5.1.3 并发消费

```yaml
ricky:
  messaging:
    kafka:
      concurrency: 3  # 每个分区一个消费者，3个线程并行消费
```

### 5.2 高可靠保证

#### 5.2.1 生产者可靠性配置

```yaml
spring:
  kafka:
    producer:
      acks: all              # 所有ISR副本确认
      retries: 2147483647    # 重试次数
      delivery-timeout: 120000  # 发送超时
```

**ACKS 配置说明**：
- `acks=0`：不等待确认，最高吞吐，可靠性最低
- `acks=1`：Leader副本确认，平衡性能与可靠性
- `acks=all`：所有ISR副本确认，最高可靠性

#### 5.2.2 消费者可靠性配置

```yaml
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest  # 从最早位置消费，避免消息丢失
      enable-auto-commit: false     # 手动提交，确保消息处理完成后提交
```

#### 5.2.3 重试与死信机制

**Kafka 配置** (`com.ricky.kafka.KafkaConfig`)：

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    // 死信队列策略
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate, 
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
    );
    
    // 重试策略：3次，每次1秒
    FixedBackOff backOff = new FixedBackOff(1000L, 3);
    
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
    
    // 某些异常不重试
    handler.addNotRetryableExceptions(IllegalArgumentException.class);
    
    return handler;
}
```

**处理流程**：
1. 消息消费失败 → 重试3次，每次间隔1秒
2. 重试耗尽 → 发送至 DLT (Dead Letter Topic)
3. DLT 消息可后续人工处理或告警

#### 5.2.4 事务消息

```java
// Kafka 事务发送
SendResult result = messageProducer.sendInTransaction(message);
```

**Kafka 事务配置**：

```java
@Bean
public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());
    DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(config);
    
    // 事务ID前缀，必须唯一
    factory.setTransactionIdPrefix("tx-ricky-");
    
    return factory;
}
```

---

## 六、多消息中间件适配

### 6.1 适配器模式

框架采用 **适配器模式** 实现多消息中间件支持：

```
MessageProducer (Interface)
    │
    ├── KafkaMessageProducer   (implements MessageProducer)
    └── RabbitMessageProducer (implements MessageProducer)
```

### 6.2 动态切换机制

通过 Spring `@ConditionalOnProperty` 注解实现运行时动态选择：

```java
@Configuration
public class MessagingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", 
                          havingValue = "kafka", matchIfMissing = true)
    public MessageProducer kafkaMessageProducer(KafkaMessageProducer kafkaMessageProducer) {
        return kafkaMessageProducer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", 
                          havingValue = "rabbitmq")
    public MessageProducer rabbitMessageProducer(RabbitMessageProducer rabbitMessageProducer) {
        return rabbitMessageProducer;
    }
}
```

**切换效果**：
- `ricky.messaging.broker=kafka` → 使用 Kafka 生产者
- `ricky.messaging.broker=rabbitmq` → 使用 RabbitMQ 生产者

### 6.3 RabbitMQ 适配实现

#### 6.3.1 RabbitMQ 配置

```java
@Configuration
@ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "rabbitmq")
public class RabbitConfig {

    @Bean
    public DirectExchange rickyExchange() {
        return new DirectExchange(properties.getRabbitmq().getExchange());
    }

    @Bean
    public List<Queue> rickyQueues() {
        // 为每个Topic创建队列，并配置死信队列
        return properties.getKafka().getTopics().stream()
                .map(topic -> QueueBuilder.durable(queueName)
                        .withArgument("x-dead-letter-exchange", "")
                        .withArgument("x-dead-letter-routing-key", queueName + ".DLT")
                        .build())
                .collect(Collectors.toList());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

#### 6.3.2 RabbitMQ 消息发送

```java
@Component
public class RabbitMessageProducer implements MessageProducer {

    @Override
    public <T> SendResult send(Message<T> message) {
        String exchange = properties.getRabbitmq().getExchange();
        String routingKey = properties.getRabbitmq().getRoutingKeyPrefix() + message.getTopic();
        
        rabbitTemplate.convertAndSend(exchange, routingKey, message.getPayload());
        
        return new RabbitSendResult(message.getTopic(), exchange, routingKey);
    }
}
```

---

## 七、消息中间件核心概念

### 7.1 Kafka 核心概念

| 概念 | 说明 |
|------|------|
| **Broker** | Kafka 服务实例，一个或多个 Broker 组成集群 |
| **Topic** | 消息分类逻辑单元，生产者向 Topic 发送消息，消费者从 Topic 消费 |
| **Partition** | Topic 的物理分区，每个 Partition 是一个有序队列 |
| **Replica** | 分区副本，包含 Leader 和 Follower |
| **ISR** | In-Sync Replicas，与 Leader 保持同步的副本集合 |
| **Offset** | 消息在分区中的唯一序号 |
| **Consumer Group** | 消费者组，同一组的消费者共同消费 Topic 各分区消息 |

**Kafka 消息发送流程**：
1. Producer 创建 `ProducerRecord`
2. 序列化 Key/Value
3. 确定 Partition（Key 哈希或轮询）
4. 发送至 Broker
5. Leader 副本写入后返回 ACK
6. Consumer 从 Offset 开始消费

### 7.2 RabbitMQ 核心概念

| 概念 | 说明 |
|------|------|
| **Exchange** | 交换机，接收生产者消息并路由到队列 |
| **Queue** | 队列，存储消息 |
| **Binding** | 绑定，Exchange 与 Queue 之间的关联关系 |
| **Routing Key** | 路由键，Exchange 根据路由键将消息投递到对应队列 |
| **Connection** | TCP 连接 |
| **Channel** | 通道，基于 Connection 的虚拟连接 |

**RabbitMQ 消息发送流程**：
1. Producer 连接 RabbitMQ
2. 创建 Channel
3. 发送消息到 Exchange（携带 Routing Key）
4. Exchange 根据绑定规则将消息路由到 Queue
5. Consumer 从 Queue 消费消息

### 7.3 对比分析

| 特性 | Kafka | RabbitMQ |
|------|-------|----------|
| **架构** | 分区+副本 | 交换机+队列+绑定 |
| **消息模型** | Pub/Sub | Pub/Sub + P2P |
| **消息顺序** | 分区内有序 | 队列内有序 |
| **消息持久化** | 顺序写入磁盘 | 可持久化 |
| **吞吐量** | 极高 | 高 |
| **延迟** | 毫秒级 | 微秒级 |
| **消费者模型** | Pull + Consumer Group | Push + Queue |
| **消息确认** | Offset 提交 | ACK 机制 |
| **死信队列** | DLT Topic | DLT Queue |
| **事务支持** | 事务日志 | 事务模式 |

---

## 八、Docker 部署

### 8.1 启动 Kafka 集群

```bash
cd demo-mq/src/main/docker
docker-compose up -d
```

### 8.2 验证服务

- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:8081`

### 8.3 docker-compose.yml 说明

```yaml
services:
  zookeeper:
    # Zookeeper 服务
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"

  kafka:
    # Kafka Broker
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1  # 单节点为1

  kafka-ui:
    # Kafka 管理界面
    image: provectuslabs/kafka-ui:latest
    ports:
      - "8081:8080"
    environment:
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
```

---

## 九、单元测试

### 9.1 测试覆盖

框架已包含以下单元测试：

- `MessageTest`：消息模型测试
- `SendResultTest`：发送结果测试
- `KafkaSendResultTest`：Kafka 发送结果测试
- `ListenerWrapperTest`：消费者包装器测试
- `DefaultMessageListenerRegistryTest`：注册表测试
- `MessagingPropertiesTest`：配置属性测试
- `MessageBrokerTypeTest`：中间件类型枚举测试
- `MessagingTemplateImplTest`：消息模板测试

### 9.2 运行测试

```bash
cd demo-mq
mvn test
```

---

## 十、扩展开发

### 10.1 添加新中间件支持

1. 在 `com.ricky.message.enums.MessageBrokerType` 添加枚举值
2. 实现 `MessageProducer` 接口
3. 实现对应的 ListenerAdapter
4. 在 `MessagingConfig` 添加 `@ConditionalOnProperty` 配置

### 10.2 扩展消息模型

可在 `Message` 类中添加业务相关字段：

```java
public class Message<T> {
    private String topic;
    private String key;
    private T payload;
    private Map<String, String> headers;
    
    // 扩展字段
    private long timestamp;
    private String traceId;
}
```

---

## 十一、注意事项

1. **Lombok 版本**：确保父 pom 中 Lombok 版本兼容
2. **Kafka 配置**：生产环境建议 `acks=all` 保证高可靠
3. **消费者线程数**：建议等于或小于分区数
4. **消息序列化**：框架默认使用 JSON 序列化
5. **异常处理**：消费失败会触发重试，耗尽后进入死信队列
6. **事务消息**：Kafka 事务需要配置唯一 Transaction ID 前缀

---

## 十二、总结

Demo-MQ 框架通过统一的消息抽象，让业务代码与底层消息中间件解耦，通过配置即可实现中间件的无缝切换。框架提供了完善的发送/消费 API、重试机制、死信队列等能力，能够满足高并发、高可靠的消息处理需求。