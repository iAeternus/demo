package com.ricky.message.rabbit;

import com.ricky.message.config.MessagingProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "rabbitmq")
public class RabbitConfig {

    private final MessagingProperties properties;

    public RabbitConfig(MessagingProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DirectExchange rickyExchange() {
        return new DirectExchange(properties.getRabbitmq().getExchange());
    }

    @Bean
    public List<Queue> rickyQueues() {
        return properties.getKafka().getTopics().stream()
                .map(topic -> QueueBuilder.durable(properties.getRabbitmq().getQueuePrefix() + topic)
                        .withArgument("x-dead-letter-exchange", "")
                        .withArgument("x-dead-letter-routing-key", properties.getRabbitmq().getQueuePrefix() + topic + ".DLT")
                        .build())
                .collect(Collectors.toList());
    }

    @Bean
    public List<Binding> rickyBindings(List<Queue> rickyQueues, DirectExchange rickyExchange) {
        return properties.getKafka().getTopics().stream()
                .map(topic -> {
                    int index = properties.getKafka().getTopics().indexOf(topic);
                    return BindingBuilder.bind(rickyQueues.get(index))
                            .to(rickyExchange)
                            .with(properties.getRabbitmq().getRoutingKeyPrefix() + topic);
                })
                .collect(Collectors.toList());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(properties.getRabbitmq().getExchange());
        return template;
    }

    @Bean
    public List<String> rickyRabbitQueueList() {
        return properties.getKafka().getTopics().stream()
                .map(topic -> properties.getRabbitmq().getQueuePrefix() + topic)
                .collect(Collectors.toList());
    }
}