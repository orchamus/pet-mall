<template>
  <view class="edit-page">
    <!-- 地址表单 -->
    <view class="form-card pet-card">
      <view class="form-item">
        <text class="label">收件人</text>
        <input
          class="input"
          type="text"
          v-model="form.receiverName"
          placeholder="请输入收件人姓名"
          maxlength="20"
        />
      </view>

      <view class="form-item">
        <text class="label">联系电话</text>
        <input
          class="input"
          type="number"
          v-model="form.phone"
          placeholder="请输入手机号码"
          maxlength="11"
        />
      </view>

      <view class="form-item">
        <text class="label">所在地区</text>
        <picker
          mode="multiSelector"
          :range="regionRange"
          :value="regionIndexes"
          @columnchange="onRegionColumnChange"
          @change="onRegionChange"
        >
          <view class="picker" :class="{ placeholder: !regionText }">
            {{ regionText || '请选择省 / 市 / 区' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">详细地址</text>
        <textarea
          class="textarea"
          v-model="form.detailAddress"
          placeholder="请输入街道、门牌号等详细地址"
          maxlength="100"
          auto-height
        />
      </view>

      <view class="form-item last">
        <text class="label">设为默认地址</text>
        <switch
          color="#FF7A45"
          :checked="form.isDefault"
          @change="form.isDefault = $event.detail.value"
        />
      </view>
    </view>

    <!-- 提示信息 -->
    <view class="tip pet-card">
      <text class="tip-text">填写真实有效的收货信息，便于商品准确送达</text>
    </view>

    <!-- 底部保存按钮 -->
    <view class="footer safe-area-bottom">
      <view class="pet-btn-primary save-btn" @click="submit">
        <text>保存</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addAddress, updateAddress, getAddressById, getAddressList } from '@/api/address.js';
import { showToast, navigateBack } from '@/utils/index.js';
import { normalizeAddress, denormalizeAddress, normalizeAddressList } from '@/utils/address.js';
import { getRegionRange, getRegionText, updateRegionRange, findRegionIndexes } from '@/utils/region.js';

// 响应式数据：页面模式（add/edit）、来源页、地址 ID、表单数据
const mode = ref('add');
const from = ref('manage');
const addressId = ref('');

const form = ref({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
});

// 省市区选择器数据
const regionRange = ref(getRegionRange());
const regionIndexes = ref([0, 0, 0]);

// 计算属性：已选省市区文案
const regionText = computed(() => {
  const { province, city, district } = form.value;
  const parts = [province, city, district].filter(Boolean);
  return parts.length ? parts.join(' / ') : '';
});

// 省市区选择器列变化：联动更新下级选项
const onRegionColumnChange = (e) => {
  const { column, value } = e.detail;
  const indexes = [...regionIndexes.value];
  indexes[column] = value;
  if (column === 0) {
    indexes[1] = 0;
    indexes[2] = 0;
  } else if (column === 1) {
    indexes[2] = 0;
  }
  regionIndexes.value = indexes;
  regionRange.value = updateRegionRange(indexes[0], indexes[1]);
};

// 省市区选择器确定：回填表单字段
const onRegionChange = (e) => {
  const indexes = e.detail.value || [0, 0, 0];
  regionIndexes.value = indexes;
  const { province, city, district } = getRegionText(indexes);
  form.value.province = province;
  form.value.city = city;
  form.value.district = district;
};

// 表单校验：收件人、手机号、省市区、详细地址
const validate = () => {
  if (!form.value.receiverName.trim()) {
    showToast('请输入收件人姓名');
    return false;
  }
  if (!form.value.phone.trim()) {
    showToast('请输入联系电话');
    return false;
  }
  if (!/^1[3-9]\d{9}$/.test(form.value.phone.trim())) {
    showToast('请输入正确的手机号码');
    return false;
  }
  if (!form.value.province || !form.value.city) {
    showToast('请选择所在地区');
    return false;
  }
  if (!form.value.detailAddress.trim()) {
    showToast('请输入详细地址');
    return false;
  }
  return true;
};

// 在地址列表中查找与当前表单匹配的新增地址
const findNewAddress = (list) => {
  const phone = form.value.phone?.trim();
  const detail = form.value.detailAddress?.trim();
  const receiver = form.value.receiverName?.trim();
  return list
    .slice()
    .sort((a, b) => (b.addressId || 0) - (a.addressId || 0))
    .find((addr) => {
      return (
        addr.phone === phone &&
        addr.detailAddress === detail &&
        addr.receiverName === receiver
      );
    });
};

// 提交地址表单：新增或编辑
const submit = async () => {
  if (!validate()) return;

  const payload = denormalizeAddress(form.value);
  try {
    if (mode.value === 'edit') {
      await updateAddress(payload);
      showToast('保存成功', 'success');
    } else {
      await addAddress(payload);
      showToast('添加成功', 'success');
    }

    if (mode.value === 'add' && from.value === 'select') {
      try {
        const res = await getAddressList();
        const list = normalizeAddressList(res.data || []);
        const newAddr = findNewAddress(list);
        if (newAddr) {
          uni.$emit('addressSelected', newAddr);
        }
      } catch (e) {
        console.error(e);
      }
      setTimeout(() => {
        uni.navigateBack({ delta: 2 });
      }, 300);
      return;
    }

    const pages = getCurrentPages();
    const prev = pages[pages.length - 2];
    if (prev && prev.route === 'pages/address/list') {
      prev.$vm?.loadAddress && prev.$vm.loadAddress(true);
    }

    setTimeout(() => {
      navigateBack();
    }, 300);
  } catch (e) {
    console.error(e);
  }
};

// 加载地址详情：编辑模式时回填表单
const loadAddress = async () => {
  if (!addressId.value) return;
  try {
    const res = await getAddressById(addressId.value);
    const normalized = normalizeAddress(res.data);
    if (normalized) {
      form.value = {
        addressId: normalized.addressId,
        receiverName: normalized.receiverName,
        phone: normalized.phone,
        province: normalized.province,
        city: normalized.city,
        district: normalized.district,
        detailAddress: normalized.detailAddress,
        isDefault: normalized.isDefault
      };
      const indexes = findRegionIndexes(normalized.province, normalized.city, normalized.district);
      regionIndexes.value = indexes;
      regionRange.value = updateRegionRange(indexes[0], indexes[1]);
    }
  } catch (e) {
    console.error(e);
    showToast('获取地址失败');
  }
};

// 页面加载：解析模式参数，编辑模式则加载地址详情
onLoad((options) => {
  mode.value = options.mode === 'edit' ? 'edit' : 'add';
  from.value = options.from === 'select' ? 'select' : 'manage';
  addressId.value = options.addressId || '';
  if (mode.value === 'edit' && addressId.value) {
    loadAddress();
  }
});
</script>

<style scoped lang="scss">
.edit-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
}

.form-card {
  margin: $pet-space-sm;
  padding: 0 $pet-space-base;
}

/* 表单项 */
.form-item {
  display: flex;
  align-items: center;
  padding: $pet-space-base 0;
  border-bottom: 1rpx solid $pet-border-light;
}
.form-item.last {
  border-bottom: none;
}
.label {
  width: 180rpx;
  font-size: $pet-font-base;
  color: $pet-text;
  font-weight: 500;
  flex-shrink: 0;
}
.input {
  flex: 1;
  font-size: $pet-font-base;
  color: $pet-text;
  text-align: right;
}
.textarea {
  flex: 1;
  min-height: 80rpx;
  max-height: 200rpx;
  font-size: $pet-font-base;
  color: $pet-text;
  text-align: right;
  line-height: 1.5;
}
.picker {
  flex: 1;
  font-size: $pet-font-base;
  color: $pet-text;
  text-align: right;
}
.picker.placeholder {
  color: $pet-text-placeholder;
}

.tip {
  margin: 0 $pet-space-sm;
  padding: $pet-space-sm $pet-space-base;
  background: rgba($pet-primary, 0.08);
}

/* 提示文本 */
.tip-text {
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
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

/* 保存按钮 */
.save-btn {
  height: 88rpx;
  font-size: $pet-font-lg;
}
</style>
