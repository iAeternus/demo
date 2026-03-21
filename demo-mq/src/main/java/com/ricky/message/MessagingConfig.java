package com.ricky.message;

import com.ricky.message.config.MessagingProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MessagingProperties.class)
public class MessagingConfig {
}