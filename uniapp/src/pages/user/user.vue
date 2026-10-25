<template>
  <view class="user-page">
    <!-- 用户信息头部：登录态判断 -->
    <view class="user-header">
      <view class="header-bg"></view>
      <view class="user-card" @click="isLogin ? null : goLogin()">
        <image
          class="avatar"
          :src="userInfo?.avatar || '/static/logo.png'"
          mode="aspectFill"
        />
        <view class="user-info" v-if="isLogin">
          <text class="user-name">{{ userInfo?.nickName || userInfo?.userName || '用户' }}</text>
          <text class="user-type">{{ userTypeText }}</text>
        </view>
        <view class="user-info" v-else>
          <text class="user-name">点击登录</text>
          <text class="user-type">登录后享受更多服务</text>
        </view>
        <text class="arrow-right">></text>
      </view>
    </view>

    <!-- 快捷入口：订单 / 地址 / 购物车 / 首页 -->
    <view class="quick-menu pet-card">
      <view class="quick-item" @click="goOrders">
        <view class="quick-icon order">📦</view>
        <text class="quick-text">我的订单</text>
      </view>
      <view class="quick-item" @click="goAddress">
        <view class="quick-icon address">📍</view>
        <text class="quick-text">收货地址</text>
      </view>
      <view class="quick-item" @click="goCart">
        <view class="quick-icon cart">🛒</view>
        <text class="quick-text">购物车</text>
      </view>
      <view class="quick-item" @click="goHome">
        <view class="quick-icon home">🏠</view>
        <text class="quick-text">首页</text>
      </view>
    </view>

    <!-- 菜单列表：登录后显示退出登录 -->
    <view class="menu-list pet-card">
      <view class="menu-item" @click="goOrders">
        <view class="menu-left">
          <text class="menu-icon">📝</text>
          <text class="menu-text">我的订单</text>
        </view>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goAddress">
        <view class="menu-left">
          <text class="menu-icon">📍</text>
          <text class="menu-text">收货地址</text>
        </view>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" v-if="isLogin" @click="logout">
        <view class="menu-left">
          <text class="menu-icon">🚪</text>
          <text class="menu-text">退出登录</text>
        </view>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <!-- 底部品牌信息 -->
    <view class="footer-info">
      <text>萌宠乐园 · 温暖陪伴</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/store/user.js';
import { navigateTo, showToast, switchTab } from '@/utils/index.js';

// 用户 Store：登录态与用户信息
const userStore = useUserStore();
const isLogin = computed(() => userStore.isLogin);
const userInfo = computed(() => userStore.userInfo);

// 计算属性：用户类型文案
const userTypeText = computed(() => {
  const type = userInfo.value?.userType;
  if (type === '0') return '普通用户';
  if (type === '1') return '商家用户';
  if (type === '2') return '管理员';
  return '';
});

// 跳转登录页
const goLogin = () => {
  navigateTo('/pages/login/login');
};

// 跳转订单列表：未登录先登录
const goOrders = () => {
  if (!isLogin.value) {
    goLogin();
    return;
  }
  navigateTo('/pages/order/order');
};

// 跳转地址管理：未登录先登录
const goAddress = () => {
  if (!isLogin.value) {
    goLogin();
    return;
  }
  navigateTo('/pages/address/list?mode=manage');
};

// 跳转购物车
const goCart = () => {
  switchTab('/pages/cart/cart');
};

// 跳转首页
const goHome = () => {
  switchTab('/pages/index/index');
};

// 退出登录
const logout = async () => {
  uni.showModal({
    title: '退出登录',
    content: '确定退出当前账号？',
    confirmColor: '#FF7A45',
    success: (res) => {
      if (res.confirm) {
        userStore.logout();
        showToast('已退出登录');
      }
    }
  });
};

// 页面显示时：若已登录但缺少用户信息，则拉取用户信息
onShow(() => {
  if (userStore.isLogin && !userStore.userInfo) {
    userStore.fetchUserInfo();
  }
});
</script>

<style scoped lang="scss">
.user-page {
  min-height: 100vh;
  background: $pet-bg;
}

/* 用户信息头部 */
.user-header {
  position: relative;
  padding: 60rpx $pet-space-base 80rpx;
  overflow: hidden;
}
.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 280rpx;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  border-radius: 0 0 $pet-radius-xl $pet-radius-xl;
}
.user-card {
  position: relative;
  display: flex;
  align-items: center;
  background: $pet-bg-white;
  border-radius: $pet-radius-lg;
  padding: $pet-space-base;
  box-shadow: $pet-shadow-base;
}
.avatar {
  width: 130rpx;
  height: 130rpx;
  border-radius: $pet-radius-circle;
  margin-right: $pet-space-base;
  border: 4rpx solid $pet-bg;
  background: $pet-bg-grey;
}
.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.user-name {
  font-size: $pet-font-xl;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: 10rpx;
}
.user-type {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
.arrow-right {
  font-size: $pet-font-lg;
  color: $pet-text-grey;
}

/* 快捷入口 */
.quick-menu {
  display: flex;
  justify-content: space-around;
  padding: $pet-space-base 0;
  margin: 0 $pet-space-base $pet-space-base;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}
.quick-icon {
  width: 90rpx;
  height: 90rpx;
  border-radius: $pet-radius-circle;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
}
.quick-icon.order { background: rgba($pet-primary, 0.12); }
.quick-icon.address { background: rgba($pet-blue, 0.12); }
.quick-icon.cart { background: rgba($pet-pink, 0.2); }
.quick-icon.home { background: rgba($pet-green, 0.12); }
.quick-text {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
}

/* 菜单列表 */
.menu-list {
  margin: 0 $pet-space-base;
  padding: 0 $pet-space-base;
}
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $pet-space-base 0;
  border-bottom: 1rpx solid $pet-border-light;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-left {
  display: flex;
  align-items: center;
}
.menu-icon {
  font-size: 36rpx;
  margin-right: $pet-space-sm;
}
.menu-text {
  font-size: $pet-font-base;
  color: $pet-text;
}
.menu-arrow {
  font-size: $pet-font-base;
  color: $pet-text-grey;
}

/* 底部信息 */
.footer-info {
  text-align: center;
  padding: 60rpx 0;
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
</style>