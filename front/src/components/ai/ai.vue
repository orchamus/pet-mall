<!-- src/components/OpenAIChat.vue -->
<template>
  <div class="chat-container">
    <!-- 聊天头部：Logo、标题与在线状态 -->
    <div class="chat-header">
      <div class="header-content">
        <div class="logo">🐾</div>
        <h2>宠物商店客服</h2>
        <div class="status-dot online"></div>
      </div>
    </div>

    <!-- 消息展示区域 -->
    <div class="messages-area" ref="messagesArea">
      <transition-group name="fade">
        <!-- 只渲染用户和客服消息 -->
        <div v-for="(msg, index) in filteredMessages" :key="index" class="message" :class="msg.role">
          <div class="message-avatar" v-if="msg.role === 'assistant'">
            <div class="logo">🐾</div>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <span class="role-label">{{ msg.role === 'user' ? '你' : '客服' }}:</span>
              <p>{{ msg.content }}</p>
            </div>
          </div>
          <div class="message-avatar" v-if="msg.role === 'user'">
            <img src="https://picsum.photos/32/32?random=2" alt="用户头像">
          </div>
        </div>
      </transition-group>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-group">

        <textarea
            v-model="userMessage"
            placeholder="输入你的问题，按Enter发送..."
            @keyup.enter="handleSend"
            ref="inputField"
            class="chat-input"
        >

    </textarea>
        <div class="input-actions">
          <button @click="handleSend" class="send-btn">
            <span class="btn-icon">→</span>
          </button>
        </div>



      </div>
    </div>

    <!-- 加载状态提示 -->
    <div class="status-area" v-if="loading">
      <div class="loading-indicator">
        <span></span>
        <span></span>
        <span></span>
      </div>
      <span class="loading-text">客服正在为你解答...</span>
    </div>

    <!-- 错误提示区域 -->
    <div class="error-area" v-if="errorMsg">
      <p>{{ errorMsg }}</p>
    </div>
  </div>

</template>
<script setup>
import { ElMessage } from 'element-plus'
import { ref, computed, nextTick } from 'vue';
import { chatWithDeepSeek } from '../../untils/openai'
import { getAllOrdersByToken } from "../../api/order/order";

// 用户输入的消息
const userMessage = ref('');
// 错误提示信息
const errorMsg = ref('');
// 加载状态
const loading = ref(false);
// 消息区域 DOM 引用
const messagesArea = ref(null);
// 输入框 DOM 引用
const inputField = ref(null);
// 当前用户订单列表
const orderList = ref([]);

/**
 * 获取当前用户的全部订单数据
 * @returns {Array|null} 订单列表或 null
 */
const fetchOrderList = async () => {
  try {
    const res = await getAllOrdersByToken();
    if (res.code === 200) {
      orderList.value = res.data;
      // 处理订单数据，添加购买时间字段
      processOrderData();
      return orderList.value; // 返回订单数据
    } else {
      ElMessage.error("获取订单列表失败");
      orderList.value = []; // 清空订单数据
      return null;
    }
  } catch (error) {
    ElMessage.error("获取订单列表失败");
    orderList.value = []; // 清空订单数据
    console.error("获取订单列表出错:", error);
    return null;
  }
};

/**
 * 处理订单数据，添加购买时间
 * 优先使用支付时间，其次创建时间
 */
const processOrderData = () => {
  orderList.value.forEach(order => {
    // 优先使用支付时间作为购买时间
    if (order.paymentTime) {
      order.purchaseTime = order.paymentTime;
    } else if (order.createdAt) {
      // 如果没有支付时间，使用创建时间
      order.purchaseTime = order.createdAt;
    } else {
      // 都没有则设为null
      order.purchaseTime = null;
    }
  });
};


// 对话历史记录，包含系统消息和用户-AI的对话
const messageHistory = ref([
  { role: "system",
    content: "你是宠物商店的专业客服，请用礼貌、专业、简洁的语言回答用户问题。\n" +
        "【普通问答模式】当用户咨询宠物养护、商品推荐、配送政策等问题时，\n" +
        "请直接提供准确建议，避免使用网络流行语，称呼用户为'尊敬的客户'。\n" +
        "【订单查询模式】当用户提到'订单'、'我的订单'或提供订单号时，\n" +
        "必须先调用fetchOrderList方法获取真实订单数据，再根据数据回复商品详情，\n" +
        "包括商品名称、数量、价格、购买时间，禁止虚构任何信息，一个订单信息输出完毕后需要换行，确保格式优美。\n" +
        "如果获取订单失败，需告知用户并引导重新查询或咨询其他问题。"
  }
]);

/**
 * 从用户消息中提取订单号
 * @param {String} message 用户输入内容
 * @returns {String|null} 提取到的订单号
 */
const extractOrderId = (message) => {
  const orderIdRegex = /(订单号|单号|order\s*id\s*[:：]\s*?)(\d+|[\w-]+)/i;
  const match = message.match(orderIdRegex);
  return match ? match[2] : null;
};

// 计算属性：过滤掉系统消息，仅展示用户与客服消息
const filteredMessages = computed(() => {
  return messageHistory.value.filter(msg => msg.role !== 'system');
});

/**
 * 监听回车键发送消息
 * 根据是否为订单查询走不同分支：普通问答直接请求 AI；订单查询先拉取订单数据再构造上下文
 */
const handleSend = async () => {
  if (!userMessage.value.trim()) {
    ElMessage.error("请输入内容");
    return;}

  const originalMessage = userMessage.value.trim();
  // 判断是否为订单相关查询
  const isOrderQuery = /订单|我的订单|单号|order/i.test(originalMessage);
  let orderDataForAI = null;

  try {

    // 添加用户消息到历史记录（所有场景通用）
    messageHistory.value.push({ role: "user", content: originalMessage });

    // 显示"思考中"消息（所有场景通用）
    const thinkingIndex = messageHistory.value.length;
    messageHistory.value.push({ role: "assistant", content: "正在为你解答..." });

    await scrollToBottom();
    userMessage.value = '';
    loading.value = true;

    if (isOrderQuery) {
      loading.value = true;
      const res = await fetchOrderList();

      if (orderList.value && orderList.value.length > 0) {
        const orderIdFromMessage = extractOrderId(originalMessage);

        if (orderIdFromMessage) {
          // 遍历所有订单查找匹配
          const specificOrder = orderList.value.find(
              order => order.orderId.toString() === orderIdFromMessage.toString()
          );

          if (specificOrder) {
            // 存在该订单，提取数据（包含购买时间）
            orderDataForAI = {
              orderId: specificOrder.orderId,
              productName: specificOrder.product.name,
              quantity: specificOrder.quantity,
              unitPrice: specificOrder.price,
              totalPrice: specificOrder.price * specificOrder.quantity,
              purchaseTime: specificOrder.purchaseTime // 添加购买时间
            };
          } else {
            // 订单不存在，生成包含所有可查询订单号的回复
            const availableOrderIds = orderList.value.map(o => o.orderId);
            const orderIdList = availableOrderIds.map(id => `"- ${id}"`).join('\n');

            messageHistory.value[thinkingIndex].content = `尊敬的客户，您好！我未能找到您提供的"${orderIdFromMessage}"订单记录。\n目前系统中可查询的订单号为：\n${orderIdList}\n请确认是否输入正确，或者您也可以咨询其他关于宠物用品或养护的问题，我很乐意为您提供帮助。`;
            await scrollToBottom();
            loading.value = false;
            return;
          }
        } else {
          // 未提供具体订单号，显示所有订单详情（包含购买时间）
          const allOrdersDetails = orderList.value.map(order => ({
            orderId: order.orderId,
            productName: order.product.name,
            quantity: order.quantity,
            unitPrice: order.price,
            totalPrice: order.price * order.quantity,
            purchaseTime: order.purchaseTime // 添加购买时间
          }));

          orderDataForAI = {
            allOrders: allOrdersDetails,
            message: "用户未指定订单号，显示所有订单详情"
          };
        }

        // 将订单数据添加到对话历史
        messageHistory.value.push({
          role: "system",
          content: `订单数据已获取：${JSON.stringify(orderDataForAI, null, 2)}`
        });

      } else {
        messageHistory.value[thinkingIndex].content = "抱歉，获取订单信息失败，请稍后再试。";
        await scrollToBottom();
        return;
      }
    }

    // 调用AI接口（统一处理所有场景）
    let aiMessages = [...messageHistory.value];
    if (orderDataForAI) {
      aiMessages = [
        ...aiMessages,
        {
          role: "system",
          content: `以下是获取到的订单数据，请根据这些数据回答用户问题：${JSON.stringify(orderDataForAI, null, 2)}`
        }
      ];
    }

    const response = await chatWithDeepSeek(aiMessages);

    // 更新AI回复（根据场景显示不同内容）
    messageHistory.value[thinkingIndex].content = response;
    await scrollToBottom();

  } catch (error) {
    errorMsg.value = error.message || '请求失败，请稍后再试';
    if (messageHistory.value.length > 0 &&
        messageHistory.value[messageHistory.value.length - 1].content.includes("正在为你")) {
      messageHistory.value.pop();
    }
    await scrollToBottom();
  } finally {
    loading.value = false;
    inputField.value.focus();
  }
};

/**
 * 滚动消息区域到底部
 */
const scrollToBottom = async () => {
  await nextTick();
  if (messagesArea.value) {
    messagesArea.value.scrollTop = messagesArea.value.scrollHeight;
  }
};
</script>

<style scoped>
/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 聊天容器 */
.chat-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 85vh;
  border-radius: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 头部样式 */
.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e6ed;
  margin-bottom: 20px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 24px;
  color: #4CAF50;
  margin-right: 10px;
}

.chat-header h2 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
  font-weight: 600;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  margin-left: 10px;
}

.online {
  background-color: #4CAF50;
  box-shadow: 0 0 10px #4CAF50;
}

/* 消息区域样式 */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  margin-bottom: 20px;
  scroll-behavior: smooth;
}

/* 自定义滚动条 */
.messages-area::-webkit-scrollbar {
  width: 6px;
}

.messages-area::-webkit-scrollbar-track {
  background: transparent;
}

.messages-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.messages-area::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.message {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-end;
  animation: fadeIn 0.3s ease-in-out;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 10px;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 75%;
  position: relative;
}

.message-bubble {
  padding: 15px 20px;
  border-radius: 24px;
  position: relative;
  word-wrap: break-word;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.message.user .message-bubble {
  background-color: #4CAF50;
  color: white;
  border-bottom-right-radius: 6px;
}

.message.assistant .message-bubble {
  background-color: white;
  color: #333;
  border-bottom-left-radius: 6px;
}

.role-label {
  font-weight: 500;
  font-size: 0.8em;
  margin-bottom: 5px;
  display: block;
  opacity: 0.8;
}

.message.user p {
  margin: 0;
  padding-right: 5px;
}

.message.assistant p {
  margin: 0;
  padding-left: 5px;
}

/* 输入区域样式 */
.input-area {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.input-group {
  display: flex;
  width: 100%;
  gap: 10px;
}

.chat-input {
  flex: 1;
  width: 600px;
  padding: 20px 20px;
  border: none;
  border-radius: 20px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.chat-input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.2);
}

.chat-input::placeholder {
  color: #aaa;
}

.input-actions {
  display: flex;
  align-items: flex-end;
  margin-bottom:10px ;
}

.send-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  font-size: 1.2rem;
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.3);
}

.send-btn:hover {
  background: #43a047;
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
}

/* 状态区域样式 */
.status-area {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 12px 15px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-indicator {
  display: flex;
  gap: 6px;
}

.loading-indicator span {
  width: 10px;
  height: 10px;
  background-color: #888;
  border-radius: 50%;
  animation: bounce 1.2s infinite ease-in-out;
}

.loading-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

.loading-text {
  margin-left: 10px;
  color: #666;
  font-size: 0.9rem;
}

.error-area {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 12px 15px;
  background-color: rgba(255, 0, 0, 0.05);
  border-left: 4px solid #f44336;
  border-radius: 15px;
}

.error-icon {
  width: 24px;
  height: 24px;
  background-color: #f44336;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 10px;
}

.error-area p {
  margin: 0;
  color: #f44336;
  font-size: 0.9rem;
}

/* 动画效果 */
.fade-enter-active, .fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    margin: 0;
    border-radius: 0;
    height: 100vh;
    padding: 10px;
  }

  .chat-header h2 {
    font-size: 1.2rem;
  }

  .message-content {
    max-width: 70%;
  }

  .input-group {
    gap: 8px;
  }

  .send-btn {
    width: 100px;
    height: 145px;
  }
}
</style>
