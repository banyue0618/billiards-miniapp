<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="16">
            <el-form ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
              <el-form-item label="所属门店" prop="storeId">
                <el-select v-model="queryParams.storeId" placeholder="选择门店" clearable style="width: 240px" @change="storeChanged">
                  <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="桌台编号" prop="tableNumber">
                <el-input v-model="queryParams.tableNumber" placeholder="请输入桌台编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="桌台状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="桌台状态" clearable style="width: 200px">
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
              <el-button type="primary" icon="Plus" :disabled="!currentStoreId" @click="handleAdd">新增桌台</el-button>
              <el-button type="success" icon="Plus" :disabled="!currentStoreId" @click="handleBatchAdd">批量新增</el-button>
              <el-button icon="Delete" :disabled="multiple" @click="handleDelete">批量删除</el-button>
              <el-button type="info" icon="Download" :disabled="!currentStoreId || loading" @click="batchDownloadQrcodes">批量下载二维码</el-button>
            </div>
          </el-col>
        </el-row>
      </template>

      <div v-if="!currentStoreId" class="empty-tip">
        <el-empty description="请先选择门店再管理桌台"></el-empty>
      </div>

      <div v-else>
        <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="桌台编号" align="center" prop="tableNumber" />
          <el-table-column label="桌台类型" align="center" prop="tableType">
            <template #default="scope">
              <el-tag v-if="scope.row.tableType === 1" type="info">普通</el-tag>
              <el-tag v-else-if="scope.row.tableType === 2" type="primary">专业</el-tag>
              <el-tag v-else-if="scope.row.tableType === 3" type="success">大师</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="计费规则" align="center">
            <template #default="scope">
              <el-popover
                placement="top"
                trigger="hover"
                :width="300"
                popper-class="price-rule-popover"
              >
                <template #default>
                  <div v-if="getPriceRule(scope.row.priceRuleId)">
                    <div class="price-rule-detail">
                      <span>名称：{{ getPriceRule(scope.row.priceRuleId).name }}</span>
                    </div>
                    <div class="price-rule-detail" v-if="getPriceRule(scope.row.priceRuleId).ruleType === 1">
                      <span>标准计费：{{ getPriceRule(scope.row.priceRuleId).priceUnit * 60 }}元/小时</span>
                    </div>
                    <div class="price-rule-detail" v-if="getPriceRule(scope.row.priceRuleId).ruleType === 1">
                      <span>会员价格：{{ getPriceRule(scope.row.priceRuleId).memberPrice * 60 }}元/小时</span>
                    </div>
                    <div v-if="getPriceRule(scope.row.priceRuleId).ruleType === 2">
                      <div class="price-rule-detail">
                        <span>阶梯计费详情：</span>
                      </div>
                      <div v-if="getPriceRule(scope.row.priceRuleId).ladderRules" class="ladder-rules-list">
                        <div v-for="(ladder, idx) in parseLadderRules(getPriceRule(scope.row.priceRuleId).ladderRules)" :key="idx" class="ladder-rule-item">
                          <span>第{{ idx+1 }}阶梯: </span>
                          <span>{{ ladder.startMinute }}分钟 - </span>
                          <span v-if="ladder.endMinute === -1">无限制</span>
                          <span v-else>{{ ladder.endMinute }}分钟</span>
                          <span>, {{ ladder.price }}元/分钟</span>
                        </div>
                      </div>
                      <div class="price-rule-detail">
                        <span>会员折扣：{{ getPriceRule(scope.row.priceRuleId).memberDiscount }} 折</span>
                      </div>
                    </div>
                  </div>
                  <div v-else>暂无计费规则详情</div>
                </template>
                <template #reference>
                  <div>
                    <span v-if="getPriceRule(scope.row.priceRuleId)" class="price-rule-container">
                      {{ getPriceRule(scope.row.priceRuleId).name }}
                      <el-tag size="small" type="success" v-if="getPriceRule(scope.row.priceRuleId).ruleType === 1" class="price-inline-tag">
                        {{ (getPriceRule(scope.row.priceRuleId).priceUnit * 60).toFixed(2) }}元/小时
                      </el-tag>
                      <el-tag size="small" type="warning" v-else-if="getPriceRule(scope.row.priceRuleId).ruleType === 2" class="price-inline-tag">
                        阶梯计费
                      </el-tag>
                    </span>
                    <span v-else>未设置</span>
                  </div>
                </template>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 0" type="success">空闲</el-tag>
              <el-tag v-else-if="scope.row.status === 1" type="warning">使用中</el-tag>
              <el-tag v-else-if="scope.row.status === 2" type="danger">维修中</el-tag>
              <el-tag v-else-if="scope.row.status === 3" type="info">锁定</el-tag>
            </template>
          </el-table-column>
          <!-- <el-table-column label="使用开始时间" align="center" prop="useStartTime" width="160" /> -->
          <!-- <el-table-column label="使用时长" align="center" prop="useTime" /> -->
          <el-table-column label="二维码" align="center">
            <template #default="scope">
              <el-button type="text" icon="Document" @click="previewQrcode(scope.row)">查看</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="240">
            <template #default="scope">
              <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
              <el-button type="text" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
              <el-dropdown>
                <el-button type="text" icon="More">更多</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="regenerateQrcode(scope.row)">
                      <el-icon><Refresh /></el-icon> 重新生成二维码
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.status === 1" @click="handleEndUse(scope.row)">
                      <el-icon><CircleClose /></el-icon> 强制结束使用
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.status !== 2" @click="handleStatus(scope.row, 2)">
                      <el-icon><Warning /></el-icon> 标记为维修中
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.status === 0" @click="handleStatus(scope.row, 3)">
                      <el-icon><Lock /></el-icon> 锁定桌台
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.status === 3" @click="handleStatus(scope.row, 0)">
                      <el-icon><Unlock /></el-icon> 解锁桌台
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.status === 2" @click="handleStatus(scope.row, 0)">
                      <el-icon><Unlock /></el-icon> 恢复桌台
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
      </div>
    </el-card>

    <!-- 添加或修改桌台对话框 -->
    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="tableRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属门店" prop="storeId">
          <el-select v-model="form.storeId" placeholder="选择门店" disabled>
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="编号前缀" prop="tablePrefix">
              <el-input v-model="form.tablePrefix" placeholder="如：A、B" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编号数字" prop="tableNumeric">
              <el-input-number v-model="form.tableNumeric" :min="1" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="桌台类型" prop="tableType">
          <el-select v-model="form.tableType" placeholder="请选择桌台类型">
            <el-option v-for="dict in tableTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="计费规则" prop="priceRuleId">
          <el-select v-model="form.priceRuleId" placeholder="请选择计费规则">
            <el-option v-for="rule in priceRuleOptions" :key="rule.id" :label="rule.name" :value="rule.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="桌台描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入桌台描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量添加桌台对话框 -->
    <el-dialog v-model="batchOpen" title="批量添加桌台" width="500px" append-to-body>
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="所属门店" prop="storeId">
          <el-select v-model="batchForm.storeId" placeholder="选择门店" disabled>
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="编号前缀" prop="tablePrefix">
          <el-input v-model="batchForm.tablePrefix" placeholder="如：A" />
        </el-form-item>
        <el-form-item label="起始编号" prop="startNumeric">
          <el-input-number v-model="batchForm.startNumeric" :min="1" :precision="0" />
        </el-form-item>
        <el-form-item label="创建数量" prop="count">
          <el-input-number v-model="batchForm.count" :min="1" :max="50" :precision="0" />
        </el-form-item>
        <el-form-item label="桌台类型" prop="tableType">
          <el-select v-model="batchForm.tableType" placeholder="请选择桌台类型">
            <el-option v-for="dict in tableTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="计费规则" prop="priceRuleId">
          <el-select v-model="batchForm.priceRuleId" placeholder="请选择计费规则">
            <el-option v-for="rule in priceRuleOptions" :key="rule.id" :label="rule.name" :value="rule.id" />
          </el-select>
        </el-form-item>
        <el-alert title="预览" type="info" :description="`将创建${batchForm.count}个桌台，编号为: ${generateNumberPreview()}`" show-icon />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitBatchForm">确 定</el-button>
          <el-button @click="batchOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 二维码查看对话框 -->
    <el-dialog v-model="qrcodeOpen" title="桌台二维码" width="450px" append-to-body center>
      <div class="qrcode-preview-container">
        <qrcode-template
          ref="qrcodeTemplateRef"
          :store-name="currentStore?.name || '台球俱乐部'"
          :store-logo="currentStore?.logoUrl"
          :table-number="currentTableNumber"
          :table-type="currentTableType"
          :qrcode-url="qrcodeUrl"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="downloadQrcode">下载二维码</el-button>
          <el-button @click="qrcodeOpen = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 强制结束使用对话框 -->
    <el-dialog v-model="endUseOpen" title="强制结束使用" width="500px" append-to-body>
      <el-form ref="endUseFormRef" :model="endUseForm" :rules="endUseRules" label-width="100px">
        <el-form-item label="桌台编号">
          <span>{{ currentTableNumber }}</span>
        </el-form-item>
        <el-form-item label="结束原因" prop="reason">
          <el-input v-model="endUseForm.reason" type="textarea" placeholder="请输入结束原因" />
        </el-form-item>
        <el-alert type="warning" title="提示" description="强制结束将立即停止计费并释放桌台，请谨慎操作！" show-icon />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmEndUse">确认结束</el-button>
          <el-button @click="endUseOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量下载进度弹窗 -->
    <el-dialog v-model="downloadProgressVisible" title="批量下载二维码" width="400px" :close-on-click-modal="false" :show-close="false">
      <div class="download-progress-container">
        <el-progress :percentage="downloadProgress" :format="progressFormat" />
        <div class="progress-text">{{ progressText }}</div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="downloadProgress < 100" @click="downloadProgressVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import html2canvas from 'html2canvas';
import JSZip from 'jszip';
import {
  listTable,
  getTable,
  addTable,
  batchAddTable,
  updateTable,
  deleteTable,
  batchDeleteTable,
  updateTableStatus,
  regenerateQrcode as regenerateQrcodeApi,
  endTableUsage
} from '@/api/billiards/table';
import { listStore } from '@/api/billiards/store';
import { listPriceRule } from '@/api/billiards/priceRule';
import QrcodeTemplate from './components/QrcodeTemplate.vue';

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
// 桌台表格数据
const tableList = ref<Array<any>>([]);
// 弹出层标题
const title = ref('');
// 是否显示弹出层
const open = ref(false);
// 是否显示批量添加弹出层
const batchOpen = ref(false);
// 是否显示二维码弹出层
const qrcodeOpen = ref(false);
// 是否显示强制结束使用弹出层
const endUseOpen = ref(false);
// 当前桌台编号
const currentTableNumber = ref('');
// 当前二维码URL
const qrcodeUrl = ref('');
// 当前选择的门店ID
const currentStoreId = computed(() => queryParams.storeId);
// 当前选择的门店信息
const currentStore = ref<any>(null);
// 当前二维码对应的桌台类型
const currentTableType = ref(1);
// 二维码模板组件引用
const qrcodeTemplateRef = ref(null);
// 门店选项
const storeOptions = ref<Array<any>>([]);
// 计费规则选项
const priceRuleOptions = ref<Array<any>>([]);
// 当前桌台ID
const currentTableId = ref('');

// 桌台状态字典
const statusOptions = ref([
  { value: 0, label: '空闲' },
  { value: 1, label: '使用中' },
  { value: 2, label: '维修中' },
  { value: 3, label: '锁定' }
]);

// 桌台类型字典
const tableTypeOptions = ref([
  { value: 1, label: '普通' },
  { value: 2, label: '专业' },
  { value: 3, label: '大师' }
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  storeId: undefined,
  tableNumber: undefined,
  status: undefined
});

// 表单参数
const form = reactive({
  id: undefined,
  storeId: '',
  tableNumber: '',
  tablePrefix: '',
  tableNumeric: 1,
  tableType: 1,
  priceRuleId: '',
  description: ''
});

// 批量添加表单
const batchForm = reactive({
  storeId: '',
  tablePrefix: '',
  startNumeric: 1,
  count: 10,
  tableType: 1,
  priceRuleId: ''
});

// 强制结束表单
const endUseForm = reactive({
  tableId: '',
  reason: ''
});

// 表单校验
const rules = reactive({
  tableNumber: [{ required: true, message: '桌台编号不能为空', trigger: 'blur' }],
  tableType: [{ required: true, message: '桌台类型不能为空', trigger: 'change' }],
  priceRuleId: [{ required: true, message: '计费规则不能为空', trigger: 'change' }]
});

// 批量表单校验
const batchRules = reactive({
  startNumeric: [{ required: true, message: '起始编号不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '创建数量不能为空', trigger: 'blur' }],
  tableType: [{ required: true, message: '桌台类型不能为空', trigger: 'change' }],
  priceRuleId: [{ required: true, message: '计费规则不能为空', trigger: 'change' }]
});

// 强制结束表单校验
const endUseRules = reactive({
  reason: [{ required: true, message: '结束原因不能为空', trigger: 'blur' }]
});

const queryRef = ref();
const tableRef = ref();
const batchFormRef = ref();
const endUseFormRef = ref();

// 批量下载进度
const downloadProgressVisible = ref(false);
const downloadProgress = ref(0);
const progressText = ref('准备下载...');

// 临时存储当前处理的桌台
const tempQrcodeTable = ref<any>(null);
const tempQrcodeTemplateRef = ref(null);

// 路由参数（用于从门店页面携带过来的 storeId）
const route = useRoute();

function progressFormat(percentage: number) {
  return percentage === 100 ? '完成' : `${percentage}%`;
}

/** 查询门店列表 */
function getStoreList() {
  listStore({ pageSize: 100 }).then((response) => {
    storeOptions.value = response.data.records;
    if (queryParams.storeId) {
      currentStore.value = storeOptions.value.find(store => store.id === queryParams.storeId);
    }
  });
}

/** 查询计费规则列表 */
function getPriceRuleList() {
  listPriceRule({ pageSize: 100, status: 0 }).then((response) => {
    priceRuleOptions.value = response.data;
  });
}

/** 根据ID获取计费规则 */
function getPriceRule(ruleId) {
  if (!ruleId || !priceRuleOptions.value || priceRuleOptions.value.length === 0) return null;
  return priceRuleOptions.value.find(rule => rule.id === ruleId);
}

/** 生成编号预览 */
function generateNumberPreview() {
  if (!batchForm.count) return '';

  const previews = [];
  const start = batchForm.startNumeric;
  const prefix = batchForm.tablePrefix || '';

  for (let i = 0; i < Math.min(5, batchForm.count); i++) {
    previews.push(`${prefix}${start + i}`);
  }

  if (batchForm.count > 5) {
    previews.push('...');
    previews.push(`${prefix}${start + batchForm.count - 1}`);
  }

  return previews.join(', ');
}

/** 查询桌台列表 */
function getList() {
  if (!queryParams.storeId) {
    tableList.value = [];
    total.value = 0;
    return;
  }

  loading.value = true;
  listTable(queryParams)
    .then((response) => {
      tableList.value = response.data.records;
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
  form.tableNumber = '';
  form.tablePrefix = '';
  form.tableNumeric = 1;
  form.tableType = 1;
  form.priceRuleId = '';
  form.description = '';
}

/** 搜索按钮操作 */
function handleQuery() {
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

/** 门店改变 */
function storeChanged() {
  if (queryParams.storeId) {
    currentStore.value = storeOptions.value.find(store => store.id === queryParams.storeId);
  } else {
    currentStore.value = null;
  }
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  form.storeId = queryParams.storeId;
  open.value = true;
  title.value = '添加桌台';
}

/** 批量新增按钮操作 */
function handleBatchAdd() {
  batchForm.storeId = queryParams.storeId;
  batchForm.tablePrefix = '';
  batchForm.startNumeric = 1;
  batchForm.count = 10;
  batchForm.tableType = 1;
  batchForm.priceRuleId = '';
  batchOpen.value = true;
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  resetForm();
  const tableId = row.id;
  getTable(tableId).then((response) => {
    Object.assign(form, response.data);
    open.value = true;
    title.value = '修改桌台';
  });
}

/** 提交按钮 */
function submitForm() {
  tableRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.id) {
        updateTable(form.id, form).then(() => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addTable(form).then(() => {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 批量添加提交 */
function submitBatchForm() {
  batchFormRef.value.validate((valid: boolean) => {
    if (valid) {
      batchAddTable(batchForm).then(() => {
        ElMessage.success('批量添加成功');
        batchOpen.value = false;
        getList();
      });
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const tableIds = row?.id ? [row.id] : ids.value;

  if (tableIds.length === 0) {
    ElMessage.warning('请至少选择一条记录');
    return;
  }

  ElMessageBox.confirm('是否确认删除所选桌台?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      // 单个删除和批量删除使用不同的API
      if (tableIds.length === 1) {
        return deleteTable(tableIds[0]);
      } else {
        return batchDeleteTable(tableIds);
      }
    })
    .then(() => {
      getList();
      ElMessage.success('删除成功');
    });
}

/** 预览二维码 */
function previewQrcode(row: any) {
  currentTableNumber.value = row.tableNumber;
  currentTableType.value = row.tableType;
  qrcodeUrl.value = row.qrcodePreviewUrl;
  qrcodeOpen.value = true;
}

/** 下载二维码 */
function downloadQrcode() {
  if (!qrcodeUrl.value || !qrcodeTemplateRef.value) return;

  html2canvas(qrcodeTemplateRef.value.qrcodeRef, {
    backgroundColor: '#fff',
    useCORS: true,
    scale: 2 // 提高导出图片质量
  }).then(canvas => {
    const link = document.createElement('a');
    link.href = canvas.toDataURL('image/png');
    link.download = `${currentTableNumber.value}-qrcode.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }).catch(err => {
    console.error('二维码导出失败', err);
    ElMessage.error('二维码导出失败');
  });
}

/** 重新生成二维码 */
function regenerateQrcode(row: any) {
  ElMessageBox.confirm('确认要重新生成二维码吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      return regenerateQrcodeApi(row.id);
    })
    .then((response) => {
      ElMessage.success('重新生成成功');
      getList(); // 刷新列表
    });
}

/** 处理桌台状态修改 */
function handleStatus(row: any, status: number) {
  const statusText = {
    0: '空闲',
    2: '维修中',
    3: '锁定'
  }[status];

  ElMessageBox.confirm(`确认将桌台"${row.tableNumber}"标记为${statusText}状态吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      return updateTableStatus(row.id, status);
    })
    .then(() => {
      getList();
      ElMessage.success(`状态修改成功`);
    });
}

/** 强制结束使用按钮 */
function handleEndUse(row: any) {
  endUseForm.tableId = row.id;
  endUseForm.reason = '';
  currentTableNumber.value = row.tableNumber;
  endUseOpen.value = true;
}

/** 确认强制结束使用 */
function confirmEndUse() {
  endUseFormRef.value.validate((valid: boolean) => {
    if (valid) {
      endTableUsage(endUseForm.tableId, endUseForm.reason).then(() => {
        ElMessage.success('已强制结束使用');
        endUseOpen.value = false;
        getList();
      });
    }
  });
}

/** 批量下载所有桌台二维码 */
async function batchDownloadQrcodes() {
  if (!currentStoreId.value) {
    ElMessage.warning('请先选择门店');
    return;
  }

  // 准备下载所有桌台数据
  downloadProgressVisible.value = true;
  downloadProgress.value = 0;
  progressText.value = '获取桌台数据...';

  try {
    // 获取门店所有桌台
    const storeParams = {
      storeId: currentStoreId.value,
      pageSize: 500 // 使用一个较大的值确保能获取所有桌台
    };

    const response = await listTable(storeParams);
    const allTables = response.data.records;

    if (!allTables || allTables.length === 0) {
      ElMessage.warning('当前门店没有桌台');
      downloadProgressVisible.value = false;
      return;
    }

    progressText.value = `找到 ${allTables.length} 个桌台，开始生成二维码...`;
    downloadProgress.value = 5;

    // 创建ZIP文件
    const zip = new JSZip();
    const qrcodeFolder = zip.folder('qrcodes');
    if (!qrcodeFolder) {
      throw new Error('无法创建ZIP文件夹');
    }

    // 逐个处理桌台二维码
    for (let i = 0; i < allTables.length; i++) {
      const table = allTables[i];
      progressText.value = `正在处理: ${table.tableNumber} (${i+1}/${allTables.length})`;
      downloadProgress.value = 5 + Math.floor((i / allTables.length) * 85);

      // 准备生成二维码
      tempQrcodeTable.value = table;
      await nextTick();

      try {
        // 创建和渲染临时二维码模板
        const tempDiv = document.createElement('div');
        tempDiv.style.position = 'absolute';
        tempDiv.style.left = '-9999px';
        document.body.appendChild(tempDiv);

        const qrcodeTemplate = createQrcodeElement(table);
        tempDiv.appendChild(qrcodeTemplate);

        await nextTick();

        // 使用html2canvas将模板转换为图片
        const canvas = await html2canvas(qrcodeTemplate, {
          backgroundColor: '#fff',
          useCORS: true,
          scale: 2
        });

        // 转换为blob并添加到ZIP
        const imgBlob = await new Promise<Blob>((resolve) => {
          canvas.toBlob((blob) => {
            if (blob) resolve(blob);
            else resolve(new Blob(['']));
          }, 'image/png');
        });

        qrcodeFolder.file(`${table.tableNumber}-qrcode.png`, imgBlob);

        // 清理临时元素
        document.body.removeChild(tempDiv);
      } catch (err) {
        console.error(`处理桌台 ${table.tableNumber} 时出错:`, err);
        progressText.value = `处理桌台 ${table.tableNumber} 时出错`;
      }
    }

    // 生成并下载ZIP文件
    progressText.value = '正在打包所有二维码...';
    downloadProgress.value = 90;

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    progressText.value = '打包完成，准备下载...';
    downloadProgress.value = 95;

    // 下载ZIP文件
    const storeName = currentStore.value?.name || '台球俱乐部';
    const timestamp = new Date().toISOString().replace(/:/g, '-').substring(0, 19);
    const link = document.createElement('a');
    link.href = URL.createObjectURL(zipBlob);
    link.download = `${storeName}-桌台二维码-${timestamp}.zip`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    progressText.value = '下载完成！';
    downloadProgress.value = 100;
  } catch (err) {
    console.error('批量下载二维码出错:', err);
    progressText.value = '下载过程中出错，请重试';
    ElMessage.error('批量下载二维码失败');
  }
}

// 创建二维码模板元素
function createQrcodeElement(table: any) {
  const div = document.createElement('div');
  div.className = 'qrcode-template';
  div.innerHTML = `
    <div class="qrcode-header">
      ${currentStore.value?.logoUrl ? `<img src="${currentStore.value.logoUrl}" class="store-logo" alt="门店logo" />` : ''}
      <div class="store-info">
        <div class="store-name">${currentStore.value?.name || '台球俱乐部'}</div>
        <div class="store-slogan">扫码使用桌台</div>
      </div>
    </div>

    <div class="qrcode-body">
      <div class="qrcode-image-container">
        <img src="${table.qrcodePreviewUrl}" alt="二维码" class="qrcode-image" />
      </div>
      <div class="table-info">
        <div class="table-number">桌台编号：${table.tableNumber}</div>
        <div class="table-type">
          桌台类型：
          <span class="type-tag ${getTypeClass(table.tableType)}">${getTableTypeName(table.tableType)}</span>
        </div>
      </div>
    </div>

    <div class="qrcode-footer">
      <div class="footer-text">欢迎使用 ${currentStore.value?.name || '台球俱乐部'} 台球室</div>
      <div class="footer-tips">扫描上方二维码即可使用桌台</div>
    </div>
  `;

  // 应用样式
  applyQrcodeTemplateStyle(div);
  return div;
}

// 获取桌台类型名称
function getTableTypeName(type: number) {
  switch (type) {
    case 1: return '普通';
    case 2: return '专业';
    case 3: return '大师';
    default: return '普通';
  }
}

// 获取桌台类型样式类
function getTypeClass(type: number) {
  switch (type) {
    case 1: return 'type-normal';
    case 2: return 'type-pro';
    case 3: return 'type-master';
    default: return 'type-normal';
  }
}

// 应用二维码模板样式
function applyQrcodeTemplateStyle(element: HTMLElement) {
  const styles = `
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
  `;

  const styleElement = document.createElement('style');
  styleElement.textContent = styles;
  element.appendChild(styleElement);
}

/** 解析阶梯规则 */
function parseLadderRules(ladderRulesStr: string) {
  if (!ladderRulesStr) return [];
  try {
    return JSON.parse(ladderRulesStr);
  } catch (e) {
    console.error('解析阶梯规则失败:', e);
    return [];
  }
}

onMounted(() => {
  const sidParam = route.query.storeId as string | string[] | undefined;
  const sid = Array.isArray(sidParam) ? sidParam[0] : sidParam;
  if (sid) {
    // 预先设置门店ID，便于首次进入时直接筛选
    (queryParams as any).storeId = sid as any;
  }
  getStoreList();
  getPriceRuleList();
  // 若带有 storeId，直接加载桌台列表
  getList();
});

// 监听路由中 storeId 的变化（例如从其他页面再次跳转）
watch(
  () => route.query.storeId,
  (newVal) => {
    const sid = Array.isArray(newVal) ? newVal[0] : (newVal as string | undefined);
    if (sid) {
      (queryParams as any).storeId = sid as any;
      storeChanged();
    }
  }
);
</script>

<style scoped>
.tool-button-group {
  display: flex;
  justify-content: flex-end;
}
.empty-tip {
  padding: 40px 0;
}

.qrcode-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  margin: 0 auto;
}

.price-tag {
  margin-top: 5px;
}
.price-rule-detail {
  margin: 5px 0;
}
.price-rule-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 5px;
}
.price-inline-tag {
  margin-left: 5px;
}

/* 阶梯规则样式 */
.ladder-rules-list {
  margin-top: 8px;
  margin-bottom: 8px;
  padding-left: 10px;
}

.ladder-rule-item {
  margin-bottom: 5px;
  padding: 3px 0;
  border-bottom: 1px dashed #eee;
  font-size: 12px;
}

.ladder-rule-item:last-child {
  border-bottom: none;
}

.download-progress-container {
  padding: 20px;
}
.progress-text {
  margin-top: 10px;
  text-align: center;
}
</style>
