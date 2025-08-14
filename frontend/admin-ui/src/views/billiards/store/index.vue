<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="16">
            <el-form ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
              <el-form-item label="门店名称" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入门店名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="营业状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="营业状态" clearable style="width: 200px">
                  <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="8">
            <div class="tool-button-group">
              <el-button type="primary" icon="Plus" @click="handleAdd">新增门店</el-button>
              <el-button icon="Delete" :disabled="multiple" @click="handleDelete">批量删除</el-button>
            </div>
          </el-col>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="storeList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="门店名称" align="center" prop="name" />
        <el-table-column label="联系电话" align="center" prop="contactPhone" />
        <el-table-column label="地址" align="center" prop="address" :show-overflow-tooltip="true" />
        <el-table-column label="营业时间" align="center" prop="businessHours" />
        <el-table-column label="桌台数量" align="center" prop="tableCount">
          <template #default="scope">
            {{ scope.row.tableCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="营业状态" align="center" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="success">营业中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="warning">休息中</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger">已停业</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="280">
          <template #default="scope">
            <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="text" icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button type="text" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="text" icon="Refresh" @click="openStatusDialog(scope.row)">切换状态</el-button>
            <el-button type="text" icon="List" @click="handleViewTables(scope.row)">查看桌台</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 添加或修改门店对话框 -->
    <el-dialog v-model="open" :title="title" width="800px" append-to-body>
      <el-form ref="storeRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="门店名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入门店名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 新增：地图选点按钮 -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="门店地址">
              <el-button type="primary" plain style="margin-left: 10px" @click="openMapDialog">
                <el-icon style="margin-right: 4px"><Location /></el-icon>在地图上选择
              </el-button>
              <div style="font-size: 12px; color: #909399; margin-left: 10px; line-height: normal; margin-top: 5px">
                点击按钮通过地图选择，可自动填充省市区、详细地址及经纬度。
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="地图选择或手动输入省份" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="地图选择或手动输入城市" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="区县" prop="district">
              <el-input v-model="form.district" placeholder="地图选择或手动输入区县" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="地图选择或手动输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="经度坐标" prop="longitude">
              <el-input v-model="form.longitude" placeholder="地图选择或自动获取" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度坐标" prop="latitude">
              <el-input v-model="form.latitude" placeholder="地图选择或自动获取" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="营业时间" prop="businessHours">
              <el-input v-model="form.businessHours" placeholder="如: 09:00-23:00" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in statusOptions" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="门店封面图" prop="coverImage">
          <el-upload class="avatar-uploader" :http-request="handleFileUpload" :show-file-list="false" :before-upload="beforeAvatarUpload">
            <img v-if="coverImagePreviewUrl" :src="coverImagePreviewUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="门店公告" prop="announcement">
          <el-input v-model="form.announcement" type="textarea" placeholder="请输入门店公告" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增：切换状态弹窗 -->
    <el-dialog v-model="statusDialogOpen" title="切换门店运营状态" width="400px" append-to-body>
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="请选择新状态">
            <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="statusForm.announcement" type="textarea" placeholder="请输入状态说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitStatusChange">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 新增：地图选点对话框 -->
    <el-dialog v-model="mapDialogVisible" title="在地图上选择位置" width="800px" append-to-body @close="handleMapDialogClose">
      <!-- 使用您创建的地图组件 -->
      <MapContainer v-if="mapDialogVisible" ref="mapComponentRef" @location-selected="handleMapLocationSelected" @load-error="handleMapLoadError" />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="mapDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirmMapSelection">确 定选点</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 门店详情弹窗 -->
    <el-dialog v-model="detailOpen" title="门店详情" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="门店名称">{{ detailForm.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailForm.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="省份">{{ detailForm.province }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ detailForm.city }}</el-descriptions-item>
        <el-descriptions-item label="区县">{{ detailForm.district }}</el-descriptions-item>
        <el-descriptions-item label="详细地址">{{ detailForm.address }}</el-descriptions-item>
        <el-descriptions-item label="经度坐标">{{ detailForm.longitude }}</el-descriptions-item>
        <el-descriptions-item label="纬度坐标">{{ detailForm.latitude }}</el-descriptions-item>
        <el-descriptions-item label="营业时间">{{ detailForm.businessHours }}</el-descriptions-item>
        <el-descriptions-item label="营业状态">
          <el-tag v-if="detailForm.status === 0" type="success">营业中</el-tag>
          <el-tag v-else-if="detailForm.status === 1" type="warning">休息中</el-tag>
          <el-tag v-else-if="detailForm.status === 2" type="danger">已停业</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailForm.createTime }}</el-descriptions-item>
        <el-descriptions-item label="桌台数量">{{ detailForm.tableCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="门店公告" :span="2">{{ detailForm.announcement }}</el-descriptions-item>
        <el-descriptions-item label="门店封面图" :span="2">
          <img v-if="detailCoverImageUrl" :src="detailCoverImageUrl" class="detail-image" />
          <span v-else>无封面图</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
          <el-button type="primary" @click="handleViewTables(detailForm)">查看桌台</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Location } from '@element-plus/icons-vue';
import MapContainer from '@/components/map/MapContainer.vue';
import { listStore, getStore, addStore, updateStore, deleteStore, changeStoreStatus } from '@/api/billiards/store';
import { uploadFile } from '@/api/system/file';
import { useUserStore } from '@/store/modules/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<Array<string>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 总条数
const total = ref(0);
// 门店表格数据
const storeList = ref<Array<any>>([]);
// 弹出层标题
const title = ref('');
// 是否显示弹出层
const open = ref(false);

// 营业状态字典
const statusOptions = ref([
  { value: 0, label: '营业中' },
  { value: 1, label: '休息中' },
  { value: 2, label: '已停业' }
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  status: undefined
});

// 表单参数
const form = reactive({
  id: undefined,
  name: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  longitude: '',
  latitude: '',
  businessHours: '',
  coverImage: '',
  announcement: '',
  status: 0
});
const coverImagePreviewUrl = ref('');

// 表单校验
const rules = reactive({
  name: [{ required: true, message: '门店名称不能为空', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
  province: [{ required: true, message: '省份不能为空', trigger: 'blur' }],
  city: [{ required: true, message: '城市不能为空', trigger: 'blur' }],
  district: [{ required: true, message: '区县不能为空', trigger: 'blur' }],
  address: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
  businessHours: [{ required: true, message: '营业时间不能为空', trigger: 'blur' }]
});

const queryRef = ref();
const storeRef = ref();

// 切换状态弹窗相关
const statusDialogOpen = ref(false);
const statusForm = reactive({
  id: '',
  status: 0,
  announcement: ''
});

// 地图选点相关
const mapDialogVisible = ref(false);
const mapComponentRef = ref();

// 详情弹窗相关
const detailOpen = ref(false);
const detailForm = reactive({
  id: '',
  name: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  longitude: '',
  latitude: '',
  businessHours: '',
  coverImage: '',
  announcement: '',
  status: 0,
  createTime: '',
  tableCount: 0
});
const detailCoverImageUrl = ref('');

/** 查询门店列表 */
function getList() {
  loading.value = true;
  listStore(queryParams)
    .then((response) => {
      storeList.value = response.data.records;
      total.value = response.data.total;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  resetForm();
}

/** 表单重置 */
function resetForm() {
  form.id = undefined;
  form.name = '';
  form.contactPhone = '';
  form.province = '';
  form.city = '';
  form.district = '';
  form.address = '';
  form.longitude = '';
  form.latitude = '';
  form.businessHours = '';
  form.coverImage = '';
  coverImagePreviewUrl.value = '';
  form.announcement = '';
  form.status = 0;
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  open.value = true;
  title.value = '添加门店';
}

/** 修改按钮操作 */
async function handleUpdate(row: any) {
  resetForm();
  const storeId = row.id;
  try {
    const response = await getStore(storeId);
    Object.assign(form, response.data);

    if (response.data && response.data.coverImagePreviewUrl) {
      coverImagePreviewUrl.value = response.data.coverImagePreviewUrl;
    } else if (form.coverImage) {
      coverImagePreviewUrl.value = '';
      console.warn('后端未直接提供封面图预览URL (coverImagePreviewUrl)，且前端未配置备用逻辑。');
    } else {
      coverImagePreviewUrl.value = '';
    }

    open.value = true;
    title.value = '修改门店';
  } catch (error) {
    console.error('获取门店详情失败:', error);
    ElMessage.error('获取门店详情失败，请重试');
  }
}

/** 详情按钮操作 */
async function handleDetail(row: any) {
  try {
    const response = await getStore(row.id);
    Object.assign(detailForm, response.data);
    
    if (response.data && response.data.coverImagePreviewUrl) {
      detailCoverImageUrl.value = response.data.coverImagePreviewUrl;
    } else if (detailForm.coverImage) {
      detailCoverImageUrl.value = '';
      console.warn('后端未直接提供封面图预览URL (coverImagePreviewUrl)，且前端未配置备用逻辑。');
    } else {
      detailCoverImageUrl.value = '';
    }
    
    detailOpen.value = true;
  } catch (error) {
    console.error('获取门店详情失败:', error);
    ElMessage.error('获取门店详情失败，请重试');
  }
}

/** 提交按钮 */
function submitForm() {
  storeRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.id) {
        updateStore(form.id, form).then(() => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addStore(form).then(() => {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const storeIds = row?.id ? [row.id] : ids.value;
  ElMessageBox.confirm('是否确认删除所选门店?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      return deleteStore(storeIds.toString());
    })
    .then(() => {
      getList();
      ElMessage.success('删除成功');
    });
}

/** 封面图上传前检查 */
function beforeAvatarUpload(file: any) {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('上传封面图只能是图片格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('上传封面图大小不能超过 2MB!');
    return false;
  }
  return true;
}

function openStatusDialog(row: any) {
  statusForm.id = row.id;
  statusForm.status = row.status;
  statusForm.announcement = row.announcement || '';
  statusDialogOpen.value = true;
}

function submitStatusChange() {
  if (!statusForm.id) return;
  changeStoreStatus({
    id: statusForm.id,
    status: statusForm.status,
    announcement: statusForm.announcement
  }).then(() => {
    ElMessage.success('门店状态切换成功');
    statusDialogOpen.value = false;
    getList();
  });
}

/**
 * 打开地图选点对话框。
 */
function openMapDialog() {
  mapDialogVisible.value = true;
}

/**
 * 处理地图对话框关闭事件 (主要用于el-dialog的@close回调，目前无特殊逻辑)。
 */
function handleMapDialogClose() {
  // console.log('地图对话框已关闭');
}

/**
 * 处理用户点击地图对话框底部的"确定选点"按钮的逻辑。
 * 会尝试从MapContainer组件获取当前最新的选点信息并应用。
 */
function confirmMapSelection() {
  if (mapComponentRef.value && typeof mapComponentRef.value.getCurrentSelection === 'function') {
    const selectedLocation = mapComponentRef.value.getCurrentSelection();

    if (selectedLocation) {
      console.log('index.vue: confirmMapSelection received data from getCurrentSelection:', JSON.parse(JSON.stringify(selectedLocation)));
      handleMapLocationSelected(selectedLocation);
    } else {
      ElMessage.info('请先在地图上选择一个位置，或当前未成功获取位置信息。');
    }
  } else {
    ElMessage.error('无法获取地图选点信息，请重试。');
    mapDialogVisible.value = false;
  }
}

/**
 * 处理从MapContainer组件通过事件(location-selected)传递过来的选点数据，
 * 或由confirmMapSelection方法获取数据后调用此方法。
 * 负责将地址信息回填到表单，并关闭地图对话框。
 * @param {any} locationData - 从MapContainer组件获取的地理位置数据对象。
 */
function handleMapLocationSelected(locationData: any) {
  console.log('index.vue: Received location data:', JSON.parse(JSON.stringify(locationData)));

  if (locationData && (locationData.address || (locationData.province && locationData.city))) {
    form.province = locationData.province || '';
    form.city = locationData.city || '';
    form.district = locationData.district || '';
    form.address = locationData.address || '';
    form.longitude = String(locationData.longitude || '');
    form.latitude = String(locationData.latitude || '');

    mapDialogVisible.value = false;
  } else {
    ElMessage.warning('从地图获取地址信息失败或信息不完整，请重试或手动输入。');
    console.warn('index.vue: Invalid or incomplete location data received:', locationData);
  }
}

/**
 * 处理MapContainer组件加载失败时触发的load-error事件。
 * @param {string} message - 从MapContainer组件传递过来的错误消息。
 */
function handleMapLoadError(message: string) {
  ElMessage.error(message || '地图组件加载失败，请检查相关配置或联系技术支持。');
  mapDialogVisible.value = false;
}

/**
 * 自定义文件上传处理
 */
async function handleFileUpload(uploadRequestOption: any) {
  const { file } = uploadRequestOption;
  try {
    const response = await uploadFile(file, 'STORE_COVER');

    if (response && response.data) {
      form.coverImage = response.data.resourceId;
      coverImagePreviewUrl.value = response.data.previewUrl;
      ElMessage.success(`封面图上传成功: ${response.data.fileName || file.name}`);
    } else {
      let errorMsg = '封面上传失败';
      if (response && response.msg) {
        errorMsg += `: ${response.msg}`;
      } else if (response && response.data && (!response.data.previewUrl || !response.data.resourceId)) {
        errorMsg += ': 服务器返回数据不完整';
      } else {
        errorMsg += ': 未知服务端错误';
      }
      ElMessage.error(errorMsg);
      if (uploadRequestOption.onError) {
        uploadRequestOption.onError(new Error(errorMsg));
      }
    }
  } catch (error: any) {
    let errorMsg = '封面上传请求失败';
    if (error && error.message) {
      errorMsg += `: ${error.message}`;
    } else if (error && error.msg) {
      errorMsg += `: ${error.msg}`;
    } else {
      errorMsg += ': 请检查网络或联系管理员';
    }
    ElMessage.error(errorMsg);
    if (uploadRequestOption.onError) {
      uploadRequestOption.onError(error);
    }
  }
}

/** 查看门店桌台按钮操作 */
function handleViewTables(row: any) {
  router.push({
    path: '/billiards/table',
    query: { storeId: row.id, storeName: row.name }
  });
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.tool-button-group {
  display: flex;
  justify-content: flex-end;
}
.detail-image {
  max-width: 300px;
  max-height: 200px;
  object-fit: contain;
}
</style>
