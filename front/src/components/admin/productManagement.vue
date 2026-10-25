<template>
  <div class="product-management">
    <!-- 页面头部：标题与搜索操作 -->
    <div class="header">
      <h2>商品管理</h2>
      <div class="actions">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商品名称"
            clearable
            style="width: 200px; margin-left: 10px;"
            @clear="fetchProducts"
            @keyup.enter="getSearchProducts">
          <el-button slot="append" icon="el-icon-search" @click="fetchProducts"></el-button>
        </el-input>
      </div>
    </div>

    <!-- 商品列表表格 -->
    <el-table
        :data="showProducts"
        border
        style="width: 100%"
        v-loading="loading">
      <el-table-column prop="productId" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="商品名称"></el-table-column>
      <el-table-column prop="category" label="类别" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.category === '0' ? 'success' : 'primary'">
            {{ scope.row.category === '0' ? '宠物' : '宠物周边' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="120"/>
      <el-table-column prop="stockQuantity" label="库存" width="100"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160"></el-table-column>
      <el-table-column label="状态(上下架)" width="110">
        <template #default="scope">
          <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
          >
          </el-switch>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
    </el-pagination>
  </div>
</template>

<script>
import {getAllProductList, getProducBykeyword} from "../../api/shopManagement/products";
import {ElMessage} from "element-plus";

export default {
  name: 'ProductList',
  data() {
    return {
      // 全部商品数据（保留原始列表）
      products: [],
      // 当前展示的商品数据（支持搜索过滤）
      showProducts: [],
      // 表格加载状态
      loading: false,
      // 搜索关键词
      searchKeyword: '',
      // 当前页码（部分逻辑中使用）
      pageNum: 1,
      // 每页条数（部分逻辑中使用）
      pageSize: 10,
      // 是否处于搜索模式
      isSearching: false,
      // 搜索查询变量（预留）
      searchQuery: '',
      // 商品类别筛选
      category: '',
      // 分页参数
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    };
  },

  // 组件挂载后加载商品列表
  mounted() {
    this.fetchProducts();
  },

  methods: {
    /**
     * 重置到第一页并加载商品数据
     */
    fetchProducts() {
      this.pageNum = 1;
      this.pagination.current = 1;
      this.loadProducts();
    },

    /**
     * 调用接口加载商品分页数据
     * 存在搜索关键词时调用搜索接口，否则调用普通列表接口
     */
    async loadProducts() {
      this.loading = true;
      try {
        let res;
        if (this.searchKeyword) {
          res = await getProducBykeyword(this.pagination.current, this.pagination.size, this.searchKeyword);
        } else {
          res = await getAllProductList(this.pagination.current, this.pagination.size, this.category);
        }

        if (res.code === 200) {
          this.showProducts = res.data.records;
          this.pagination.total = res.data.total;
        } else {
          ElMessage.error(res.message);
          this.showProducts = [];
        }
      } catch (error) {
        ElMessage.error('加载商品失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * 搜索商品：重置分页后重新加载
     */
    getSearchProducts() {
      this.pagination.current = 1;
      this.loadProducts();
    },

    /**
     * 每页条数变化
     * @param {Number} size 新的每页条数
     */
    handleSizeChange(size) {
      this.pagination.size = size;
      this.loadProducts();
    },

    /**
     * 当前页码变化
     * @param {Number} current 新的页码
     */
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.loadProducts();
    }
  }
};
</script>

<style scoped>
/* 页面容器 */
.product-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

/* 页面头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 操作区 */
.actions {
  display: flex;
  align-items: center;
}

/* 表格区域 */
.el-table {
  margin-top: 15px;
}
</style>
