<template>
  <view class="video-list-page">
    <!-- 页面头部：返回与标题 -->
    <view class="video-header">
      <view class="header-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="header-title">萌宠视频</text>
      <view class="header-placeholder"></view>
    </view>

    <!-- 视频网格列表 -->
    <view class="video-grid">
      <view
        class="video-card pet-card"
        v-for="item in videoList"
        :key="item.videoId"
        @click="goDetail(item.videoId)"
      >
        <view class="video-cover-wrap">
          <image class="video-cover" :src="item.coverUrl" mode="aspectFill" />
          <view class="video-play">▶</view>
          <view class="video-tag" v-if="item.productId">关联商品</view>
        </view>
        <view class="video-info">
          <text class="video-title">{{ item.title }}</text>
          <text class="video-desc">{{ item.description }}</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="!loading && !videoList.length">
      <text class="empty-icon">🎬</text>
      <text class="empty-text">暂无视频内容</text>
    </view>

    <!-- 加载提示 -->
    <view class="loading-text" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getVideoList } from '@/api/video.js';
import { navigateBack, navigateTo, showToast } from '@/utils/index.js';

// 响应式数据：视频列表、加载状态
const videoList = ref([]);
const loading = ref(false);

// 返回上一页
const goBack = () => {
  navigateBack();
};

// 跳转视频详情页
const goDetail = (videoId) => {
  navigateTo('/pages/video/detail?videoId=' + videoId);
};

// 加载视频列表
const loadVideos = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const res = await getVideoList();
    videoList.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    console.error(e);
    showToast('获取视频失败', 'none');
  } finally {
    loading.value = false;
  }
};

// 页面挂载时加载视频列表
onMounted(() => {
  loadVideos();
});
</script>

<style scoped lang="scss">
.video-list-page {
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

.video-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: $pet-space-sm;
}

/* 视频卡片 */
.video-card {
  width: calc(50% - 10rpx);
  margin-bottom: $pet-space-base;
  overflow: hidden;
}

.video-cover-wrap {
  position: relative;
  width: 100%;
  height: 360rpx;
  background: $pet-bg-grey;
}

/* 视频封面与播放按钮 */
.video-cover {
  width: 100%;
  height: 100%;
}

.video-play {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80rpx;
  height: 80rpx;
  border-radius: $pet-radius-circle;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  padding-left: 6rpx;
}

.video-tag {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  background: $pet-primary;
  color: #fff;
  font-size: $pet-font-xs;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.video-info {
  padding: $pet-space-sm;
}

/* 视频标题与描述 */
.video-title {
  display: block;
  font-size: $pet-font-base;
  font-weight: 700;
  color: $pet-text;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.video-desc {
  display: block;
  font-size: $pet-font-xs;
  color: $pet-text-grey;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;
}

/* 空状态 */
.empty-icon {
  font-size: 100rpx;
  margin-bottom: $pet-space-sm;
}

.empty-text {
  font-size: $pet-font-base;
  color: $pet-text-grey;
}

.loading-text {
  text-align: center;
  padding: 40rpx 0;
  color: $pet-text-grey;
  font-size: $pet-font-sm;
}
</style>
