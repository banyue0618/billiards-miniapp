<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="累计充值" prop="totalRecharge">
              <el-input v-model="queryParams.totalRecharge" placeholder="请输入累计充值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="累计退款" prop="totalRefund">
              <el-input v-model="queryParams.totalRefund" placeholder="请输入累计退款" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:walletAccount:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:walletAccount:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:walletAccount:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:walletAccount:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="walletAccountList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="记录ID" align="center" prop="id" v-if="false" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="当前余额" align="center" prop="balance" />
        <el-table-column label="冻结金额" align="center" prop="freezeAmount" />
        <el-table-column label="累计充值" align="center" prop="totalRecharge" />
        <el-table-column label="累计退款" align="center" prop="totalRefund" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:walletAccount:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:walletAccount:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改用户钱包账户对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="walletAccountFormRef" :model="form" :rules="rules" label-width="80px">
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

<script setup name="WalletAccount" lang="ts">
import { listWalletAccount, getWalletAccount, delWalletAccount, addWalletAccount, updateWalletAccount } from '@/api/billiards/walletAccount';
import { WalletAccountVO, WalletAccountQuery, WalletAccountForm } from '@/api/billiards/walletAccount/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const walletAccountList = ref<WalletAccountVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const walletAccountFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: WalletAccountForm = {
}
const data = reactive<PageData<WalletAccountForm, WalletAccountQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    totalRecharge: undefined,
    totalRefund: undefined,
    params: {
    }
  },
  rules: {
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询用户钱包账户列表 */
const getList = async () => {
  loading.value = true;
  const res = await listWalletAccount(queryParams.value);
  walletAccountList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  walletAccountFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: WalletAccountVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加用户钱包账户";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: WalletAccountVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getWalletAccount(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改用户钱包账户";
}

/** 提交按钮 */
const submitForm = () => {
  walletAccountFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateWalletAccount(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addWalletAccount(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: WalletAccountVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除用户钱包账户编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delWalletAccount(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/walletAccount/export', {
    ...queryParams.value
  }, `walletAccount_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
