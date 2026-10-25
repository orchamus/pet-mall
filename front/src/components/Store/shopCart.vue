<template>
  <div class="cart-management">
    <el-card class="cart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">🛒</span>
            <span>购物车</span>
          </div>
          <el-button
            type="danger"
            plain
            :disabled="selectedItems.length === 0"
            :loading="batchDeleteLoading"
            @click="handleBatchDelete"
          >
            删除选中
          </el-button>
        </div>
      </template>

      <!-- 购物车商品列表 -->
      <el-table
        :data="cartItems"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                :src="getProductImage(row.product)"
                :preview-src-list="[getProductImage(row.product)]"
                fit="cover"
                class="product-image"
                preview-teleported
              />
              <div class="product-detail" @click="goToProduct(row.product.productId, row.product.status)">
                <div class="product-name">{{ row.product?.name }}</div>
                <div class="product-price">¥{{ row.product?.price?.toFixed(2) }}</div>
                <div class="product-tags">
                  <el-tag v-if="row.product?.productType === '0'" type="warning" size="small">宠物活体</el-tag>
                  <el-tag v-else type="success" size="small">周边用品</el-tag>
                  <el-tag v-if="row.product?.status === '1'" type="danger" size="small">已下架</el-tag>
                  <el-tag v-else-if="row.product?.stockQuantity === 0" type="info" size="small">暂时缺货</el-tag>
                  <el-tag v-else-if="row.product?.stockQuantity < row.quantity" type="danger" size="small">库存不足</el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="120" align="center">
          <template #default="{ row }">
            <span :class="{ 'stock-out': row.product?.stockQuantity === 0 }">
              {{ row.product?.stockQuantity ?? 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="200" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="row.product?.stockQuantity === 0 ? 0 : 1"
              :max="row.product?.stockQuantity"
              :disabled="row.product?.status === '1' || row.product?.stockQuantity === 0"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="150" align="right">
          <template #default="{ row }">
            <span class="subtotal-price" :class="{ 'disabled-text': row.product?.status === '1' || row.product?.stockQuantity === 0 }">
              ¥{{ (row.product?.price * row.quantity).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 底部结算栏 -->
      <div class="cart-footer">
        <div class="selection-info">
          已选 <strong>{{ selectedItems.length }}</strong> 件商品
        </div>
        <div class="total-price">
          合计：<span class="price">¥{{ selectedTotalPrice.toFixed(2) }}</span>
        </div>
        <el-button
          type="primary"
          size="large"
          class="checkout-btn"
          :disabled="selectedItems.length === 0"
          @click="getAddresses"
        >
          去结算 ({{ selectedItems.length }})
        </el-button>
      </div>
    </el-card>

    <!-- 选择收货地址弹窗 -->
    <el-dialog v-model="addressVisible" title="选择收货地址" width="520px" destroy-on-close>
      <div v-if="addresses.length === 0" class="empty-tip">
        <el-empty description="暂无收货地址，请先到个人中心添加"></el-empty>
      </div>
      <div v-else class="address-list">
        <AddressItem
          v-for="address in addresses"
          :key="address.addressId"
          :address="address"
          :show-actions="false"
          :class="{ selected: addressId === address.addressId }"
          @click="selectAddress(address.addressId)"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCartItems, getCartList } from "../../api/shopCart/cart";
import { createOrderBycart } from "../../api/order/order";
import AddressItem from "../PersonalCenter/AddressItem.vue";
import { getAddressList } from "../../api/PersonalCenter/Address";
import { getProductImage } from "../../utils/image";

export default {
  components: { AddressItem },
  data() {
    return {
      // 列表加载状态
      loading: false,
      // 批量删除加载状态
      batchDeleteLoading: false,
      // 购物车商品列表
      cartItems: [],
      // 当前选中的商品
      selectedItems: [],
      // 选中商品的 cartId 与数量（用于下单）
      selectedItemQuantityAndIds: [],
      // 选中商品的 cartId 列表（用于删除）
      selectedItemIds: [],
      // 地址选择弹窗显示状态
      addressVisible: false,
      // 收货地址列表
      addresses: [],
      // 选中的收货地址 ID
      addressId: ''
    }
  },
  computed: {
    // 选中商品总价
    selectedTotalPrice() {
      return this.selectedItems.reduce((total, item) => {
        return total + (item.product?.price * item.quantity || 0)
      }, 0)
    },
    // 是否包含已下架商品
    hasUnavailableItems() {
      return this.selectedItems.some(item => item.product?.status === '1')
    },
    // 是否包含库存不足商品
    beOutOfStock() {
      return this.selectedItems.some(item =>
        item.product?.stockQuantity <= 0 || item.product?.stockQuantity < item.quantity
      )
    }
  },
  // 组件创建时加载购物车
  created() {
    this.fetchCartItems()
  },
  methods: {
    getProductImage,
    // 加载购物车列表
    fetchCartItems() {
      this.loading = true
      getCartList().then(res => {
        if (!res.data) {
          ElMessage.error(res.message || '获取购物车失败')
        } else {
          this.cartItems = (res.data || []).map(item => ({
            ...item,
            quantity: item.quantity || 1
          }))
        }
      }).finally(() => {
        this.loading = false
      })
    },
    // 表格选中项变化
    handleSelectionChange(val) {
      this.selectedItems = val
      this.selectedItemQuantityAndIds = val.map(item => ({
        cartId: item.cartId,
        quantity: item.quantity
      }))
      this.selectedItemIds = val.map(item => item.cartId)
    },
    // 批量删除选中的购物车商品
    handleBatchDelete() {
      if (this.selectedItems.length === 0) {
        ElMessage.warning('请先选择要删除的商品')
        return
      }
      ElMessageBox.confirm(`确定要删除选中的 ${this.selectedItems.length} 件商品吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.batchDeleteLoading = true
        deleteCartItems(this.selectedItemIds).then(res => {
          if (res) {
            this.fetchCartItems()
            ElMessage.success(res.message || '删除成功')
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        }).finally(() => {
          this.batchDeleteLoading = false
        })
      }).catch(() => {})
    },
    // 调用购物车下单 API
    buyBycart() {
      createOrderBycart(this.selectedItemQuantityAndIds, this.addressId).then(res => {
        if (res) {
          ElMessage.success(res.message || '下单成功')
          this.fetchCartItems()
        } else {
          ElMessage.error(res.message || '下单失败')
        }
      }).catch(err => {
        console.error(err)
        ElMessage.error('下单失败')
      })
    },
    // 获取收货地址并打开选择弹窗
    getAddresses() {
      if (this.hasUnavailableItems) {
        ElMessage.error('选中的商品中包含已下架商品，请重新选择')
        return
      }
      if (this.beOutOfStock) {
        ElMessage.error('选中的商品中包含库存不足商品，请重新选择')
        return
      }
      if (sessionStorage.getItem('token')) {
        this.addressVisible = true
        getAddressList().then(res => {
          this.addresses = res.data || []
        })
      } else {
        ElMessage.error('请先登录')
      }
    },
    // 选择收货地址并确认下单
    selectAddress(addressId) {
      this.addressId = addressId
      this.addressVisible = false
      ElMessageBox.confirm(
        `确定购买选中的 ${this.selectedItems.length} 件商品吗？共支付 ¥${this.selectedTotalPrice.toFixed(2)} 元`,
        '确认下单',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
      ).then(() => {
        this.buyBycart()
      }).catch(() => {
        ElMessage.info('已取消')
      })
    },
    // 跳转商品详情页
    goToProduct(productId, status) {
      if (status === '0') {
        this.$router.push("/product?id=" + productId)
      } else {
        ElMessage.error('该商品已下架')
      }
    }
  }
}
</script>

<style scoped>
.cart-management {
  padding: 24px 40px;
  max-width: 1600px;
  margin: 0 auto;
}
.cart-card {
  border-radius: var(--pet-radius-base);
  box-shadow: var(--pet-shadow-base);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--pet-text);
}
.title-icon {
  font-size: 24px;
}
.product-info {
  display: flex;
  align-items: center;
}
.product-image {
  width: 80px;
  height: 80px;
  border-radius: var(--pet-radius-sm);
  margin-right: 16px;
  object-fit: cover;
  background: var(--pet-bg-grey);
}
.product-detail {
  cursor: pointer;
  transition: opacity 0.2s;
}
.product-detail:hover {
  opacity: 0.8;
}
.product-name {
  font-weight: 700;
  color: var(--pet-text);
  margin-bottom: 8px;
  font-size: 15px;
}
.product-price {
  color: var(--pet-error);
  font-weight: 700;
  margin-bottom: 8px;
  font-size: 15px;
}
.product-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.stock-out {
  color: var(--pet-error);
  font-weight: 700;
}
.subtotal-price {
  color: var(--pet-error);
  font-weight: 700;
  font-size: 16px;
}
.disabled-text {
  color: var(--pet-text-disable);
  text-decoration: line-through;
}

/* 底部结算栏 */
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--pet-border);
}
.selection-info {
  margin-right: 24px;
  color: var(--pet-text-secondary);
  font-size: 14px;
}
.total-price {
  margin-right: 24px;
  font-size: 16px;
  color: var(--pet-text);
}
.total-price .price {
  font-size: 24px;
  font-weight: 800;
  color: var(--pet-error);
}
.checkout-btn {
  padding: 0 32px;
  font-weight: 600;
}
.empty-tip {
  padding: 20px 0;
}
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.address-list :deep(.address-item) {
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: var(--pet-radius-base);
  transition: all 0.2s;
}
.address-list :deep(.address-item:hover) {
  border-color: var(--pet-primary-light);
}
.address-list :deep(.address-item.selected) {
  border-color: var(--pet-primary);
  background: var(--pet-bg-hover);
}
</style>
