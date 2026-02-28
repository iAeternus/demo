package com.ricky.llm.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LLMChatRequest {

    @NotBlank
    String userMessage;
    String systemPrompt;

}
