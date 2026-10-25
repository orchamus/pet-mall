<template>
  <header class="store-header">
    <!-- 左侧 Logo 与品牌信息 -->
    <router-link to="/">
      <div class="header-left">
        <div class="logo-wrap">
          <img src="../assets/logo.svg" alt="宠物商店" class="logo" />
        </div>
        <div class="brand-text">
          <h1 class="store-name">萌宠乐园</h1>
          <span class="store-slogan">温暖 · 陪伴 · 信赖</span>
        </div>
      </div>
    </router-link>

    <!-- 中间导航栏 -->
    <div class="header-center">
      <nav class="header-nav">
        <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">首页</router-link>
        <router-link to="/cart" class="nav-link" v-if="user.userType === '0'">购物车</router-link>
        <router-link to="/customerOrder" class="nav-link" v-if="user.userType === '0'">我的订单</router-link>
        <router-link to="/personalCenter" class="nav-link">个人中心</router-link>
      </nav>
    </div>

    <!-- 右侧用户头像下拉菜单 -->
    <div class="header-right">
      <!-- 已登录状态 -->
      <el-dropdown trigger="hover" v-if="user.userName !== ''" >
        <div class="user-avatar">
          <el-avatar :size="42" :src="this.baseUrl + user.avatar" :key="user.avatar" />
          <span class="user-name">{{ user.nickName || user.userName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-icon><User /></el-icon>
              <span>{{user.nickName}}</span>
            </el-dropdown-item>
            <router-link to="/personalCenter"><el-dropdown-item>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item></router-link>
            <router-link to="/AddressList" v-if="user.userType === '0'"><el-dropdown-item>
              <el-icon><Location /></el-icon>
              <span>地址管理</span>
            </el-dropdown-item></router-link>
            <router-link to="/customerOrder" v-if="user.userType === '0'">
            <el-dropdown-item>
              <el-icon><Tickets /></el-icon>
              <span>我的订单</span>
            </el-dropdown-item></router-link>
            <router-link to="/cart"><el-dropdown-item v-if="user.userType === '0'">
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
            </el-dropdown-item></router-link>
            <router-link to="/merchant"> <el-dropdown-item v-if="user.userType === '1'">
              <el-icon><Shop /></el-icon>
              <span>商店管理</span>
            </el-dropdown-item></router-link>
            <router-link to="/shopManage"> <el-dropdown-item v-if="user.userType === '2'">
              <el-icon><Shop /></el-icon>
              <span>商店管理</span>
            </el-dropdown-item></router-link>
            <router-link to="/productManage"> <el-dropdown-item v-if="user.userType === '2'">
              <el-icon><GoodsFilled/></el-icon>
              <span>商品管理</span>
            </el-dropdown-item></router-link>
            <router-link to="/memberManage"> <el-dropdown-item v-if="user.userType === '2'">
              <el-icon><UserFilled /></el-icon>
              <span>会员管理</span>
            </el-dropdown-item></router-link>
            <el-dropdown-item divided @click="logout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <!-- 未登录状态 -->
      <el-dropdown trigger="hover" v-if="user.userName === ''">
        <div class="user-avatar">
          <el-avatar :size="42" :src="this.baseUrl + '/media/userAvatar/default.jpg'" />
          <span class="user-name">登录 / 注册</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/loginAndUser"><el-dropdown-item>登录</el-dropdown-item></router-link>
            <router-link to="/register"><el-dropdown-item>注册</el-dropdown-item></router-link>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script>
import {userLogout} from "../api/loginAndUser";
import {ElMessage} from "element-plus";
import { ArrowDown } from "@element-plus/icons-vue";

export default {
  name: "Header",
  components: { ArrowDown },
  data() {
    return {
      // 后端 API 基础地址
      baseUrl: import.meta.env.VITE_API_BASE_URL,
      // 搜索关键词（当前未使用）
      searchQuery: "",
    };
  },
  // 从父组件接收当前用户信息
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  methods: {
    // 退出登录：调用 API、清除缓存并跳转登录页
    logout() {
      userLogout().then((res) => {
        if(res.data){
          this.$router.push("/loginAndUser");
          ElMessage.success(res.message);
          sessionStorage.clear();
          localStorage.clear();

        }else{
          ElMessage.error("退出失败");
        }
      });
    },
  },
};
</script>

<style scoped>
.store-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 72px;
  background: linear-gradient(90deg, #ffffff 0%, #fff9f6 100%);
  box-shadow: var(--pet-shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

/* 左侧 Logo 区域 */
.header-left {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-wrap {
  width: 48px;
  height: 48px;
  border-radius: var(--pet-radius-circle);
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-pink));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--pet-shadow-sm);
}

.logo {
  width: 32px;
  height: 32px;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.store-name {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: var(--pet-primary);
  line-height: 1.2;
}

.store-slogan {
  font-size: 12px;
  color: var(--pet-text-grey);
  letter-spacing: 1px;
}

/* 中间导航栏 */
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-nav {
  display: flex;
  gap: 8px;
  background: var(--pet-bg);
  padding: 6px;
  border-radius: var(--pet-radius-lg);
}

.nav-link {
  padding: 8px 22px;
  border-radius: var(--pet-radius-base);
  color: var(--pet-text-secondary);
  font-size: 15px;
  font-weight: 500;
  transition: all 0.25s;
}

.nav-link:hover,
.nav-link.active {
  color: var(--pet-primary);
  background: var(--pet-bg-white);
  box-shadow: var(--pet-shadow-sm);
}

/* 右侧用户区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px;
  border-radius: var(--pet-radius-lg);
  transition: background 0.2s;
}

.user-avatar:hover {
  background: var(--pet-bg-hover);
}

.user-name {
  font-size: 14px;
  color: var(--pet-text-secondary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式 */
@media (max-width: 992px) {
  .store-header {
    padding: 0 20px;
  }
  .header-center {
    display: none;
  }
}

@media (max-width: 576px) {
  .store-slogan,
  .user-name {
    display: none;
  }
}
</style>
