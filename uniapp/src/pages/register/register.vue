<template>
  <view class="container">
    <view class="form">
      <input
        class="input"
        v-model="form.userName"
        placeholder="请输入用户名"
      />
      <input
        class="input"
        v-model="form.nickName"
        placeholder="请输入昵称"
      />
      <input
        class="input"
        v-model="form.password"
        placeholder="请输入密码"
        password
      />
      <input
        class="input"
        v-model="form.email"
        placeholder="请输入邮箱"
      />
      <view class="code-row">
        <input
          class="input code-input"
          v-model="form.code"
          placeholder="请输入验证码"
        />
        <button class="btn-code" @click="sendCode" :disabled="counting">
          {{ counting ? count + 's' : '获取验证码' }}
        </button>
      </view>
      <!-- 模拟验证码提示：开发测试环境展示 -->
      <view v-if="mockCode" class="mock-code-tip">
        <text>模拟验证码：{{ mockCode }}</text>
        <text class="copy-link" @click="copyCode">复制</text>
      </view>
      <button class="btn-register" @click="handleRegister">注册</button>
    </view>

    <!-- 底部操作：去登录 -->
    <view class="actions">
      <text @click="goLogin">已有账号？去登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { register, sendEmailCodeForRegister } from '@/api/user.js';
import { showToast, navigateTo } from '@/utils/index.js';

// 响应式数据：注册表单、验证码倒计时、模拟验证码
const form = ref({
  userName: '',
  nickName: '',
  password: '',
  email: '',
  code: ''
});
const counting = ref(false);
const count = ref(60);
const mockCode = ref('');
let timer = null;

// 发送邮箱验证码并启动倒计时
const sendCode = async () => {
  if (!form.value.email) {
    showToast('请输入邮箱');
    return;
  }
  try {
    const res = await sendEmailCodeForRegister(form.value.email);
    if (res.data) {
      mockCode.value = res.data;
      form.value.code = res.data;
      showToast('验证码已生成，可直接复制使用');
    } else {
      showToast(res.message || '验证码生成失败');
    }
    counting.value = true;
    count.value = 60;
    timer = setInterval(() => {
      count.value--;
      if (count.value <= 0) {
        clearInterval(timer);
        counting.value = false;
      }
    }, 1000);
  } catch (e) {
    console.error(e);
  }
};

// 复制验证码到剪贴板
const copyCode = () => {
  uni.setClipboardData({
    data: mockCode.value,
    success: () => {
      showToast('验证码已复制');
    },
    fail: () => {
      showToast('复制失败');
    }
  });
};

// 注册：校验必填项后提交
const handleRegister = async () => {
  if (!form.value.userName || !form.value.password || !form.value.email || !form.value.code) {
    showToast('请填写完整信息');
    return;
  }
  try {
    await register(form.value, form.value.code);
    showToast('注册成功', 'success');
    setTimeout(() => {
      navigateTo('/pages/login/login');
    }, 800);
  } catch (e) {
    console.error(e);
  }
};

// 返回登录页
const goLogin = () => {
  uni.navigateBack();
};
</script>

<style scoped>
.container {
  padding: 60rpx 40rpx;
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
.code-row {
  display: flex;
  align-items: center;
}

/* 验证码输入框 */
.code-input {
  flex: 1;
  margin-right: 20rpx;
}
.btn-code {
  width: 220rpx;
  font-size: 26rpx;
  background: #f0f0f0;
  color: #333;
  border-radius: 10rpx;
}
.btn-code[disabled] {
  background: #ddd;
  color: #999;
}
.btn-register {
  margin-top: 40rpx;
  background: #4cd964;
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

.mock-code-tip {
  display: flex;
  align-items: center;
  gap: 20rpx;
  color: #ff9900;
  font-size: 28rpx;
  margin-bottom: 20rpx;
}

/* 复制链接 */
.copy-link {
  color: #007aff;
  text-decoration: underline;
}
</style>
