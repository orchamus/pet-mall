package com.example.back.controller;

import com.example.back.Result.ResponseResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 对话模块 Controller。
 *
 * <p>提供基于智谱 GLM 模型的 AI 聊天对话能力，基础路径为 /ai。</p>
 *

 */
@RestController
@RequestMapping("/ai")
public class AiChatController {

    /** 智谱 API Key：从环境变量 ZHIPU_API_KEY 读取，禁止把真实 key 提交到仓库。 */
    private static final String API_KEY = System.getenv().getOrDefault("ZHIPU_API_KEY", "");
    private static final String BASE_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String MODEL = "glm-4-flash";

    private final RestTemplate restTemplate;

    public AiChatController() {
        // 配置请求工厂，设置连接与读取超时时间为 30 秒
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * AI 聊天。
     *
     * <p>接收消息列表，调用 GLM 模型接口返回 AI 回复内容。</p>
     *
     * @param body 请求体，包含 messages 字段
     * @return AI 回复文本
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/chat")
    public ResponseResult<String> chat(@RequestBody Map<String, Object> body) {
        if (API_KEY.isEmpty()) {
            return ResponseResult.fail("未配置 ZHIPU_API_KEY 环境变量");
        }
        List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(BASE_URL, entity, Map.class);
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return ResponseResult.success("操作成功", content);
                }
            }
            return ResponseResult.fail("AI 响应异常");
        } catch (Exception e) {
            // 异常时打印堆栈并返回错误信息
            e.printStackTrace();
            return ResponseResult.fail("AI 请求失败: " + e.getMessage());
        }
    }
}
