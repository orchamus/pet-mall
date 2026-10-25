<template>
  <view class="detail-page">
    <!-- 加载状态：数据请求中展示 -->
    <view class="loading-state" v-if="loading">
      <view class="loading-dot"></view>
      <view class="loading-dot"></view>
      <view class="loading-dot"></view>
      <text>加载中</text>
    </view>

    <!-- 商品详情主体 -->
    <view class="detail-body" v-else-if="product">
      <!-- 商品主图轮播 -->
      <view class="gallery-wrap">
        <swiper class="gallery-swiper" indicator-dots autoplay circular indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#fff" @change="onSwiperChange">
          <swiper-item v-for="(img, idx) in productImages" :key="idx">
            <image
              class="gallery-image"
              :src="img"
              mode="aspectFill"
              @error="handleImageError"
            />
          </swiper-item>
        </swiper>
        <view class="image-counter" v-if="productImages.length > 1">{{ currentImage + 1 }} / {{ productImages.length }}</view>
      </view>

      <!-- 价格、标题与库存状态 -->
      <view class="info-card pet-card">
        <view class="price-row">
          <view class="price-wrap">
            <text class="price-symbol">¥</text>
            <text class="product-price">{{ formatPrice(product.price) }}</text>
          </view>
          <view class="stock-tag" :class="stockClass">
            {{ stockText }}
          </view>
        </view>
        <view class="product-tags">
          <text class="pet-tag" :class="typeTagClass">{{ typeText }}</text>
          <text v-if="product.status === '1'" class="pet-tag pet-tag-danger">已下架</text>
        </view>
        <text class="product-name">{{ product.name }}</text>
        <text class="product-desc">{{ product.description }}</text>
      </view>

      <!-- 商品规格信息（宠物/用品字段差异化展示） -->
      <view class="info-card pet-card">
        <view class="section-title">
          <view class="title-bar"></view>
          <text>商品信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">库存</text>
          <text class="info-value">{{ product.stockQuantity }} 件</text>
        </view>
        <view class="info-row" v-if="isPet && product.pets">
          <text class="info-label">品种</text>
          <text class="info-value">{{ breedText }}</text>
        </view>
        <view class="info-row" v-if="isPet && product.pets">
          <text class="info-label">性别</text>
          <text class="info-value">{{ genderText }}</text>
        </view>
        <view class="info-row" v-if="isPet && product.pets">
          <text class="info-label">健康状况</text>
          <text class="info-value">{{ healthText }}</text>
        </view>
        <view class="info-row" v-if="isSupply && product.supplies">
          <text class="info-label">重量</text>
          <text class="info-value">{{ supplyWeight }}</text>
        </view>
        <view class="info-row" v-if="isSupply && product.supplies">
          <text class="info-label">尺寸</text>
          <text class="info-value">{{ supplySize }}</text>
        </view>
        <view class="info-row" v-if="isSupply && product.supplies">
          <text class="info-label">材质</text>
          <text class="info-value">{{ supplyMaterial }}</text>
        </view>
        <view class="info-row" v-if="isSupply && product.supplies">
          <text class="info-label">适用宠物</text>
          <text class="info-value">{{ supplyTarget }}</text>
        </view>
      </view>

      <!-- 店铺信息卡片 -->
      <view class="shop-card pet-card" v-if="shop" @click="goShop">
        <view class="shop-header">
          <view class="shop-icon">🏪</view>
          <view class="shop-main">
            <text class="shop-name">{{ shopNameText }}</text>
            <text class="shop-address">{{ shopAddressText }}</text>
          </view>
          <text class="shop-arrow">></text>
        </view>
        <text class="shop-desc">{{ shopDescText }}</text>
      </view>

      <!-- 商品介绍 -->
      <view class="info-card pet-card">
        <view class="section-title">
          <view class="title-bar"></view>
          <text>商品介绍</text>
        </view>
        <text class="intro-text">{{ product.description || '暂无商品介绍' }}</text>
      </view>

      <!-- 商品评价列表 -->
      <view class="info-card pet-card" v-if="reviews.length">
        <view class="section-title">
          <view class="title-bar"></view>
          <text>商品评价</text>
        </view>
        <view class="review-item" v-for="item in reviews" :key="item.reviewId">
          <view class="review-header">
            <image class="review-avatar" :src="item.user?.avatar || '/static/logo.png'" mode="aspectFill" />
            <text class="review-name">{{ item.user?.userName || '匿名用户' }}</text>
          </view>
          <text class="review-content">{{ item.content }}</text>
          <text class="review-time">{{ formatDate(item.createdAt) }}</text>
        </view>
      </view>

      <!-- 底部占位：避免内容被固定底部遮挡 -->
      <view class="bottom-placeholder"></view>

      <!-- 底部操作栏：首页、购物车、数量选择、购买 -->
      <view class="action-bar safe-area-bottom">
        <view class="action-left">
          <view class="action-icon" @click="goHome">
            <text class="icon-text">🏠</text>
            <text class="icon-label">首页</text>
          </view>
          <view class="action-icon" @click="goCart">
            <text class="icon-text">🛒</text>
            <text class="icon-label">购物车</text>
          </view>
        </view>
        <view class="action-right">
          <view class="quantity-box">
            <text class="qty-btn minus" :class="{ disabled: quantity <= 1 }" @click="decrease">-</text>
            <input
              class="qty-input"
              type="number"
              v-model.number="quantity"
              :disabled="product.stockQuantity <= 0 || product.status === '1'"
              @blur="validateQuantity"
            />
            <text class="qty-btn plus" :class="{ disabled: quantity >= product.stockQuantity }" @click="increase">+</text>
          </view>
          <view class="btn-cart pet-btn-secondary" :class="{ disabled: !canBuy }" @click="addToCart">
            <text>加入购物车</text>
          </view>
          <view class="btn-buy pet-btn-primary" :class="{ disabled: !canBuy }" @click="buyNow">
            <text>立即购买</text>
          </view>
        </view>
      </view>

    </view>

    <!-- 错误/空状态：商品不存在或已下架 -->
    <view class="empty-state" v-else>
      <view class="empty-icon">🐾</view>
      <text class="empty-text">商品不存在或已下架</text>
      <view class="pet-btn-primary empty-btn" @click="goBack">返回首页</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { getAProductById } from '@/api/products.js';
import { getProductReviews } from '@/api/reviews.js';
import { addToCart as addToCartApi } from '@/api/cart.js';
import { formatPrice, getProductImage, showToast, navigateTo, switchTab } from '@/utils/index.js';
import { normalizeBreed, normalizeHealthStatus, normalizeShopName, normalizeShopDesc, normalizeAddress, normalizeSupplyValue } from '@/utils/text.js';
import { savePaymentSession } from '@/utils/payment.js';

// 响应式数据：商品 ID、商品详情、店铺信息、购买数量、加载状态、评价分页
const productId = ref(null);
const product = ref(null);
const shop = ref(null);
const quantity = ref(1);
const loading = ref(true);
const currentImage = ref(0);
const reviews = ref([]);
const reviewPage = ref({ pageNum: 1, pageSize: 10, total: 0 });

// 计算属性：商品主图列表，无多图时取默认图
const productImages = computed(() => {
  if (!product.value || !Array.isArray(product.value.productMedias) || !product.value.productMedias.length) {
    return [getProductImage(product.value)];
  }
  return product.value.productMedias.map(item => {
    const media = item.media || item;
    return getProductImage({ productMedias: [{ media: { url: media.url || item.mediaUrl } }] });
  });
});

// 计算属性：商品分类、是否为宠物/用品
const category = computed(() => product.value?.category ?? product.value?.productType ?? '');
const isPet = computed(() => category.value === '0' || category.value === 0);
const isSupply = computed(() => category.value === '1' || category.value === 1);

// 计算属性：类型文案与标签样式
const typeText = computed(() => isPet.value ? '宠物活体' : '周边用品');
const typeTagClass = computed(() => isPet.value ? 'pet-tag-pink' : 'pet-tag-green');

// 计算属性：商品是否可购买
const canBuy = computed(() => {
  return product.value && product.value.status !== '1' && product.value.stockQuantity > 0;
});

// 计算属性：库存状态文案
const stockText = computed(() => {
  if (product.value?.status === '1') return '已下架';
  if (!product.value || product.value.stockQuantity <= 0) return '暂时缺货';
  if (product.value.stockQuantity <= 5) return '仅剩 ' + product.value.stockQuantity + ' 件';
  return '有货';
});

// 计算属性：库存状态标签样式
const stockClass = computed(() => {
  if (product.value?.status === '1') return 'tag-grey';
  if (!product.value || product.value.stockQuantity <= 0) return 'tag-grey';
  if (product.value.stockQuantity <= 5) return 'tag-warning';
  return 'tag-success';
});

// 计算属性：宠物性别文案
const genderText = computed(() => {
  if (!product.value?.pets) return '-';
  const g = product.value.pets.gender;
  if (g === '0' || g === 0) return '公';
  if (g === '1' || g === 1) return '母';
  return '公母皆有';
});

// 计算属性：品种、健康、店铺、用品规格等展示文案
const breedText = computed(() => normalizeBreed(product.value));
const healthText = computed(() => normalizeHealthStatus(product.value?.pets?.healthStatus));
const shopNameText = computed(() => normalizeShopName(shop.value?.shopName));
const shopDescText = computed(() => normalizeShopDesc(shop.value?.description));
const shopAddressText = computed(() => normalizeAddress(shop.value?.address));
const supplyWeight = computed(() => {
  const w = normalizeSupplyValue(product.value?.supplies?.weightKg, '');
  return w ? w + ' kg' : '暂无';
});
const supplySize = computed(() => normalizeSupplyValue(product.value?.supplies?.size));
const supplyMaterial = computed(() => normalizeSupplyValue(product.value?.supplies?.material));
const supplyTarget = computed(() => normalizeSupplyValue(product.value?.supplies?.targetSpecies));

// 加载商品详情与评价
const loadProduct = async () => {
  loading.value = true;
  try {
    const res = await getAProductById(productId.value);
    const data = res.data;
    if (data && data.product) {
      product.value = data.product;
      shop.value = data.shop;
      quantity.value = 1;
      await loadReviews();
    } else {
      product.value = null;
      reviews.value = [];
    }
  } catch (e) {
    console.error(e);
    product.value = null;
    reviews.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载商品评价列表
const loadReviews = async () => {
  if (!product.value?.productId) return;
  try {
    const res = await getProductReviews(product.value.productId, reviewPage.value.pageNum, reviewPage.value.pageSize);
    reviews.value = res.data?.records || [];
    reviewPage.value.total = res.data?.total || 0;
  } catch (e) {
    console.error(e);
    reviews.value = [];
  }
};

// 图片加载失败时使用默认 logo
const handleImageError = (e) => {
  e.currentTarget.src = '/static/logo.png';
};

// 轮播图切换时更新当前索引
const onSwiperChange = (e) => {
  currentImage.value = e.detail?.current || 0;
};

// 增加购买数量，受库存上限限制
const increase = () => {
  if (!canBuy.value) return;
  if (quantity.value < product.value.stockQuantity) {
    quantity.value++;
  } else {
    showToast('已达到最大库存');
  }
};

// 减少购买数量，最低为 1
const decrease = () => {
  if (!canBuy.value) return;
  if (quantity.value > 1) {
    quantity.value--;
  }
};

// 数量输入失焦校验，非法值自动修正为有效范围
const validateQuantity = () => {
  if (!product.value) return;
  let qty = parseInt(quantity.value) || 1;
  if (qty < 1) qty = 1;
  if (product.value.stockQuantity > 0 && qty > product.value.stockQuantity) {
    qty = product.value.stockQuantity;
    showToast('超出库存，已调整数量');
  }
  quantity.value = qty;
};

// 加入购物车
const addToCart = async () => {
  if (!canBuy.value) {
    showToast('商品暂时无法购买');
    return;
  }
  try {
    await addToCartApi({ productId: product.value.productId }, quantity.value);
    showToast('已加入购物车', 'success');
  } catch (e) {
    console.error(e);
    showToast('加入购物车失败', 'none');
  }
};

// 立即购买：保存支付会话并跳转结算页
const buyNow = () => {
  if (!canBuy.value) {
    showToast('商品暂时无法购买');
    return;
  }
  validateQuantity();
  if (quantity.value > product.value.stockQuantity) {
    showToast('超出库存数量');
    return;
  }

  savePaymentSession({
    from: 'detail',
    items: [{
      productId: product.value.productId,
      quantity: quantity.value,
      name: product.value.name || '商品',
      price: product.value.price || 0,
      image: getProductImage(product.value)
    }],
    total: (product.value.price || 0) * quantity.value
  });

  navigateTo('/pages/order/settlement?from=detail');
};

// 跳转首页
const goHome = () => {
  switchTab('/pages/index/index');
};

// 跳转购物车
const goCart = () => {
  switchTab('/pages/cart/cart');
};

// 跳转店铺详情
const goShop = () => {
  if (shop.value?.shopId) {
    navigateTo('/pages/shop/shop?shopId=' + shop.value.shopId);
  }
};

// 返回首页
const goBack = () => {
  switchTab('/pages/index/index');
};

// 页面加载：获取商品 ID 并加载详情
onLoad((options) => {
  productId.value = options.productId;
  loadProduct();
});

// 页面显示时刷新商品详情
onShow(() => {
  if (productId.value) loadProduct();
});

// 页面挂载：若未加载则补充加载
onMounted(() => {
  if (productId.value && !product.value) loadProduct();
});
</script>

<style scoped lang="scss">
.detail-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

/* 加载状态 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding-top: 400rpx;
  color: $pet-text-grey;
  font-size: $pet-font-sm;
}
.loading-dot {
  width: 14rpx;
  height: 14rpx;
  background: $pet-primary;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}
.loading-dot:nth-child(2) { animation-delay: 0.16s; }
.loading-dot:nth-child(3) { animation-delay: 0.32s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 商品图 */
.gallery-wrap {
  position: relative;
  background: $pet-bg-grey;
}
.gallery-swiper {
  width: 100%;
  height: 600rpx;
}
.gallery-image {
  width: 100%;
  height: 100%;
}
.image-counter {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: $pet-font-xs;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

/* 信息卡片 */
.info-card {
  margin: $pet-space-sm;
  padding: $pet-space-base;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $pet-space-sm;
}
.price-wrap {
  display: flex;
  align-items: baseline;
}
.price-symbol {
  font-size: $pet-font-lg;
  color: $pet-error;
  font-weight: 700;
}
.product-price {
  color: $pet-error;
  font-size: $pet-font-xxl;
  font-weight: 800;
  margin-left: 4rpx;
}

.stock-tag {
  font-size: $pet-font-xs;
  padding: 6rpx 14rpx;
  border-radius: $pet-radius-sm;
  font-weight: 600;
}
.tag-success {
  background: rgba($pet-success, 0.12);
  color: $pet-success;
}
.tag-warning {
  background: rgba($pet-warning, 0.15);
  color: #D48806;
}
.tag-grey {
  background: $pet-bg-grey;
  color: $pet-text-grey;
}

.product-tags {
  display: flex;
  gap: 10rpx;
  margin-bottom: $pet-space-sm;
}
.product-name {
  display: block;
  font-size: $pet-font-xl;
  font-weight: 700;
  color: $pet-text;
  line-height: 1.4;
  margin-bottom: 10rpx;
}
.product-desc {
  display: block;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  line-height: 1.5;
}

/* 区块标题 */
.section-title {
  display: flex;
  align-items: center;
  gap: $pet-space-xs;
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: $pet-space-base;
}
.title-bar {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, $pet-primary, $pet-primary-light);
  border-radius: 4rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $pet-border-light;
}
.info-row:last-child {
  border-bottom: none;
}
.info-label {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
}
.info-value {
  font-size: $pet-font-base;
  color: $pet-text;
  font-weight: 500;
}

/* 店铺卡片 */
.shop-card {
  margin: $pet-space-sm;
  padding: $pet-space-base;
}
.shop-header {
  display: flex;
  align-items: center;
  gap: $pet-space-sm;
}
.shop-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: $pet-radius-circle;
  background: linear-gradient(135deg, $pet-primary-soft, $pet-bg-hover);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}
.shop-main {
  flex: 1;
  min-width: 0;
}
.shop-name {
  display: block;
  font-size: $pet-font-base;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: 6rpx;
}
.shop-address {
  display: block;
  font-size: $pet-font-xs;
  color: $pet-text-grey;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.shop-arrow {
  font-size: $pet-font-lg;
  color: $pet-text-grey;
}
.shop-desc {
  display: block;
  margin-top: $pet-space-sm;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  line-height: 1.5;
}

/* 商品介绍 */
.intro-text {
  display: block;
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  line-height: 1.7;
}

.bottom-placeholder {
  height: 140rpx;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $pet-bg-white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx $pet-space-base;
  box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 100;
}
.action-left {
  display: flex;
  align-items: center;
  gap: $pet-space-base;
}
.action-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}
.icon-text {
  font-size: 40rpx;
}
.icon-label {
  font-size: $pet-font-xs;
  color: $pet-text-secondary;
}
.action-right {
  display: flex;
  align-items: center;
  gap: $pet-space-sm;
}

.quantity-box {
  display: flex;
  align-items: center;
  border: 1rpx solid $pet-border;
  border-radius: $pet-radius-sm;
  overflow: hidden;
}
.qty-btn {
  width: 52rpx;
  height: 56rpx;
  line-height: 52rpx;
  text-align: center;
  font-size: $pet-font-lg;
  color: $pet-text-secondary;
  background: $pet-bg-grey;
}
.qty-btn.disabled {
  color: $pet-text-disable;
}
.qty-btn.minus {
  border-right: 1rpx solid $pet-border;
}
.qty-btn.plus {
  border-left: 1rpx solid $pet-border;
  color: $pet-primary;
}
.qty-input {
  width: 72rpx;
  height: 56rpx;
  text-align: center;
  font-size: $pet-font-base;
  color: $pet-text;
  background: $pet-bg-white;
}

.btn-cart {
  height: 72rpx;
  padding: 0 28rpx;
  border-radius: $pet-radius-lg;
  font-size: $pet-font-sm;
  font-weight: 600;
}
.btn-buy {
  height: 72rpx;
  padding: 0 28rpx;
  border-radius: $pet-radius-lg;
  font-size: $pet-font-sm;
  font-weight: 600;
}

.pet-btn-secondary {
  background: linear-gradient(135deg, $pet-primary-light, $pet-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pet-btn-secondary.disabled,
.pet-btn-primary.disabled {
  opacity: 0.5;
  pointer-events: none;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 300rpx;
  color: $pet-text-grey;
}
.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
}
.empty-text {
  font-size: $pet-font-lg;
  color: $pet-text;
  font-weight: 600;
  margin-bottom: $pet-space-base;
}
.empty-btn {
  width: 280rpx;
  height: 80rpx;
  font-size: $pet-font-base;
}

/* 商品评价 */
.review-item {
  padding: $pet-space-sm 0;
  border-bottom: 1rpx solid $pet-border-light;
}
.review-item:last-child {
  border-bottom: none;
}
.review-header {
  display: flex;
  align-items: center;
  gap: $pet-space-sm;
  margin-bottom: $pet-space-sm;
}
.review-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: $pet-radius-circle;
  background: $pet-bg-grey;
}
.review-name {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
}
.review-content {
  display: block;
  font-size: $pet-font-base;
  color: $pet-text;
  line-height: 1.6;
  margin-bottom: $pet-space-sm;
}
.review-time {
  font-size: $pet-font-xs;
  color: $pet-text-grey;
}
</style>
