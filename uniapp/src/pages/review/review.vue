<template>
  <view class="review-page">
    <!-- 评价内容输入卡片 -->
    <view class="review-card pet-card">
      <text class="review-label">评价内容（10-500字）</text>
      <textarea
        class="review-textarea"
        v-model="content"
        placeholder="分享您的购买体验，帮助更多铲屎官~"
        maxlength="500"
      />
      <text class="review-count">{{ content.length }}/500</text>
    </view>

    <!-- 操作按钮：取消 / 提交 -->
    <view class="review-actions">
      <view class="btn btn-cancel" @click="goBack">取消</view>
      <view class="btn btn-submit" :class="{ disabled: submitting }" @click="submit">提交评价</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addReview } from '@/api/reviews.js';
import { showToast, navigateBack } from '@/utils/index.js';

// 响应式数据：订单 ID、商品 ID、评价内容、提交中状态
const orderId = ref('');
const productId = ref(null);
const content = ref('');
const submitting = ref(false);

// 页面加载：读取订单与商品参数
onLoad((options) => {
  orderId.value = options.orderId || '';
  productId.value = options.productId ? Number(options.productId) : null;
});

// 返回上一页
const goBack = () => {
  navigateBack();
};

// 提交评价：校验长度后调用接口
const submit = async () => {
  if (submitting.value) return;
  const text = content.value.trim();
  if (text.length < 10 || text.length > 500) {
    showToast('评价内容长度需在 10-500 字符之间');
    return;
  }
  submitting.value = true;
  try {
    await addReview(orderId.value, productId.value, text);
    uni.$emit('reviewSubmitted', { orderId: orderId.value });
    showToast('评价成功', 'success');
    setTimeout(() => navigateBack(), 800);
  } catch (e) {
    console.error(e);
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped lang="scss">
.review-page {
  min-height: 100vh;
  background: $pet-bg;
  padding: $pet-space-sm;
}
.review-card {
  padding: $pet-space-base;
}

/* 评价输入区 */
.review-label {
  font-size: $pet-font-base;
  color: $pet-text;
  font-weight: 600;
  margin-bottom: $pet-space-sm;
  display: block;
}
.review-textarea {
  width: 100%;
  height: 300rpx;
  padding: $pet-space-sm;
  box-sizing: border-box;
  font-size: $pet-font-base;
  color: $pet-text;
  background: $pet-bg-grey;
  border-radius: $pet-radius-base;
}
.review-count {
  text-align: right;
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  margin-top: $pet-space-sm;
  display: block;
}
.review-actions {
  display: flex;
  gap: $pet-space-sm;
  margin-top: $pet-space-base;
}

/* 操作按钮 */
.btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: $pet-radius-lg;
  font-size: $pet-font-base;
  font-weight: 600;
}
.btn-cancel {
  background: #fff;
  color: $pet-text-secondary;
  border: 1rpx solid $pet-border;
}
.btn-submit {
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
}
.btn-submit.disabled {
  opacity: 0.6;
}
</style>
