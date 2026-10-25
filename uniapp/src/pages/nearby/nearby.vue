<template>
  <view class="nearby-page">
    <!-- 页面头部：返回、标题、定位按钮 -->
    <view class="nearby-header">
      <view class="header-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="header-title">附近门店</text>
      <view class="header-action" @click="locateMe">
        <text class="action-icon">📍</text>
        <text class="action-text">定位</text>
      </view>
    </view>

    <!-- 地图容器：高德地图 Web 渲染 -->
    <view class="map-wrap">
      <div id="amap-container" class="amap-container"></div>
      <view class="location-btn" @click="locateMe">
        <text class="locate-icon">🎯</text>
      </view>
    </view>

    <!-- 当前位置信息卡片 -->
    <view class="position-card pet-card" v-if="currentAddress || loadingLocation">
      <view class="position-row">
        <text class="position-icon">📍</text>
        <view class="position-main">
          <text class="position-title">我的位置</text>
          <text class="position-address">{{ currentAddress || '正在定位...' }}</text>
        </view>
      </view>
    </view>

    <!-- 附近店铺列表 -->
    <view class="shop-section">
      <view class="section-title">
        <view class="title-bar"></view>
        <text>附近店铺</text>
        <text class="shop-count" v-if="shopList.length">共 {{ shopList.length }} 家</text>
      </view>

      <view class="shop-list">
        <view
          class="shop-item pet-card"
          v-for="(shop, index) in shopList"
          :key="shop.shopId || index"
          @click="focusShop(shop, index)"
        >
          <view class="shop-info">
            <text class="shop-name">{{ shop.shopName || '萌宠门店' }}</text>
            <text class="shop-address">{{ shop.address || '暂无地址' }}</text>
          </view>
          <text class="shop-distance" v-if="shop.distance">{{ shop.distance }}km</text>
          <text class="shop-arrow">›</text>
        </view>
        <view v-if="!shopList.length && !loadingShops" class="empty-tip">
          <text>暂无门店信息</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getShopLocations } from '@/api/location.js';
import { navigateBack, showToast } from '@/utils/index.js';

// 高德地图 Key 与安全密钥
const AMAP_KEY = 'fa3d7162a52695a26a976038bbaca11c';
const AMAP_SECURITY_CONFIG = '238ea41cf6ba03d7036b0e41ffbc3385';

// 厦门市三区固定坐标（GCJ-02），无坐标店铺随机分配其一
const FIXED_COORDINATES = [
  { name: '集美区', lng: 118.1000, lat: 24.5750 },
  { name: '海沧区', lng: 117.9900, lat: 24.4850 },
  { name: '思明区', lng: 118.0820, lat: 24.4450 }
];
// 根据地址中的区名匹配坐标，避免重复；未匹配到时按索引轮流分配
const getFixedCoordinate = (shop, index) => {
  if (shop.address) {
    if (shop.address.includes('思明')) return FIXED_COORDINATES.find(c => c.name === '思明区');
    if (shop.address.includes('集美')) return FIXED_COORDINATES.find(c => c.name === '集美区');
    if (shop.address.includes('海沧')) return FIXED_COORDINATES.find(c => c.name === '海沧区');
  }
  return FIXED_COORDINATES[index % FIXED_COORDINATES.length];
};

// 响应式数据：地图实例、地理编码实例、加载状态、当前位置、店铺列表、标记点
const map = ref(null);
const geocoder = ref(null);
const amapLoaded = ref(false);
const currentPosition = ref(null);
const currentAddress = ref('');
const shopList = ref([]);
const loadingLocation = ref(false);
const loadingShops = ref(false);
const markers = ref([]);

// 返回上一页
const goBack = () => {
  navigateBack();
};

// 动态加载高德地图 JS SDK
const loadAMapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve(window.AMap);
      return;
    }
    window._AMapSecurityConfig = {
      securityJsCode: AMAP_SECURITY_CONFIG
    };
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&plugin=AMap.Geocoder,AMap.Geolocation,AMap.Marker`;
    script.onerror = () => reject(new Error('高德地图脚本加载失败'));
    script.onload = () => {
      const check = () => {
        if (window.AMap) {
          resolve(window.AMap);
        } else {
          setTimeout(check, 100);
        }
      };
      check();
    };
    document.head.appendChild(script);
  });
};

// 初始化高德地图与地理编码服务
const initMap = async () => {
  try {
    const AMap = await loadAMapScript();
    amapLoaded.value = true;
    map.value = new AMap.Map('amap-container', {
      zoom: 12,
      center: [118.0894, 24.4798]
    });
    geocoder.value = new AMap.Geocoder({});
  } catch (e) {
    console.error(e);
    showToast('地图加载失败', 'none');
  }
};

// 清空地图上的所有标记点
const clearMarkers = () => {
  if (!map.value) return;
  markers.value.forEach(marker => {
    if (marker && typeof marker.setMap === 'function') {
      marker.setMap(null);
    }
  });
  markers.value = [];
};

// 在地图上添加单个标记点
const addMarker = (lng, lat, title, icon = '') => {
  if (!map.value || !window.AMap) return;
  const marker = new window.AMap.Marker({
    position: [lng, lat],
    map: map.value,
    title: title,
    label: title ? { content: title, direction: 'bottom' } : null
  });
  markers.value.push(marker);
  return marker;
};

// 判断当前运行环境是否为 H5，以决定是否使用浏览器原生定位
const isH5 = () => typeof window !== 'undefined' && typeof navigator !== 'undefined' && !!navigator.geolocation;

// WGS84 坐标转 GCJ-02 坐标（火星坐标系）
const wgs84ToGcj02 = (lng, lat) => {
  const PI = 3.1415926535897932384626;
  const a = 6378245.0;
  const ee = 0.00669342162296594323;
  const transformlat = (lng, lat) => {
    let ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
    ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320.0 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
    return ret;
  };
  const transformlng = (lng, lat) => {
    let ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
    ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
    return ret;
  };
  const outOfChina = (lng, lat) => lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
  if (outOfChina(lng, lat)) return [lng, lat];
  let dlat = transformlat(lng - 105.0, lat - 35.0);
  let dlng = transformlng(lng - 105.0, lat - 35.0);
  const radlat = lat / 180.0 * PI;
  let magic = Math.sin(radlat);
  magic = 1 - ee * magic * magic;
  const sqrtmagic = Math.sqrt(magic);
  dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
  dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
  return [lng + dlng, lat + dlat];
};

// 处理定位结果：设置地图中心、添加当前位置标记、逆地理编码并计算距离
const handlePosition = (lng, lat) => {
  currentPosition.value = [lng, lat];
  map.value.setCenter(currentPosition.value);
  map.value.setZoom(14);
  clearMarkers();
  addMarker(lng, lat, '我的位置');
  reverseGeocode(lng, lat);
  calculateDistances();
};

// 地图定位：优先 H5 原生定位，失败或小程序环境使用 uni.getLocation
const locateMe = () => {
  if (!amapLoaded.value) {
    showToast('地图尚未加载完成', 'none');
    return;
  }
  loadingLocation.value = true;
  currentAddress.value = '';

  const timeoutId = setTimeout(() => {
    loadingLocation.value = false;
    showToast('定位超时，请检查定位权限或重试', 'none');
  }, 10000);

  const onFail = (err) => {
    clearTimeout(timeoutId);
    console.error(err);
    const msg = err && (err.message || err.errMsg) ? (err.message || err.errMsg) : '定位失败，请检查定位权限';
    showToast(msg, 'none');
    loadingLocation.value = false;
  };

  const tryGcj02 = () => {
    uni.getLocation({
      type: 'gcj02',
      isHighAccuracy: true,
      success: (res) => {
        clearTimeout(timeoutId);
        handlePosition(res.longitude, res.latitude);
      },
      fail: (err) => {
        if (err && err.errMsg && err.errMsg.includes('translate coordinate system')) {
          uni.getLocation({
            type: 'wgs84',
            isHighAccuracy: true,
            success: (res2) => {
              clearTimeout(timeoutId);
              const [gcjLng, gcjLat] = wgs84ToGcj02(res2.longitude, res2.latitude);
              handlePosition(gcjLng, gcjLat);
            },
            fail: onFail
          });
        } else {
          onFail(err);
        }
      }
    });
  };

  if (isH5()) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        clearTimeout(timeoutId);
        const [gcjLng, gcjLat] = wgs84ToGcj02(position.coords.longitude, position.coords.latitude);
        handlePosition(gcjLng, gcjLat);
      },
      () => {
        tryGcj02();
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
    return;
  }

  tryGcj02();
};

// 逆地理编码：将经纬度转换为可读地址
const reverseGeocode = (lng, lat) => {
  if (!geocoder.value) return;
  geocoder.value.getAddress([lng, lat], (status, result) => {
    loadingLocation.value = false;
    if (status === 'complete' && result.regeocode) {
      currentAddress.value = result.regeocode.formattedAddress;
    } else {
      currentAddress.value = `${lng.toFixed(4)}, ${lat.toFixed(4)}`;
    }
  });
};

// 加载门店数据：先分配固定坐标保证展示，再尝试地址编码覆盖真实坐标
const loadShops = async () => {
  loadingShops.value = true;
  try {
    const res = await getShopLocations();
    const list = Array.isArray(res.data) ? res.data : (res.data?.records || []);
    // 先给每个店铺随机分配一个固定坐标，确保一定能显示距离和标记
    shopList.value = list.map((item, index) => {
      const coord = getFixedCoordinate(item, index);
      return { ...item, lng: coord.lng, lat: coord.lat, distance: '' };
    });
    shopList.value.forEach(shop => {
      shop.marker = addMarker(shop.lng, shop.lat, shop.shopName || '门店');
    });
    if (currentPosition.value) {
      calculateDistances();
    }
    // 再尝试用真实地址做地理编码，成功则覆盖随机坐标
    geocodeShops();
  } catch (e) {
    console.error(e);
    showToast('获取门店失败', 'none');
  } finally {
    loadingShops.value = false;
  }
};

// 根据门店地址进行地理编码，更新真实坐标
const geocodeShops = () => {
  if (!shopList.value.length || !geocoder.value) return;

  shopList.value.forEach((shop) => {
    if (!shop.address) return;
    geocoder.value.getLocation(shop.address, (status, result) => {
      if (status === 'complete' && result.geocodes && result.geocodes.length) {
        const loc = result.geocodes[0].location;
        shop.lng = loc.lng;
        shop.lat = loc.lat;
        if (shop.marker && typeof shop.marker.setPosition === 'function') {
          shop.marker.setPosition([loc.lng, loc.lat]);
        }
        if (currentPosition.value) {
          shop.distance = calculateDistance(currentPosition.value[1], currentPosition.value[0], loc.lat, loc.lng);
        }
      }
    });
  });
};

// 根据两点经纬度计算直线距离（单位：km）
const calculateDistance = (lat1, lng1, lat2, lng2) => {
  const R = 6371;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLng = (lng2 - lng1) * Math.PI / 180;
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLng / 2) * Math.sin(dLng / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return (R * c).toFixed(1);
};

// 计算当前位置到所有门店的距离
const calculateDistances = () => {
  if (!currentPosition.value) return;
  shopList.value.forEach(shop => {
    if (shop.lat && shop.lng) {
      shop.distance = calculateDistance(currentPosition.value[1], currentPosition.value[0], shop.lat, shop.lng);
    }
  });
};

// 点击门店时聚焦到地图对应标记
const focusShop = (shop, index) => {
  if (shop.lng && shop.lat && map.value) {
    map.value.setCenter([shop.lng, shop.lat]);
    map.value.setZoom(16);
  } else {
    showToast('该门店暂无坐标信息', 'none');
  }
};

// 页面挂载：初始化地图、加载门店、触发定位
onMounted(async () => {
  await initMap();
  await loadShops();
  locateMe();
});
</script>

<style scoped lang="scss">
.nearby-page {
  min-height: 100vh;
  background: $pet-bg;
  padding-bottom: calc($pet-space-lg + env(safe-area-inset-bottom));
}

/* 页面头部 */
.nearby-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 $pet-space-sm;
  background: $pet-bg-white;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.header-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 52rpx;
  color: $pet-text;
  line-height: 1;
}

.header-title {
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
}

.header-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 8rpx 16rpx;
  background: $pet-primary-soft;
  border-radius: $pet-radius-xl;
}

.action-icon {
  font-size: 26rpx;
}

.action-text {
  font-size: $pet-font-sm;
  color: $pet-primary;
  font-weight: 600;
}

.map-wrap {
  position: relative;
  width: 100%;
  height: 520rpx;
  background: $pet-bg-grey;
}

/* 地图容器与定位按钮 */
.amap-container {
  width: 100%;
  height: 100%;
}

.location-btn {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  width: 80rpx;
  height: 80rpx;
  border-radius: $pet-radius-circle;
  background: $pet-bg-white;
  box-shadow: $pet-shadow-base;
  display: flex;
  align-items: center;
  justify-content: center;
}

.locate-icon {
  font-size: 40rpx;
}

.position-card {
  margin: $pet-space-sm;
  padding: $pet-space-base;
}

/* 当前位置卡片 */
.position-row {
  display: flex;
  align-items: center;
  gap: $pet-space-sm;
}

.position-icon {
  font-size: 44rpx;
}

.position-main {
  flex: 1;
  min-width: 0;
}

.position-title {
  display: block;
  font-size: $pet-font-base;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: 6rpx;
}

.position-address {
  display: block;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-section {
  margin: 0 $pet-space-sm;
}

/* 店铺列表 */
.section-title {
  display: flex;
  align-items: center;
  gap: $pet-space-xs;
  font-size: $pet-font-lg;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: $pet-space-sm;
}

.title-bar {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, $pet-primary, $pet-primary-light);
  border-radius: 4rpx;
}

.shop-count {
  margin-left: auto;
  font-size: $pet-font-sm;
  color: $pet-text-grey;
  font-weight: 400;
}

.shop-list {
  display: flex;
  flex-direction: column;
  gap: $pet-space-sm;
  padding-bottom: $pet-space-base;
}

.shop-item {
  display: flex;
  align-items: center;
  padding: $pet-space-base;
}

.shop-info {
  flex: 1;
  min-width: 0;
}

.shop-name {
  display: block;
  font-size: $pet-font-base;
  font-weight: 700;
  color: $pet-text;
  margin-bottom: 8rpx;
}

.shop-address {
  display: block;
  font-size: $pet-font-sm;
  color: $pet-text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-distance {
  font-size: $pet-font-sm;
  color: $pet-primary;
  font-weight: 600;
  margin-right: $pet-space-sm;
}

.shop-arrow {
  font-size: $pet-font-lg;
  color: $pet-text-grey;
}

.empty-tip {
  text-align: center;
  padding: 80rpx 0;
  color: $pet-text-grey;
  font-size: $pet-font-base;
}
</style>
