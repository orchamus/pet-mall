<template>
  <div class="merchant-dashboard">
    <el-container>
      <!-- 侧边栏导航 -->
      <el-aside width="200px">
        <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            @select="handleMenuSelect">
          <el-menu-item index="shopInfo">
            <i class="el-icon-shop"></i>
            <span>店铺信息</span>
          </el-menu-item>
          <el-menu-item
              index="productList"
              v-if="hasShop"
              :disabled="!hasShop">
            <i class="el-icon-goods"></i>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item
              index="orderList"
              v-if="hasShop"
              :disabled="!hasShop">
            <i class="el-icon-tickets"></i>
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main>
        <div v-if="activeMenu === 'shopInfo'">
          <ShopInfo
              :hasShop.sync="hasShop"
              @shop-created="handleShopCreated" />
        </div>
        <div v-else-if="activeMenu === 'productList' && hasShop">
          <ProductList :shopId="shopId" />
        </div>
        <div v-else-if="activeMenu === 'orderList' && hasShop">
          <OrderList :shopId="shopId" />
        </div>
        <div v-else-if="!hasShop" class="no-shop-hint">
          <el-alert
              title="请先创建您的店铺"
              type="info"
              description="您需要先创建店铺才能使用商品管理和订单管理功能"
              show-icon
              :closable="false">
          </el-alert>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import ShopInfo from './ShopInfo.vue';
import ProductList from './ProductList.vue';
import OrderList from './OrderList.vue';

export default {
  name: 'MerchantDashboard',
  components: {
    ShopInfo,
    ProductList,
    OrderList
  },
  data() {
    return {
      // 当前激活的菜单项
      activeMenu: 'shopInfo',
      // 是否已创建店铺
      hasShop:'',
      // 店铺ID
      shopId: ''
    };
  },
  methods: {
    // 菜单选择切换
    handleMenuSelect(index) {
      this.activeMenu = index;
    },

    // 店铺创建成功后更新状态
    handleShopCreated() {
      this.hasShop = true;
    }
  }
};
</script>

<style scoped>
.merchant-dashboard {
  height: 100vh;
  display: flex;
}

.el-container {
  height: 100%;
}

/* 侧边栏 */
.el-aside {
  background-color: #f5f7fa;
}

.el-menu-vertical {
  height: 100%;
}

/* 主内容区 */
.el-main {
  padding: 20px;
  background-color: #fff;
}

.no-shop-hint {
  margin: 20px;
}
</style>
