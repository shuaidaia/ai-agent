package com.belly.aiagent.controller;

import com.belly.aiagent.demo.TestChat;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Belly
 * @version 1.1.0
 * 日期 2025/6/15
 * 时间 18:32
 */
@RestController("/test")
@RequiredArgsConstructor
public class TestController {
    private final TestChat testChat;

    @RequestMapping("/ollama/chat")
    public String ollamaChat() {
        return testChat.ollamaChat();
    }

    @RequestMapping("/deepSeek/chat")
    public String deepSeekChat(String prompt) {
        return testChat.deepSeekChat(prompt);
    }

    /**
     * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
     */
    @RequestMapping("/stream/chat")
    public Flux<String> streamChat(String prompt, HttpServletResponse response) {
//        return testChat.streamChat(response, prompt);
        return testChat.streamChatClient(response, prompt);
    }
}
