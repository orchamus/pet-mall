<template>
  <div class="order-management-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>

      <!-- 搜索和筛选区域 -->
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="订单号">
            <el-input v-model="queryParams.orderId" placeholder="请输入订单号" clearable></el-input>
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="queryParams.status" placeholder="请选择订单状态"  style="width: 200px">
              <el-option label="待发货" value="1"></el-option>
              <el-option label="已发货" value="2"></el-option>
              <el-option label="已完成" value="3"></el-option>
              <el-option label="已取消" value="4"></el-option>
              <el-option label="退款中" value="5"></el-option>
              <el-option label="已退款" value="6"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="下单时间范围">
            <el-date-picker
                v-model="queryParams.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 订单表格 -->
      <el-table
          :data="orderList"
          border
          style="width: 100%"
          v-loading="loading">
        <el-table-column prop="orderId" label="订单号" width="200" sortable="custom"></el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100" sortable="custom"></el-table-column>
        <el-table-column label="商品信息" width="220">
          <template #default="scope">
            <el-tooltip
                :content="scope.row.product.name"
                placement="top"
                :disabled="scope.row.product.name.length <= 10">
              <span> {{ scope.row.product.name.length > 10 ? scope.row.product.name.substring(0, 10) + '...' : scope.row.product.name }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="总价" width="120" sortable="custom">
          <template #default="scope">
            ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" sortable="custom">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ formatOrderStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="180" sortable="custom">
          <template #default="scope">
            {{ scope.row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button
                size="small"
                type="primary"
                v-if="scope.row.status === 1"
                @click="handleShipOrder(scope.row)">
              发货
            </el-button>
            <el-button
                size="small"
                type="danger"
                v-if="scope.row.status === 5"
                @click="handleProcessRefund(scope.row)">
              处理退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParams.pageNum"
            :page-sizes="[10, 20, 30, 2]"
            :page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
        title="订单详情"
        v-model="detailDialogVisible"
        width="70%"
        :close-on-click-modal="false">
      <div v-if="currentOrder" class="order-detail-container">
        <!-- 订单基本信息 -->
        <el-descriptions title="订单信息" border :column="2">
          <el-descriptions-item label="订单号">{{ currentOrder.orderId }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(currentOrder.status)">
              {{ formatOrderStatus(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime || '未支付' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ currentOrder.deliveryTime || '未发货' }}</el-descriptions-item>
          <el-descriptions-item label="收货时间">{{ currentOrder.receiveTime || '未收货' }}</el-descriptions-item>
          <el-descriptions-item label="关闭时间">{{ currentOrder.closeTime || '未关闭' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 商品信息 -->
        <el-card class="goods-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>商品信息</span>
            </div>
          </template>
          <el-table :data="[currentOrder]" border>
            <el-table-column prop="product.name" label="商品名称" width="200"></el-table-column>
            <el-table-column prop="price" label="单价" width="120">
              <template #default="scope">
                <span>¥{{ scope.row.price.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="scope">
                ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
          <div class="total-amount">
            合计：<span class="amount">¥{{ (currentOrder.price * currentOrder.quantity).toFixed(2) }}</span>
          </div>
        </el-card>

        <!-- 收货地址 -->
        <el-card class="address-card" shadow="never" v-if="currentOrder.addressId">
          <h2>他的收获地址</h2>
          <AddressItem
              :key="address.addressId"
              :address="address"
              :show-actions="false"
          />
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button
              type="primary"
              v-if="currentOrder.status === 1"
              @click="handleShipOrder(currentOrder)">
            发货
          </el-button>
          <el-button
              type="danger"
              v-if="currentOrder.status === 5"
              @click="handleProcessRefund(currentOrder)">
            处理退款
          </el-button>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import {
  changOrderStatusToNext, getOrderRefundByOrderId,
  getOrdersByTokenForOrderManagement, reviewRefund,
  searchOrdersForOrder
} from "../../api/order/order";
import {getAddressById} from "../../api/PersonalCenter/Address";
import AddressItem from "../PersonalCenter/AddressItem.vue";
import {ElMessage} from "element-plus";
export default {
  components: {
    AddressItem
  },
  data() {
    return {
      // 查询参数
      queryParams: {
        orderId: '',
        productName: '',
        status: '',
        dateRange: [],
        pageNum: 1,
        pageSize: 10,
        sortField: 'createdAt',
        sortOrder: 'descending'
      },
      // 当前是否为搜索模式
      isSearching: false,
      // 当前订单的收货地址
      address:{},
      // 订单列表
      orderList: [],
      // 总条数
      total: 0,
      // 加载状态
      loading: false,
      // 当前操作的订单
      currentOrder: null,
      // 详情对话框显示状态
      detailDialogVisible: false,
    }
  },
  // 组件创建时加载订单列表
  created() {
    this.getOrderList()
  },
  methods: {
    /**
     * 获取订单列表：普通分页或搜索模式
     */
    getOrderList() {
      if(!this.isSearching){
        getOrdersByTokenForOrderManagement(this.queryParams.pageNum, this.queryParams.pageSize).then(res => {
          this.orderList = res.data.records
          this.total = res.data.total
        })
      }else{
        searchOrdersForOrder(this.queryParams).then(res => {
          this.orderList = res.data.records
          this.total = res.data.total
        })
      }
    },

    // 处理搜索
    handleSearch() {
      this.queryParams.pageNum = 1
      this.isSearching = true
      this.getOrderList()
    },

    // 重置查询条件
    resetQuery() {
      this.queryParams = {
        orderId: '',
        productName: '',
        status: '',
        dateRange: [],
        pageNum: 1,
        pageSize: 10,
        sortField: 'createdAt',
        sortOrder: 'descending'
      }
      this.isSearching = false
      this.getOrderList()
    },

    // 每页条数变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getOrderList()
    },

    // 当前页码变化
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getOrderList()
    },

    // 查看订单详情并获取收货地址
    handleViewDetail(row) {
      this.currentOrder = row
      getAddressById(row.addressId).then(res => {
        this.address = res.data
        this.detailDialogVisible = true
      })
    },

    // 发货：将订单状态推进到下一步
    handleShipOrder(row){
      this.$confirm('确定发货该订单吗？',{ title: '提示' ,confirmButtonText: '确定',cancelButtonText: '取消' }).then(() => {
        changOrderStatusToNext(row.orderId).then(res => {
          if(res.data){
            ElMessage.success('发货成功')
          }else{
            ElMessage.error('发货失败')
          }
          this.getOrderList()
        })
      }).catch(() => {
        ElMessage.info('已取消操作')
      })
    },

    // 处理退款申请
    handleProcessRefund(row) {
      this.currentOrder = row
      getOrderRefundByOrderId(row.orderId).then(res => {
        this.$confirm('退款理由：'+res.data.refundReason,{
          title:'是否同意退款申请?' ,
          confirmButtonText: '通过',
          cancelButtonText: '拒绝' ,
          distinguishCancelAndClose: true }).then(() => {
          // 用户点击"通过"
          this.handleApproveRefund(res.data.refundId, row.orderId);
        }).catch((action) => {
          if (action === 'cancel') {
            // 用户点击"拒绝"时执行的逻辑
            this.handleRejectRefund(res.data.refundId, row.orderId);
          }
        })
      })
    },

    // 通过退款申请
    handleApproveRefund(refundId, orderId) {
      reviewRefund(refundId, orderId, true).then(res => {
        if(res.data){
          ElMessage.success('退款申请已通过')
        }else{
          ElMessage.error('退款申请失败')
        }
        this.getOrderList()
      })
    },
    // 拒绝退款申请
    handleRejectRefund(refundId, orderId){
       reviewRefund(refundId, orderId, false).then(res => {
         if(res.data){
           ElMessage.success('退款申请已拒绝')
         }else{
           ElMessage.error('退款申请失败')
         }
         this.getOrderList()
       })
    },

    // 格式化订单状态
    formatOrderStatus(status) {
      const statusMap = {
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消',
        5: '退款中',
        6: '已退款'
      }
      return statusMap[status] || '未知状态'
    },

    /**
     * 获取状态标签类型
     * @param {Number} status 订单状态码
     * @returns {String} 标签类型
     */
    getStatusTagType(status) {
      const typeMap = {
        1: 'warning',
        2: '',
        3: 'success',
        4: 'info',
        5: 'danger',
        6: 'info'
      }
      return typeMap[status] || ''
    },

  }
}
</script>

<style scoped>
.order-management-container {
  padding: 20px;
}

/* 查询条件区域 */
.filter-container {
  margin-bottom: 20px;
}

/* 分页区域 */
.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>
