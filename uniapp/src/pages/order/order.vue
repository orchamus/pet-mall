<template>
  <view class="order-page">
    <!-- 状态标签栏：按订单状态筛选 -->
    <view class="status-tabs pet-card">
      <view
        class="tab-item"
        v-for="tab in statusTabs"
        :key="tab.value"
        :class="{ active: currentStatus === tab.value }"
        @click="changeStatus(tab.value)"
      >
        {{ tab.label }}
        <text v-if="tab.count" class="tab-count">{{ tab.count }}</text>
      </view>
    </view>

    <!-- 空状态：无订单时展示 -->
    <view class="empty-state" v-if="!orderList.length">
      <view class="empty-icon">📦</view>
      <text class="empty-text">暂无{{ currentStatus ? getStatusText(currentStatus) : '' }}订单</text>
      <text class="empty-sub">下单后在这里查看订单状态</text>
      <view class="pet-btn-primary empty-btn" @click="goShopping">去逛逛</view>
    </view>

    <!-- 订单列表 -->
    <view v-else class="order-list">
      <view class="order-card pet-card" v-for="order in orderList" :key="order.orderId">
        <view class="order-header">
          <view class="order-no">
            <text class="order-label">订单号</text>
            <text class="order-id">{{ order.orderId }}</text>
          </view>
          <view class="order-status" :class="'status-' + order.status">
            {{ getStatusText(order.status) }}
          </view>
        </view>

        <view class="order-product" @click="goProduct(order.product?.productId, order.product?.status)">
          <image
            class="product-image"
            :src="getProductImage(order.product)"
            mode="aspectFill"
          />
          <view class="product-info">
            <text class="product-name">{{ order.product?.name }}</text>
            <view class="product-meta">
              <text class="product-price">¥{{ formatPrice(order.price) }}</text>
              <text class="product-quantity">×{{ order.quantity }}</text>
            </view>
            <view class="order-total">
              <text class="total-label">实付款</text>
              <text class="price-symbol">¥</text>
              <text class="total-amount">{{ formatPrice(order.price * order.quantity) }}</text>
            </view>
          </view>
        </view>

        <!-- 订单底部：时间与操作按钮 -->
        <view class="order-footer">
          <text class="order-time">{{ formatDate(order.createdAt) }}</text>
          <view class="order-actions">
            <button
              v-if="order.status === 1"
              class="btn btn-default"
              @click="cancelOrderItem(order)"
            >取消订单</button>
            <button
              v-if="order.status === 2"
              class="btn btn-primary"
              @click="confirmReceiptItem(order)"
            >确认收货</button>
            <button
              v-if="order.status === 7"
              class="btn btn-success"
              @click="goReview(order)"
            >评价</button>
            <button
              v-if="[1, 2, 3, 7].includes(order.status)"
              class="btn btn-danger"
              :disabled="order.status === 5 || order.status === 6"
              @click="applyRefundItem(order)"
            >{{ order.status === 5 || order.status === 6 ? '退款中' : '申请退款' }}</button>
          </view>
        </view>
      </view>

      <!-- 加载更多 / 到底提示 -->
      <view class="load-more" v-if="hasMore">
        <view class="load-btn" @click="loadMore">
          <text>加载更多</text>
        </view>
      </view>
      <view class="finished-text" v-else-if="orderList.length">已经到底啦 ~</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getOrderList, cancelOrder, confirmReceipt, requestRefund } from '@/api/orders.js';
import { hasReviewed } from '@/api/reviews.js';
import { formatPrice, getProductImage, formatDate, navigateTo, showToast, switchTab } from '@/utils/index.js';

// 订单状态筛选项
const statusTabs = [
  { label: '全部', value: '' },
  { label: '待发货', value: 1 },
  { label: '已发货', value: 2 },
  { label: '待评价', value: 7 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 },
  { label: '退款中', value: 5 },
  { label: '已退款', value: 6 }
];

// 响应式数据：当前筛选状态、订单列表、分页信息
const currentStatus = ref('');
const orderList = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);

// 订单状态文案映射
const statusMap = {
  1: '待发货',
  2: '已发货',
  3: '已完成',
  4: '已取消',
  5: '退款中',
  6: '已退款',
  7: '待评价'
};

// 订单状态样式类名映射
const statusColorMap = {
  1: 'status-pending',
  2: 'status-shipped',
  3: 'status-completed',
  4: 'status-cancelled',
  5: 'status-refunding',
  6: 'status-refunded',
  7: 'status-completed'
};

// 根据状态码获取状态文案
const getStatusText = (status) => statusMap[status] || '未知状态';

// 加载订单列表，支持重置分页
const loadOrders = async (reset = false) => {
  try {
    if (reset) {
      pageNum.value = 1;
      orderList.value = [];
    }
    const res = await getOrderList(pageNum.value, pageSize.value);
    const records = res.data?.records || [];
    const filtered = currentStatus.value === ''
      ? records
      : records.filter(item => String(item.status) === String(currentStatus.value));

    if (pageNum.value === 1) {
      orderList.value = filtered;
    } else {
      orderList.value.push(...filtered);
    }

    hasMore.value = records.length === pageSize.value;
  } catch (e) {
    console.error(e);
  }
};

// 切换订单状态筛选
const changeStatus = (status) => {
  currentStatus.value = status;
  loadOrders(true);
};

// 加载下一页数据
const loadMore = () => {
  pageNum.value++;
  loadOrders();
};

// 跳转首页
const goShopping = () => {
  switchTab('/pages/index/index');
};

// 跳转商品详情，已下架则提示
const goProduct = (productId, status) => {
  if (!productId) return;
  if (status === '1') {
    showToast('该商品已下架');
    return;
  }
  navigateTo('/pages/product/detail?productId=' + productId);
};

// 取消订单
const cancelOrderItem = (order) => {
  uni.showModal({
    title: '取消订单',
    content: '确认取消该订单？取消后不可恢复',
    confirmColor: '#FF4D4F',
    success: (res) => {
      if (res.confirm) {
        cancelOrder(order.orderId).then(() => {
          showToast('取消成功', 'success');
          loadOrders(true);
        }).catch((e) => {
          console.error(e);
          showToast('取消失败');
        });
      }
    }
  });
};

// 确认收货，成功后弹窗引导评价
const confirmReceiptItem = (order) => {
  uni.showModal({
    title: '确认收货',
    content: '确认已收到商品？确认后订单将完成',
    confirmColor: '#FF7A45',
    success: (res) => {
      if (res.confirm) {
        confirmReceipt(order.orderId).then(() => {
          showToast('确认收货成功', 'success');
          loadOrders(true);
          uni.showModal({
            title: '提示',
            content: '订单已收货，是否立即评价？评价后将完成订单。',
            confirmText: '去评价',
            cancelText: '暂不去',
            success: (r) => {
              if (r.confirm) goReview(order);
            }
          });
        }).catch((e) => {
          console.error(e);
          showToast('确认收货失败');
        });
      }
    }
  });
};

// 跳转评价页：先校验是否已评价
const goReview = (order) => {
  if (!order.product?.productId) {
    showToast('商品信息缺失');
    return;
  }
  hasReviewed(order.orderId).then(res => {
    if (res.data) {
      showToast('该订单已评价');
    } else {
      navigateTo('/pages/review/review?orderId=' + order.orderId + '&productId=' + order.product.productId);
    }
  }).catch(() => {
    navigateTo('/pages/review/review?orderId=' + order.orderId + '&productId=' + order.product.productId);
  });
};

// 申请退款：填写退款原因后提交
const applyRefundItem = (order) => {
  if (order.status === 5 || order.status === 6) return;
  uni.showModal({
    title: '申请退款',
    editable: true,
    placeholderText: '请输入退款原因，便于商家处理',
    confirmColor: '#FF4D4F',
    success: (res) => {
      if (res.confirm && res.content && res.content.trim()) {
        requestRefund(order.orderId, res.content.trim()).then(() => {
          showToast('申请成功', 'success');
          loadOrders(true);
        }).catch((e) => {
          console.error(e);
          showToast('申请退款失败');
        });
      } else if (res.confirm) {
        showToast('退款原因不能为空');
      }
    }
  });
};

// 页面显示时刷新订单列表
onShow(() => {
  loadOrders(true);
});

// 页面挂载时刷新订单列表
onMounted(() => {
  loadOrders(true);
});
</script>

<style scoped lang="scss">
.order-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: $pet-space-base;
}

/* 状态标签栏 */
.status-tabs {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding: $pet-space-sm;
  margin: $pet-space-sm;
  gap: 12rpx;
}
.tab-item {
  flex-shrink: 0;
  padding: 12rpx 24rpx;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  background: $pet-bg-grey;
  border-radius: $pet-radius-lg;
  transition: all 0.25s;
}
.tab-item.active {
  color: #fff;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  box-shadow: 0 4rpx 12rpx rgba($pet-primary, 0.25);
  font-weight: 600;
}
.tab-count {
  margin-left: 6rpx;
  font-size: $pet-font-xs;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 220rpx;
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

/* 订单列表 */
.order-list {
  padding: 0 $pet-space-sm;
}
.order-card {
  padding: $pet-space-base;
  margin-bottom: $pet-space-sm;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $pet-space-base;
  padding-bottom: $pet-space-sm;
  border-bottom: 1rpx solid $pet-border-light;
}
.order-no {
  display: flex;
  align-items: center;
}
.order-label {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  margin-right: 10rpx;
}
.order-id {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  font-weight: 500;
}
.order-status {
  font-size: $pet-font-base;
  font-weight: 700;
}
.status-1 { color: $pet-warning; }
.status-2 { color: $pet-blue; }
.status-3 { color: $pet-success; }
.status-4 { color: $pet-text-grey; }
.status-5 { color: $pet-warning; }
.status-6 { color: $pet-text-grey; }

/* 商品信息 */
.order-product {
  display: flex;
  background: $pet-bg;
  border-radius: $pet-radius-base;
  padding: $pet-space-base;
}
.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: $pet-radius-base;
  margin-right: $pet-space-base;
  flex-shrink: 0;
  background: $pet-bg-grey;
}
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}
.product-name {
  font-size: $pet-font-base;
  font-weight: 600;
  color: $pet-text;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}
.product-meta {
  display: flex;
  align-items: baseline;
  margin-top: 10rpx;
}
.product-price {
  font-size: $pet-font-base;
  color: $pet-error;
  font-weight: 700;
  margin-right: $pet-space-sm;
}
.product-quantity {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
.order-total {
  display: flex;
  align-items: baseline;
  margin-top: 16rpx;
}
.total-label {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  margin-right: 10rpx;
}
.total-amount {
  color: $pet-error;
  font-size: $pet-font-lg;
  font-weight: 800;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: $pet-space-base;
  padding-top: $pet-space-sm;
  border-top: 1rpx solid $pet-border-light;
}
.order-time {
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
.order-actions {
  display: flex;
}
.btn {
  margin-left: $pet-space-sm;
  font-size: $pet-font-sm;
  padding: 10rpx 26rpx;
  border-radius: $pet-radius-lg;
  line-height: 1.5;
  background: #fff;
}
.btn::after {
  border: none;
}
.btn-default {
  color: $pet-text-secondary;
  border: 1rpx solid $pet-border;
}
.btn-primary {
  color: $pet-primary;
  border: 1rpx solid $pet-primary;
}
.btn-danger {
  color: $pet-error;
  border: 1rpx solid $pet-error;
}
.btn-success {
  color: $pet-success;
  border: 1rpx solid $pet-success;
}
.btn[disabled] {
  color: $pet-text-disable;
  border-color: $pet-border;
  background: $pet-bg-grey;
}

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: $pet-space-base;
}
.load-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  background: $pet-bg-white;
  padding: 16rpx 48rpx;
  border-radius: $pet-radius-lg;
  box-shadow: $pet-shadow-sm;
}
.finished-text {
  text-align: center;
  padding: $pet-space-base 0;
  font-size: $pet-font-sm;
  color: $pet-text-grey;
}
</style>