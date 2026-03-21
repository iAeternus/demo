package com.ricky.message;

public interface MessageConsumer<T> {

    void onMessage(Message<T> message) throws Exception;

}
