<template>
  <div class="product-management">
    <!-- 页面头部：标题与搜索操作 -->
    <div class="header">
      <h2>商店管理</h2>
      <div class="actions">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商店名称"
            clearable
            style="width: 200px; margin-left: 10px;"
            @clear="fetchShops"
            @keyup.enter="getSearchShops">
          <el-button slot="append" icon="el-icon-search" @click="fetchShops"></el-button>
        </el-input>
      </div>
    </div>

    <!-- 商店列表表格 -->
    <el-table
        :data="showShops"
        border
        style="width: 100%"
        v-loading="loading">
      <el-table-column prop="shopId" label="ID" width="80"></el-table-column>
      <el-table-column prop="userId" label="店主ID" width="80"></el-table-column>
      <el-table-column prop="shopName" label="商店名称" width="120"></el-table-column>
      <el-table-column prop="description" label="商店描述" width="250"></el-table-column>
      <el-table-column prop="contactEmail" label="邮箱" width="180" />
      <el-table-column prop="address" label="地址" width="270"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="360"></el-table-column>
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
import {getAllShop} from "../../api/shopManagement/shop";
import {ElMessage} from "element-plus";

export default {
  name: 'ShopManagement',
  data() {
    return {
      // 全部商店数据（保留原始列表）
      shops: [],
      // 当前展示的商店数据（支持搜索过滤）
      showShops: [],
      // 表格加载状态
      loading: false,
      // 搜索关键词
      searchKeyword: '',
      // 分页参数
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    };
  },

  // 组件挂载后加载商店列表
  mounted() {
    this.fetchShops();
  },

  methods: {
    /**
     * 重置到第一页并加载商店数据
     */
    fetchShops() {
      this.pageNum = 1;
      this.pagination.current = 1;
      this.loadShops();
    },

    /**
     * 调用接口加载商店分页数据
     */
    loadShops() {
      this.loading = true;
      getAllShop(this.pagination.current, this.pagination.size, this.searchKeyword.trim())
          .then(res => {
            if (res.code === 200) {
              this.showShops = res.data.records;
              this.pagination.total = res.data.total;

              // 如果当前页无数据且不是第一页，则回退一页重新加载
              if (res.data.records.length === 0 && this.pagination.current > 1) {
                this.pagination.current--;
                this.loadShops(); // Reload previous page
                ElMessage.info('没有更多商店了');
              }
            } else {
              this.showShops = [];
              ElMessage.error(res.message);
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },

    /**
     * 搜索商店：重置分页后重新加载
     */
    getSearchShops() {
      this.pagination.current = 1;
      this.loadShops();
    },

    /**
     * 每页条数变化
     * @param {Number} size 新的每页条数
     */
    handleSizeChange(size) {
      this.pagination.size = size;
      this.loadShops();
    },

    /**
     * 当前页码变化
     * @param {Number} current 新的页码
     */
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.loadShops();
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
