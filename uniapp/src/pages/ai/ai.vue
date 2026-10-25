<template>
  <view class="ai-page">
    <!-- 客服头部：头像、状态、清空 -->
    <view class="ai-header">
      <view class="header-content">
        <view class="ai-avatar">🐾</view>
        <view class="ai-info">
          <text class="ai-name">宠物商店客服</text>
          <view class="ai-status">
            <view class="status-dot"></view>
            <text class="status-text">在线中</text>
          </view>
        </view>
      </view>
      <view class="header-action" @click="clearChat">
        <text class="clear-icon">🗑️</text>
      </view>
    </view>

    <!-- 消息区域：欢迎语、消息列表、思考中 -->
    <scroll-view
      class="ai-messages"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      :enable-back-to-top="true"
    >
      <view class="welcome-card pet-card" v-if="filteredMessages.length === 0">
        <view class="welcome-icon">👋</view>
        <text class="welcome-title">您好，尊敬的客户</text>
        <text class="welcome-desc">
          我是您的专属宠物顾问，可以为您解答宠物养护、商品推荐、订单查询等问题。
        </text>
      </view>

      <view
        class="message-item"
        :class="msg.role"
        v-for="(msg, index) in filteredMessages"
        :key="index"
      >
        <view class="message-avatar" v-if="msg.role === 'assistant'">
          <text>🐾</text>
        </view>
        <view class="message-content">
          <view class="message-bubble">
            <text class="message-text">{{ msg.content }}</text>
          </view>
        </view>
        <view class="message-avatar user-avatar" v-if="msg.role === 'user'">
          <text>🙂</text>
        </view>
      </view>

      <!-- 客服思考中动画 -->
      <view class="message-item assistant" v-if="loading">
        <view class="message-avatar">
          <text>🐾</text>
        </view>
        <view class="message-content">
          <view class="message-bubble typing">
            <view class="typing-dots">
              <view class="dot"></view>
              <view class="dot"></view>
              <view class="dot"></view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部输入区域 -->
    <view class="ai-input safe-area-bottom">
      <view class="input-wrap">
        <textarea
          class="chat-textarea"
          v-model="userMessage"
          placeholder="输入您的问题，按发送咨询客服..."
          :auto-height="true"
          :maxlength="500"
          :fixed="true"
          :show-confirm-bar="false"
          @confirm="handleSend"
        />
        <view
          class="send-btn"
          :class="{ active: userMessage.trim() }"
          @click="handleSend"
        >
          <text class="send-icon">➤</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue';
import { chatWithDeepSeek } from '@/utils/ai.js';
import { getOrderList } from '@/api/orders.js';

// 页面挂载：禁用外层滚动，避免聊天页面整体滑动
onMounted(() => {
	const page = document.querySelector('uni-page');
	const wrapper = document.querySelector('uni-page-wrapper');
	if (page) page.style.overflow = 'hidden';
	if (wrapper) wrapper.style.overflow = 'hidden';
});

// 页面卸载：恢复外层滚动
onUnmounted(() => {
	const page = document.querySelector('uni-page');
	const wrapper = document.querySelector('uni-page-wrapper');
	if (page) page.style.overflow = '';
	if (wrapper) wrapper.style.overflow = '';
});

// 响应式数据：用户输入、加载状态、滚动位置、消息历史
const userMessage = ref('');
const loading = ref(false);
const scrollTop = ref(0);

// 客服系统提示词，定义回答规范与订单查询模式
const messageHistory = ref([
  {
    role: 'system',
    content:
      '你是宠物商店的专业客服，请用礼貌、专业、简洁的语言回答用户问题。\n' +
      '【普通问答模式】当用户咨询宠物养护、商品推荐、配送政策等问题时，\n' +
      '请直接提供准确建议，避免使用网络流行语，称呼用户为"尊敬的客户"。\n' +
      '【订单查询模式】当用户提到"订单"、"我的订单"或提供订单号时，\n' +
      '必须先获取真实订单数据，再根据数据回复商品详情，\n' +
      '包括商品名称、数量、价格、购买时间，禁止虚构任何信息，一个订单信息输出完毕后需要换行，确保格式优美。\n' +
      '如果获取订单失败，需告知用户并引导重新查询或咨询其他问题。'
  }
]);

// 计算属性：过滤掉系统提示，仅展示用户与客服消息
const filteredMessages = computed(() => {
  return messageHistory.value.filter((msg) => msg.role !== 'system');
});

// 滚动消息区域到底部
const scrollToBottom = async () => {
  await nextTick();
  scrollTop.value = 99999 + Math.random();
};

// 获取用户订单列表，用于客服订单查询
const fetchOrderList = async () => {
  try {
    const res = await getOrderList(1, 100);
    const orders = res.data?.records || [];
    orders.forEach((order) => {
      order.purchaseTime = order.paymentTime || order.createdAt || null;
    });
    return orders;
  } catch (e) {
    console.error(e);
    return [];
  }
};

// 从用户消息中提取订单号
const extractOrderId = (message) => {
  const regex = /(订单号|单号|order\s*id\s*[:：]\s*?)([\w-]+)/i;
  const match = message.match(regex);
  return match ? match[2] : null;
};

// 发送消息：普通问答直接请求 AI；订单查询先校验登录并拉取真实订单数据
const handleSend = async () => {
  const text = userMessage.value.trim();
  if (!text || loading.value) return;

  const isOrderQuery = /订单|我的订单|单号|order/i.test(text);
  let orderDataForAI = null;

  try {
    messageHistory.value.push({ role: 'user', content: text });
    await scrollToBottom();
    userMessage.value = '';
    loading.value = true;

    // 订单查询模式：校验登录态并拉取订单数据
    if (isOrderQuery) {
      const token = uni.getStorageSync('satoken');
      if (!token) {
        messageHistory.value.push({
          role: 'assistant',
          content: '尊敬的客户，查询订单需要先登录，请前往“我的”页面登录后再试。'
        });
        loading.value = false;
        await scrollToBottom();
        return;
      }
      const orders = await fetchOrderList();
      if (orders.length > 0) {
        const orderIdFromMessage = extractOrderId(text);
        if (orderIdFromMessage) {
          const specificOrder = orders.find(
            (o) => String(o.orderId) === String(orderIdFromMessage)
          );
          if (specificOrder) {
            orderDataForAI = {
              orderId: specificOrder.orderId,
              productName: specificOrder.product?.name,
              quantity: specificOrder.quantity,
              unitPrice: specificOrder.price,
              totalPrice: specificOrder.price * specificOrder.quantity,
              purchaseTime: specificOrder.purchaseTime
            };
          } else {
            const availableIds = orders.map((o) => o.orderId).join('、');
            messageHistory.value.push({
              role: 'assistant',
              content: `尊敬的客户，我未能找到您提供的"${orderIdFromMessage}"订单记录。\n目前系统中可查询的订单号为：${availableIds}\n请确认是否输入正确，或咨询其他问题。`
            });
            loading.value = false;
            await scrollToBottom();
            return;
          }
        } else {
          orderDataForAI = {
            allOrders: orders.map((o) => ({
              orderId: o.orderId,
              productName: o.product?.name,
              quantity: o.quantity,
              unitPrice: o.price,
              totalPrice: o.price * o.quantity,
              purchaseTime: o.purchaseTime
            })),
            message: '用户未指定订单号，显示所有订单详情'
          };
        }
      } else {
        messageHistory.value.push({
          role: 'assistant',
          content: '尊敬的客户，暂未查询到您的订单记录，您可以咨询其他关于宠物用品或养护的问题。'
        });
        loading.value = false;
        await scrollToBottom();
        return;
      }
    }

    // 普通问答：拼接系统提示与订单数据后请求 AI
    let aiMessages = [...messageHistory.value];
    if (orderDataForAI) {
      aiMessages.push({
        role: 'system',
        content: `以下是获取到的订单数据，请根据这些数据回答用户问题：${JSON.stringify(orderDataForAI, null, 2)}`
      });
    }

    const reply = await chatWithDeepSeek(aiMessages);
    messageHistory.value.push({ role: 'assistant', content: reply });
  } catch (error) {
    console.error(error);
    messageHistory.value.push({
      role: 'assistant',
      content: '抱歉，客服暂时无法响应，请稍后再试。'
    });
  } finally {
    loading.value = false;
    await scrollToBottom();
  }
};

// 清空对话：仅保留系统提示词
const clearChat = () => {
  uni.showModal({
    title: '清空对话',
    content: '确定清空当前对话记录？',
    confirmColor: '#FF7A45',
    success: (res) => {
      if (res.confirm) {
        messageHistory.value = messageHistory.value.filter((m) => m.role === 'system');
      }
    }
  });
};
</script>

<style scoped lang="scss">
page {
  overflow: hidden;
}

.ai-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 50px);
  background: $pet-bg;
  overflow: hidden;
}

/* 头部 */
.ai-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $pet-space-sm;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
}

.header-content {
  display: flex;
  align-items: center;
  gap: $pet-space-sm;
}

.ai-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $pet-radius-circle;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
}

.ai-info {
  display: flex;
  flex-direction: column;
}

.ai-name {
  font-size: $pet-font-lg;
  font-weight: 700;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 4rpx;
  font-size: $pet-font-sm;
  opacity: 0.95;
}

.status-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: $pet-radius-circle;
  background: $pet-success;
  box-shadow: 0 0 8rpx $pet-success;
}

.header-action {
  padding: $pet-space-xs;
  opacity: 0.9;
}

.clear-icon {
  font-size: $pet-font-lg;
}

/* 消息区域 */
.ai-messages {
  flex: 1;
  height: 0;
  padding: $pet-space-sm;
}

.welcome-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: $pet-space-lg;
  margin: $pet-space-base 0;
}

.welcome-icon {
  font-size: 80rpx;
  margin-bottom: $pet-space-sm;
}

.welcome-title {
  font-size: $pet-font-xl;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: $pet-space-xs;
}

.welcome-desc {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  line-height: 1.6;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: $pet-space-base;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: $pet-radius-circle;
  background: $pet-bg-white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  box-shadow: $pet-shadow-sm;
  flex-shrink: 0;
}

.user-avatar {
  background: linear-gradient(135deg, $pet-primary-soft, #fff);
}

.message-content {
  max-width: 70%;
  margin: 0 $pet-space-sm;
}

.message-bubble {
  padding: $pet-space-sm;
  border-radius: $pet-radius-lg;
  word-break: break-word;
  line-height: 1.6;
  font-size: $pet-font-base;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  border-bottom-right-radius: $pet-radius-sm;
}

.message-item.assistant .message-bubble {
  background: $pet-bg-white;
  color: $pet-text;
  border-bottom-left-radius: $pet-radius-sm;
  box-shadow: $pet-shadow-sm;
}

.message-text {
  white-space: pre-wrap;
}

.typing {
  padding: 28rpx 32rpx;
}

.typing-dots {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: $pet-radius-circle;
  background: $pet-text-grey;
  animation: bounce 1.2s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

/* 输入区域 */
.ai-input {
  flex-shrink: 0;
  padding: $pet-space-sm;
  background: $pet-bg-white;
  border-top: 1rpx solid $pet-border;
}

.input-wrap {
  display: flex;
  align-items: flex-end;
  gap: $pet-space-sm;
  background: $pet-bg-grey;
  border-radius: $pet-radius-xl;
  padding: 12rpx 20rpx;
}

.chat-textarea {
  flex: 1;
  max-height: 160rpx;
  min-height: 56rpx;
  font-size: $pet-font-base;
  color: $pet-text;
  line-height: 1.5;
  padding: 10rpx 0;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: $pet-radius-circle;
  background: $pet-border;
  color: $pet-text-disable;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn.active {
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  box-shadow: 0 6rpx 16rpx rgba($pet-primary, 0.3);
}

.send-icon {
  font-size: 32rpx;
  transform: rotate(-45deg);
  margin-left: 4rpx;
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}
</style>
