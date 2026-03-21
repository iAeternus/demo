package com.ricky.message.kafka;

import com.ricky.message.SendResult;
import lombok.Getter;

import java.util.Map;

@Getter
public class KafkaSendResult extends SendResult {

    private final Integer partition;
    private final Long offset;

    public KafkaSendResult(String topic, Integer partition, Long offset) {
        this.success = true;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }

    @Override
    public Object getMetadata() {
        return Map.of("partition", partition, "offset", offset);
    }
}