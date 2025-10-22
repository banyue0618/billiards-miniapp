<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="告警类型" prop="alertType">
              <el-select v-model="queryParams.alertType" placeholder="请选择告警类型" clearable>
                <el-option v-for="dict in alert_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="告警级别" prop="alertLevel">
              <el-select v-model="queryParams.alertLevel" placeholder="请选择告警级别" clearable>
                <el-option v-for="dict in alert_level" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备编号" prop="deviceCode">
              <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="处理状态" prop="status">
              <el-select v-model="queryParams.status" clearable>
                <el-option v-for="dict in alert_process_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button v-hasPermi="['billiards:iotDeviceAlert:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDeviceAlert:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDeviceAlert:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDeviceAlert:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="iotDeviceAlertList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="主键" align="center" prop="id" />
        <el-table-column label="告警类型" align="center" prop="alertType">
          <template #default="scope">
            <dict-tag :options="alert_type" :value="scope.row.alertType" />
          </template>
        </el-table-column>
        <el-table-column label="告警级别" align="center" prop="alertLevel">
          <template #default="scope">
            <dict-tag :options="alert_level" :value="scope.row.alertLevel" />
          </template>
        </el-table-column>
        <el-table-column label="设备编号" align="center" prop="deviceCode" />
        <el-table-column label="设备名称" align="center" prop="deviceName" />
        <el-table-column label="处理状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="alert_process_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="处理人" align="center" prop="handler" />
        <el-table-column label="处理时间" align="center" prop="handleTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.handleTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['billiards:iotDeviceAlert:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                v-hasPermi="['billiards:iotDeviceAlert:remove']"
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
    <!-- 添加或修改设备告警（记录设备异常信息）对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="iotDeviceAlertFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="告警类型" prop="alertType">
          <el-select v-model="form.alertType">
            <el-option v-for="dict in alert_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="告警级别" prop="alertLevel">
          <el-select v-model="form.alertType">
            <el-option v-for="dict in alert_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="告警内容描述">
          <editor v-model="form.alertContent" :min-height="192" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in alert_process_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理人" prop="handler">
          <el-input v-model="form.handler" placeholder="请输入处理人" />
        </el-form-item>
        <el-form-item label="处理时间" prop="handleTime">
          <el-date-picker v-model="form.handleTime" clearable type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择处理时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="处理备注" prop="handleRemark">
          <el-input v-model="form.handleRemark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="IotDeviceAlert" lang="ts">
import { listIotDeviceAlert, getIotDeviceAlert, delIotDeviceAlert, addIotDeviceAlert, updateIotDeviceAlert } from '@/api/billiards/iotDeviceAlert';
import { IotDeviceAlertVO, IotDeviceAlertQuery, IotDeviceAlertForm } from '@/api/billiards/iotDeviceAlert/types';
import { parseTime } from '../../../utils/ruoyi';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { alert_process_status, alert_type, alert_level } = toRefs<any>(proxy?.useDict('alert_process_status', 'alert_type', 'alert_level'));

const iotDeviceAlertList = ref<IotDeviceAlertVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const iotDeviceAlertFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: IotDeviceAlertForm = {
  id: undefined,
  alertType: undefined,
  alertLevel: undefined,
  deviceCode: undefined,
  deviceName: undefined,
  alertContent: undefined,
  alertData: undefined,
  status: undefined,
  handler: undefined,
  handleTime: undefined,
  handleRemark: undefined
};
const data = reactive<PageData<IotDeviceAlertForm, IotDeviceAlertQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    alertType: undefined,
    alertLevel: undefined,
    deviceCode: undefined,
    deviceName: undefined,
    status: undefined,
    params: {}
  },
  rules: {}
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备告警（记录设备异常信息）列表 */
const getList = async () => {
  loading.value = true;
  const res = await listIotDeviceAlert(queryParams.value);
  iotDeviceAlertList.value = res.rows;
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
  iotDeviceAlertFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: IotDeviceAlertVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加设备告警（记录设备异常信息）';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: IotDeviceAlertVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getIotDeviceAlert(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改设备告警（记录设备异常信息）';
};

/** 提交按钮 */
const submitForm = () => {
  iotDeviceAlertFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateIotDeviceAlert(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addIotDeviceAlert(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: IotDeviceAlertVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备告警（记录设备异常信息）编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delIotDeviceAlert(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/iotDeviceAlert/export',
    {
      ...queryParams.value
    },
    `iotDeviceAlert_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
