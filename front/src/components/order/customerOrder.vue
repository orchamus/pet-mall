<template>
  <div class="order-management">
    <el-card class="order-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">📦</span>
            <span>我的订单</span>
          </div>
        </div>
      </template>

      <!-- 订单查询条件区域 -->
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="order-filter">
          <el-form-item label="订单号">
            <el-input v-model="queryParams.orderId" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable style="width: 160px">
              <el-option label="待发货" value="1"></el-option>
            <el-option label="已发货" value="2"></el-option>
            <el-option label="待评价" value="7"></el-option>
            <el-option label="已完成" value="3"></el-option>
            <el-option label="已取消" value="4"></el-option>
            <el-option label="退款中" value="5"></el-option>
            <el-option label="已退款" value="6"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="下单时间">
            <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 订单列表表格 -->
      <el-table :data="orderList" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="orderId" label="订单号" width="170" />
        <el-table-column label="商品封面" width="100" align="center">
          <template #default="scope">
            <el-image
              preview-teleported
              :src="getProductImage(scope.row.product)"
              :preview-src-list="[getProductImage(scope.row.product)]"
              class="order-product-image"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="200">
          <template #default="scope">
            <div class="product-info">
              <el-tooltip
                :content="scope.row.product?.name"
                placement="top"
                :disabled="!scope.row.product?.name || scope.row.product.name.length <= 20">
                <span class="text-ellipsis">{{ scope.row.product?.name?.length > 20 ? scope.row.product.name.substring(0, 20) + '...' : (scope.row.product?.name || '-') }}</span>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column label="单价" width="100" align="right">
          <template #default="scope">
            ¥{{ scope.row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="总价" width="110" align="right">
          <template #default="scope">
            <span class="total-price">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" effect="light">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 1"
              type="danger"
              size="small"
              plain
              @click="handleCancelOrder(scope.row)"
            >取消订单</el-button>
            <el-button
              v-if="scope.row.status === 2"
              type="primary"
              size="small"
              @click="handleConfirmReceipt(scope.row)"
            >确认收货</el-button>
            <el-button
              v-if="scope.row.status === 7"
              type="success"
              size="small"
              @click="openReviewDialog(scope.row)"
            >评价</el-button>
            <el-button
              v-if="[1, 2, 3, 7].includes(scope.row.status)"
              type="warning"
              size="small"
              :disabled="scope.row.status === 5 || scope.row.status === 6"
              @click="handleApplyRefund(scope.row)"
            >{{ scope.row.status === 5 || scope.row.status === 6 ? '退款中' : '申请退款' }}</el-button>
            <span v-if="![1, 2, 3, 5, 6, 7].includes(scope.row.status)" class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>

    <!-- 评价弹窗 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="发表评价"
      width="520px"
      destroy-on-close
    >
      <el-input
        type="textarea"
        v-model="reviewForm.content"
        :rows="5"
        placeholder="分享您的购买体验（10-500字）"
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  cancelOrder,
  changOrderStatusToNext,
  getOrderByToken,
  requestRefund,
  searchOrdersForOrderCustomer
} from "../../api/order/order";
import { addReviews, hasReviewed } from "../../api/shopManagement/products";
import { ElMessage } from "element-plus";
import { getProductImage } from "../../utils/image";

export default {
  data() {
    return {
      // 表格加载状态
      loading: false,
      // 订单列表数据
      orderList: [],
      // 当前是否为搜索模式
      isSearch: false,
      // 查询参数
      queryParams: {
        orderId: '',
        productName: '',
        status: '',
        dateRange: [],
      },
      // 订单总数
      total: 0,
      // 当前页码
      pageNum: 1,
      // 每页条数
      pageSize: 5,
      // 评价弹窗显示状态
      reviewDialogVisible: false,
      // 评价表单
      reviewForm: {
        orderId: '',
        productId: null,
        content: ''
      },
      // 订单状态文本映射
      statusMap: {
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消',
        5: '退款中',
        6: '已退款',
        7: '待评价'
      },
      // 订单状态标签样式映射
      statusTagType: {
        1: 'info',
        2: 'warning',
        3: 'success',
        4: 'danger',
        5: 'warning',
        6: 'info',
        7: 'success'
      }
    }
  },
  // 组件创建时加载订单列表
  created() {
    this.fetchOrderList()
  },
  methods: {
    getProductImage,
    // 加载订单列表：搜索模式或普通分页模式
    fetchOrderList() {
      this.loading = true
      const apiCall = this.isSearch
        ? searchOrdersForOrderCustomer(this.queryParams, this.pageNum, this.pageSize)
        : getOrderByToken(this.pageNum, this.pageSize)

      apiCall.then(res => {
        if (res.code === 200) {
          this.orderList = res.data.records || []
          this.total = res.data.total || 0
        } else {
          ElMessage.error(this.isSearch ? "搜索订单失败" : "获取订单列表失败")
        }
      }).catch(() => {
        ElMessage.error(this.isSearch ? "搜索订单失败" : "获取订单列表失败")
      }).finally(() => {
        this.loading = false
      })
    },
    // 根据状态码获取状态文本
    getStatusText(status) {
      return this.statusMap[status] || '未知状态'
    },
    // 根据状态码获取标签样式
    getStatusTagType(status) {
      return this.statusTagType[status] || ''
    },
    // 确认收货：将订单状态推进到下一步
    handleConfirmReceipt(order) {
      this.$confirm('确认已收到商品了吗？确认后订单将完成。', '确认收货', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        changOrderStatusToNext(order.orderId).then(res => {
          if (res.data) {
            this.fetchOrderList()
            ElMessage.success("确认收货成功")
            // 收货成功后提示是否立即评价
            this.$confirm('订单已收货，是否立即评价？评价后将完成订单。', '提示', {
              confirmButtonText: '去评价',
              cancelButtonText: '暂不去',
              type: 'info'
            }).then(() => {
              this.openReviewDialog(order)
            }).catch(() => {})
          } else {
            ElMessage.error("确认收货失败")
          }
        })
      }).catch(() => {
        ElMessage.info('已取消操作')
      })
    },
    // 打开评价弹窗，先校验是否已评价
    openReviewDialog(order) {
      hasReviewed(order.orderId).then(res => {
        if (res.data) {
          ElMessage.warning('该订单已评价')
        } else {
          this.reviewForm.orderId = order.orderId
          this.reviewForm.productId = order.productId
          this.reviewForm.content = ''
          this.reviewDialogVisible = true
        }
      })
    },
    // 提交商品评价
    submitReview() {
      const text = this.reviewForm.content.trim()
      if (text.length < 10 || text.length > 500) {
        ElMessage.warning('评价内容长度需在 10-500 字符之间')
        return
      }
      addReviews(this.reviewForm.orderId, text, this.reviewForm.productId).then(res => {
        if (res.data) {
          ElMessage.success(res.message || '评价成功')
          this.reviewDialogVisible = false
          this.fetchOrderList()
        } else {
          ElMessage.error(res.message || '评价失败')
        }
      }).catch(() => {
        ElMessage.error('评价提交失败')
      })
    },
    // 每页条数变化
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchOrderList()
    },
    // 当前页码变化
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchOrderList()
    },
    // 申请退款
    handleApplyRefund(order) {
      this.$prompt('请输入退款理由', '申请退款', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请详细描述退款原因...',
        inputValidator: (value) => {
          if (!value || value.trim().length === 0) {
            return '退款理由不能为空'
          }
          if (value.length > 200) {
            return '退款理由不能超过200个字符'
          }
          return true
        },
        inputErrorMessage: '请输入有效的退款理由',
        type: 'warning'
      }).then(({ value }) => {
        requestRefund(order.orderId, value).then(res => {
          if (res.data) {
            this.fetchOrderList()
            ElMessage.success("申请退款成功")
          } else {
            ElMessage.error("申请退款失败")
          }
        }).catch(() => {
          ElMessage.error("申请退款失败")
        })
      }).catch(() => {})
    },
    // 搜索订单
    handleSearch() {
      this.pageNum = 1
      this.isSearch = true
      this.fetchOrderList()
    },
    // 重置查询条件
    resetQuery() {
      this.queryParams = {
        orderId: '',
        productName: '',
        status: '',
        dateRange: [],
      }
      this.pageNum = 1
      this.isSearch = false
      this.fetchOrderList()
    },
    // 取消订单
    handleCancelOrder(order) {
      this.$confirm('确认取消订单吗？取消后不可恢复。', '取消订单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelOrder(order.orderId).then(res => {
          if (res.data) {
            ElMessage.success("取消订单成功")
            this.fetchOrderList()
          } else {
            ElMessage.error("取消订单失败")
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.order-management {
  padding: 24px 40px;
  max-width: 1600px;
  margin: 0 auto;
}
.order-card {
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

/* 查询条件区域 */
.filter-container {
  margin-bottom: 20px;
  padding: 20px;
  background: var(--pet-bg);
  border-radius: var(--pet-radius-base);
}
.order-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 0;
}
.order-product-image {
  width: 60px;
  height: 60px;
  border-radius: var(--pet-radius-sm);
  object-fit: cover;
  background: var(--pet-bg-grey);
}
.product-info {
  display: flex;
  align-items: center;
}
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 100%;
}
.total-price {
  color: var(--pet-error);
  font-weight: 700;
}
.no-action {
  color: var(--pet-text-grey);
}

/* 分页区域 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
:deep(.el-textarea__inner) {
  min-height: 100px !important;
}
</style>
