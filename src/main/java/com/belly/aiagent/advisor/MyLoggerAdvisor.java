package com.belly.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义日志 Advisor
 * 打印 info 级别日志、只输出单次用户提示词和 AI 回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI Request: {}", request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        ChatResponse response = advisedResponse.response();
        if (null == response) {
            log.info("AI Response is null");
            return;
        }
        ChatResponseMetadata responseMetadata = response.getMetadata();
        // 输出使用的tokens数
        Usage usage = responseMetadata.getUsage();
        log.info("已使用的总tokens数：{}", usage.getTotalTokens());
        log.info("输入的tokens数：{}", usage.getPromptTokens());
        log.info("输出的tokens数：{}", usage.getCompletionTokens());
        AssistantMessage assistantMessage = response.getResult().getOutput();
        List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
        // 输出提示信息
        String result = assistantMessage.getText();
        log.info("问题：{}", result);
        log.info("选择了 [{}] 个工具来使用", toolCallList.size());
        String toolCallInfo = toolCallList.stream()
                .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                .collect(Collectors.joining("\n"));
        log.info(toolCallInfo);
        log.info("AI Response: {}", assistantMessage.getText());
    }

    @NotNull
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }


    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }
}

