<template>
  <el-card class="address-item" :class="{ 'default-address': address.isDefault }">
    <!-- 地址头部：收货人、邮箱、默认标识 -->
    <div class="address-header">
      <span class="name">收货人：{{ address.consignee }}</span>
      <span class="email">邮箱：{{ address.email }}</span>
      <el-tag v-if="address.isDefault == '1'" size="small" type="danger">默认</el-tag>
    </div>

    <!-- 地址内容：完整地址与标签 -->
    <div class="address-content">
      <p>
        收获地址：{{ getFullAddress() }}
        <el-tag  size="small" type="success" style="margin-left: 10px;">{{ address.tag }}</el-tag>
      </p>
    </div>

    <!-- 地址操作按钮 -->
    <div class="address-actions" v-if="showActions">
      <el-button size="small" @click="$emit('edit', address)">编辑</el-button>
      <el-button size="small" type="danger" @click="$emit('delete', address)">删除</el-button>
      <el-button
          v-if="address.isDefault == '0'"
          size="small"
          plain
          @click="$emit('set-default', address)"
      >
        设为默认
      </el-button>
    </div>
  </el-card>
</template>

<script>
import { regionData } from 'element-china-area-data';

export default {
  props: {
    // 地址对象
    address: {
      type: Object,
      required: true
    },
    // 是否显示操作按钮，默认显示
    showActions: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      // 中国地区级联数据
      regionData: regionData
    };
  },
  methods: {
    /**
     * 拼接完整地址（地区 + 详细地址）
     * @returns {String} 完整地址字符串
     */
    getFullAddress() {
      return  this.address.region + this.address.detail;
    }
  }
};
</script>

<style scoped>
/* 单个地址卡片 */
.address-item {
  margin-bottom: 0;
  transition: all 0.3s;
}

.address-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 默认地址标识 */
.default-address {
  border-left: 4px solid #f56c6c;
}

/* 地址头部 */
.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.address-header .name {
  font-weight: bold;
  margin-right: 10px;
  width: 150px;
}

.address-header .email {
  color: #666;
  margin-right: 10px;
}

/* 地址内容 */
.address-content {
  color: #666;
  margin-bottom: 15px;
  min-height: 40px;
}

/* 操作按钮区 */
.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
