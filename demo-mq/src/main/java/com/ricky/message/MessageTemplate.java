package com.ricky.message;

import java.util.concurrent.CompletableFuture;

public interface MessageTemplate {

    <T> void send(String topic, T payload);

    <T> CompletableFuture<SendResult> sendAsync(String topic, T payload);

}
