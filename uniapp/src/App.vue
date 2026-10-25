<script>
/**
 * 应用根组件
 * 处理 UniApp 应用级生命周期钩子
 */
export default {
  /**
   * 应用启动时触发
   * 用于执行初始化操作，如日志记录、全局状态恢复等
   */
  onLaunch: function () {
    console.log('App Launch')
  },
  /**
   * 应用进入前台时触发
   */
  onShow: function () {
    console.log('App Show')
  },
  /**
   * 应用进入后台时触发
   */
  onHide: function () {
    console.log('App Hide')
  },
}
</script>

<style lang="scss">
/* 每个页面公共 css */
page {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  color: $pet-text;
  background-color: $pet-bg;
  line-height: $pet-line-height;
  -webkit-font-smoothing: antialiased;
}

/* 通用容器 */
.pet-container {
  padding: $pet-space-sm;
}

/* 卡片通用样式 */
.pet-card {
  background: $pet-bg-white;
  border-radius: $pet-radius-base;
  box-shadow: $pet-shadow-sm;
}

/* 主按钮 */
.pet-btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  background: linear-gradient(135deg, $pet-primary, $pet-primary-light);
  color: #fff;
  font-size: $pet-font-lg;
  font-weight: 600;
  border-radius: $pet-radius-lg;
  box-shadow: 0 8rpx 20rpx rgba($pet-primary, 0.3);
}

/* 标签 */
.pet-tag {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: $pet-font-xs;
}

.pet-tag-primary {
  background: rgba($pet-primary, 0.12);
  color: $pet-primary;
}

.pet-tag-pink {
  background: rgba($pet-pink, 0.25);
  color: #D46A7E;
}

.pet-tag-green {
  background: rgba($pet-green, 0.12);
  color: $pet-green;
}

.pet-tag-danger {
  background: rgba($pet-error, 0.1);
  color: $pet-error;
}

/* 价格 */
.pet-price {
  color: $pet-error;
  font-weight: 700;
}

/* 安全区适配 */
.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
