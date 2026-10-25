<template>
  <div class="map-container">
    <!-- 百度地图容器 -->
    <div id="map-container" ref="mapContainer"></div>

    <!-- 地图控制面板：定位与附近店铺查询 -->
    <div class="control-panel">
      <button @click="getCurrentLocation" class="location-btn">
        获取当前位置
      </button>
      <button @click="getAddressFromServer" class="location-btn">
        显示附近店铺
      </button>

      <!-- 当前位置信息展示 -->
      <div v-if="currentPosition" class="position-info">
        <p>经度: {{ currentPosition.lng }}</p>
        <p>纬度: {{ currentPosition.lat }}</p>
        <p>地址: {{ currentAddress || '正在获取...' }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getLocation } from '../../api/module/location'
import { normalizeShopName } from '../../utils/text'

export default {
  name: 'BaiduMap',
  data () {
    return {
      // 百度地图实例
      map: null,
      // 当前定位坐标
      currentPosition: null,
      // 当前定位地址文本
      currentAddress: '',
      // 百度地图地理编码实例
      geocoder: null,
      // 当前位置标记
      marker: null,
      // 所有店铺标记点集合
      allMarkers: [],
      // 信息窗口实例
      infoWindow: null
    }
  },
  // 组件挂载后初始化地图
  mounted () {
    this.initMap()
  },
  methods: {
    /**
     * 初始化百度地图
     * 若 BMap 全局对象未加载完成，则循环等待
     */
    initMap () {
      if (typeof BMap !== 'undefined') {
        this.createMap()
      } else {
        setTimeout(this.initMap, 100)
      }
    },

    /**
     * 创建地图实例并设置中心点与交互能力
     */
    createMap () {
      this.map = new BMap.Map(this.$refs.mapContainer)
      const point = new BMap.Point(116.404, 39.915)
      this.map.centerAndZoom(point, 15)
      this.map.enableScrollWheelZoom()
      this.map.enableDragging()
      this.geocoder = new BMap.Geocoder()

      // 初始化信息窗口
      this.infoWindow = new BMap.InfoWindow('', {
        width: 250,
        height: 100,
        title: '位置信息'
      })
    },

    /**
     * 从服务端获取店铺数据并在地图上添加标记
     */
    async getAddressFromServer() {
      try {
        // 清除之前的标记
        this.clearAllMarkers();

        const response = await getLocation({});
        console.log('完整响应:', response); // 打印完整响应以便调试

        // 更健壮的数据检查，兼容多种响应结构
        let shops = [];

        if (Array.isArray(response)) {
          shops = response;
        } else if (Array.isArray(response?.data)) {
          shops = response.data;
        } else if (response?.statusCode === '200' && Array.isArray(response?.data)) {
          shops = response.data;
        } else {
          throw new Error('无法解析店铺数据');
        }

        if (shops.length === 0) {
          alert('没有获取到店铺数据');
          return;
        }

        // 处理每个店铺地址，添加地图标记
        shops.forEach(shop => {
          if (shop.address) {
            this.addShopMarker(shop.address, normalizeShopName(shop.shopName), shop.shopId);
          }
        });

        // 如果有当前位置，将地图中心设置为当前位置
        if (this.currentPosition) {
          const point = new BMap.Point(this.currentPosition.lng, this.currentPosition.lat);
          this.map.setCenter(point);
        }
      } catch (error) {
        console.error('获取店铺数据失败:', error);
        alert(`获取店铺数据失败: ${error.message || '未知错误'}`);
      }
    },

    /**
     * 添加店铺标记到地图，并绑定点击与悬停事件
     * @param {String} address 店铺地址
     * @param {String} name 店铺名称
     * @param {Number} shopId 店铺 ID
     */
    addShopMarker (address, name,shopId) {
      this.geocoder.getPoint(address, (point) => {
        if (point) {
          // 创建标记
          const marker = new BMap.Marker(point)
          this.map.addOverlay(marker)
          this.allMarkers.push(marker) // 保存标记引用

          // 创建信息窗口内容
          const infoContent = `
            <div style="font-size:14px;color:#333;">
              <h4 style="margin:0 0 5px 0;">${name}</h4>
              <p style="margin:0;">地址: ${address}</p>
            </div>
          `

          // 添加点击事件：确认后跳转店铺详情
          marker.addEventListener('click', () => {
            this.$confirm('是否要跳转到商店界面?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              // 用户点击确定后的逻辑
              this.$router.push({path: '/shop', query: {id: shopId}})// 跳转到商店页面
            }).catch(() => {
              // 用户点击取消后的逻辑
              this.$message({
                type: 'info',
                message: '已取消跳转'
              });
            });
          })

          // 添加鼠标悬停事件（模拟 hover 效果）
          marker.addEventListener('mouseover', () => {
            // 创建信息窗口并打开
            const infoWindow = new BMap.InfoWindow(infoContent, {
              width: 250,
              height: 100,
              offset: new BMap.Size(0, -30) // 调整窗口位置避免遮挡标记
            });
            marker.openInfoWindow(infoWindow);

          });

          // 鼠标移出时关闭信息窗口
          marker.addEventListener('mouseout', () => {
            marker.closeInfoWindow(); // 关闭信息窗口
            // 恢复默认图标
          });

          // 添加自定义图标（可选）
          // const icon = new BMap.Icon("path/to/icon.png", new BMap.Size(30, 30))
          // marker.setIcon(icon)
        } else {
          console.warn(`无法解析地址: ${address}`)
        }
      }, '厦门市') // 设置城市可以提高解析精度
    },

    /**
     * 地址解析：将地址转换为坐标并显示
     * @param {String} address 待解析地址
     */
    geocodeAddress (address) {
      this.geocoder.getPoint(address, (point) => {
        if (point) {
          this.showLocation(point.lng, point.lat)
          this.currentAddress = address
        } else {
          alert('无法解析该地址，请检查地址是否正确')
        }
      })
    },

    /**
     * 清除地图上所有标记
     */
    clearAllMarkers () {
      // 清除当前位置标记
      if (this.marker) {
        this.map.removeOverlay(this.marker)
      }
      this.marker = null

      // 清除所有店铺标记
      this.allMarkers.forEach(marker => {
        this.map.removeOverlay(marker)
      })
      this.allMarkers = []
    },

    /**
     * 获取浏览器当前定位，并转换为百度坐标后展示
     */
    getCurrentLocation () {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          async (position) => {
            const { latitude, longitude } = position.coords
            const baiduPoint = await this.convertCoordinate(longitude, latitude)
            this.showLocation(baiduPoint.lng, baiduPoint.lat)
          },
          (error) => {
            console.error('获取位置失败:', error)
            alert('获取位置失败: ' + this.getErrorMsg(error))
          },
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
          }
        )
      } else {
        alert('您的浏览器不支持地理定位功能')
      }
    },

    /**
     * 坐标转换：将 GPS 坐标转换为百度坐标
     * @param {Number} lng 经度
     * @param {Number} lat 纬度
     * @returns {Promise<Object>} 转换后的百度坐标点
     */
    convertCoordinate (lng, lat) {
      return new Promise((resolve) => {
        const convertor = new BMap.Convertor()
        const pointArr = [new BMap.Point(lng, lat)]
        convertor.translate(pointArr, 1, 5, (data) => {
          if (data.status === 0) {
            resolve(data.points[0])
          } else {
            resolve({ lng, lat })
          }
        })
      })
    },

    /**
     * 在地图上展示指定坐标位置
     * @param {Number} lng 经度
     * @param {Number} lat 纬度
     */
    showLocation (lng, lat) {
      this.currentPosition = { lng, lat }
      const point = new BMap.Point(lng, lat)

      this.map.centerAndZoom(point, 18)

      this.clearAllMarkers()

      this.marker = new BMap.Marker(point)
      this.map.addOverlay(this.marker)

      // 获取地址信息
      this.getAddress(point)
    },

    /**
     * 根据坐标获取地址信息并展示信息窗口
     * @param {Object} point 百度坐标点
     */
    getAddress (point) {
      this.geocoder.getLocation(point, (result) => {
        this.currentAddress = result ? result.address : '无法获取地址信息'

        // 显示信息窗口
        const infoWindow = new BMap.InfoWindow(`
          <div style="font-size:14px;">
            <p>${this.currentAddress}</p>
          </div>
        `)
        this.marker.openInfoWindow(infoWindow)
      })
    },

    /**
     * 根据浏览器定位错误码返回中文错误信息
     * @param {Object} error 定位错误对象
     * @returns {String} 错误说明
     */
    getErrorMsg (error) {
      switch (error.code) {
        case error.PERMISSION_DENIED:
          return '用户拒绝了位置请求'
        case error.POSITION_UNAVAILABLE:
          return '位置信息不可用'
        case error.TIMEOUT:
          return '获取位置请求超时'
        case error.UNKNOWN_ERROR:
          return '未知错误'
        default:
          return '发生错误'
      }
    }
  }
}
</script>

<style scoped>
.map-container {
  position: relative;
  width: 100%;
  height: 600px;
}

/* 地图容器 */
#map-container {
  width: 100%;
  height: 100%;
}

/* 地图控制面板 */
.control-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  background: white;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 定位按钮 */
.location-btn {
  padding: 8px 16px;
  background: #3385ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.location-btn:hover {
  background: #2d78f4;
}

/* 当前位置信息 */
.position-info {
  margin-top: 10px;
  font-size: 14px;
  color: #333;
  background: #f8f8f8;
  padding: 8px;
  border-radius: 4px;
}

.position-info p {
  margin: 5px 0;
}
</style>
