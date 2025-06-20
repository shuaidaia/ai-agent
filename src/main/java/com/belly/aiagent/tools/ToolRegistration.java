package com.belly.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具注册类
 */
@Configuration
public class ToolRegistration {
    @Bean
    public ToolCallback[] allTools() {
        // 搜索工具
//        WebSearchTool webSearchTool = new WebSearchTool("api");
        WeatherTool weatherTool = new WeatherTool();
        return ToolCallbacks.from(
                weatherTool
//                , webSearchTool
        );
    }
}
