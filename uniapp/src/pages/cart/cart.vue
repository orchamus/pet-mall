<template>
  <view class="cart-page">
    <!-- 空状态：未登录或购物车为空时展示 -->
    <view class="empty-state" v-if="!cartList.length">
      <view class="empty-icon">{{ isLogin ? '🛒' : '🔒' }}</view>
      <text class="empty-text">{{ isLogin ? '购物车还是空的' : '您还未登录' }}</text>
      <text class="empty-sub">{{ isLogin ? '快去给毛孩子挑选好物吧' : '登录后查看购物车商品' }}</text>
      <view class="pet-btn-primary empty-btn" @click="isLogin ? goShopping() : goLogin()">
        {{ isLogin ? '去逛逛' : '去登录' }}
      </view>
    </view>

    <!-- 购物车有商品时的主体内容 -->
    <view v-else class="cart-body">
      <!-- 购物车商品列表 -->
      <view class="cart-list">
        <view class="cart-item pet-card" v-for="item in cartList" :key="item.cartId">
          <checkbox
            class="cart-check"
            color="#FF7A45"
            :checked="selectedIds.includes(item.cartId)"
            :disabled="isUnavailable(item)"
            @click="toggleSelect(item.cartId)"
          />
          <image
            class="cart-image"
            :src="getProductImage(item.product)"
            mode="aspectFill"
            @click="goDetail(item.product.productId, item.product.status)"
          />
          <view class="cart-info">
            <text class="cart-name" @click="goDetail(item.product.productId, item.product.status)">
              {{ item.product?.name }}
            </text>
            <text class="cart-desc">{{ getProductDesc(item.product) }}</text>
            <view class="cart-tags">
              <text v-if="item.product?.status === '1'" class="pet-tag pet-tag-danger">已下架</text>
              <text v-else-if="item.product?.stockQuantity === 0" class="pet-tag pet-tag-danger">暂时缺货</text>
              <text v-else-if="item.product?.stockQuantity < item.quantity" class="pet-tag pet-tag-danger">库存不足</text>
              <text v-else class="pet-tag pet-tag-green">有货</text>
            </view>
            <view class="cart-bottom">
              <view class="price-wrap">
                <text class="price-symbol">¥</text>
                <text class="cart-price">{{ formatPrice(item.product?.price) }}</text>
              </view>
              <view class="quantity-box" :class="{ disabled: isUnavailable(item) }">
                <text class="qty-btn minus" @click="decrease(item)">-</text>
                <input
                  class="qty-input"
                  type="number"
                  v-model.number="item.quantity"
                  :disabled="isUnavailable(item)"
                  @blur="validateQuantity(item)"
                />
                <text class="qty-btn plus" @click="increase(item)">+</text>
              </view>
            </view>
          </view>
          <view class="delete-btn" @click="deleteItem(item.cartId)">
            <text class="delete-icon">🗑️</text>
          </view>
        </view>
      </view>

      <!-- 底部结算栏：全选、合计金额与去结算 -->
      <view class="cart-footer safe-area-bottom">
        <view class="footer-left">
          <checkbox color="#FF7A45" :checked="isAllSelected" @click="toggleSelectAll" />
          <text class="select-text" @click="toggleSelectAll">全选</text>
          <view class="total-wrap">
            <text class="total-label">合计：</text>
            <text class="price-symbol">¥</text>
            <text class="total-price">{{ formatPrice(totalPrice) }}</text>
          </view>
        </view>
        <view
          class="settlement-btn"
          :class="{ disabled: !selectedItems.length }"
          @click="goSettlement"
        >
          <text>去结算</text>
          <text class="btn-count" v-if="selectedItems.length">({{ selectedItems.length }})</text>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getCartList, deleteCart } from '@/api/cart.js';
import { formatPrice, getProductImage, switchTab, navigateTo, showToast } from '@/utils/index.js';
import { normalizeText, normalizeBreed } from '@/utils/text.js';
import { savePaymentSession } from '@/utils/payment.js';

// 响应式数据：购物车商品列表、当前选中的 cartId、登录状态
const cartList = ref([]);
const selectedIds = ref([]);
const isLogin = ref(false);

// 登录态校验：读取本地 satoken 并更新登录状态
const checkLogin = () => {
  const token = uni.getStorageSync('satoken') || '';
  isLogin.value = !!token;
  return isLogin.value;
};

// 跳转登录页
const goLogin = () => {
  navigateTo('/pages/login/login');
};

// 判断商品是否不可购买（下架或库存不足）
const isUnavailable = (item) => {
  const product = item.product;
  return !product || product.status === '1' || product.stockQuantity <= 0;
};

// 组合商品描述：优先使用 description，否则展示品种信息
const getProductDesc = (product) => {
  if (!product) return '暂无商品描述';
  const desc = normalizeText(product.description, '');
  if (desc) return desc;
  const breed = normalizeBreed(product);
  return breed ? `${breed} · 健康可爱` : '暂无商品描述';
};

// 计算属性：当前选中的商品项
const selectedItems = computed(() => {
  return cartList.value.filter(item => selectedIds.value.includes(item.cartId));
});

// 计算属性：全部有效商品是否都被选中
const isAllSelected = computed(() => {
  const available = cartList.value.filter(item => !isUnavailable(item));
  return available.length > 0 && available.every(item => selectedIds.value.includes(item.cartId));
});

// 计算属性：选中商品合计金额
const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * (item.quantity || 0);
  }, 0);
});

// 加载购物车列表，未登录时清空数据
const loadCart = async () => {
  if (!checkLogin()) {
    cartList.value = [];
    selectedIds.value = [];
    return;
  }
  try {
    const res = await getCartList();
    cartList.value = (res.data || []).map(item => ({
      ...item,
      quantity: item.quantity || 1
    }));
    // 过滤掉已失效商品的选中状态
    selectedIds.value = selectedIds.value.filter(id => {
      const item = cartList.value.find(i => i.cartId === id);
      return item && !isUnavailable(item);
    });
  } catch (e) {
    console.error(e);
  }
};

// 切换单个商品的选中状态
const toggleSelect = (cartId) => {
  const idx = selectedIds.value.indexOf(cartId);
  if (idx > -1) {
    selectedIds.value.splice(idx, 1);
  } else {
    selectedIds.value.push(cartId);
  }
};

// 全选/取消全选：仅对有效商品生效
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedIds.value = [];
  } else {
    selectedIds.value = cartList.value
      .filter(item => !isUnavailable(item))
      .map(item => item.cartId);
  }
};

// 增加商品数量，受库存上限限制
const increase = (item) => {
  if (isUnavailable(item)) return;
  if (item.quantity < item.product.stockQuantity) {
    item.quantity++;
  } else {
    showToast('已达到最大库存');
  }
};

// 减少商品数量，最低为 1
const decrease = (item) => {
  if (isUnavailable(item)) return;
  if (item.quantity > 1) {
    item.quantity--;
  }
};

// 数量输入失焦校验，非法值自动修正为有效范围
const validateQuantity = (item) => {
  if (isUnavailable(item)) return;
  let qty = parseInt(item.quantity) || 1;
  if (qty < 1) qty = 1;
  if (item.product?.stockQuantity > 0 && qty > item.product.stockQuantity) {
    qty = item.product.stockQuantity;
    showToast('超出库存，已调整数量');
  }
  item.quantity = qty;
};

// 删除购物车商品
const deleteItem = async (cartId) => {
  uni.showModal({
    title: '删除商品',
    content: '确定将该商品从购物车移除？',
    confirmColor: '#FF7A45',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteCart([cartId]);
          selectedIds.value = selectedIds.value.filter(id => id !== cartId);
          loadCart();
          showToast('删除成功', 'success');
        } catch (e) {
          console.error(e);
        }
      }
    }
  });
};

// 跳转首页
const goShopping = () => {
  switchTab('/pages/index/index');
};

// 跳转商品详情，已下架商品给予提示
const goDetail = (productId, status) => {
  if (status === '1') {
    showToast('该商品已下架');
    return;
  }
  navigateTo('/pages/product/detail?productId=' + productId);
};

// 去结算：校验选中商品有效性并保存支付会话
const goSettlement = () => {
  if (!selectedItems.value.length) {
    showToast('请选择商品');
    return;
  }
  if (selectedItems.value.some(item => isUnavailable(item))) {
    showToast('选中商品包含无效商品');
    return;
  }

  const items = selectedItems.value.map(item => ({
    cartId: item.cartId,
    quantity: item.quantity,
    productId: item.product?.productId,
    name: item.product?.name || '商品',
    price: item.product?.price || 0,
    image: getProductImage(item.product)
  }));

  savePaymentSession({
    from: 'cart',
    items,
    total: totalPrice.value
  });

  navigateTo('/pages/order/settlement?from=cart');
};

// 页面显示时刷新购物车
onShow(() => {
  loadCart();
});

// 页面挂载时刷新购物车
onMounted(() => {
  loadCart();
});
</script>

<style scoped lang="scss">
.cart-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc(220rpx + env(safe-area-inset-bottom));
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 240rpx;
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
  margin-bottom: 12rpx;
}
.empty-sub {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  margin-bottom: 40rpx;
}
.empty-btn {
  width: 280rpx;
  height: 80rpx;
  font-size: $pet-font-base;
}

/* 商品列表 */
.cart-list {
  padding: $pet-space-sm;
}
.cart-item {
  display: flex;
  align-items: flex-start;
  padding: $pet-space-base;
  margin-bottom: $pet-space-sm;
  transition: transform 0.2s;
}
.cart-check {
  margin-right: $pet-space-sm;
  transform: scale(0.9);
  align-self: center;
}
.cart-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: $pet-radius-base;
  margin-right: $pet-space-base;
  flex-shrink: 0;
  background: $pet-bg-grey;
}
.cart-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
  padding: 4rpx 0;
}
.cart-name {
  font-size: $pet-font-base;
  font-weight: 600;
  color: $pet-text;
  display: block;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.cart-desc {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  margin-top: 6rpx;
  display: block;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}
.cart-tags {
  margin-top: 10rpx;
}
.pet-tag-danger {
  background: rgba($pet-error, 0.1);
  color: $pet-error;
}
.cart-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
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
.cart-price {
  color: $pet-error;
  font-size: $pet-font-lg;
  font-weight: 800;
}

/* 数量选择器 */
.quantity-box {
  display: flex;
  align-items: center;
  border: 1rpx solid $pet-border;
  border-radius: $pet-radius-sm;
  overflow: hidden;
}
.quantity-box.disabled {
  opacity: 0.5;
}
.qty-btn {
  width: 52rpx;
  height: 52rpx;
  line-height: 48rpx;
  text-align: center;
  font-size: $pet-font-lg;
  color: $pet-text-secondary;
  background: $pet-bg-grey;
}
.qty-btn.minus {
  border-right: 1rpx solid $pet-border;
}
.qty-btn.plus {
  border-left: 1rpx solid $pet-border;
  color: $pet-primary;
}
.qty-input {
  width: 76rpx;
  height: 52rpx;
  text-align: center;
  font-size: $pet-font-base;
  color: $pet-text;
  background: $pet-bg-white;
}

/* 删除按钮 */
.delete-btn {
  margin-left: $pet-space-sm;
  padding: 12rpx;
  align-self: center;
  flex-shrink: 0;
}
.delete-icon {
  font-size: 36rpx;
  opacity: 0.6;
}

/* 底部结算栏 */
.cart-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: var(--window-bottom);
  height: 100rpx;
  background: $pet-bg-white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $pet-space-base;
  box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 100;
}
.footer-left {
  display: flex;
  align-items: center;
}
.select-text {
  margin-left: 10rpx;
  font-size: $pet-font-base;
  color: $pet-text-secondary;
}
.total-wrap {
  display: flex;
  align-items: baseline;
  margin-left: $pet-space-base;
}
.total-label {
  font-size: $pet-font-base;
  color: $pet-text;
}
.total-price {
  color: $pet-error;
  font-size: $pet-font-xl;
  font-weight: 800;
}
.settlement-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  padding: 0 40rpx;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  font-size: $pet-font-base;
  font-weight: 600;
  border-radius: $pet-radius-lg;
  box-shadow: 0 8rpx 20rpx rgba($pet-primary, 0.3);
}
.settlement-btn.disabled {
  background: $pet-text-disable;
  box-shadow: none;
}
.btn-count {
  margin-left: 6rpx;
  font-size: $pet-font-sm;
}
</style>