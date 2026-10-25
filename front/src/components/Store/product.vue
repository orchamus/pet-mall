<template>
  <div class="product-container" v-if="product && product.productId">
    <div class="product-content card">
      <!-- 商品图片与视频展示区 -->
      <div class="product-gallery">
        <el-carousel
          :interval="5000"
          arrow="always"
          indicator-position="outside"
          height="480px"
          @change="handleCarouselChange"
          class="product-carousel"
        >
          <el-carousel-item v-for="(productMedia, index) in product.productMedias" :key="index" class="carousel-item">
            <div v-if="isImage(getMediaUrl(productMedia))" class="media-container">
              <el-image
                :src="getMediaUrl(productMedia)"
                class="product-media"
                :preview-src-list="[getMediaUrl(productMedia)]"
                fit="cover"
                preview-teleported
              />
            </div>
            <div
              v-else-if="isVideo(getMediaUrl(productMedia))"
              class="media-container"
              @click="toggleVideoDisplay(index)"
            >
              <el-image
                :src="coverPageUrl"
                class="product-media"
                :style="{ display: videoStates[index] ? 'none' : 'block' }"
                fit="cover"
              />
              <div class="play-icon" v-if="!videoStates[index]">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <video
                controls
                :autoplay="false"
                :ref="'videoPlayer' + index"
                :src="getMediaUrl(productMedia)"
                class="product-media"
                :style="{ display: videoStates[index] ? 'block' : 'none' }"
                @click.stop
              ></video>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 商品基本信息区 -->
      <div class="product-info">
        <div class="info-header">
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="product-tags">
            <el-tag :type="isPet ? 'success' : 'info'" effect="light" size="small">
              {{ isPet ? '宠物活体' : '周边用品' }}
            </el-tag>
            <el-tag v-if="product.status === '1'" type="danger" effect="light" size="small">已下架</el-tag>
            <el-tag v-else-if="product.stockQuantity === 0" type="info" effect="light" size="small">暂时缺货</el-tag>
            <el-tag v-else type="success" effect="light" size="small">有货</el-tag>
          </div>
        </div>

        <div class="product-price">
          <span class="price-symbol">¥</span>
          <span class="current-price">{{ product.price?.toFixed(2) }}</span>
          <span class="stock-text" :class="{ 'out': product.stockQuantity <= 0 || product.status === '1' }">
            {{ stockText }}
          </span>
        </div>

        <div class="product-description">
          <h3>商品描述</h3>
          <p>{{ product.description || '暂无商品描述' }}</p>
        </div>

        <!-- 宠物信息 -->
        <div v-if="isPet && product.pets" class="detail-block">
          <h3>宠物信息</h3>
          <div class="detail-grid">
            <div class="detail-item"><span class="label">品种</span><span class="value">{{ breedText }}</span></div>
            <div class="detail-item"><span class="label">性别</span><span class="value">{{ genderText }}</span></div>
            <div class="detail-item"><span class="label">健康状况</span><span class="value">{{ healthText }}</span></div>
            <div class="detail-item"><span class="label">库存</span><span class="value">{{ product.stockQuantity }} 件</span></div>
          </div>
        </div>

        <!-- 用品信息 -->
        <div v-if="isSupply && product.supplies" class="detail-block">
          <h3>用品详情</h3>
          <div class="detail-grid">
            <div class="detail-item"><span class="label">重量</span><span class="value">{{ supplyWeight }}</span></div>
            <div class="detail-item"><span class="label">尺寸</span><span class="value">{{ supplySize }}</span></div>
            <div class="detail-item"><span class="label">材质</span><span class="value">{{ supplyMaterial }}</span></div>
            <div class="detail-item"><span class="label">适用宠物</span><span class="value">{{ supplyTarget }}</span></div>
            <div class="detail-item"><span class="label">库存</span><span class="value">{{ product.stockQuantity }} 件</span></div>
          </div>
        </div>

        <!-- 商家信息 -->
        <div class="shop-card" @click="gotoShop()">
          <div class="shop-header">
            <div class="shop-icon">🏪</div>
            <div class="shop-title">
              <h3>{{ shopNameText }}</h3>
              <p>{{ shopAddressText }}</p>
            </div>
            <el-icon class="shop-arrow"><ArrowRight /></el-icon>
          </div>
          <p class="shop-desc">{{ shopDescText }}</p>
        </div>

        <!-- 购买操作区 -->
        <div class="product-actions">
          <div class="quantity-wrap">
            <span class="quantity-label">数量</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stockQuantity"
              :disabled="!canBuy"
              size="large"
            ></el-input-number>
          </div>

          <el-button
            type="primary"
            size="large"
            :disabled="!canBuy"
            @click="addToCart"
            class="action-btn"
          >
            <el-icon><ShoppingCart /></el-icon>
            加入购物车
          </el-button>

          <el-button
            type="danger"
            size="large"
            :disabled="!canBuy"
            @click="getAddresses()"
            class="action-btn"
          >
            立即购买
          </el-button>
        </div>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comment-section card">
      <h2 class="comment-title">商品评价</h2>

      <!-- 评论列表 -->
      <div class="comment-list" v-if="comments.length">
        <div v-for="(comment, index) in comments" :key="index" class="comment-item">
          <div class="comment-user">
            <el-avatar :size="48" :src="getAvatarUrl(comment.user?.avatar)"></el-avatar>
            <p class="user-name">{{ comment.user?.userName || '匿名用户' }}</p>
          </div>
          <div class="comment-content">
            <p class="comment-text">{{ comment.content }}</p>
            <div class="comment-meta">
              <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价，快来抢沙发吧~" />
    </div>
  </div>

  <el-empty v-else description="商品不存在或已下架" class="empty-tip" />

  <!-- 选择收货地址弹窗 -->
  <el-dialog v-model="addressVisible" title="选择收货地址" width="520px" destroy-on-close>
    <div v-if="addresses.length === 0" class="empty-tip">
      <el-empty description="暂无收货地址，请先到个人中心添加"></el-empty>
    </div>

    <div v-else class="address-list">
      <AddressItem
        v-for="address in addresses"
        :key="address.addressId"
        :address="address"
        :show-actions="false"
        :class="['address-select-item', { selected: addressId === address.addressId }]"
        @click="selectAddress(address.addressId)"
      />
    </div>
  </el-dialog>
</template>

<script>
import { ShoppingCart, VideoPlay, ArrowRight } from '@element-plus/icons-vue'
import { getAProductById, getProductReviews } from "../../api/shopManagement/products";
import { ElMessage } from "element-plus";
import { addCart } from "../../api/shopCart/cart";
import AddressItem from "../PersonalCenter/AddressItem.vue";
import { getAddressList } from "../../api/PersonalCenter/Address";
import { createOrderBynow } from "../../api/order/order";
import { getProductImage } from "../../utils/image";
import { normalizeBreed, normalizeHealthStatus, normalizeShopName, normalizeShopDesc, normalizeAddress, normalizeSupplyValue } from "../../utils/text";

export default {
  components: {
    ShoppingCart,
    VideoPlay,
    ArrowRight
  },
  data() {
    return {
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 商品所属店铺信息
      shop: {},
      // 购买数量
      quantity: 1,
      // 评论列表
      comments: [],
      // 当前商品详情
      product: {},
      // 评论每页条数
      pageSize: 10,
      // 评论总数（当前未使用）
      totalComments: '',
      // 评论当前页码
      pageNum: 1,
      // 视频封面图地址
      coverPageUrl: '',
      // 选中的收货地址 ID
      addressId: '',
      // 收货地址列表
      addresses: [],
      // 地址选择弹窗显示状态
      addressVisible: false,
      // 当前是否已登录
      isLogin: false,
      // 各视频媒体的播放状态数组
      videoStates: [],
    }
  },
  computed: {
    // 商品分类
    category() {
      return this.product?.category ?? this.product?.productType ?? ''
    },
    // 是否为宠物活体商品
    isPet() {
      return this.category === '0' || this.category === 0
    },
    // 是否为周边用品商品
    isSupply() {
      return this.category === '1' || this.category === 1
    },
    // 当前商品是否可购买
    canBuy() {
      return this.product?.status !== '1' && this.product?.stockQuantity > 0
    },
    // 库存状态文本
    stockText() {
      if (this.product?.status === '1') return '已下架'
      if (!this.product || this.product.stockQuantity <= 0) return '暂时缺货'
      if (this.product.stockQuantity <= 5) return `仅剩 ${this.product.stockQuantity} 件`
      return '有货'
    },
    // 宠物性别文本
    genderText() {
      if (!this.product?.pets) return '-'
      const g = this.product.pets.gender
      if (g === '0' || g === 0) return '公'
      if (g === '1' || g === 1) return '母'
      return '公母皆有'
    },
    // 宠物品种文本
    breedText() {
      return normalizeBreed(this.product)
    },
    // 宠物健康状况文本
    healthText() {
      return normalizeHealthStatus(this.product?.pets?.healthStatus)
    },
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
    },
    // 用品重量文本
    supplyWeight() {
      const w = normalizeSupplyValue(this.product?.supplies?.weightKg, '')
      return w ? w + ' kg' : '暂无'
    },
    // 用品尺寸文本
    supplySize() {
      return normalizeSupplyValue(this.product?.supplies?.size)
    },
    // 用品材质文本
    supplyMaterial() {
      return normalizeSupplyValue(this.product?.supplies?.material)
    },
    // 用品适用宠物文本
    supplyTarget() {
      return normalizeSupplyValue(this.product?.supplies?.targetSpecies)
    }
  },
  // 组件挂载后注册滚动监听
  mounted() {
    this.scrollHandler = this.debounce(this.handleScroll, 200);
    window.addEventListener('scroll', this.scrollHandler);
  },
  // 组件卸载前移除滚动监听
  beforeUnmount() {
    window.removeEventListener('scroll', this.scrollHandler);
  },
  // 组件创建时判断是否登录并加载商品数据
  created() {
    if (sessionStorage.getItem('token')) {
      this.isLogin = true;
    }
    this.fetchProductData()
  },

  methods: {
    getProductImage,
    // 拼接媒体资源完整 URL
    getMediaUrl(productMedia) {
      if (!productMedia) return ''
      const media = productMedia.media || productMedia
      const url = media?.url || productMedia.mediaUrl
      if (!url) return ''
      if (url.startsWith('http')) return url
      return this.BaseUrl + (url.startsWith('/') ? url : '/' + url)
    },
    // 拼接头像完整 URL
    getAvatarUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return this.BaseUrl + (url.startsWith('/') ? url : '/' + url)
    },
    // 轮播切换时暂停并重置所有视频
    handleCarouselChange() {
      Object.keys(this.$refs).forEach(refKey => {
        if (refKey.startsWith('videoPlayer')) {
          const video = this.$refs[refKey][0];
          if (video) {
            video.pause();
            video.currentTime = 0;
          }
        }
      });
      this.videoStates = this.product.productMedias.map(() => false);
    },
    // 防抖函数
    debounce(fn, delay) {
      let timer = null;
      return function () {
        const context = this;
        const args = arguments;
        clearTimeout(timer);
        timer = setTimeout(() => {
          fn.apply(context, args);
        }, delay);
      };
    },
    // 滚动到底部时加载更多评论
    handleScroll() {
      if (this.isLoading) return;
      const scrollPosition = window.innerHeight + window.scrollY;
      const pageHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
      const threshold = 100;

      if (scrollPosition >= pageHeight - threshold) {
        this.pageNum++;
        this.fetchComments();
      }
    },
    // 获取商品详情与评论
    fetchProductData() {
      getAProductById(this.$route.query.id).then(response => {
        if (response.data) {
          this.product = response.data.product;
          this.shop = response.data.shop;
          this.fetchComments()
          this.videoStates = (this.product.productMedias || []).map(() => false);
          this.product.productMedias && this.product.productMedias.forEach(item => {
            if (item.mediaType === '0') {
              this.coverPageUrl = this.getMediaUrl(item);
              return;
            }
          })
        } else {
          ElMessage.error('商品不存在');
          this.$router.push('/404')
        }
      })
    },
    // 加载商品评论，支持追加
    fetchComments() {
      if (!this.product?.productId) return
      getProductReviews(this.product.productId, this.pageNum, this.pageSize).then(response => {
        if (this.pageNum === 1) {
          this.comments = response.data.records || []
        } else {
          this.comments = [...this.comments, ...(response.data.records || [])]
        }
      })
    },
    // 加入购物车
    addToCart() {
      if (!this.canBuy) {
        ElMessage.warning('商品暂时无法购买')
        return
      }
      addCart(this.product, this.quantity).then(response => {
        if (response.data) {
          ElMessage.success(response.message)
        } else {
          ElMessage.error(response.message)
        }
      })
    },
    // 立即购买创建订单
    buyNow() {
      createOrderBynow(this.product.productId, this.quantity, this.addressId).then(response => {
        if (response.data) {
          ElMessage.success(response.message)
          this.fetchProductData()
        } else {
          ElMessage.error(response.message)
        }
      })
    },
    // 格式化日期时间
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
    },

    // 判断 URL 是否为图片
    isImage(url) {
      if (!url) return false
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp']
      return imageExtensions.some(ext => url.toLowerCase().endsWith(ext))
    },
    // 判断 URL 是否为视频
    isVideo(url) {
      if (!url) return false
      const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.avi']
      return videoExtensions.some(ext => url.toLowerCase().endsWith(ext))
    },
    // 切换视频显示/隐藏并播放
    toggleVideoDisplay(index) {
      this.videoStates[index] = !this.videoStates[index];
      const video = this.$refs['videoPlayer' + index][0];
      if (video) {
        video.play();
      }
    },
    // 获取收货地址列表并打开选择弹窗
    getAddresses() {
      if (!this.canBuy) {
        ElMessage.warning('商品暂时无法购买')
        return
      }
      if (sessionStorage.getItem('token')) {
        getAddressList().then(res => {
          if (res.data) {
            this.addresses = res.data;
            this.addressVisible = true
          }
        })
      } else {
        ElMessage.error('请先登录');
      }
    },
    // 选择收货地址并确认购买
    selectAddress(addressId) {
      this.addressId = addressId;
      this.addressVisible = false;
      this.$confirm('确定要购买该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.buyNow()
      }).catch(() => {
        ElMessage.info('已取消')
      })
    },
    // 跳转店铺详情页
    gotoShop() {
      this.$router.push({ path: '/shop', query: { id: this.shop.shopId } })
    }
  }
}
</script>

<style scoped>
.product-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.product-content {
  display: flex;
  gap: 40px;
  padding: 32px;
  margin-bottom: 24px;
}

/* 商品图片与视频展示区 */
.product-gallery {
  flex: 1;
  min-width: 0;
}

.product-carousel {
  border-radius: var(--pet-radius-base);
  overflow: hidden;
  background: var(--pet-bg-grey);
}

.carousel-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-container {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.product-media {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 72px;
  height: 72px;
  font-size: 32px;
  color: white;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s;
}

.play-icon:hover {
  background-color: rgba(0, 0, 0, 0.7);
  transform: translate(-50%, -50%) scale(1.05);
}

/* 商品基本信息区 */
.product-info {
  flex: 1;
  min-width: 0;
}

.info-header {
  margin-bottom: 16px;
}

.product-name {
  font-size: 28px;
  font-weight: 700;
  color: var(--pet-text);
  margin: 0 0 12px;
  line-height: 1.3;
}

.product-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 16px 20px;
  background: linear-gradient(90deg, var(--pet-primary-soft), #fff);
  border-radius: var(--pet-radius-base);
  margin-bottom: 20px;
}

.price-symbol {
  font-size: 20px;
  color: var(--pet-error);
  font-weight: 700;
}

.current-price {
  font-size: 32px;
  color: var(--pet-error);
  font-weight: 800;
}

.stock-text {
  font-size: 14px;
  color: var(--pet-success);
  font-weight: 500;
}

.stock-text.out {
  color: var(--pet-text-grey);
}

.product-description {
  margin-bottom: 20px;
}

.product-description h3,
.detail-block h3 {
  font-size: 16px;
  font-weight: 700;
  color: var(--pet-text);
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 4px solid var(--pet-primary);
}

.product-description p {
  font-size: 14px;
  color: var(--pet-text-secondary);
  line-height: 1.7;
}

.detail-block {
  margin-bottom: 20px;
  padding: 16px;
  background: var(--pet-bg);
  border-radius: var(--pet-radius-base);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.detail-item .label {
  color: var(--pet-text-grey);
  min-width: 72px;
}

.detail-item .value {
  color: var(--pet-text);
  font-weight: 500;
}

/* 商家信息卡片 */
.shop-card {
  margin-bottom: 24px;
  padding: 16px;
  background: var(--pet-bg);
  border-radius: var(--pet-radius-base);
  cursor: pointer;
  transition: all 0.25s;
}

.shop-card:hover {
  background: var(--pet-primary-soft);
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.shop-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--pet-radius-circle);
  background: linear-gradient(135deg, var(--pet-primary-light), var(--pet-primary));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.shop-title {
  flex: 1;
  min-width: 0;
}

.shop-title h3 {
  margin: 0 0 4px;
  font-size: 16px;
  color: var(--pet-text);
}

.shop-title p {
  margin: 0;
  font-size: 13px;
  color: var(--pet-text-grey);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-arrow {
  color: var(--pet-text-grey);
}

.shop-desc {
  margin: 12px 0 0;
  font-size: 13px;
  color: var(--pet-text-secondary);
  line-height: 1.6;
}

/* 购买操作区 */
.product-actions {
  margin-top: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.quantity-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quantity-label {
  font-size: 14px;
  color: var(--pet-text-secondary);
}

.action-btn {
  min-width: 140px;
}

/* 评论区 */
.comment-section {
  padding: 32px;
}

.comment-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--pet-text);
  margin-bottom: 24px;
  padding-left: 12px;
  border-left: 4px solid var(--pet-primary);
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid var(--pet-border);
}

.comment-user {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 20px;
  min-width: 80px;
}

.user-name {
  margin-top: 8px;
  margin-bottom: 0;
  font-size: 13px;
  color: var(--pet-text-grey);
  text-align: center;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-text {
  margin: 10px 0 0;
  font-size: 14px;
  color: var(--pet-text);
  line-height: 1.7;
}

.comment-meta {
  margin-top: 10px;
  font-size: 12px;
  color: var(--pet-text-grey);
}

.empty-tip {
  padding: 80px 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-select-item {
  border-radius: var(--pet-radius-base);
  border: 2px solid transparent;
  transition: all 0.2s;
}

.address-select-item.selected {
  border-color: var(--pet-primary);
  background: var(--pet-primary-soft);
}

@media (max-width: 900px) {
  .product-content {
    flex-direction: column;
  }

  .product-carousel {
    height: auto;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
