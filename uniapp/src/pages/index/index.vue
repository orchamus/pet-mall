<template>
  <view class="home-page">
    <!-- 顶部品牌栏：Logo 与名称 -->
    <view class="brand-header">
      <image class="brand-logo" src="/static/logo.png" mode="aspectFit" />
      <text class="brand-name">萌宠乐园</text>
    </view>

    <!-- 搜索栏：关键词搜索商品 -->
    <view class="search-box pet-card">
      <text class="search-icon">🔍</text>
      <input
        class="search-input"
        v-model="keyword"
        placeholder="搜索萌宠、周边好物"
        confirm-type="search"
        @confirm="onSearch"
      />
      <view class="search-btn" @click="onSearch">搜索</view>
    </view>

    <!-- 轮播 Banner：运营位入口 -->
    <swiper class="banner" indicator-dots autoplay circular indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#fff">
      <swiper-item>
        <view class="banner-item banner-hot">
          <view class="banner-content">
            <text class="banner-title">萌宠精选</text>
            <text class="banner-subtitle">把温暖带回家</text>
          </view>
          <view class="banner-decoration">🐾</view>
        </view>
      </swiper-item>
      <swiper-item>
        <view class="banner-item banner-new">
          <view class="banner-content">
            <text class="banner-title">周边好物</text>
            <text class="banner-subtitle">给毛孩子最好的</text>
          </view>
          <view class="banner-decoration">🦴</view>
        </view>
      </swiper-item>
    </swiper>

    <!-- 快捷分类入口 -->
    <view class="category-section pet-card">
      <view
        class="category-item"
        :class="{ active: currentCategory === '' }"
        @click="selectCategory('')"
      >
        <view class="category-icon icon-all">🐾</view>
        <text class="category-name">全部</text>
      </view>
      <view
        class="category-item"
        :class="{ active: currentCategory === '0' }"
        @click="selectCategory('0')"
      >
        <view class="category-icon icon-pet">🐱</view>
        <text class="category-name">宠物活体</text>
      </view>
      <view
        class="category-item"
        :class="{ active: currentCategory === '1' }"
        @click="selectCategory('1')"
      >
        <view class="category-icon icon-goods">🧸</view>
        <text class="category-name">周边用品</text>
      </view>
      <view
        class="category-item"
        @click="goNearby"
      >
        <view class="category-icon icon-nearby">📍</view>
        <text class="category-name">附近门店</text>
      </view>
      <view
        class="category-item"
        @click="goVideo"
      >
        <view class="category-icon icon-video">🎬</view>
        <text class="category-name">萌宠视频</text>
      </view>
    </view>

    <!-- 限时活动入口：宠物专区 / 周边爆款 -->
    <view class="promo-section">
      <view class="promo-card promo-sale" @click="selectCategory('0')">
        <text class="promo-title">宠物专区</text>
        <text class="promo-desc">健康可爱 等你领养</text>
      </view>
      <view class="promo-card promo-new" @click="selectCategory('1')">
        <text class="promo-title">周边爆款</text>
        <text class="promo-desc">精选好物 限时优惠</text>
      </view>
    </view>

    <!-- 热门商品列表 -->
    <view class="section-header">
      <view class="section-title">
        <view class="title-bar"></view>
        <text>热门商品</text>
      </view>
      <text class="section-more" @click="selectCategory('')">查看全部 ></text>
    </view>

    <!-- 商品卡片列表 -->
    <view class="product-list">
      <view
        class="product-card pet-card"
        v-for="item in productList"
        :key="item.productId"
        @click="goDetail(item.productId)"
      >
        <view class="product-image-wrap">
          <image
            class="product-image"
            :src="getProductImage(item)"
            mode="aspectFill"
            @error="handleImageError"
          />
          <view class="product-tag" v-if="item.stockQuantity <= 5">仅剩 {{ item.stockQuantity }} 件</view>
        </view>
        <view class="product-info">
          <view class="product-tags">
            <text class="pet-tag pet-tag-pink" v-if="item.productType === '0'">萌宠</text>
            <text class="pet-tag pet-tag-green" v-else>周边</text>
          </view>
          <text class="product-name">{{ item.name }}</text>
          <text class="product-desc">{{ item.description }}</text>
          <view class="product-bottom">
            <view class="price-wrap">
              <text class="price-symbol">¥</text>
              <text class="product-price">{{ formatPrice(item.price) }}</text>
            </view>
            <view class="add-cart" @click.stop="handleAddToCart(item)">
              <text class="add-cart-icon">🛒</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view class="loading-text" v-if="loading">
      <view class="loading-dot"></view>
      <view class="loading-dot"></view>
      <view class="loading-dot"></view>
      <text>加载中</text>
    </view>
    <!-- 到底提示 -->
    <view class="finished-text" v-if="finished && productList.length">已经到底啦 ~</view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="!loading && !productList.length">
      <text class="empty-icon">🐶</text>
      <text class="empty-text">暂无相关商品</text>
      <view class="empty-btn" @click="selectCategory('')">去逛逛</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { getAllProductList, getProductBySearch } from '@/api/products.js';
import { addToCart } from '@/api/cart.js';
import { formatPrice, getProductImage, navigateTo } from '@/utils/index.js';

// 分类配置（用于首页快捷入口展示）
const categories = [
  { label: '全部', value: '', icon: '🐾' },
  { label: '宠物活体', value: '0', icon: '🐱' },
  { label: '周边用品', value: '1', icon: '🧸' }
];

// 响应式数据：搜索关键词、商品列表、分页信息、加载状态、是否到底、当前分类
const keyword = ref('');
const productList = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const finished = ref(false);
const currentCategory = ref('');

// 加载商品列表：支持搜索与分类筛选，支持重置分页
const loadProducts = async (reset = false) => {
  if (loading.value) return;
  loading.value = true;
  if (reset) {
    pageNum.value = 1;
    productList.value = [];
    finished.value = false;
  }
  try {
    let res;
    if (keyword.value) {
      res = await getProductBySearch(pageNum.value, pageSize.value, keyword.value);
    } else {
      res = await getAllProductList(pageNum.value, pageSize.value, currentCategory.value);
    }
    const records = res.data.records || [];
    productList.value.push(...records);
    if (records.length < pageSize.value) {
      finished.value = true;
    }
  } catch (e) {
    console.error(e);
    uni.showToast({ title: '加载失败', icon: 'none' });
  } finally {
    loading.value = false;
    uni.stopPullDownRefresh();
  }
};

// 搜索商品
const onSearch = () => {
  loadProducts(true);
};

// 选择分类：清空关键词并重新加载
const selectCategory = (value) => {
  currentCategory.value = value;
  keyword.value = '';
  loadProducts(true);
};

// 跳转商品详情
const goDetail = (productId) => {
  navigateTo('/pages/product/detail?productId=' + productId);
};

// 跳转附近门店
const goNearby = () => {
  navigateTo('/pages/nearby/nearby');
};

// 跳转视频列表
const goVideo = () => {
  navigateTo('/pages/video/list');
};

// 商品卡片加入购物车
const handleAddToCart = async (item) => {
  try {
    await addToCart({ productId: item.productId }, 1);
    uni.showToast({ title: '已加入购物车', icon: 'success' });
  } catch (e) {
    console.error(e);
    uni.showToast({ title: '加入失败', icon: 'none' });
  }
};

// 图片加载失败时使用默认 logo
const handleImageError = (e) => {
  e.currentTarget.src = '/static/logo.png';
};

// 下拉刷新：重置并加载商品
onPullDownRefresh(() => {
  loadProducts(true);
});

// 上拉加载更多：分页加载
onReachBottom(() => {
  if (!finished.value) {
    pageNum.value++;
    loadProducts();
  }
});

// 页面显示时：处理分类页跳转带来的分类选择
onShow(() => {
  const selected = uni.getStorageSync('selectedCategory');
  if (selected !== '' && selected !== currentCategory.value) {
    currentCategory.value = selected;
    keyword.value = '';
    loadProducts(true);
    uni.removeStorageSync('selectedCategory');
  }
});

// 页面挂载时加载商品
onMounted(() => {
  loadProducts(true);
});
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  padding: $pet-space-sm;
  padding-bottom: calc($pet-space-lg + env(safe-area-inset-bottom));
}

/* 品牌头部 */
.brand-header {
  display: flex;
  align-items: center;
  gap: $pet-space-xs;
  margin-bottom: $pet-space-sm;
}

.brand-logo {
  width: 68rpx;
  height: 68rpx;
  border-radius: $pet-radius-circle;
  background: transparent;
}

.brand-name {
  font-size: $pet-font-xl;
  font-weight: 800;
  color: $pet-primary;
  letter-spacing: 2rpx;
}

/* 搜索栏 */
.search-box {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  margin-bottom: $pet-space-sm;
}

.search-icon {
  font-size: $pet-font-base;
  margin-right: $pet-space-xs;
  opacity: 0.6;
}

.search-input {
  flex: 1;
  height: 48rpx;
  font-size: $pet-font-base;
  color: $pet-text;
}

.search-btn {
  padding: 10rpx 24rpx;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  font-size: $pet-font-sm;
  border-radius: 28rpx;
  font-weight: 600;
}

/* Banner */
.banner {
  height: 320rpx;
  border-radius: $pet-radius-base;
  overflow: hidden;
  margin-bottom: $pet-space-sm;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 48rpx;
  color: #fff;
  position: relative;
}

.banner-hot {
  background: linear-gradient(135deg, #FF9A56 0%, #FF7A45 100%);
}

.banner-new {
  background: linear-gradient(135deg, #5B8FF9 0%, #9C6ADE 100%);
}

.banner-content {
  display: flex;
  flex-direction: column;
  z-index: 1;
}

.banner-title {
  font-size: $pet-font-xxl;
  font-weight: 800;
  margin-bottom: 12rpx;
  text-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.banner-subtitle {
  font-size: $pet-font-base;
  opacity: 0.95;
}

.banner-decoration {
  font-size: 120rpx;
  opacity: 0.25;
  position: absolute;
  right: 40rpx;
  bottom: -10rpx;
}

/* 分类 */
.category-section {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  padding: $pet-space-base 0;
  margin-bottom: $pet-space-sm;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 20%;
  transition: transform 0.2s;
  margin-bottom: $pet-space-sm;
}

.category-item:active {
  transform: scale(0.95);
}

.category-item.active .category-name {
  color: $pet-primary;
  font-weight: 700;
}

.category-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: $pet-radius-circle;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-bottom: 12rpx;
  box-shadow: $pet-shadow-sm;
}

.icon-all {
  background: linear-gradient(135deg, #FFF5F0, #FFDDD1);
}

.icon-pet {
  background: linear-gradient(135deg, #FFF0F3, #FFD6DD);
}

.icon-goods {
  background: linear-gradient(135deg, #F0FFF0, #D6F5D6);
}

.icon-nearby {
  background: linear-gradient(135deg, #FFF8E1, #FFECB3);
}

.icon-video {
  background: linear-gradient(135deg, #F3E5F5, #E1BEE7);
}

.category-name {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
}

/* 促销入口 */
.promo-section {
  display: flex;
  gap: $pet-space-sm;
  margin-bottom: $pet-space-base;
}

.promo-card {
  flex: 1;
  border-radius: $pet-radius-base;
  padding: $pet-space-base;
  display: flex;
  flex-direction: column;
  color: #fff;
  box-shadow: $pet-shadow-base;
}

.promo-sale {
  background: linear-gradient(135deg, #FF7A45, #FFB08A);
}

.promo-new {
  background: linear-gradient(135deg, #4CAF50, #81C784);
}

.promo-title {
  font-size: $pet-font-lg;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.promo-desc {
  font-size: $pet-font-xs;
  opacity: 0.95;
}

/* 区块标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $pet-space-sm;
}

.section-title {
  display: flex;
  align-items: center;
  gap: $pet-space-xs;
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
}

.title-bar {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, $pet-primary, $pet-primary-light);
  border-radius: 4rpx;
}

.section-more {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}

/* 商品列表 */
.product-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.product-card {
  width: calc(50% - 10rpx);
  margin-bottom: $pet-space-base;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:active {
  transform: translateY(-4rpx);
  box-shadow: $pet-shadow-lg;
}

.product-image-wrap {
  position: relative;
  width: 100%;
  height: 320rpx;
  background: $pet-bg-grey;
}

.product-image {
  width: 100%;
  height: 100%;
}

.product-tag {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  background: $pet-error;
  color: #fff;
  font-size: $pet-font-xs;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.product-info {
  padding: $pet-space-sm;
}

.product-tags {
  display: flex;
  gap: 8rpx;
  margin-bottom: 10rpx;
}

.product-name {
  display: block;
  font-size: $pet-font-base;
  font-weight: 600;
  color: $pet-text;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 76rpx;
}

.product-desc {
  display: block;
  font-size: $pet-font-xs;
  color: $pet-text-grey;
  margin-top: 6rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: $pet-space-sm;
}

.price-wrap {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: $pet-font-sm;
  color: $pet-error;
  font-weight: 700;
}

.product-price {
  font-size: $pet-font-lg;
  color: $pet-error;
  font-weight: 800;
}

.add-cart {
  width: 52rpx;
  height: 52rpx;
  border-radius: $pet-radius-circle;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 16rpx rgba($pet-primary, 0.3);
}

.add-cart-icon {
  font-size: 26rpx;
}

/* 加载与空状态 */
.loading-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  color: $pet-text-grey;
  font-size: $pet-font-sm;
  padding: $pet-space-base 0;
}

.loading-dot {
  width: 12rpx;
  height: 12rpx;
  background: $pet-primary;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.finished-text {
  text-align: center;
  color: $pet-text-grey;
  font-size: $pet-font-sm;
  padding: $pet-space-base 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: $pet-space-sm;
}

.empty-text {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  margin-bottom: $pet-space-base;
}

.empty-btn {
  padding: 16rpx 48rpx;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  font-size: $pet-font-base;
  border-radius: $pet-radius-lg;
  font-weight: 600;
}
</style>
