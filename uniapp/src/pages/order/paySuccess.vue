<template>
  <view class="success-page">
    <!-- 支付成功卡片 -->
    <view class="success-card pet-card">
      <view class="success-icon">
        <text class="check">✓</text>
      </view>
      <text class="success-title">支付成功</text>
      <text class="success-desc">您的订单已生成，我们将尽快为您发货</text>

      <!-- 实付金额展示 -->
      <view class="amount-box">
        <text class="amount-label">实付金额</text>
        <view class="amount-value">
          <text class="symbol">¥</text>
          <text class="amount">{{ amount }}</text>
        </view>
      </view>

      <!-- 操作按钮：查看订单 / 继续购物 -->
      <view class="actions">
        <view class="btn btn-primary" @click="goOrders">
          <text>查看订单</text>
        </view>
        <view class="btn btn-default" @click="goHome">
          <text>继续购物</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { switchTab, navigateTo } from '@/utils/index.js';

// 响应式数据：实付金额
const amount = ref('0.00');

// 跳转订单列表
const goOrders = () => {
  navigateTo('/pages/order/order');
};

// 跳转首页
const goHome = () => {
  switchTab('/pages/index/index');
};

// 页面加载：读取 URL 中的金额参数
onLoad((options) => {
  amount.value = options?.amount || '0.00';
});
</script>

<style scoped lang="scss">
.success-page {
  min-height: 100vh;
  background: $pet-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $pet-space-base;
}

.success-card {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx $pet-space-base;
  text-align: center;
  animation: fadeInUp 0.5s ease-out;
}

/* 入场动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.success-icon {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, $pet-success, #81C784);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40rpx;
  box-shadow: 0 12rpx 32rpx rgba($pet-success, 0.3);
  animation: scaleIn 0.4s ease-out 0.2s both;
}

/* 成功图标缩放动画 */
@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

.check {
  color: #fff;
  font-size: 72rpx;
  font-weight: 700;
}

.success-title {
  font-size: $pet-font-xxl;
  font-weight: 800;
  color: $pet-text;
  margin-bottom: 16rpx;
}

.success-desc {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  margin-bottom: 60rpx;
}

.amount-box {
  background: $pet-bg-grey;
  border-radius: $pet-radius-lg;
  padding: 40rpx 80rpx;
  margin-bottom: 60rpx;
}

/* 金额展示 */
.amount-label {
  display: block;
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  margin-bottom: 10rpx;
}
.amount-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
}
.symbol {
  font-size: $pet-font-lg;
  color: $pet-error;
  font-weight: 700;
}
.amount {
  font-size: 56rpx;
  color: $pet-error;
  font-weight: 800;
}

.actions {
  display: flex;
  gap: $pet-space-base;
  width: 100%;
}

/* 操作按钮 */
.btn {
  flex: 1;
  height: 84rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $pet-radius-lg;
  font-size: $pet-font-base;
  font-weight: 600;
}
.btn-primary {
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
}
.btn-default {
  background: $pet-bg-grey;
  color: $pet-text;
}
</style>
