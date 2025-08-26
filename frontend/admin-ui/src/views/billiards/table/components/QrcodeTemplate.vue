<template>
  <div class="qrcode-template" ref="qrcodeRef">
    <div class="qrcode-header">
      <img v-if="storeLogo" :src="storeLogo" class="store-logo" alt="门店logo" />
      <div class="store-info">
        <div class="store-name">{{ storeName }}</div>
        <div class="store-slogan">扫码使用桌台</div>
      </div>
    </div>
    
    <div class="qrcode-body">
      <div class="qrcode-image-container">
        <img :src="qrcodeUrl" alt="二维码" class="qrcode-image" />
      </div>
      <div class="table-info">
        <div class="table-number">桌台编号：{{ tableNumber }}</div>
        <div class="table-type">
          桌台类型：
          <span class="type-tag" :class="typeClass">{{ tableTypeName }}</span>
        </div>
      </div>
    </div>

    <div class="qrcode-footer">
      <div class="footer-text">欢迎使用 {{ storeName }} 台球室</div>
      <div class="footer-tips">扫描上方二维码即可使用桌台</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const props = defineProps({
  storeName: {
    type: String,
    default: '台球俱乐部'
  },
  storeLogo: {
    type: String,
    default: ''
  },
  tableNumber: {
    type: String,
    required: true
  },
  tableType: {
    type: Number,
    default: 1
  },
  qrcodeUrl: {
    type: String,
    required: true
  }
});

const qrcodeRef = ref(null);

const tableTypeName = computed(() => {
  switch (props.tableType) {
    case 1: return '普通';
    case 2: return '专业';
    case 3: return '大师';
    default: return '普通';
  }
});

const typeClass = computed(() => {
  switch (props.tableType) {
    case 1: return 'type-normal';
    case 2: return 'type-pro';
    case 3: return 'type-master';
    default: return 'type-normal';
  }
});

defineExpose({
  qrcodeRef
});
</script>

<style scoped>
.qrcode-template {
  width: 350px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.qrcode-header {
  background: linear-gradient(135deg, #3a8ee6, #5254ae);
  color: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
}

.store-logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 15px;
  background: #fff;
  padding: 3px;
}

.store-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 3px;
}

.store-slogan {
  font-size: 12px;
  opacity: 0.9;
}

.qrcode-body {
  padding: 25px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qrcode-image-container {
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.qrcode-image {
  width: 200px;
  height: 200px;
  display: block;
}

.table-info {
  width: 100%;
  font-size: 14px;
  color: #333;
  margin-top: 5px;
}

.table-number {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  text-align: center;
}

.table-type {
  text-align: center;
  margin-bottom: 5px;
}

.type-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.type-normal {
  background-color: #909399;
}

.type-pro {
  background-color: #409EFF;
}

.type-master {
  background-color: #67C23A;
}

.qrcode-footer {
  background-color: #f5f7fa;
  padding: 15px;
  text-align: center;
  font-size: 12px;
  color: #606266;
  border-top: 1px solid #ebeef5;
}

.footer-text {
  margin-bottom: 5px;
  font-weight: bold;
}

.footer-tips {
  font-size: 11px;
  color: #909399;
}
</style> 