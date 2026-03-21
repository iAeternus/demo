package com.ricky.message;

import lombok.Getter;

@Getter
public abstract class SendResult {

    protected boolean success;
    protected String topic;
    protected String messageId;
    protected Throwable error;

    public boolean shouldRetry() {
        return !success;
    }

    public abstract Object getMetadata();

}