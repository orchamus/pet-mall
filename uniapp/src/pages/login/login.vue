<template>
  <view class="container">
    <!-- Logo 与标题 -->
    <view class="logo-wrap">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">宠物商城</text>
    </view>

    <!-- 登录表单 -->
    <view class="form">
      <input
        class="input"
        v-model="form.userName"
        placeholder="请输入用户名"
      />
      <input
        class="input"
        v-model="form.password"
        placeholder="请输入密码"
        password
      />
      <button class="btn-login" @click="handleLogin">登录</button>
    </view>

    <!-- 底部操作：去注册 -->
    <view class="actions">
      <text @click="goRegister">还没有账号？立即注册</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/store/user.js';
import { showToast, switchTab } from '@/utils/index.js';

// 用户 Store
const userStore = useUserStore();

// 响应式数据：登录表单
const form = ref({
  userName: '',
  password: ''
});

// 登录：校验必填项后调用 Store 登录
const handleLogin = async () => {
  if (!form.value.userName) {
    showToast('请输入用户名');
    return;
  }
  if (!form.value.password) {
    showToast('请输入密码');
    return;
  }
  try {
    await userStore.login(form.value);
    showToast('登录成功', 'success');
    setTimeout(() => {
      switchTab('/pages/index/index');
    }, 800);
  } catch (e) {
    console.error(e);
  }
};

// 跳转注册页
const goRegister = () => {
  uni.navigateTo({ url: '/pages/register/register' });
};
</script>

<style scoped>
.container {
  padding: 60rpx 40rpx;
}
.logo-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

/* Logo */
.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 20rpx;
}
.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
}
.form {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
}

/* 输入框 */
.input {
  height: 90rpx;
  border-bottom: 1rpx solid #eee;
  margin-bottom: 20rpx;
  font-size: 30rpx;
}
.btn-login {
  margin-top: 40rpx;
  background: #007aff;
  color: #fff;
  border-radius: 50rpx;
}

/* 底部操作区 */
.actions {
  margin-top: 40rpx;
  text-align: center;
  color: #007aff;
  font-size: 28rpx;
}
</style>
