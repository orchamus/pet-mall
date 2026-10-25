<template>
  <div class="store-container">
    <!-- 顶部搜索区 -->
    <div class="search-section">
      <div class="search-box">
        <el-icon class="search-icon"><Search /></el-icon>
        <el-input
          v-model="searchQuery"
          placeholder="搜索宠物或商品..."
          class="search-input"
          clearable
          @clear="fetchProducts"
          @keyup.enter="getSearchProducts"
        />
        <el-button type="primary" class="search-btn" @click="getSearchProducts">搜索</el-button>
      </div>
    </div>

    <!-- 分类与轮播图区域 -->
    <div class="category-banner-section">
      <!-- 左半部分 - 商品分类 -->
      <div class="category-section card">
        <h2 class="section-title">
          <span class="title-dot"></span>
          快速服务
        </h2>
        <div class="category-list">
          <div
            v-for="item in serviceNavs"
            :key="item.id"
            class="category-item"
            :class="{ active: activeService === item.id }"
            @click="handleServiceClick(item)"
          >
            <span class="category-icon">{{ item.icon }}</span>
            <span class="category-name">{{ item.name }}</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 右半部分 - 图片走马灯 -->
      <div class="banner-section card">
        <el-carousel :interval="4000" type="card" height="320px" indicator-position="outside">
          <el-carousel-item v-for="banner in banners" :key="banner.id" @click="gotoWebSite(banner.url)">
            <el-image :src="banner.image" :alt="banner.title" class="banner-image" fit="cover" />
            <div class="banner-title">{{ banner.title }}</div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- 促销入口 -->
    <div class="promo-section">
      <div class="promo-card promo-pet" @click="getCategoryProducts('0')">
        <div class="promo-content">
          <h3>🐱 宠物活体</h3>
          <p>健康可爱，等你带回家</p>
        </div>
      </div>
      <div class="promo-card promo-goods" @click="getCategoryProducts('1')">
        <div class="promo-content">
          <h3>🧸 周边用品</h3>
          <p>精选好物，给毛孩子最好的</p>
        </div>
      </div>
      <div class="promo-card promo-service" @click="goToMap">
        <div class="promo-content">
          <h3>🐾 专业服务</h3>
          <p>附近门店，贴心陪伴</p>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-dot"></span>
          商品列表
        </h2>
        <span class="product-count" v-if="products.length">共 {{ products.length }} 件好物</span>
      </div>
      <div class="product-grid">
        <div v-for="product in products" :key="product.productId" class="product-card">
          <router-link :to="`/product?id=${product.productId}`" v-if="product.status === '0'">
            <div class="product-image">
              <el-image :src="getProductImage(product)" fit="cover" />
              <div class="product-tag" v-if="product.stockQuantity <= 5">仅剩 {{ product.stockQuantity }} 件</div>
            </div>
            <div class="product-info">
              <div class="product-tags">
                <span class="tag tag-pink" v-if="product.productType === '0'">萌宠</span>
                <span class="tag tag-green" v-else>周边</span>
              </div>
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc" v-if="product.description">{{ product.description }}</p>
              <div class="price-section">
                <span class="current-price">¥{{ product.price }}</span>
              </div>
              <div class="product-actions">
                <el-button type="primary" size="small" class="buy-btn">查看详情</el-button>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="!products.length && !isSearching">
      <div class="empty-icon">🐶</div>
      <p>暂无相关商品</p>
      <el-button type="primary" @click="fetchProducts">去逛逛</el-button>
    </div>
  </div>
</template>

<script>
import {
  Picture,
  ArrowRight,
  ShoppingCart,
  Search,
} from '@element-plus/icons-vue';
import { ElMessage } from "element-plus";
import {getAllProductList, getProducBykeyword} from "../../api/shopManagement/products";

export default {
  name: 'StorePage',
  components: {
    Picture,
    ArrowRight,
    Search,
  },
  data() {
    return {
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 当前页码
      pageNum: 1,
      // 每页条数
      pageSize: 12,
      // 当前选中的商品分类
      category:'',
      // 当前激活的分类 ID
      activeCategory: '',
      // 当前激活的服务导航 ID
      activeService: '',
      // 商品列表
      products: [],
      // 左侧快速服务导航
      serviceNavs: [
        { id: "all", name: '全部商品', icon: '🐾', action: 'category', value: '' },
        { id: "pet", name: '宠物活体', icon: '🐱', action: 'category', value: '0' },
        { id: "goods", name: '周边用品', icon: '🧸', action: 'category', value: '1' },
        { id: "map", name: '附近门店', icon: '📍', action: 'route', value: '/petShopAddress' },
        { id: "ai", name: 'AI 客服', icon: '💬', action: 'route', value: '/ai' },
      ],
      // 搜索关键词
      searchQuery:'',
      // 当前是否处于搜索模式
      isSearching: false,
      // 首页轮播图数据
      banners:[
        {image:import.meta.env.VITE_API_BASE_URL+"/media/banner/1.jpg",title:"萌宠精选",url:"www.jd.com"},
        {image:import.meta.env.VITE_API_BASE_URL+"/media/banner/2.jpg ",title:"周边好物",url:"www.tmall.com"},
        {image:import.meta.env.VITE_API_BASE_URL+"/media/banner/3.jpg",title:"限时特惠",url:"www.pinduoduo.com"},
      ]
    }
  },
  methods: {
    // 获取商品封面图完整 URL
    getProductImage(product) {
      if (!product || !Array.isArray(product.productMedias) || !product.productMedias.length) {
        return this.BaseUrl + '/media/productMedia/default.jpg';
      }
      const first = product.productMedias[0];
      const mediaUrl = first && (first.media?.url || first.mediaUrl);
      if (!mediaUrl) return this.BaseUrl + '/media/productMedia/default.jpg';
      return mediaUrl.startsWith('http') ? mediaUrl : this.BaseUrl + (mediaUrl.startsWith('/') ? mediaUrl : '/' + mediaUrl);
    },
    // 重置条件并加载全部商品
    fetchProducts(){
      this.pageNum = 1;
      this.category = '';
      this.activeCategory = '';
      this.searchQuery = '';
      this.isSearching = false;
      this.loadProducts();
    },
    // 加载商品列表：搜索或普通分类
    loadProducts() {
      if(this.isSearching){
        getProducBykeyword(this.pageNum,this.pageSize,this.searchQuery).then(res => {
          if (res.code === 200) {
            if (res.data.records.length !== 0) {
              if (this.pageNum === 1) {
                this.products = res.data.records;
              } else {
                // 如果不是第一页，将新数据追加到现有数组
                this.products = [...this.products, ...res.data.records];
              }
            } else if (this.pageNum > 1 && res.data.records.length === 0) {
              // 如果加载的不是第一页且没有数据，提示没有更多
              ElMessage.info('没有更多商品了');
            }
          } else {
            this.products = [];
            ElMessage.error(res.message);
          }
        })
      }else {
        getAllProductList(this.pageNum, this.pageSize, this.category).then(res => {
          if (res.code === 200) {
            if (res.data.records.length !== 0) {
              if (this.pageNum === 1) {
                this.products = res.data.records;
              } else {
                // 如果不是第一页，将新数据追加到现有数组
                this.products = [...this.products, ...res.data.records];
              }
            } else if (this.pageNum > 1 && res.data.records.length === 0) {
              // 如果加载的不是第一页且没有数据，提示没有更多
              ElMessage.info('没有更多商品了');
            }
          } else {
            this.products = [];
            ElMessage.error(res.message);
          }
        })
      }
    },

    // 按分类筛选商品
    getCategoryProducts(id) {
      this.pageNum = 1;
      this.category = id;
      this.activeCategory = id;
      this.activeService = '';
      this.isSearching = false;
      this.searchQuery = '';
      this.loadProducts();
    },
    // 点击左侧服务导航：跳转路由或切换分类
    handleServiceClick(item) {
      this.activeService = item.id;
      if (item.action === 'route') {
        this.$router.push(item.value);
      } else if (item.action === 'category') {
        this.getCategoryProducts(item.value);
      }
    },
    // 跳转附近门店地图页
    goToMap() {
      this.$router.push('/petShopAddress');
    },
    // 防抖函数
    debounce(fn, delay) {
      let timer = null;
      return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timer);
        timer = setTimeout(() => {
          fn.apply(context, args);
        }, delay);
      };
    },

    // 滚动处理函数：接近底部时加载下一页
    handleScroll() {
      if (this.isLoading) return;
      // 底部检测
      const scrollPosition = window.innerHeight + window.scrollY;
      const pageHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
      const threshold = 100; // 提前100px触发

      if (scrollPosition >= pageHeight - threshold) {
        this.pageNum++;
        this.loadProducts();
      }
    },

    // 按关键词搜索商品
    getSearchProducts(){
      this.pageNum = 1;
      this.isSearching = true;
      this.activeCategory = '';
      this.loadProducts();
    },
    // 打开轮播图对应外链
    gotoWebSite(url){
      window.open("https://" + url);
    }
  },
  // 组件挂载时加载商品并注册滚动监听
  mounted() {
    this.loadProducts();
    // 监听滚动事件
    this.scrollHandler = this.debounce(this.handleScroll, 200);
    window.addEventListener('scroll', this.scrollHandler);
  },
  // 组件销毁时移除事件监听
  beforeUnmount() {
    window.removeEventListener('scroll', this.scrollHandler);
  }
};
</script>

<style scoped>
.store-container {
  padding: 24px 40px;
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
}

.card {
  background: var(--pet-bg-white);
  border-radius: var(--pet-radius-base);
  box-shadow: var(--pet-shadow-sm);
  padding: 20px;
}

/* 搜索区 */
.search-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.search-box {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 720px;
  background: var(--pet-bg-white);
  border-radius: var(--pet-radius-lg);
  padding: 8px 8px 8px 16px;
  box-shadow: var(--pet-shadow-base);
  border: 1px solid var(--pet-border);
  transition: box-shadow 0.25s;
}

.search-box:focus-within {
  box-shadow: 0 0 0 3px rgba(255, 122, 69, 0.12), var(--pet-shadow-base);
  border-color: var(--pet-primary-light);
}

.search-icon {
  font-size: 20px;
  color: var(--pet-text-grey);
  margin-right: 10px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
}

.search-btn {
  border-radius: var(--pet-radius-base);
  padding: 0 28px;
  height: 44px;
}

/* 分类与轮播图区域 */
.category-banner-section {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  width: 100%;
}

.category-section {
  flex: 0 0 260px;
}

.banner-section {
  flex: 1;
  padding: 0;
  overflow: hidden;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--pet-text);
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 700;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--pet-border);
}

.title-dot {
  width: 6px;
  height: 22px;
  background: linear-gradient(180deg, var(--pet-primary), var(--pet-primary-light));
  border-radius: 3px;
}

/* 分类列表样式 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 14px 12px;
  border-radius: var(--pet-radius-base);
  cursor: pointer;
  transition: all 0.25s;
}

.category-item:hover {
  background-color: var(--pet-bg-hover);
  transform: translateX(4px);
}

.category-item.active {
  background: linear-gradient(90deg, var(--pet-primary-soft), var(--pet-bg-white));
  color: var(--pet-primary);
  box-shadow: var(--pet-shadow-sm);
}

.category-item.active .arrow-icon {
  color: var(--pet-primary);
}

.category-icon {
  font-size: 22px;
  margin-right: 12px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--pet-bg);
  border-radius: var(--pet-radius-circle);
}

.category-name {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
}

.arrow-icon {
  color: var(--pet-text-grey);
  font-size: 14px;
  transition: transform 0.25s;
}

.category-item:hover .arrow-icon {
  transform: translateX(4px);
}

/* 轮播图样式 */
.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--pet-radius-base);
}

:deep(.el-carousel__item) {
  border-radius: var(--pet-radius-base);
}

.banner-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.6));
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  border-radius: 0 0 var(--pet-radius-base) var(--pet-radius-base);
}

/* 促销入口 */
.promo-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.promo-card {
  border-radius: var(--pet-radius-base);
  padding: 24px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.promo-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--pet-shadow-lg);
}

.promo-card::after {
  content: '';
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 100px;
  height: 100px;
  background: rgba(255,255,255,0.12);
  border-radius: 50%;
}

.promo-pet {
  background: linear-gradient(135deg, #FF7A45, #FF9E6D);
}

.promo-goods {
  background: linear-gradient(135deg, #4CAF50, #81C784);
}

.promo-service {
  background: linear-gradient(135deg, #5B8FF9, #9C6ADE);
}

.promo-content h3 {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
}

.promo-content p {
  font-size: 14px;
  opacity: 0.95;
}

/* 商品区域 */
.product-section {
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-count {
  font-size: 14px;
  color: var(--pet-text-grey);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  padding: 4px;
}

.product-card {
  background: var(--pet-bg-white);
  border-radius: var(--pet-radius-base);
  overflow: hidden;
  box-shadow: var(--pet-shadow-sm);
  transition: all 0.3s ease;
  position: relative;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--pet-shadow-lg);
}

.product-card a {
  color: inherit;
  display: block;
}

.product-image {
  width: 100%;
  height: 240px;
  display: flex;
  background-color: var(--pet-bg-grey);
  position: relative;
  overflow: hidden;
}

.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.product-image :deep(.el-image__inner) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .product-image :deep(.el-image__inner) {
  transform: scale(1.05);
}

.product-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: var(--pet-error);
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 600;
}

.product-info {
  padding: 16px;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.tag-pink {
  background: rgba(255, 182, 193, 0.3);
  color: #C75B7A;
}

.tag-green {
  background: rgba(76, 175, 80, 0.12);
  color: var(--pet-green);
}

.product-name {
  margin: 0 0 8px;
  font-size: 15px;
  color: var(--pet-text);
  font-weight: 600;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 42px;
}

.product-desc {
  font-size: 13px;
  color: var(--pet-text-grey);
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.current-price {
  font-size: 20px;
  font-weight: 800;
  color: var(--pet-error);
}

.product-actions {
  display: flex;
  gap: 10px;
}

.buy-btn {
  width: 100%;
  border-radius: var(--pet-radius-base);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: var(--pet-text-secondary);
  margin-bottom: 20px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .store-container {
    padding: 20px;
  }
  .category-banner-section {
    flex-direction: column;
  }
  .category-section {
    flex: auto;
  }
}

@media (max-width: 768px) {
  .promo-section {
    grid-template-columns: 1fr;
  }
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  .product-image {
    height: 180px;
  }
}

@media (max-width: 480px) {
  .store-container {
    padding: 16px;
  }
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
