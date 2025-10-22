<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="设备编号" prop="deviceCode">
              <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="触发场景" prop="triggerScene">
              <el-select v-model="queryParams.triggerScene" placeholder="请选择触发场景" clearable>
                <el-option v-for="dict in trigger_scene" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="执行状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择执行状态" clearable>
                <el-option v-for="dict in device_control_command_execute_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotControlLog:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotControlLog:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotControlLog:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotControlLog:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="iotControlLogList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="主键" align="center" prop="id" />
        <el-table-column label="设备编号" align="center" prop="deviceCode" />
        <el-table-column label="控制命令" align="center" prop="command">
          <template #default="scope">
            <dict-tag :options="device_control_command" :value="scope.row.command" />
          </template>
        </el-table-column>
        <el-table-column label="触发场景" align="center" prop="triggerScene">
          <template #default="scope">
            <dict-tag :options="trigger_scene" :value="scope.row.triggerScene" />
          </template>
        </el-table-column>
        <el-table-column label="执行状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="device_control_command_execute_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="执行时间" align="center" prop="executeTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.executeTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="响应耗时" align="center" prop="responseTime" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['billiards:iotControlLog:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                v-hasPermi="['billiards:iotControlLog:remove']"
                link
                type="primary"
                icon="Delete"
                @click="handleDelete(scope.row)"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 添加或修改设备控制日志（记录执行命令历史）对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="iotControlLogFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编号" readonly />
        </el-form-item>
        <el-form-item label="控制命令" prop="command">
          <el-select v-model="form.command">
            <el-option v-for="dict in device_control_command" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发来源" prop="triggerBy">
          <el-input v-model="form.triggerBy" readonly />
        </el-form-item>
        <el-form-item label="触发场景" prop="triggerScene">
          <el-select v-model="form.triggerScene">
            <el-option v-for="dict in trigger_scene" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联订单ID" prop="orderId">
          <el-input v-model="form.orderId" readonly />
        </el-form-item>
        <el-form-item label="关联台桌ID" prop="tableId">
          <el-input v-model="form.tableId" readonly />
        </el-form-item>
        <el-form-item label="执行状态" prop="status">
          <el-select v-model="form.status">
            <el-option v-for="dict in device_control_command_execute_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="失败原因" prop="errorMsg">
          <el-input v-model="form.errorMsg" type="textarea" readonly />
        </el-form-item>
        <el-form-item label="重试次数" prop="retryCount">
          <el-input v-model="form.retryCount" readonly />
        </el-form-item>
        <el-form-item label="执行时间" prop="executeTime">
          <el-date-picker v-model="form.executeTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" readonly>
          </el-date-picker>
        </el-form-item>
        <el-form-item label="响应耗时" prop="responseTime">
          <el-input v-model="form.responseTime" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="IotControlLog" lang="ts">
import { listIotControlLog, getIotControlLog, delIotControlLog, addIotControlLog, updateIotControlLog } from '@/api/billiards/iotControlLog';
import { IotControlLogVO, IotControlLogQuery, IotControlLogForm } from '@/api/billiards/iotControlLog/types';
import { parseTime } from '../../../utils/ruoyi';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { device_control_command_execute_status, trigger_scene, device_control_command } = toRefs<any>(
  proxy?.useDict('device_control_command_execute_status', 'trigger_scene', 'device_control_command')
);

const iotControlLogList = ref<IotControlLogVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const iotControlLogFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: IotControlLogForm = {
  id: undefined,
  deviceCode: undefined,
  command: undefined,
  params: undefined,
  triggerBy: undefined,
  triggerScene: undefined,
  orderId: undefined,
  tableId: undefined,
  status: undefined,
  errorMsg: undefined,
  retryCount: undefined,
  executeTime: undefined,
  responseTime: undefined
};
const data = reactive<PageData<IotControlLogForm, IotControlLogQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceCode: undefined,
    triggerScene: undefined,
    status: undefined,
    params: {}
  },
  rules: {
    deviceCode: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
    command: [{ required: true, message: '控制命令不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备控制日志（记录执行命令历史）列表 */
const getList = async () => {
  loading.value = true;
  const res = await listIotControlLog(queryParams.value);
  iotControlLogList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  iotControlLogFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: IotControlLogVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加设备控制日志（记录执行命令历史）';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: IotControlLogVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getIotControlLog(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改设备控制日志（记录执行命令历史）';
};

/** 提交按钮 */
const submitForm = () => {
  iotControlLogFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateIotControlLog(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addIotControlLog(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: IotControlLogVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备控制日志（记录执行命令历史）编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delIotControlLog(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/iotControlLog/export',
    {
      ...queryParams.value
    },
    `iotControlLog_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
