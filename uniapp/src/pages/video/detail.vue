<template>
  <view class="video-detail-page">
    <!-- 页面头部：返回与标题 -->
    <view class="video-header">
      <view class="header-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="header-title">视频详情</text>
      <view class="header-placeholder"></view>
    </view>

    <!-- 视频播放器 -->
    <view class="video-player-wrap">
      <video
        v-if="video.videoUrl"
        class="video-player"
        :src="video.videoUrl"
        :poster="video.coverUrl"
        controls
        autoplay
        object-fit="contain"
      ></video>
    </view>

    <!-- 视频标题与描述 -->
    <view class="video-meta pet-card">
      <text class="meta-title">{{ video.title || '加载中' }}</text>
      <text class="meta-desc">{{ video.description || '' }}</text>
    </view>

    <!-- 视频同款商品入口 -->
    <view class="product-section" v-if="video.productId">
      <view class="section-title">
        <view class="title-bar"></view>
        <text>视频同款</text>
      </view>
      <view class="product-card pet-card" @click="goProduct(video.productId)">
        <image
          class="product-image"
          :src="video.coverUrl"
          mode="aspectFill"
        />
        <view class="product-info">
          <text class="product-name">{{ video.productName || '萌宠好物' }}</text>
          <text class="product-price">¥{{ formatPrice(video.productPrice) }}</text>
        </view>
        <view class="product-arrow">›</view>
      </view>
    </view>

    <!-- 加载提示 -->
    <view class="loading-text" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getVideoDetail } from '@/api/video.js';
import { navigateBack, navigateTo, showToast, formatPrice } from '@/utils/index.js';

// 响应式数据：视频详情、加载状态
const video = ref({});
const loading = ref(false);

// 返回上一页
const goBack = () => {
  navigateBack();
};

// 跳转视频关联商品详情
const goProduct = (productId) => {
  if (!productId) return;
  navigateTo('/pages/product/detail?productId=' + productId);
};

// 加载视频详情
const loadDetail = async (videoId) => {
  if (!videoId) return;
  loading.value = true;
  try {
    const res = await getVideoDetail(videoId);
    video.value = res.data || {};
  } catch (e) {
    console.error(e);
    showToast('获取视频失败', 'none');
  } finally {
    loading.value = false;
  }
};

// 页面加载：读取视频 ID 并加载详情
onLoad((options) => {
  const videoId = options && options.videoId;
  if (videoId) {
    loadDetail(Number(videoId));
  }
});
</script>

<style scoped lang="scss">
.video-detail-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc($pet-space-lg + env(safe-area-inset-bottom));
}

.video-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 $pet-space-sm;
  background: $pet-bg-white;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

/* 头部返回与占位 */
.header-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 52rpx;
  color: $pet-text;
  line-height: 1;
}

.header-title {
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
}

.header-placeholder {
  width: 60rpx;
}

.video-player-wrap {
  width: 100%;
  height: 420rpx;
  background: #000;
}

/* 视频播放器 */
.video-player {
  width: 100%;
  height: 100%;
}

.video-meta {
  margin: $pet-space-sm;
  padding: $pet-space-base;
}

/* 视频标题与描述 */
.meta-title {
  display: block;
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: 12rpx;
}

.meta-desc {
  display: block;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  line-height: 1.5;
}

.product-section {
  margin: 0 $pet-space-sm;
}

/* 区块标题 */
.section-title {
  display: flex;
  align-items: center;
  gap: $pet-space-xs;
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: $pet-space-sm;
}

.title-bar {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, $pet-primary, $pet-primary-light);
  border-radius: 4rpx;
}

.product-card {
  display: flex;
  align-items: center;
  padding: $pet-space-sm;
}

/* 关联商品卡片 */
.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: $pet-radius-base;
  background: $pet-bg-grey;
  margin-right: $pet-space-sm;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  display: block;
  font-size: $pet-font-base;
  font-weight: 600;
  color: $pet-text;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: block;
  font-size: $pet-font-base;
  color: $pet-primary;
  font-weight: 700;
}

.product-arrow {
  font-size: $pet-font-lg;
  color: $pet-text-grey;
  margin-left: $pet-space-sm;
}

.loading-text {
  text-align: center;
  padding: 40rpx 0;
  color: $pet-text-grey;
  font-size: $pet-font-sm;
}
</style>
