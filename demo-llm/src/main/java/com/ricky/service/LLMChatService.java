package com.ricky.service;

import com.ricky.domain.LLMChatRequest;
import com.ricky.domain.LLMChatResponse;
import com.ricky.domain.LLMStreamChunk;
import reactor.core.publisher.Flux;

/**
 * 大语言模型对话服务
 */
public interface LLMChatService {

    /**
     * 同步对话
     */
    LLMChatResponse chat(LLMChatRequest request);

    /**
     * 流式对话
     */
    Flux<LLMStreamChunk> streamChat(LLMChatRequest request);

}
