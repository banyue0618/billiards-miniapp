<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="设备编号" prop="code">
              <el-input v-model="queryParams.code" placeholder="请输入设备唯一编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入设备名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备类型" prop="type">
              <el-select v-model="queryParams.type" placeholder="选择设备类型" clearable>
                <el-option v-for="dict in device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="协议类型" prop="protocol">
              <el-select v-model="queryParams.protocol" placeholder="选择协议类型" clearable>
                <el-option v-for="dict in device_protocol_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="选择设备状态" clearable>
                <el-option v-for="dict in device_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <!--            <el-form-item label="最后心跳时间" prop="lastHeartbeat">-->
            <!--              <el-date-picker v-model="queryParams.lastHeartbeat" clearable type="date" value-format="YYYY-MM-DD" placeholder="请选择最后心跳时间" />-->
            <!--            </el-form-item>-->
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
            <el-button v-hasPermi="['billiards:iotDevice:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDevice:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDevice:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDevice:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="iotDeviceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="主键" align="center" prop="id" />
        <el-table-column label="设备唯一编号" align="center" prop="code" />
        <el-table-column label="设备名称" align="center" prop="name" />
        <el-table-column label="设备类型" align="center" prop="type">
          <template #default="scope">
            <dict-tag :options="device_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="协议类型" align="center" prop="protocol">
          <template #default="scope">
            <dict-tag :options="device_protocol_type" :value="scope.row.protocol" />
          </template>
        </el-table-column>
        <el-table-column label="协议配置" align="center" prop="protocolConfig" />
        <el-table-column label="设备状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="device_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="最后心跳时间" align="center" prop="lastHeartbeat" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注信息" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['billiards:iotDevice:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['billiards:iotDevice:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 添加或修改IoT设备对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="iotDeviceFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入设备唯一编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型：light/lock/speaker/other">
            <el-option v-for="dict in device_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="协议类型" prop="protocol">
          <el-select v-model="form.protocol" placeholder="请选择协议类型：mqtt/http/modbus">
            <el-option v-for="dict in device_protocol_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in device_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最后心跳时间" prop="lastHeartbeat" label-width="100px">
          <el-input v-model="form.lastHeartbeat" readonly />
        </el-form-item>
        <el-form-item label="备注信息" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

<script setup name="IotDevice" lang="ts">
import { listIotDevice, getIotDevice, delIotDevice, addIotDevice, updateIotDevice } from '@/api/billiards/iotDevice';
import { IotDeviceVO, IotDeviceQuery, IotDeviceForm } from '@/api/billiards/iotDevice/types';
import { parseTime } from '../../../utils/ruoyi';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { device_status, device_type, device_protocol_type } = toRefs<any>(proxy?.useDict('device_status', 'device_type', 'device_protocol_type'));

const iotDeviceList = ref<IotDeviceVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const iotDeviceFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: IotDeviceForm = {
  id: undefined,
  code: undefined,
  name: undefined,
  type: undefined,
  protocol: undefined,
  protocolConfig: undefined,
  status: undefined,
  lastHeartbeat: undefined,
  remark: undefined
};
const data = reactive<PageData<IotDeviceForm, IotDeviceQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    code: undefined,
    name: undefined,
    type: undefined,
    protocol: undefined,
    protocolConfig: undefined,
    status: undefined,
    lastHeartbeat: undefined,
    params: {}
  },
  rules: {
    code: [{ required: true, message: '设备唯一编号不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }],
    type: [{ required: true, message: '设备类型：light/lock/speaker/other不能为空', trigger: 'change' }],
    protocol: [{ required: true, message: '协议类型：mqtt/http/modbus不能为空', trigger: 'change' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询IoT设备列表 */
const getList = async () => {
  loading.value = true;
  const res = await listIotDevice(queryParams.value);
  iotDeviceList.value = res.rows;
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
  iotDeviceFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: IotDeviceVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加IoT设备';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: IotDeviceVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getIotDevice(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改IoT设备';
};

/** 提交按钮 */
const submitForm = () => {
  iotDeviceFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateIotDevice(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addIotDevice(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: IotDeviceVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除IoT设备编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delIotDevice(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/iotDevice/export',
    {
      ...queryParams.value
    },
    `iotDevice_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
