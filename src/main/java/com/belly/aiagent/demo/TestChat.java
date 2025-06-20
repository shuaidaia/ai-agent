package com.belly.aiagent.demo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.belly.aiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试AI模型接口
 */
@Component
@Slf4j
public class TestChat implements CommandLineRunner {
    // ollama模型
    @Autowired(required = false)
    @Qualifier("ollamaChatModel")
    private ChatModel ollamaChatModel;

    // DashScope灵积
    @Autowired(required = false)
    private DashScopeChatModel chatModel;

    @Resource
    private ToolCallback[] allTools;

    private ChatClient chatClient;
    public String ollamaChat() {
        if (null == ollamaChatModel) {
            return "ollamaChatModel is null";
        }
        return ollamaChatModel.call(new Prompt("你好！Belly")).getResult().getOutput().getText();
    }

    public String deepSeekChat(String prompt) {
        if (null == chatModel) {
            return "dashScopeChatModel is null";
        }
        return chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
    }

    public Flux<String> streamChat(HttpServletResponse response, String prompt) {
        if (null == chatModel) {
            return Flux.just("dashScopeChatModel is null");
        }
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        Flux<ChatResponse> stream = chatModel.stream(new Prompt(prompt));
        return stream.map(resp -> resp.getResult().getOutput().getText());
    }

    public Flux<String> streamChatClient(HttpServletResponse response, String prompt) {
        if (null == chatModel && null == chatClient) {
            return Flux.just("dashScopeChatModel is null");
        }
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(new Prompt(prompt)).stream().content();
    }

    /**
     * 测试大模型调用自定义工具
     */
    public String streamChatClientByTools(HttpServletResponse response, String prompt) {
        if (null == chatModel && null == chatClient) {
            return "dashScopeChatModel is null";
        }
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        // 获取返回的结果response
        ChatResponse chatResponse = chatClient
                        .prompt(new Prompt(prompt))
                        .tools(allTools)
                        .call()
                        .chatResponse();
        assert chatResponse != null;
//        // 输出使用的tokens数
//        Usage usage = chatResponse.getMetadata().getUsage();
//        log.info("已使用的总tokens数：{}", usage.getTotalTokens());
//        log.info("输入的tokens数：{}", usage.getPromptTokens());
//        log.info("输出的tokens数：{}", usage.getCompletionTokens());
//        // 获取助手消息
//        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
//        // 获取要调用的工具列表
//        List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
//        // 输出提示信息
//        String result = assistantMessage.getText();
//        log.info("思考：{}", result);
//        log.info("选择了 [{}] 个工具来使用", toolCallList.size());
//        String toolCallInfo = toolCallList.stream()
//                .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
//                .collect(Collectors.joining("\n"));
//        log.info(toolCallInfo);
        return chatResponse.getResult().getOutput().getText();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("测试启动咯~~~~~");
        if (chatModel != null) {
            chatClient = ChatClient.builder(chatModel).defaultAdvisors(new MyLoggerAdvisor()).build();
            log.info("大模型 [{}] 已启动...", chatModel.getDefaultOptions().getModel());
        }
        if (ollamaChatModel != null) {
            log.info("大模型 [{}] 已启动...", ollamaChatModel.getDefaultOptions().getModel());
        }
    }
}
