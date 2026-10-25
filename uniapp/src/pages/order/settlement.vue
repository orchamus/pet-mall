<template>
  <view class="settlement-page">
    <!-- 订单信息：商品清单与倒计时 -->
    <view class="order-card pet-card">
      <view class="section-title">
        <text class="title-text">订单信息</text>
        <text class="countdown" :class="{ warning: remainingSeconds < 300 }">
          ⏱ {{ countdownText }}
        </text>
      </view>

      <!-- 商品清单 -->
      <view class="product-list">
        <view class="product-item" v-for="(item, idx) in orderItems" :key="idx">
          <image class="product-image" :src="item.image" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-price">¥{{ formatPrice(item.price) }} × {{ item.quantity }}</text>
          </view>
          <text class="item-total">¥{{ formatPrice(item.price * item.quantity) }}</text>
        </view>
      </view>

      <view class="order-total">
        <text class="total-label">共 {{ totalQuantity }} 件</text>
        <view class="total-right">
          <text class="total-text">合计：</text>
          <text class="price-symbol">¥</text>
          <text class="total-amount">{{ formatPrice(totalAmount) }}</text>
        </view>
      </view>
    </view>

    <!-- 收货地址：有地址展示详情，无地址提示选择 -->
    <view class="address-card pet-card" @click="chooseAddress">
      <view class="address-header">
        <text class="section-title-text">收货地址</text>
        <text class="address-arrow">></text>
      </view>

      <view v-if="selectedAddress" class="address-body">
        <view class="address-user">
          <text class="address-name">{{ selectedAddress.receiverName }}</text>
          <text class="address-phone">{{ selectedAddress.phone }}</text>
          <text v-if="selectedAddress.isDefault" class="address-default">默认</text>
        </view>
        <text class="address-full">{{ formatFullAddress(selectedAddress) }}</text>
      </view>

      <view v-else class="address-empty">
        <text class="empty-icon">📍</text>
        <text class="empty-text">请选择或添加收货地址</text>
      </view>
    </view>

    <!-- 支付方式：当前仅支持模拟支付 -->
    <view class="pay-card pet-card">
      <text class="section-title-text">支付方式</text>
      <view class="pay-method">
        <text class="pay-icon">💳</text>
        <text class="pay-text">模拟支付</text>
        <text class="pay-tag">安全</text>
      </view>
    </view>

    <!-- 底部占位：避免内容被固定底部遮挡 -->
    <view class="footer-placeholder"></view>

    <!-- 底部支付栏：实付金额与确认支付 -->
    <view class="settlement-footer safe-area-bottom">
      <view class="footer-left">
        <text class="pay-label">实付金额</text>
        <view class="pay-amount">
          <text class="price-symbol">¥</text>
          <text class="amount">{{ formatPrice(totalAmount) }}</text>
        </view>
      </view>
      <view class="pay-btn pet-btn-primary" :class="{ disabled: paying }" @click="confirmPay">
        <text v-if="!paying">确认支付</text>
        <text v-else>支付中...</text>
      </view>
    </view>

    <!-- 支付加载遮罩 -->
    <view class="pay-mask" v-if="paying">
      <view class="pay-loading">
        <view class="loading-spinner"></view>
        <text class="loading-text">正在安全支付...</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue';
import { onLoad, onShow, onHide } from '@dcloudio/uni-app';
import { createOrderByCart, createOrderByNow } from '@/api/orders.js';
import { getAddressList } from '@/api/address.js';
import {
  formatPrice,
  showToast,
  navigateTo,
  navigateBack
} from '@/utils/index.js';
import { normalizeAddressList, formatFullAddress } from '@/utils/address.js';
import {
  getPaymentSession,
  savePaymentSession,
  clearPaymentSession,
  saveAddressCache,
  getAddressCache,
  simulatePayment,
  formatCountdown,
  updatePaymentSessionStatus
} from '@/utils/payment.js';

// 支付会话有效期：30 分钟（单位：秒）
const SESSION_TIMEOUT = 30 * 60;

// 响应式数据：支付会话、地址列表、选中地址、支付中状态、剩余秒数
const session = ref(null);
const addressList = ref([]);
const selectedAddress = ref(null);
const paying = ref(false);
const remainingSeconds = ref(SESSION_TIMEOUT);
let countdownTimer = null;

// 计算属性：订单商品、总数量、总金额、倒计时文案
const orderItems = computed(() => session.value?.items || []);
const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0);
});
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 0), 0);
});
const countdownText = computed(() => formatCountdown(remainingSeconds.value));

// 加载收货地址：优先使用缓存，再请求接口同步最新数据
const loadAddress = async () => {
  try {
    const cache = getAddressCache();
    if (cache && cache.list) {
      addressList.value = cache.list;
      if (!selectedAddress.value) {
        const def = cache.list.find((a) => a.isDefault);
        selectedAddress.value = def || cache.list[0] || null;
      }
    }

    const res = await getAddressList();
    const list = normalizeAddressList(res.data || []);
    addressList.value = list;
    saveAddressCache(list);

    if (!selectedAddress.value) {
      const def = list.find((a) => a.isDefault);
      selectedAddress.value = def || list[0] || null;
    } else {
      const updated = list.find((a) => String(a.addressId) === String(selectedAddress.value.addressId));
      if (updated) selectedAddress.value = updated;
    }
  } catch (e) {
    console.error(e);
  }
};

// 校验支付会话有效性：无数据或已超时则返回上一页
const checkSession = () => {
  const data = getPaymentSession();
  if (!data || !data.items || !data.items.length) {
    showToast('订单信息已失效');
    navigateBack();
    return false;
  }
  session.value = data;
  const elapsed = Math.floor((Date.now() - (data.createdAt || Date.now())) / 1000);
  remainingSeconds.value = Math.max(0, SESSION_TIMEOUT - elapsed);
  if (remainingSeconds.value <= 0) {
    handleTimeout();
    return false;
  }
  return true;
};

// 启动倒计时
const startCountdown = () => {
  stopCountdown();
  countdownTimer = setInterval(() => {
    remainingSeconds.value--;
    if (remainingSeconds.value <= 0) {
      stopCountdown();
      handleTimeout();
    }
  }, 1000);
};

// 停止倒计时
const stopCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
};

// 重新校准倒计时（页面再次显示时调用）
const recalibrateCountdown = () => {
  if (!session.value) return;
  const elapsed = Math.floor((Date.now() - (session.value.createdAt || Date.now())) / 1000);
  const next = Math.max(0, SESSION_TIMEOUT - elapsed);
  remainingSeconds.value = next;
  if (next <= 0) {
    handleTimeout();
  }
};

// 会话超时处理：清空会话并返回
const handleTimeout = () => {
  stopCountdown();
  clearPaymentSession();
  uni.showModal({
    title: '会话超时',
    content: '会话已超时，请重新操作',
    showCancel: false,
    confirmText: '知道了',
    success: () => {
      navigateBack();
    }
  });
};

// 跳转地址列表进行选择
const chooseAddress = () => {
  const selectedId = selectedAddress.value?.addressId || '';
  navigateTo(`/pages/address/list?mode=select&selectedId=${selectedId}`);
};

// 地址选择回调
const onAddressSelected = (addr) => {
  selectedAddress.value = addr;
};

// 根据来源（购物车/立即购买）创建订单
const createOrder = async () => {
  const addressId = selectedAddress.value?.addressId;
  if (!addressId) throw new Error('缺少收货地址');

  if (session.value.from === 'cart') {
    const payload = session.value.items.map((item) => ({
      cartId: item.cartId,
      quantity: item.quantity
    }));
    return await createOrderByCart(payload, addressId);
  } else {
    const item = session.value.items[0];
    return await createOrderByNow(item.productId, item.quantity, addressId);
  }
};

// 确认支付：校验地址与会话，调用模拟支付并创建订单
const confirmPay = async () => {
  if (paying.value) return;
  if (!selectedAddress.value) {
    showToast('请选择收货地址');
    return;
  }
  if (remainingSeconds.value <= 0) {
    handleTimeout();
    return;
  }

  paying.value = true;
  updatePaymentSessionStatus('paying');
  try {
    const payRes = await simulatePayment();
    // 模拟支付失败时弹窗引导重试
    if (!payRes.success) {
      paying.value = false;
      updatePaymentSessionStatus('failed');
      uni.showModal({
        title: '支付失败',
        content: payRes.message || '支付过程中发生异常，请重新尝试',
        cancelText: '取消',
        confirmText: '重新支付',
        confirmColor: '#FF7A45',
        success: (res) => {
          if (res.confirm) {
            confirmPay();
          }
        }
      });
      return;
    }

    // 支付成功后创建订单并跳转成功页
    updatePaymentSessionStatus('success');
    const orderRes = await createOrder();
    clearPaymentSession();
    paying.value = false;
    navigateTo(`/pages/order/paySuccess?amount=${totalAmount.value.toFixed(2)}`);
  } catch (e) {
    console.error(e);
    paying.value = false;
    updatePaymentSessionStatus('failed');
    showToast('下单失败，请重试');
  }
};

// 页面加载：校验会话、启动倒计时、监听地址选择事件
onLoad(() => {
  if (!checkSession()) return;
  startCountdown();
  uni.$on('addressSelected', onAddressSelected);
});

// 页面显示时重新校准倒计时并刷新地址
onShow(() => {
  if (!session.value && !checkSession()) return;
  recalibrateCountdown();
  loadAddress();
});

// 页面卸载：停止倒计时并移除事件监听
onUnmounted(() => {
  stopCountdown();
  uni.$off('addressSelected', onAddressSelected);
});
</script>

<style scoped lang="scss">
.settlement-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
}

.order-card,
.address-card,
.pay-card {
  margin: $pet-space-sm;
  padding: $pet-space-base;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $pet-space-base;
  padding-bottom: $pet-space-sm;
  border-bottom: 1rpx solid $pet-border-light;
}
.title-text {
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
}
.countdown {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  background: $pet-bg-grey;
  padding: 6rpx 14rpx;
  border-radius: $pet-radius-lg;
}
.countdown.warning {
  color: $pet-error;
  background: rgba($pet-error, 0.1);
}

.product-list {
  margin-bottom: $pet-space-base;
}
.product-item {
  display: flex;
  align-items: center;
  padding: $pet-space-sm 0;
  border-bottom: 1rpx solid $pet-border-light;
}
.product-item:last-child {
  border-bottom: none;
}
.product-image {
  width: 140rpx;
  height: 140rpx;
  border-radius: $pet-radius-base;
  margin-right: $pet-space-sm;
  background: $pet-bg-grey;
  flex-shrink: 0;
}
.product-info {
  flex: 1;
  min-width: 0;
}
.product-name {
  display: block;
  font-size: $pet-font-base;
  color: $pet-text;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 10rpx;
}
.product-price {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
.item-total {
  font-size: $pet-font-base;
  color: $pet-error;
  font-weight: 700;
  margin-left: $pet-space-sm;
}

.order-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $pet-space-sm;
  border-top: 1rpx solid $pet-border-light;
}
.total-label {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
.total-right {
  display: flex;
  align-items: baseline;
}
.total-text {
  font-size: $pet-font-base;
  color: $pet-text;
}
.total-amount {
  font-size: $pet-font-xl;
  color: $pet-error;
  font-weight: 800;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $pet-space-sm;
}
.section-title-text {
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
}
.address-arrow {
  font-size: $pet-font-lg;
  color: $pet-text-grey;
}
.address-body {
  padding-top: $pet-space-sm;
  border-top: 1rpx solid $pet-border-light;
}
.address-user {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}
.address-name {
  font-size: $pet-font-base;
  font-weight: 700;
  color: $pet-text;
  margin-right: $pet-space-sm;
}
.address-phone {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  margin-right: $pet-space-sm;
}
.address-default {
  font-size: $pet-font-xs;
  color: $pet-primary;
  border: 1rpx solid $pet-primary;
  padding: 2rpx 10rpx;
  border-radius: $pet-radius-sm;
}
.address-full {
  display: block;
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  line-height: 1.5;
}
.address-empty {
  display: flex;
  align-items: center;
  padding: $pet-space-base 0;
  color: $pet-text-grey;
}
.empty-icon {
  font-size: 48rpx;
  margin-right: $pet-space-sm;
}
.empty-text {
  font-size: $pet-font-base;
}

.pay-method {
  display: flex;
  align-items: center;
  padding-top: $pet-space-sm;
  border-top: 1rpx solid $pet-border-light;
}
.pay-icon {
  font-size: 40rpx;
  margin-right: $pet-space-sm;
}
.pay-text {
  flex: 1;
  font-size: $pet-font-base;
  color: $pet-text;
}
.pay-tag {
  font-size: $pet-font-xs;
  color: $pet-primary;
  border: 1rpx solid $pet-primary;
  padding: 2rpx 10rpx;
  border-radius: $pet-radius-sm;
}

.footer-placeholder {
  height: 140rpx;
}

.settlement-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: var(--window-bottom);
  background: $pet-bg-white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $pet-space-sm $pet-space-base;
  box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 100;
}
.footer-left {
  display: flex;
  align-items: baseline;
}
.pay-label {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  margin-right: $pet-space-sm;
}
.pay-amount {
  display: flex;
  align-items: baseline;
}
.amount {
  color: $pet-error;
  font-size: $pet-font-xl;
  font-weight: 800;
}
.price-symbol {
  color: $pet-error;
  font-size: $pet-font-sm;
  font-weight: 700;
}
.pay-btn {
  height: 76rpx;
  padding: 0 48rpx;
  font-size: $pet-font-lg;
}
.pay-btn.disabled {
  opacity: 0.7;
}

.pay-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.pay-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: $pet-bg-white;
  padding: 60rpx 80rpx;
  border-radius: $pet-radius-xl;
  box-shadow: $pet-shadow-lg;
}
.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid $pet-border;
  border-top-color: $pet-primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: $pet-space-base;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.loading-text {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
}
</style>
