<template>
  <div class="product-management">
    <!-- 页面头部：标题与搜索操作 -->
    <div class="header">
      <h2>用户管理</h2>
      <div class="actions">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名称"
            clearable
            style="width: 200px; margin-left: 10px;"
            @clear="fetchUsers"
            @keyup.enter="getSearchUsers">
          <el-button slot="append" icon="el-icon-search" @click="fetchUsers"></el-button>
        </el-input>
      </div>
    </div>

    <!-- 用户列表表格 -->
    <el-table
        :data="showUsers"
        border
        style="width: 100%"
        v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="userName" label="用户名" width="200"></el-table-column>
      <el-table-column
          prop="userType"
          label="用户类型"
          width="200"
          :formatter="formatUserType">
      </el-table-column>
      <el-table-column prop="nickName" label="昵称" width="200"></el-table-column>
      <el-table-column prop="sex" label="性别" width="120">
        <template #default="{row}">
          {{ formatSexDisplay(row.sex) }}
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱"/>
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
import {getAllUser} from "../../api/loginAndUser";
import {ElMessage} from "element-plus";

export default {
  name: 'UserManagement',
  data() {
    return {
      // 全部用户数据（保留原始列表）
      users: [],
      // 当前展示的用户数据（支持搜索过滤）
      showUsers: [],
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

  // 组件挂载后加载用户列表
  mounted() {
    this.fetchUsers();
  },

  methods: {
    /**
     * 重置到第一页并加载用户数据
     */
    fetchUsers() {
      this.pagination.current = 1;
      this.loadUsers();
    },

    /**
     * 调用接口加载用户分页数据
     */
    loadUsers() {
      this.loading = true;
      getAllUser(this.pagination.current, this.pagination.size, this.searchKeyword)
          .then(res => {
            if (res.code === 200) {
              this.showUsers = res.data.records;
              this.pagination.total = res.data.total;

              // 如果当前页无数据且不是第一页，则回退一页重新加载
              if (res.data.records.length === 0 && this.pagination.current > 1) {
                this.pagination.current--;
                this.loadUsers();
                ElMessage.info('没有更多商店了');
              }
            } else {
              this.showUsers = [];
              ElMessage.error(res.message);
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },

    /**
     * 搜索用户：重置分页后重新加载
     */
    getSearchUsers() {
      this.pagination.current = 1;
      this.loadUsers();
    },

    /**
     * 每页条数变化
     * @param {Number} size 新的每页条数
     */
    handleSizeChange(size) {
      this.pagination.size = size;
      this.loadUsers();
    },

    /**
     * 当前页码变化
     * @param {Number} current 新的页码
     */
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.loadUsers();
    },

    /**
     * 格式化用户类型显示
     * @param {Object} row 当前行数据
     * @param {Object} column 当前列配置
     * @param {Number} cellValue 单元格值
     * @returns {String} 用户类型文本
     */
    formatUserType(row, column, cellValue) {
      const typeMap = {
        0: '普通用户',
        1: '商家管理员',
        2: '超级管理员'
      };
      return typeMap[cellValue] || '未知类型';
    },

    /**
     * 格式化性别显示
     * @param {Number|String} sexValue 性别值
     * @returns {String} 性别文本
     */
    formatSexDisplay(sexValue) {
      // 确保处理各种可能的输入类型
      const value = parseInt(sexValue);
      if (isNaN(value)) return '未知';

      const sexMap = {
        0: '男',
        1: '女',
        2: '其他'
      };
      return sexMap[value] || '未知';
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
