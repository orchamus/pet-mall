<template>
  <div class="home">
    <!-- 顶部公共导航头 -->
    <Header :user="user" />
    <!-- 子路由页面渲染区域 -->
    <RouterView/>

    <!-- 右下角浮动按钮组 -->
    <div class="floating-buttons">
      <!-- 查找附近商店按钮 -->
      <el-tooltip
          effect="dark"
          content="查找附近商店"
          placement="left"
      >
        <div
            class="floating-button shop-button"
            @click="findNearbyShops"
        >
          <el-icon :size="24"><AddLocation /></el-icon>
        </div>
      </el-tooltip>

      <!-- 联系AI客服按钮 -->
      <el-tooltip
          effect="dark"
          content="AI客服"
          placement="left"
      >
        <div
            class="floating-button ai-button"
            @click="contactAIService"
        >
          <el-icon :size="24"><ChatRound /></el-icon>
        </div>
      </el-tooltip>
    </div>

    <!-- 页面底部标注 -->
    <div class="footer-note">
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue'
import {getUserInfo} from "../api/loginAndUser";
import {AddLocation, ChatRound} from "@element-plus/icons-vue";


export default {
  data(){
    return {
      // 后端 API 基础地址
      baseUrl: import.meta.env.VITE_API_BASE_URL,
      // 当前登录用户信息
      user:{
        userName:'',
        email:'',
        sex:'',
        nickName:'',
        userType:'',
        avatar:''
      }
    }
  },
  components: {
    AddLocation,
    ChatRound,
    Header,
  },
  // 组件挂载后获取当前登录用户信息
  mounted(){
    if(sessionStorage.getItem('token'))
      getUserInfo().then(res=>{
        this.user=res.data;
        // 为头像添加时间戳，避免浏览器缓存
        this.user.avatar = this.user.avatar + "?" + new Date().getTime().toString();
      })
  },

  methods:{
    // 跳转附近商店地图页
    findNearbyShops(){
      this.$router.push("/petShopAddress");
    },

    // 跳转 AI 客服页面
    contactAIService(){
      this.$router.push("/ai");
    }
  }
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  max-width: 1600px;
  position: relative;
  padding-bottom: 40px; /* 为底部标注留出空间 */
}

/* 浮动按钮组 */
.floating-buttons {
  position: fixed;
  right: 30px;
  bottom: 30px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  z-index: 999;

  .floating-button {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    color: white;

    &:hover {
      transform: scale(1.1);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
    }

    .el-icon {
      transition: all 0.3s ease;
    }
  }

  .shop-button {
    background: linear-gradient(135deg, var(--pet-primary), var(--pet-primary-light));

    &:hover {
      background: linear-gradient(135deg, var(--pet-primary-dark), var(--pet-primary));

      .el-icon {
        transform: rotate(15deg);
      }
    }
  }

  .ai-button {
    background: linear-gradient(135deg, var(--pet-green), #66BB6A);

    &:hover {
      background: linear-gradient(135deg, #43A047, var(--pet-green));

      .el-icon {
        transform: scale(1.2);
      }
    }
  }
}

.footer-note {
  position: fixed;
  bottom: 10px;
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: #888;
}
</style>
