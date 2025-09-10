<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="交易金额" prop="amount">
              <el-input v-model="queryParams.amount" placeholder="请输入交易金额" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="交易类型" prop="transType">
              <el-select v-model="queryParams.transType" placeholder="请选择规则类型" clearable>
                <el-option v-for="dict in transaction_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button v-hasPermi="['billiards:walletTransaction:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:walletTransaction:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['billiards:walletTransaction:remove']"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:walletTransaction:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="walletTransactionList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="记录ID" align="center" prop="id" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="交易类型" align="center" prop="transType">
          <template #default="scope">
            <dict-tag :options="transaction_type" :value="scope.row.transType" />
          </template>
        </el-table-column>
        <el-table-column label="交易金额" align="center" prop="amount" />
        <el-table-column label="微信交易id" align="center" prop="transactionId" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button
                v-hasPermi="['billiards:walletTransaction:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                v-hasPermi="['billiards:walletTransaction:remove']"
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
    <!-- 添加或修改用户钱包流水对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="walletTransactionFormRef" :model="form" :rules="rules" label-width="80px"> </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="WalletTransaction" lang="ts">
import {
  listWalletTransaction,
  getWalletTransaction,
  delWalletTransaction,
  addWalletTransaction,
  updateWalletTransaction
} from '@/api/billiards/walletTransaction';
import { WalletTransactionVO, WalletTransactionQuery, WalletTransactionForm } from '@/api/billiards/walletTransaction/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { transaction_type } = toRefs<any>(proxy?.useDict('transaction_type'));

const walletTransactionList = ref<WalletTransactionVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const walletTransactionFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: WalletTransactionForm = {};
const data = reactive<PageData<WalletTransactionForm, WalletTransactionQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    transType: undefined,
    amount: undefined,
    params: {}
  },
  rules: {}
});

const { queryParams, form, rules } = toRefs(data);

/** 查询用户钱包流水列表 */
const getList = async () => {
  loading.value = true;
  const res = await listWalletTransaction(queryParams.value);
  walletTransactionList.value = res.rows;
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
  walletTransactionFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: WalletTransactionVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加用户钱包流水';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: WalletTransactionVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getWalletTransaction(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改用户钱包流水';
};

/** 提交按钮 */
const submitForm = () => {
  walletTransactionFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateWalletTransaction(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addWalletTransaction(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: WalletTransactionVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除用户钱包流水编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delWalletTransaction(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/walletTransaction/export',
    {
      ...queryParams.value
    },
    `walletTransaction_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
