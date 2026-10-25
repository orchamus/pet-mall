<template>
  <view class="container">
    <!-- 分类卡片列表 -->
    <view class="category-list">
      <view
        class="category-card"
        v-for="item in categories"
        :key="item.value"
        @click="goCategory(item.value)"
      >
        <text class="category-icon">{{ item.icon }}</text>
        <text class="category-name">{{ item.label }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
// 分类数据：文案、值、图标
const categories = [
  { label: '宠物活体', value: '0', icon: '🐱' },
  { label: '周边用品', value: '1', icon: '🧸' },
  { label: '宠物食品', value: '2', icon: '🍖' },
  { label: '宠物玩具', value: '3', icon: '🎾' }
];

// 点击分类：跳转首页并缓存选中分类
const goCategory = (category) => {
  uni.switchTab({ url: '/pages/index/index' });
  uni.setStorageSync('selectedCategory', category);
};
</script>

<style scoped>
.container {
  padding: 20rpx;
}
.category-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

/* 分类卡片 */
.category-card {
  width: 48%;
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20rpx;
}
.category-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

/* 分类名称 */
.category-name {
  font-size: 30rpx;
  color: #333;
}
</style>
