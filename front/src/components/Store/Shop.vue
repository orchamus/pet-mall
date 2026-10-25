<template>
  <div class="shop-container">
    <!-- 商店基本信息 -->
    <div class="shop-header">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-image
              :src="this.BaseUrl + shop.logo"
              fit="cover"
              class="shop-logo"
          ></el-image>
        </el-col>
        <el-col :span="20">
          <div class="shop-info">
            <h1>{{ shopNameText }}</h1>
            <div class="shop-meta">
              <el-tag :type="getShopStatusType(shop.status)">
                {{ getShopStatusText(shop.status) }}
              </el-tag>
            </div>
            <p class="shop-description">{{ shopDescText }}</p>
            <div class="shop-contact">
              <el-icon><Location /></el-icon>
              <span>{{ shopAddressText }}</span>
              <el-icon class="contact-icon"><Message /></el-icon>
              <span>{{ shop.contactEmail || '暂无联系方式' }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <el-row :gutter="20">
        <el-col
            :span="6"
            v-for="product in products.list"
            :key="product.productId"
            @click="goProduct(product.productId)"
            class="product-item"
        >
          <el-card :body-style="{ padding: '0px' }">
            <div class="product-image-container">
              <el-image
                  v-if="product.productMedias && product.productMedias.length > 0"
                  :src="this.BaseUrl + product.productMedias[0].media.url"
                  fit="cover"
                  class="product-image"
              ></el-image>
              <el-tag class="product-status" :type="product.status === '0' ? 'success' : 'info'">
                {{ product.status === '0' ? '上架中' : '已下架' }}
              </el-tag>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span class="stock">库存: {{ product.stockQuantity }}</span>
              </div>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-footer">
                <el-tag>{{ product.category === '0' ? '宠物活体' : '周边用品' }}</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="products.total"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { Location, Message, Search } from '@element-plus/icons-vue'
import {getShopAndProducts} from "../../api/shopManagement/products";
import { normalizeShopName, normalizeShopDesc, normalizeAddress } from "../../utils/text";

export default {
  components: {
    Location,
    Message,
    Search
  },
  data() {
    return {
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 当前店铺信息
      shop: {},
      // 店铺商品列表与总数
      products: {
        list: [],
        total: 0,
      },
      // 搜索关键词（当前未使用）
      searchQuery: '',
      // 分类筛选（当前未使用）
      categoryFilter: '',
      // 状态筛选（当前未使用）
      statusFilter: '',
      // 当前页码
      currentPage: 1,
      // 每页条数
      pageSize: 12
    }
  },
  computed: {
    // 店铺名称文本
    shopNameText() {
      return normalizeShopName(this.shop?.shopName)
    },
    // 店铺描述文本
    shopDescText() {
      return normalizeShopDesc(this.shop?.description)
    },
    // 店铺地址文本
    shopAddressText() {
      return normalizeAddress(this.shop?.address)
    }
  },
  // 组件创建时获取店铺 ID 并加载数据
  created() {
    this.shopId = this.$route.query.id;
    this.fetchShop();
  },
  methods: {
    // 加载店铺信息及商品列表
    fetchShop() {
      getShopAndProducts(this.currentPage, this.pageSize,this.shopId).then(res => {
        this.shop = res.data.shop;
        this.products.list = res.data.products.records;
        this.products.total = res.data.products.total;
      })
    },
    // 根据店铺状态获取标签样式
    getShopStatusType(status) {
      switch (status) {
        case 0: // 关闭
          return 'danger'; // 红色
        case 1: // 营业中
          return 'success'; // 绿色
        case 2: // 暂停营业
          return 'warning'; // 黄色
        default:
          return 'info'; // 蓝色（未知状态）
      }
    },
    // 根据店铺状态获取状态文本
    getShopStatusText(status) {
      switch (status) {
        case 0: // 关闭
          return '已关闭';
        case 1: // 营业中
          return '营业中';
        case 2: // 暂停营业
          return '暂停营业';
        default:
          return '未知状态';
      }
    },
    // 跳转商品详情页
    goProduct(productId) {
      this.$router.push({path: '/product', query: {id: productId}})
      console.log(productId);
    },
    // 分页页码变化
    handlePageChange(){
      getShopAndProducts(this.currentPage, this.pageSize,this.shopId).then(res => {
        this.products.list = res.data.products.records;
        this.products.total = res.data.products.total;
      })
    }
  }
}
</script>

<style scoped>
.shop-container {
  max-width: 1600px;
  margin: 0 20px;
  padding: 20px;
}

/* 店铺头部信息 */
.shop-header {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.shop-logo {
  width: 100%;
  height: 150px;
  border-radius: 8px;
}

.shop-info h1 {
  margin-top: 0;
  color: #333;
}

.shop-meta {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.create-time {
  margin-left: 15px;
  color: #999;
  font-size: 14px;
}

.shop-description {
  color: #666;
  line-height: 1.6;
}

.shop-contact {
  margin-top: 15px;
  color: #666;
  display: flex;
  align-items: center;
}

.contact-icon {
  margin-left: 15px;
}

.product-filter {
  margin-bottom: 20px;
}

/* 商品列表 */
.product-list {
  margin-top: 20px;
}

.product-item {
  margin-bottom: 20px;
  cursor: pointer;
}

.product-image-container {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.product-image:hover {
  transform: scale(1.05);
}

.product-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.product-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.current-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.stock {
  color: #999;
  font-size: 14px;
}

.product-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

/* 分页 */
.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>
