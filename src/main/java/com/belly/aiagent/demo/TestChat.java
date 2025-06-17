package com.belly.aiagent.demo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 测试AI模型接口
 */
@Component
public class TestChat implements CommandLineRunner {
    // ollama模型
    @Autowired(required = false)
    @Qualifier("ollamaChatModel")
    private ChatModel ollamaChatModel;

    // deepSeekChat模型
    @Autowired(required = false)
    private DashScopeChatModel deepSeekChatModel;
    public String ollamaChat() {
        if (null == ollamaChatModel) {
            return "ollamaChatModel is null";
        }
        return ollamaChatModel.call(new Prompt("你好！Belly")).getResult().getOutput().getText();
    }

    public String deepSeekChat(String prompt) {
        if (null == deepSeekChatModel) {
            return "dashScopeChatModel is null";
        }
        return deepSeekChatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("嘻嘻嘻嘻");
    }
}
