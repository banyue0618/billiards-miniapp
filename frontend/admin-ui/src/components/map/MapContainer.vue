<template>
  <!-- 地图容器DOM元素 -->
  <div ref="mapContainerRef" style="width: 100%; height: 500px"></div>
  <!-- 用户提示信息 -->
  <div v-if="!isKeyAvailable" style="text-align: center; padding: 20px; color: red">错误：地图API Key未配置 (请检查VITE_APP_AMAP_KEY环境变量)。</div>
  <div v-else-if="loadError" style="text-align: center; padding: 20px; color: red">
    错误：地图加载失败，请检查API Key、安全密钥、域名白名单或网络连接。
  </div>
  <div v-else style="font-size: 12px; color: #606266; margin-top: 8px; text-align: center">请点击地图选择位置，或拖动标记调整。</div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch, defineExpose } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';

// 定义经纬度对象接口
interface Position {
  longitude: number | string; // 经度
  latitude: number | string; // 纬度
}

// 定义高德地图逆地理编码返回的地址组成部分接口
interface AddressComponent {
  province: string; // 省份
  city: string; // 城市
  citycode: string; // 城市编码
  district: string; // 区县
  adcode: string; // 行政区编码
  township: string; // 乡镇/街道
  street: string; // 道路
  streetNumber: string; // 门牌号
}

// 定义组件向父组件传递的地理位置数据接口
interface LocationData {
  province: string; // 省份
  city: string; // 城市
  district: string; // 区县
  address: string; // 详细地址 (通常不含省市区前缀)
  longitude: number; // 经度
  latitude: number; // 纬度
  fullAddress?: string; // 包含省市区的完整格式化地址
  addressComponent?: AddressComponent; // 原始的地址组成部分对象
}

// 定义组件接收的props
const props = defineProps({
  // 初始定位信息，用于地图打开时定位到已有坐标
  initialPosition: {
    type: Object as () => Position | null,
    default: null
  }
});

// 定义组件可以触发的自定义事件
const emit = defineEmits<{
  (e: 'location-selected', data: LocationData): void; // 当用户选择位置并成功获取地址信息后触发
  (e: 'load-error', message: string): void; // 当地图加载或初始化失败时触发
}>();

const mapContainerRef = ref<HTMLDivElement | null>(null); // 地图容器的DOM引用
let map: any = null; // 高德地图实例
let marker: any = null; // 地图上的点标记实例
let geocoder: any = null; // 高德逆地理编码服务实例
const loadError = ref(false); // 标记地图是否加载失败
const isKeyAvailable = ref(false); // 标记API Key是否已配置
let amapInstance: any = null; // 存储从AMapLoader加载的AMap对象，用于后续创建地图相关对象

// 存储最近一次成功获取到的选点数据，用于通过defineExpose暴露给父组件
const latestLocationData = ref<LocationData | null>(null);

// 组件挂载后执行初始化
onMounted(() => {
  // 从环境变量中获取高德地图API Key
  const apiKey = import.meta.env.VITE_APP_AMAP_KEY as string | undefined;
  if (!apiKey) {
    console.error('MapContainer: VITE_APP_AMAP_KEY is not defined in environment variables.');
    loadError.value = true;
    isKeyAvailable.value = false;
    emit('load-error', '地图API Key未配置 (VITE_APP_AMAP_KEY)');
    return;
  }
  isKeyAvailable.value = true;

  // 确保地图容器DOM已准备好
  if (mapContainerRef.value) {
    initializeMap(apiKey);
  } else {
    console.error('MapContainer: mapContainerRef is not available on mount.');
    emit('load-error', '地图容器初始化失败');
  }
});

// 监听initialPosition prop的变化，以便在父组件更新初始位置时，地图能相应更新
watch(
  () => props.initialPosition,
  (newPos, oldPos) => {
    // 仅当新旧位置不同，且地图和AMap实例已初始化时处理
    if (map && newPos && amapInstance && (newPos.longitude !== oldPos?.longitude || newPos.latitude !== oldPos?.latitude)) {
      const lng = parseFloat(String(newPos.longitude));
      const lat = parseFloat(String(newPos.latitude));
      if (!isNaN(lng) && !isNaN(lat)) {
        const newLngLat = new amapInstance.LngLat(lng, lat); // 创建高德地图经纬度对象
        map.setCenter(newLngLat); // 设置地图中心点
        if (marker) {
          marker.setPosition(newLngLat); // 更新标记位置
        } else {
          // 如果标记实例不存在 (例如，在initialPosition首次有效时)，则创建新标记
          if (amapInstance) {
            marker = new amapInstance.Marker({
              position: newLngLat,
              draggable: true, // 标记可拖拽
              map: map // 将标记添加到地图上
            });
            marker.on('dragend', handleMarkerDragEnd); // 监听标记拖拽结束事件
          }
        }
        // 当initialPosition变化时，对新位置进行逆地理编码并存储，但不立即emit事件
        geocodeInitialPosition(newLngLat);
      }
    }
  },
  { deep: true }
); // 使用deep watch来侦听对象内部属性的变化

/**
 * 对给定的经纬度进行逆地理编码，并将结果存储在latestLocationData中。
 * 此函数主要用于处理初始位置或父组件更新位置时，不希望立即触发location-selected事件的场景。
 * @param lnglat 高德地图经纬度对象 (AMap.LngLat)
 */
async function geocodeInitialPosition(lnglat: any) {
  if (!geocoder) return; // 确保逆地理编码服务已初始化
  geocoder.getAddress(lnglat, (status: string, result: any) => {
    if (status === 'complete' && result.info === 'OK' && result.regeocode) {
      const addressComponent = result.regeocode.addressComponent as AddressComponent;
      const formattedAddress = result.regeocode.formattedAddress as string;
      let city = addressComponent.city;
      // 处理直辖市：在高德API中，直辖市的city字段可能为空，此时city应取province的值
      if (!city && addressComponent.province) {
        const municipalities = ['北京市', '上海市', '天津市', '重庆市'];
        if (municipalities.includes(addressComponent.province)) {
          city = addressComponent.province;
        }
      }
      // 尝试从完整地址中移除省市区前缀，得到更纯粹的详细地址
      let detailedAddress = formattedAddress;
      if (addressComponent.province) detailedAddress = detailedAddress.replace(addressComponent.province, '');
      if (city) detailedAddress = detailedAddress.replace(city, '');
      if (addressComponent.district) detailedAddress = detailedAddress.replace(addressComponent.district, '');
      detailedAddress = detailedAddress.trim();
      // 如果移除后详细地址为空，则使用乡镇/街道+路+门牌号拼接
      if (!detailedAddress) {
        detailedAddress = (addressComponent.township || '') + (addressComponent.street || '') + (addressComponent.streetNumber || '');
      }
      // 更新内部存储的最新选点数据
      latestLocationData.value = {
        province: addressComponent.province || '',
        city: city || '',
        district: addressComponent.district || '',
        address: detailedAddress.trim() || '',
        longitude: lnglat.getLng(),
        latitude: lnglat.getLat(),
        fullAddress: formattedAddress,
        addressComponent: addressComponent
      };
      console.log(
        'MapContainer: Geocoded initial/silent position and updated latestLocationData:',
        JSON.parse(JSON.stringify(latestLocationData.value))
      );
    } else {
      console.error('MapContainer: Initial position geocoding failed:', status, result);
      latestLocationData.value = null; // 编码失败则清空
    }
  });
}

/**
 * 初始化高德地图及相关插件和服务
 * @param apiKey 高德地图Web JS API Key
 */
async function initializeMap(apiKey: string) {
  let geoLocatedPosition: { longitude: number; latitude: number } | null = null;

  // 1. Attempt to get browser's current location if initialPosition is not provided
  if (!props.initialPosition && navigator.geolocation) {
    console.log('MapContainer: initialPosition not provided, attempting browser geolocation.');
    try {
      const position = await new Promise<GeolocationPosition>((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject, {
          enableHighAccuracy: true,
          timeout: 5000, // 5 seconds timeout
          maximumAge: 0, // Do not use a cached position
        });
      });
      geoLocatedPosition = {
        longitude: position.coords.longitude,
        latitude: position.coords.latitude,
      };
      console.log('MapContainer: Browser geolocation successful:', geoLocatedPosition);
    } catch (error: any) {
      console.warn('MapContainer: Browser geolocation failed:', error.message, 'Will proceed with defaults or props.');
      // geoLocatedPosition will remain null, and logic will fallback gracefully
    }
  }

  AMapLoader.load({
    key: apiKey,
    version: '2.0', // 指定JS API版本
    plugins: ['AMap.Geocoder', 'AMap.Marker'] // 需要加载的插件列表
  })
    .then((AMap) => {
      console.log('map init');
      amapInstance = AMap; // 保存AMap对象引用
      loadError.value = false;
      let initialLng = 116.397428; // 默认中心经度 (北京天安门)
      let initialLat = 39.90923; // 默认中心纬度
      let initialZoom = 11; // 默认缩放级别
      let shouldUseThisInitialPointForGeocode = false;

      // 如果父组件传入了initialPosition，则使用它作为地图的初始中心
      if (props.initialPosition) {
        const lng = parseFloat(String(props.initialPosition.longitude));
        const lat = parseFloat(String(props.initialPosition.latitude));
        if (!isNaN(lng) && !isNaN(lat)) {
          initialLng = lng;
          initialLat = lat;
          initialZoom = 15; // 如果有具体初始位置，则放大地图级别
          shouldUseThisInitialPointForGeocode = true;
        }
      } else if (geoLocatedPosition) { // Else, if geolocation was successful
        initialLng = geoLocatedPosition.longitude;
        initialLat = geoLocatedPosition.latitude;
        initialZoom = 15; // Zoom in for current location
        console.log('MapContainer: Using browser geolocated position:', { initialLng, initialLat });
        shouldUseThisInitialPointForGeocode = true;
      } else {
        // Fallback to default (Tiananmen) if no props.initialPosition and geolocation failed or was not attempted
        console.log('MapContainer: Using default Tiananmen position:', { initialLng, initialLat });
        // We might want to geocode the default as well for consistency
        shouldUseThisInitialPointForGeocode = true;
      }

      if (!mapContainerRef.value) {
        console.error('MapContainer: Cannot initialize map, container ref is null.');
        loadError.value = true;
        emit('load-error', '地图容器初始化失败');
        return;
      }
      // 创建地图实例
      map = new AMap.Map(mapContainerRef.value, {
        viewMode: '2D', // 使用2D视图模式
        zoom: initialZoom,
        center: [initialLng, initialLat],
        resizeEnable: true // 允许监听窗口大小调整地图尺寸
      });

      // 初始化逆地理编码服务
      geocoder = new AMap.Geocoder({
        radius: 1000, // 查询半径，单位米
        extensions: 'all' // 返回详细信息
      });

      // 创建一个可拖拽的标记点，并添加到地图上
      marker = new AMap.Marker({
        position: [initialLng, initialLat], // 标记初始位置为地图中心
        draggable: true,
        map: map
      });

      // 监听地图点击事件
      map.on('click', handleMapClick);
      // 监听标记拖拽结束事件
      marker.on('dragend', handleMarkerDragEnd);

      // 如果有初始位置，则对该位置进行一次逆地理编码并存储（不立即emit）
      if (marker.getPosition()) {
        geocodeInitialPosition(marker.getPosition());
      }
    })
    .catch((e) => {
      console.error('MapContainer: 高德地图JSAPI加载失败:', e);
      loadError.value = true;
      emit('load-error', '地图服务加载失败，请检查API Key或网络。');
    });
}

/**
 * 处理地图点击事件
 * @param e 地图点击事件对象，包含经纬度信息 e.lnglat
 */
function handleMapClick(e: any) {
  if (e.lnglat && marker) {
    updateMarkerAndGeocode(e.lnglat); // 更新标记位置并进行逆地理编码
  }
}

/**
 * 处理标记拖拽结束事件
 * @param e 标记拖拽事件对象，包含经纬度信息 e.lnglat
 */
function handleMarkerDragEnd(e: any) {
  if (e.lnglat) {
    updateMarkerAndGeocode(e.lnglat); // 更新标记位置并进行逆地理编码
  }
}

/**
 * 更新标记位置，并对新位置进行逆地理编码，然后更新内部存储并emit事件
 * @param lnglat 高德地图经纬度对象 (AMap.LngLat)
 */
function updateMarkerAndGeocode(lnglat: any) {
  if (!map || !geocoder || !marker || !amapInstance) return;

  marker.setPosition(lnglat); // 设置标记的新位置
  map.setCenter(lnglat); // 将地图中心移动到新位置

  // 执行逆地理编码
  geocoder.getAddress(lnglat, (status: string, result: any) => {
    if (status === 'complete' && result.info === 'OK' && result.regeocode) {
      const addressComponent = result.regeocode.addressComponent as AddressComponent;
      const formattedAddress = result.regeocode.formattedAddress as string;
      let city = addressComponent.city;
      if (!city && addressComponent.province) {
        const municipalities = ['北京市', '上海市', '天津市', '重庆市'];
        if (municipalities.includes(addressComponent.province)) {
          city = addressComponent.province;
        }
      }
      let detailedAddress = formattedAddress;
      if (addressComponent.province) detailedAddress = detailedAddress.replace(addressComponent.province, '');
      if (city) detailedAddress = detailedAddress.replace(city, '');
      if (addressComponent.district) detailedAddress = detailedAddress.replace(addressComponent.district, '');
      detailedAddress = detailedAddress.trim();
      if (!detailedAddress) {
        detailedAddress = (addressComponent.township || '') + (addressComponent.street || '') + (addressComponent.streetNumber || '');
      }
      const locationData: LocationData = {
        province: addressComponent.province || '',
        city: city || '',
        district: addressComponent.district || '',
        address: detailedAddress.trim() || '',
        longitude: lnglat.getLng(),
        latitude: lnglat.getLat(),
        fullAddress: formattedAddress,
        addressComponent: addressComponent
      };
      latestLocationData.value = locationData; // 更新内部存储的最新选点数据
      console.log('MapContainer: Emitting location-selected (and updated latestLocationData):', JSON.parse(JSON.stringify(locationData)));
      // emit('location-selected', locationData); // 触发事件，将数据传递给父组件
    } else {
      console.error('MapContainer: 逆地理编码失败:', status, result);
      latestLocationData.value = null; // 编码失败则清空
    }
  });
}

// 组件卸载时销毁地图实例，释放资源
onUnmounted(() => {
  if (map) {
    map.destroy();
  }
  map = null;
  marker = null;
  geocoder = null;
  amapInstance = null;
});

// 使用defineExpose向父组件暴露方法
defineExpose({
  /**
   * 获取当前地图组件内部存储的最新选点信息。
   * @returns {LocationData | null} 最新的选点数据对象，如果无有效选点则返回null。
   */
  getCurrentSelection: () => {
    return latestLocationData.value;
  }
});
</script>

<style scoped>
/* 此处可以添加组件的局部样式 */
</style>
