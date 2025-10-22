<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="业务场景" prop="scene">
              <el-select v-model="queryParams.scene" placeholder="请选择触发场景" clearable>
                <el-option v-for="dict in trigger_scene" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备编号" prop="deviceCode">
              <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="控制命令" prop="command">
              <el-select v-model="queryParams.command" clearable>
                <el-option v-for="dict in device_control_command" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="是否启用" prop="enabled">
              <el-select v-model="queryParams.enabled" clearable>
                <el-option v-for="dict in enable_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button v-hasPermi="['billiards:iotDeviceBinding:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDeviceBinding:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['billiards:iotDeviceBinding:remove']"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:iotDeviceBinding:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="iotDeviceBindingList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="true" label="主键" align="center" prop="id" />
        <el-table-column label="业务场景" align="center" prop="scene">
          <template #default="scope">
            <dict-tag :options="trigger_scene" :value="scope.row.scene" />
          </template>
        </el-table-column>
        <el-table-column label="设备编号" align="center" prop="deviceCode" />
        <el-table-column label="控制命令" align="center" prop="command">
          <template #default="scope">
            <dict-tag :options="device_control_command" :value="scope.row.command" />
          </template>
        </el-table-column>
        <el-table-column label="命令参数" align="center" prop="params" />
        <el-table-column label="是否启用" align="center" prop="enabled">
          <template #default="scope">
            <dict-tag :options="enable_status" :value="scope.row.enabled" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button
                v-hasPermi="['billiards:iotDeviceBinding:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                v-hasPermi="['billiards:iotDeviceBinding:remove']"
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
    <!-- 添加或修改设备业务绑定（定义场景与设备动作映射）对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="520px" append-to-body>
      <el-form ref="iotDeviceBindingFormRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="选择门店" prop="storeId">
          <el-select v-model="form.storeId" placeholder="请选择门店" filterable clearable @change="onStoreChange">
            <el-option v-for="opt in storeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择桌台" prop="tableId">
          <el-select v-model="form.tableId" :disabled="!form.storeId" placeholder="请选择桌台" filterable clearable>
            <el-option v-for="opt in tableOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务场景" prop="scene">
          <el-select v-model="form.scene" placeholder="请选择触发场景" clearable>
            <el-option v-for="dict in trigger_scene" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="控制命令" prop="command">
          <el-select v-model="form.command" clearable>
            <el-option v-for="dict in device_control_command" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行顺序" prop="executeOrder">
          <el-input v-model="form.executeOrder" placeholder="请输入执行顺序，1、2、3..." />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-select v-model="form.enabled" clearable>
            <el-option v-for="dict in enable_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
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

<script setup name="IotDeviceBinding" lang="ts">
import {
  listIotDeviceBinding,
  getIotDeviceBinding,
  delIotDeviceBinding,
  addIotDeviceBinding,
  updateIotDeviceBinding
} from '@/api/billiards/iotDeviceBinding';
import { IotDeviceBindingVO, IotDeviceBindingQuery, IotDeviceBindingForm } from '@/api/billiards/iotDeviceBinding/types';
import { listStore } from '@/api/billiards/store';
import { listTable } from '@/api/billiards/table';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const { trigger_scene, device_control_command, enable_status } = toRefs<any>(
  proxy?.useDict('trigger_scene', 'device_control_command', 'enable_status')
);

const iotDeviceBindingList = ref<IotDeviceBindingVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const iotDeviceBindingFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: IotDeviceBindingForm = {
  id: undefined,
  tableId: undefined,
  storeId: undefined,
  scene: undefined,
  deviceCode: undefined,
  command: undefined,
  params: undefined,
  executeOrder: undefined,
  enabled: undefined
};
const data = reactive<PageData<IotDeviceBindingForm, IotDeviceBindingQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    scene: undefined,
    deviceCode: undefined,
    command: undefined,
    enabled: undefined,
    params: {}
  },
  rules: {
    storeId: [{ required: true, message: '请选择门店', trigger: 'change' }],
    tableId: [{ required: true, message: '请选择桌台', trigger: 'change' }],
    scene: [{ required: true, message: '业务场景：open_table/close_table/timeout等不能为空', trigger: 'blur' }],
    deviceCode: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
    command: [{ required: true, message: '控制命令：turn_on/turn_off/play_audio等不能为空', trigger: 'blur' }],
    executeOrder: [{ required: true, message: '执行顺序不能为空，当同一桌台绑定了多台设备，需要确认每台设备的执行顺序，1，2，3...', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备业务绑定（定义场景与设备动作映射）列表 */
const getList = async () => {
  loading.value = true;
  const res = await listIotDeviceBinding(queryParams.value);
  iotDeviceBindingList.value = res.rows;
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
  iotDeviceBindingFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: IotDeviceBindingVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加设备业务绑定（定义场景与设备动作映射）';
  loadStores();
};

/** 修改按钮操作 */
const handleUpdate = async (row?: IotDeviceBindingVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getIotDeviceBinding(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改设备业务绑定（定义场景与设备动作映射）';
  await loadStores();
  if (form.value.storeId) {
    await loadTables(form.value.storeId as any);
  }
};

/** 提交按钮 */
const submitForm = () => {
  iotDeviceBindingFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateIotDeviceBinding(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addIotDeviceBinding(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: IotDeviceBindingVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal
    .confirm('是否确认删除设备业务绑定（定义场景与设备动作映射）编号为"' + _ids + '"的数据项？')
    .finally(() => (loading.value = false));
  await delIotDeviceBinding(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/iotDeviceBinding/export',
    {
      ...queryParams.value
    },
    `iotDeviceBinding_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});

// 门店/桌台联动与数据加载
const storeOptions = ref<Array<{ label: string; value: string | number }>>([]);
const tableOptions = ref<Array<{ label: string; value: string | number }>>([]);

const onStoreChange = async (val: string | number) => {
  form.value.tableId = undefined;
  tableOptions.value = [];
  if (val) {
    await loadTables(val);
  }
};

const loadStores = async () => {
  const res: any = await listStore({ pageNum: 1, pageSize: 999 });
  console.log(res.code);
  const rows = res?.data?.rows || res?.data?.records || [];
  storeOptions.value = rows.map((s: any) => ({
    label: s.name,
    value: s.id
  }));
};

const loadTables = async (storeId: string | number) => {
  const res: any = await listTable({ storeId, pageNum: 1, pageSize: 999 });
  const rows = res?.data?.rows || res?.data?.records || [];
  tableOptions.value = rows.map((t: any) => ({
    label: t.tableNumber,
    value: t.id
  }));
};
</script>
