package com.belly.aiagent.controller;

import com.belly.aiagent.demo.TestChat;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Belly
 * @version 1.1.0
 * 日期 2025/6/15
 * 时间 18:32
 */
@RestController("/test")
@AllArgsConstructor
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
}
