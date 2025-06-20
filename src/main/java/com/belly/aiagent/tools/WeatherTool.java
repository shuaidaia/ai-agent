package com.belly.aiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 获取城市天气信息工具
 */
public class WeatherTool {
    @Tool(description = "Get city weather information")
    public String getWeather(@ToolParam(description = "City name") String city) {
        return "It's rain in " + city;
    }
}
