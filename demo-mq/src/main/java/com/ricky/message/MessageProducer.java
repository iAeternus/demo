package com.ricky.message;

import java.util.concurrent.CompletableFuture;

public interface MessageProducer {

    <T> SendResult send(Message<T> message);

    <T> CompletableFuture<SendResult> sendAsync(Message<T> message);

    <T> SendResult sendInTransaction(Message<T> message);
}