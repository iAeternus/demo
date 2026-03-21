package com.ricky.message;

public class FailedSendResult extends SendResult {

    public FailedSendResult(String topic, Throwable error) {
        this.success = false;
        this.topic = topic;
        this.error = error;
    }

    @Override
    public Object getMetadata() {
        return null;
    }
}