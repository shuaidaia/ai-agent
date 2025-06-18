package com.belly.aiagent.demo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.belly.aiagent.advisor.MyLoggerAdvisor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

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
