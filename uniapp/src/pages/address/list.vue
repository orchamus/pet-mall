<template>
  <view class="address-page">
    <!-- 搜索栏：按收件人/电话/地址过滤 -->
    <view class="search-bar pet-card">
      <text class="search-icon">🔍</text>
      <input
        class="search-input"
        type="text"
        v-model="searchKey"
        placeholder="搜索收件人、电话或地址"
        placeholder-class="placeholder"
      />
      <text v-if="searchKey" class="search-clear" @click="searchKey = ''">×</text>
    </view>

    <!-- 地址列表：支持选择模式高亮当前选中项 -->
    <scroll-view scroll-y class="address-list">
      <view
        class="address-item pet-card"
        :class="{ active: isSelected(addr) }"
        v-for="addr in filteredList"
        :key="addr.addressId"
        @click="onSelect(addr)"
      >
        <view class="addr-header">
          <text class="addr-name">{{ addr.receiverName }}</text>
          <text class="addr-phone">{{ addr.phone }}</text>
          <text v-if="addr.isDefault" class="addr-default">默认</text>
        </view>
        <text class="addr-detail">{{ formatFullAddress(addr) }}</text>

        <view class="addr-actions" @click.stop>
          <view class="action-btn default" @click="setDefault(addr)">
            <text v-if="!addr.isDefault">设为默认</text>
            <text v-else class="active-text">默认地址</text>
          </view>
          <view class="action-btn" @click="editAddress(addr)">
            <text>编辑</text>
          </view>
          <view class="action-btn danger" @click="removeAddress(addr)">
            <text>删除</text>
          </view>
        </view>

        <view v-if="isSelected(addr)" class="selected-mark">✓</view>
      </view>

      <!-- 空状态：无地址或搜索无结果 -->
      <view v-if="!filteredList.length" class="empty-state">
        <text class="empty-icon">📍</text>
        <text class="empty-text">{{ searchKey ? '没有找到匹配的地址' : '暂无收货地址' }}</text>
        <text v-if="!searchKey" class="empty-sub">添加一个地址，给毛孩子送货吧</text>
      </view>

      <view class="list-bottom"></view>
    </scroll-view>

    <!-- 底部按钮：新建收货地址 -->
    <view class="footer safe-area-bottom">
      <view class="pet-btn-primary add-btn" @click="addAddress">
        <text>+ 新建收货地址</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import {
  getAddressList,
  deleteAddress,
  setDefaultAddress
} from '@/api/address.js';
import { showToast, navigateTo, navigateBack } from '@/utils/index.js';
import { normalizeAddressList, formatFullAddress } from '@/utils/address.js';
import { saveAddressCache, getAddressCache } from '@/utils/payment.js';

// 响应式数据：页面模式（manage/select）、选中地址 ID、地址列表、搜索关键词
const mode = ref('manage');
const selectedId = ref('');
const addressList = ref([]);
const searchKey = ref('');

// 计算属性：根据搜索关键词过滤地址列表
const filteredList = computed(() => {
  const key = searchKey.value.trim();
  if (!key) return addressList.value;
  return addressList.value.filter((addr) => {
    const text = `${addr.receiverName} ${addr.phone} ${formatFullAddress(addr)}`;
    return text.toLowerCase().includes(key.toLowerCase());
  });
});

// 判断地址是否为当前选中项
const isSelected = (addr) => {
  return mode.value === 'select' && String(addr.addressId) === String(selectedId.value);
};

// 加载地址列表：请求接口并同步缓存
const loadAddress = async (silent = false) => {
  try {
    const res = await getAddressList();
    const list = normalizeAddressList(res.data || []);
    addressList.value = list;
    saveAddressCache(list);
    if (!silent && mode.value === 'select' && selectedId.value) {
      const exists = list.some((a) => String(a.addressId) === String(selectedId.value));
      if (!exists) selectedId.value = '';
    }
  } catch (e) {
    console.error(e);
    if (!silent) showToast('获取地址失败');
  }
};

// 从缓存加载地址列表，提升首屏体验
const loadCache = () => {
  const cache = getAddressCache();
  if (cache && cache.list && cache.list.length) {
    addressList.value = cache.list;
  }
};

// 设置默认地址
const setDefault = async (addr) => {
  if (addr.isDefault) return;
  try {
    await setDefaultAddress(addr.addressId);
    showToast('设置成功', 'success');
    loadAddress(true);
  } catch (e) {
    console.error(e);
  }
};

// 跳转地址编辑页
const editAddress = (addr) => {
  navigateTo(`/pages/address/edit?mode=edit&addressId=${addr.addressId}&from=${mode.value}`);
};

// 跳转地址新增页
const addAddress = () => {
  navigateTo(`/pages/address/edit?mode=add&from=${mode.value}`);
};

// 删除地址
const removeAddress = (addr) => {
  uni.showModal({
    title: '删除地址',
    content: '确定删除该收货地址？',
    confirmColor: '#FF4D4F',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteAddress(addr.addressId);
          showToast('删除成功', 'success');
          loadAddress(true);
        } catch (e) {
          console.error(e);
        }
      }
    }
  });
};

// 选择地址：选择模式下通知上一页并返回
const onSelect = (addr) => {
  if (mode.value !== 'select') return;
  uni.$emit('addressSelected', addr);
  navigateBack();
};

// 页面加载：解析模式参数并读取缓存
onLoad((options) => {
  mode.value = options.mode === 'select' ? 'select' : 'manage';
  if (options.selectedId) selectedId.value = options.selectedId;
  loadCache();
});

// 页面显示时刷新地址列表
onShow(() => {
  loadAddress(true);
});
</script>

<style scoped lang="scss">
.address-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
}

.search-bar {
  display: flex;
  align-items: center;
  margin: $pet-space-sm;
  padding: $pet-space-sm $pet-space-base;
}
.search-icon {
  font-size: $pet-font-lg;
  margin-right: $pet-space-sm;
  color: $pet-text-grey;
}
.search-input {
  flex: 1;
  font-size: $pet-font-base;
  color: $pet-text;
}
.search-clear {
  font-size: 36rpx;
  color: $pet-text-grey;
  padding: 0 10rpx;
}

.address-list {
  height: calc(100vh - 180rpx - env(safe-area-inset-bottom));
  padding: 0 $pet-space-sm;
}
.address-item {
  position: relative;
  padding: $pet-space-base;
  margin-bottom: $pet-space-sm;
  transition: all 0.2s;
}
.address-item.active {
  background: $pet-bg-hover;
  border: 2rpx solid $pet-primary-light;
}
.addr-header {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}
.addr-name {
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
  margin-right: $pet-space-sm;
}
.addr-phone {
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  margin-right: $pet-space-sm;
}
.addr-default {
  font-size: $pet-font-xs;
  color: $pet-primary;
  border: 1rpx solid $pet-primary;
  padding: 2rpx 10rpx;
  border-radius: $pet-radius-sm;
}
.addr-detail {
  display: block;
  font-size: $pet-font-base;
  color: $pet-text-secondary;
  line-height: 1.5;
  margin-bottom: 6rpx;
}
.addr-actions {
  display: flex;
  justify-content: flex-end;
  gap: $pet-space-sm;
  padding-top: $pet-space-sm;
  border-top: 1rpx solid $pet-border-light;
}
.action-btn {
  padding: 8rpx 24rpx;
  border-radius: $pet-radius-lg;
  border: 1rpx solid $pet-border;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
}
.action-btn.default {
  color: $pet-primary;
  border-color: $pet-primary;
}
.action-btn.danger {
  color: $pet-error;
  border-color: $pet-error;
}
.active-text {
  color: $pet-text-disable;
}
.selected-mark {
  position: absolute;
  right: $pet-space-base;
  top: 50%;
  transform: translateY(-50%);
  width: 40rpx;
  height: 40rpx;
  line-height: 40rpx;
  text-align: center;
  border-radius: $pet-radius-circle;
  background: $pet-primary;
  color: #fff;
  font-size: $pet-font-sm;
}

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
}
.list-bottom {
  height: 40rpx;
}

.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: var(--window-bottom);
  background: $pet-bg-white;
  padding: $pet-space-sm $pet-space-base;
  box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.06);
  z-index: 100;
}
.add-btn {
  height: 88rpx;
  font-size: $pet-font-lg;
}
</style>
