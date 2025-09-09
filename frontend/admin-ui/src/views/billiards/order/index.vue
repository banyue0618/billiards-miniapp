<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="18">
            <el-form ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
              <el-form-item label="所属门店" prop="storeId">
                <el-select v-model="queryParams.storeId" placeholder="选择门店" clearable style="width: 200px">
                  <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="订单编号" prop="orderNo">
                <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="订单状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 200px">
                  <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="日期范围" prop="dateRange">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 240px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="6">
            <div class="tool-button-group">
              <el-button type="primary" icon="Download" @click="handleExport">导出订单</el-button>
              <el-button type="success" icon="View" @click="handleActiveOrderView">进行中订单看板</el-button>
            </div>
          </el-col>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="订单编号" align="center" prop="orderNo" width="180" />
        <el-table-column label="门店名称" align="center" prop="storeName" />
        <el-table-column label="桌台编号" align="center" prop="tableNumber" />
        <el-table-column label="开始时间" align="center" prop="startTime" width="160" />
        <el-table-column label="结束时间" align="center" prop="endTime" width="160">
          <template #default="scope">
            <span v-if="scope.row.endTime">{{ scope.row.endTime }}</span>
            <el-tag v-else type="warning">进行中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="用时(分钟)" align="center" prop="duration" />
        <el-table-column label="消费金额" align="center" prop="actualAmount">
          <template #default="scope">
            <span class="price-text">¥{{ parseFloat(scope.row.actualAmount).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" align="center" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">进行中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" align="center" prop="paymentStatus">
          <template #default="scope">
            <el-tag v-if="scope.row.paymentStatus === 0" type="info">未支付</el-tag>
            <el-tag v-else-if="scope.row.paymentStatus === 1" type="success">已支付</el-tag>
            <el-tag v-else-if="scope.row.paymentStatus === 2" type="danger">退款中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="text" icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button v-if="scope.row.status === 0" type="text" icon="CircleClose" @click="handleEndOrder(scope.row)">强制结束</el-button>
<!--            <el-button v-if="scope.row.status === 0" type="text" icon="Delete" @click="handleCancelOrder(scope.row)">取消订单</el-button>-->
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailOpen" title="订单详情" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ orderDetail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag v-if="orderDetail.status === 0" type="warning">进行中</el-tag>
          <el-tag v-else-if="orderDetail.status === 1" type="success">已完成</el-tag>
          <el-tag v-else-if="orderDetail.status === 2" type="danger">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="门店名称">{{ orderDetail.storeName }}</el-descriptions-item>
        <el-descriptions-item label="桌台编号">{{ orderDetail.tableNumber }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ orderDetail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">
          <span v-if="orderDetail.endTime">{{ orderDetail.endTime }}</span>
          <el-tag v-else type="warning">进行中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="使用时长">{{ orderDetail.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ orderDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="原始金额">¥{{ parseFloat(orderDetail.originalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="折扣金额">¥{{ parseFloat(orderDetail.discountAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="实际金额">
          <span class="price-text">¥{{ parseFloat(orderDetail.actualAmount || 0).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag v-if="orderDetail.paymentStatus === 0" type="info">未支付</el-tag>
          <el-tag v-else-if="orderDetail.paymentStatus === 1" type="success">已支付</el-tag>
          <el-tag v-else-if="orderDetail.paymentStatus === 2" type="danger">退款中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款状态">
          <el-tag v-if="orderDetail.refundStatus === 0" type="info">退款中</el-tag>
          <el-tag v-else-if="orderDetail.refundStatus === 1" type="success">退款成功</el-tag>
          <el-tag v-else-if="orderDetail.refundStatus === 2" type="danger">退款失败</el-tag>
          <el-tag v-else>无需退款</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ parseFloat(orderDetail.refundAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="用户信息" :span="2"> {{ orderDetail.userName }} ({{ (orderDetail.userPhone || '用户暂未绑定手机号')}}) </el-descriptions-item>
        <el-descriptions-item label="计费规则" :span="2">
          <!-- 标准计费 -->
          <div v-if="orderDetail.priceUnit && !orderDetail.ladderRules">
            <div>标准计费：<span class="price-text">¥{{ orderDetail.priceUnit }}/分钟</span></div>
            <div v-if="orderDetail.memberPrice">会员价格：<span class="price-text">¥{{ orderDetail.memberPrice }}/分钟</span></div>
          </div>
          <!-- 阶梯计费 -->
          <div v-else-if="orderDetail.ladderRules">
            <div>阶梯计费：</div>
            <div v-for="(rule, index) in parseLadderRules(orderDetail.ladderRules)" :key="index" class="ladder-rule-item">
              {{ rule.startMinute }}分钟 - {{ rule.endMinute === -1 ? '不限' : rule.endMinute + '分钟' }}：
              <span class="price-text">¥{{ rule.price }}/分钟</span>
            </div>
          </div>
          <!-- 会员折扣 -->
          <div v-if="orderDetail.memberDiscount">
            会员折扣：<span class="price-text">{{ orderDetail.memberDiscount }}折</span>
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="orderDetail.status === 0" class="order-actions">
        <el-divider content-position="center">操作</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button type="danger" plain icon="CircleClose" @click="handleEndOrder(orderDetail)">强制结束</el-button>
          </el-col>
<!--          <el-col :span="12">-->
<!--            <el-button type="warning" plain icon="Edit" @click="handleUpdateAmount(orderDetail)">修改订单金额</el-button>-->
<!--          </el-col>-->
        </el-row>
      </div>
    </el-dialog>

    <!-- 强制结束订单对话框 -->
    <el-dialog v-model="endOrderOpen" title="强制结束订单" width="500px" append-to-body>
      <el-form ref="endOrderFormRef" :model="endOrderForm" :rules="endOrderRules" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ endOrderForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="桌台编号">
          <span>{{ endOrderForm.tableNumber }}</span>
        </el-form-item>
        <el-form-item label="结束原因" prop="reason">
          <el-input v-model="endOrderForm.reason" type="textarea" placeholder="请输入结束原因" />
        </el-form-item>
        <el-alert type="warning" title="提示" description="强制结束将立即停止计费并释放桌台，用户将被通知订单已结束。请谨慎操作！" show-icon />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmEndOrder">确认结束</el-button>
          <el-button @click="endOrderOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 取消订单对话框 -->
    <el-dialog v-model="cancelOrderOpen" title="取消订单" width="500px" append-to-body>
      <el-form ref="cancelOrderFormRef" :model="cancelOrderForm" :rules="cancelOrderRules" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ cancelOrderForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="桌台编号">
          <span>{{ cancelOrderForm.tableNumber }}</span>
        </el-form-item>
        <el-form-item label="取消原因" prop="reason">
          <el-input v-model="cancelOrderForm.reason" type="textarea" placeholder="请输入取消原因" />
        </el-form-item>
        <el-alert
          type="warning"
          title="提示"
          description="取消订单将不会产生任何费用且立即释放桌台。此操作通常用于订单创建错误的情况。请谨慎操作！"
          show-icon
        />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmCancelOrder">确认取消</el-button>
          <el-button @click="cancelOrderOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改订单金额对话框 -->
    <el-dialog v-model="updateAmountOpen" title="修改订单金额" width="500px" append-to-body>
      <el-form ref="updateAmountFormRef" :model="updateAmountForm" :rules="updateAmountRules" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ updateAmountForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="当前金额">
          <span class="price-text">¥{{ parseFloat(updateAmountForm.actualAmount || 0).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="新金额" prop="amount">
          <el-input-number v-model="updateAmountForm.amount" :precision="2" :step="1" :min="0" controls-position="right" style="width: 200px" />
        </el-form-item>
        <el-form-item label="修改原因" prop="reason">
          <el-input v-model="updateAmountForm.reason" type="textarea" placeholder="请输入修改原因" />
        </el-form-item>
        <el-alert type="warning" title="提示" description="修改订单金额将被记录在操作日志中，请确保有合理理由。" show-icon />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmUpdateAmount">确认修改</el-button>
          <el-button @click="updateAmountOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 进行中订单看板对话框 -->
    <el-dialog v-model="activeOrderOpen" title="进行中订单看板" width="900px" append-to-body>
      <el-form :inline="true">
        <el-form-item label="选择门店">
          <el-select v-model="activeOrderStoreId" placeholder="全部门店" clearable @change="listOngoingOrders">
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Refresh" @click="listOngoingOrders">刷新</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="20" class="active-order-summary">
        <el-col :span="8">
          <div class="summary-card">
            <div class="card-title">当前使用桌台</div>
            <div class="card-value">{{ activeOrders.length }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="summary-card">
            <div class="card-title">预计营收</div>
            <div class="card-value">¥{{ calculateTotalAmount() }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="summary-card">
            <div class="card-title">平均使用时长</div>
            <div class="card-value">{{ calculateAverageDuration() }}分钟</div>
          </div>
        </el-col>
      </el-row>

      <el-table :data="activeOrders" style="margin-top: 20px">
        <el-table-column label="门店名称" align="center" prop="storeName" />
        <el-table-column label="桌台编号" align="center" prop="tableNumber" />
        <el-table-column label="开始时间" align="center" prop="startTime" width="160" />
        <el-table-column label="已用时长" align="center" prop="duration">
          <template #default="scope"> {{ scope.row.duration }} 分钟 </template>
        </el-table-column>
        <el-table-column label="当前费用" align="center" prop="actualAmount">
          <template #default="scope">
            <span class="price-text">¥{{ parseFloat(scope.row.actualAmount).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button type="text" icon="CircleClose" @click="handleEndOrder(scope.row)">结束使用</el-button>
            <el-button type="text" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  listOrder,
  getOrder,
  endOrder,
  changeOrderAmount,
  cancelOrder,
  listOngoingOrders as listOngoingOrdersApi,
  exportOrders as exportOrdersApi
} from '@/api/billiards/order';
import { listStore } from '@/api/billiards/store';

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
// 订单表格数据
const orderList = ref<Array<any>>([]);
// 是否显示订单详情对话框
const detailOpen = ref(false);
// 是否显示强制结束对话框
const endOrderOpen = ref(false);
// 是否显示取消订单对话框
const cancelOrderOpen = ref(false);
// 是否显示修改金额对话框
const updateAmountOpen = ref(false);
// 是否显示进行中订单看板
const activeOrderOpen = ref(false);
// 门店选项
const storeOptions = ref<Array<any>>([]);
// 日期范围
const dateRange = ref<string[]>([]);
// 订单详情
const orderDetail = ref<any>({});
// 进行中订单列表
const activeOrders = ref<Array<any>>([]);
// 进行中订单门店筛选
const activeOrderStoreId = ref('');

// 订单状态字典
const statusOptions = ref([
  { value: 0, label: '进行中' },
  { value: 1, label: '已完成' },
  { value: 2, label: '已取消' }
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  storeId: undefined,
  orderNo: undefined,
  status: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 结束订单表单
const endOrderForm = reactive({
  orderId: '',
  orderNo: '',
  tableNumber: '',
  reason: ''
});

// 取消订单表单
const cancelOrderForm = reactive({
  orderId: '',
  orderNo: '',
  tableNumber: '',
  reason: ''
});

// 修改金额表单
const updateAmountForm = reactive({
  orderId: '',
  orderNo: '',
  actualAmount: 0,
  amount: 0,
  reason: ''
});

// 表单校验
const endOrderRules = reactive({
  reason: [{ required: true, message: '结束原因不能为空', trigger: 'blur' }]
});

const cancelOrderRules = reactive({
  reason: [{ required: true, message: '取消原因不能为空', trigger: 'blur' }]
});

const updateAmountRules = reactive({
  amount: [{ required: true, message: '新金额不能为空', trigger: 'blur' }],
  reason: [{ required: true, message: '修改原因不能为空', trigger: 'blur' }]
});

const queryRef = ref();
const endOrderFormRef = ref();
const cancelOrderFormRef = ref();
const updateAmountFormRef = ref();

/** 查询门店列表 */
function getStoreList() {
  listStore({ pageSize: 100 }).then((response) => {
    storeOptions.value = response.data.records;
  });
}

/** 查询订单列表 */
function getList() {
  loading.value = true;

  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.beginTime = dateRange.value[0];
    queryParams.endTime = dateRange.value[1];
  } else {
    queryParams.beginTime = undefined;
    queryParams.endTime = undefined;
  }

  listOrder(queryParams)
    .then((response) => {
      orderList.value = response.data.records;
      total.value = response.data.total;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 查看订单详情 */
function handleDetail(row: any) {
  const orderId = row.id;
  getOrder(orderId).then((response) => {
    orderDetail.value = response.data;
    detailOpen.value = true;
  });
}

/** 强制结束订单按钮操作 */
function handleEndOrder(row: any) {
  endOrderForm.orderId = row.id;
  endOrderForm.orderNo = row.orderNo;
  endOrderForm.tableNumber = row.tableNumber;
  endOrderForm.reason = '';
  endOrderOpen.value = true;
  detailOpen.value = false; // 如果是从详情页打开，关闭详情页
}

/** 取消订单按钮操作 */
function handleCancelOrder(row: any) {
  cancelOrderForm.orderId = row.id;
  cancelOrderForm.orderNo = row.orderNo;
  cancelOrderForm.tableNumber = row.tableNumber;
  cancelOrderForm.reason = '';
  cancelOrderOpen.value = true;
  detailOpen.value = false; // 如果是从详情页打开，关闭详情页
}

/** 修改订单金额按钮操作 */
function handleUpdateAmount(row: any) {
  updateAmountForm.orderId = row.id;
  updateAmountForm.orderNo = row.orderNo;
  updateAmountForm.actualAmount = row.actualAmount;
  updateAmountForm.amount = row.actualAmount;
  updateAmountForm.reason = '';
  updateAmountOpen.value = true;
  detailOpen.value = false; // 如果是从详情页打开，关闭详情页
}

/** 确认强制结束订单 */
function confirmEndOrder() {
  endOrderFormRef.value.validate((valid: boolean) => {
    if (valid) {
      endOrder(endOrderForm.orderId, endOrderForm.reason).then(() => {
        ElMessage.success('订单已强制结束');
        endOrderOpen.value = false;
        // 刷新数据
        getList();
        if (activeOrderOpen.value) {
          listOngoingOrders();
        }
      });
    }
  });
}

/** 确认取消订单 */
function confirmCancelOrder() {
  cancelOrderFormRef.value.validate((valid: boolean) => {
    if (valid) {
      cancelOrder(cancelOrderForm.orderId, cancelOrderForm.reason).then(() => {
        ElMessage.success('订单已取消');
        cancelOrderOpen.value = false;
        // 刷新数据
        getList();
        if (activeOrderOpen.value) {
          listOngoingOrders();
        }
      });
    }
  });
}

/** 确认修改金额 */
function confirmUpdateAmount() {
  updateAmountFormRef.value.validate((valid: boolean) => {
    if (valid) {
      changeOrderAmount(updateAmountForm.orderId, updateAmountForm.amount, updateAmountForm.reason).then(() => {
        ElMessage.success('订单金额已修改');
        updateAmountOpen.value = false;
        // 刷新数据
        getList();
      });
    }
  });
}

/** 导出订单 */
function handleExport() {
  // 处理日期范围
  const exportParams = { ...queryParams };
  if (dateRange.value && dateRange.value.length === 2) {
    exportParams.beginTime = dateRange.value[0];
    exportParams.endTime = dateRange.value[1];
  }

  ElMessageBox.confirm('是否确认导出所有订单数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportOrdersApi(exportParams).then((response) => {
      const blob = new Blob([response], { type: 'application/vnd.ms-excel' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = '订单数据.xlsx';
      link.click();
      URL.revokeObjectURL(link.href);
    });
  });
}

/** 查看进行中订单看板 */
function handleActiveOrderView() {
  activeOrderOpen.value = true;
  activeOrderStoreId.value = '';
  listOngoingOrders();
}

/** 获取进行中订单 */
function listOngoingOrders() {
  listOngoingOrdersApi(activeOrderStoreId.value).then((response) => {
    activeOrders.value = response.data;
  });
}

/** 计算进行中订单总金额 */
function calculateTotalAmount() {
  if (activeOrders.value.length === 0) return '0.00';
  const total = activeOrders.value.reduce((sum, order) => sum + parseFloat(String(order.actualAmount || 0)), 0);
  return total.toFixed(2);
}

/** 计算平均使用时长 */
function calculateAverageDuration() {
  if (activeOrders.value.length === 0) return '0';
  const totalDuration = activeOrders.value.reduce((sum, order) => sum + parseInt(String(order.duration || 0)), 0);
  return Math.floor(totalDuration / activeOrders.value.length);
}

/** 解析阶梯计费规则 */
function parseLadderRules(ladderRulesStr: string) {
  try {
    if (!ladderRulesStr) return [];
    const rules = JSON.parse(ladderRulesStr);
    return Array.isArray(rules) ? rules : [];
  } catch (e) {
    console.error('解析阶梯计费规则失败:', e);
    return [];
  }
}

onMounted(() => {
  getStoreList();
  getList();
});
</script>

<style scoped>
.tool-button-group {
  display: flex;
  justify-content: flex-end;
}
.price-text {
  color: #f56c6c;
  font-weight: bold;
}
.order-actions {
  margin-top: 20px;
}
.active-order-summary {
  margin-top: 20px;
}
.summary-card {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}
.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
.ladder-rule-item {
  margin-left: 15px;
  padding: 5px 0;
}
</style>
