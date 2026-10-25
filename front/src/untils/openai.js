/**
 * DeepSeek 对话接口封装。
 *
 * <p>API Key 通过 Vite 环境变量注入：在 front/.env.local 中配置
 * {@code VITE_DEEPSEEK_API_KEY=sk-xxx}，不要把真实 key 写进源码。</p>
 */
import OpenAI from "openai";

const API_KEY = import.meta.env.VITE_DEEPSEEK_API_KEY;

/**
 * 创建 DeepSeek 客户端。
 *
 * @param {string} [apiKey] 可覆盖默认的 key
 * @returns {OpenAI} 客户端实例
 */
export const createOpenAIClient = (apiKey = API_KEY) => {
  return new OpenAI({
    baseURL: "https://api.deepseek.com",
    apiKey,
    dangerouslyAllowBrowser: true,
  });
};

/**
 * 调用 DeepSeek 对话接口。
 *
 * @param {Array<Object>} messages 消息列表
 * @param {string} [model] 模型名
 * @returns {Promise<string>} 回复文本
 */
export const chatWithDeepSeek = async (messages, model = "deepseek-chat") => {
  const client = createOpenAIClient();
  try {
    const completion = await client.chat.completions.create({ messages, model });
    return completion.choices[0].message.content;
  } catch (error) {
    console.error("DeepSeek 调用失败:", error);
    throw error;
  }
};
