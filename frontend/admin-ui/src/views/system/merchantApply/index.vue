<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="申请单号" prop="applyNo">
              <el-input v-model="queryParams.applyNo" placeholder="请输入申请单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="商户名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入商户名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="联系人姓名" prop="contactName">
              <el-input v-model="queryParams.contactName" placeholder="请输入联系人姓名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="联系人手机号" prop="contactPhone">
              <el-input v-model="queryParams.contactPhone" placeholder="请输入联系人手机号" clearable @keyup.enter="handleQuery" />
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
            <el-button v-hasPermi="['system:merchantApply:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:merchantApply:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:merchantApply:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:merchantApply:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="merchantApplyList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="申请单号" align="center" prop="applyNo" />
        <el-table-column label="商户名称" align="center" prop="name" />
        <el-table-column label="联系人姓名" align="center" prop="contactName" />
        <el-table-column label="联系人手机号" align="center" prop="contactPhone" />
        <el-table-column label="联系人邮箱" align="center" prop="contactEmail" />
        <el-table-column label="Logo 资源ID/URL" align="center" prop="logo" />
        <el-table-column label="营业执照附件(资源ID/URL)" align="center" prop="businessLicense" />
        <el-table-column label="备注/补充说明" align="center" prop="remark" />
        <el-table-column label="状态:0待审 1通过 2驳回" align="center" prop="status" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:merchantApply:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:merchantApply:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="审核" placement="top">
              <el-button
                v-hasPermi="['system:merchantApply:audit']"
                link
                type="primary"
                icon="Refrigerator"
                @click="handleApprove(scope.row)"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 添加或修改商户注册申请对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="merchantApplyFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="微信商户号(选填或后置提交)" prop="wxMchId">
          <el-input v-model="form.wxMchId" placeholder="请输入微信商户号(选填或后置提交)" />
        </el-form-item>
        <el-form-item label="联系人姓名" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系人手机号" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系人手机号" />
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="请输入联系人邮箱" />
        </el-form-item>
        <el-form-item label="" prop="province">
          <el-input v-model="form.province" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="city">
          <el-input v-model="form.city" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="district">
          <el-input v-model="form.district" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="address">
          <el-input v-model="form.address" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="Logo 资源ID/URL" prop="logo">
          <el-input v-model="form.logo" placeholder="请输入Logo 资源ID/URL" />
        </el-form-item>
        <el-form-item label="营业执照附件(资源ID/URL)" prop="businessLicense">
          <el-input v-model="form.businessLicense" placeholder="请输入营业执照附件(资源ID/URL)" />
        </el-form-item>
        <el-form-item label="结算-账户名" prop="bankAccountName">
          <el-input v-model="form.bankAccountName" placeholder="请输入结算-账户名" />
        </el-form-item>
        <el-form-item label="结算-账号" prop="bankAccountNo">
          <el-input v-model="form.bankAccountNo" placeholder="请输入结算-账号" />
        </el-form-item>
        <el-form-item label="结算-开户行" prop="bankName">
          <el-input v-model="form.bankName" placeholder="请输入结算-开户行" />
        </el-form-item>
        <el-form-item label="备注/补充说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="dialog.approve" :loading="buttonLoading" type="primary" @click="approve">通 过</el-button>
          <el-button v-if="dialog.approve" :loading="buttonLoading" type="danger" @click="reject">驳 回</el-button>

          <el-button v-if="!dialog.approve" :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MerchantApply" lang="ts">
import {
  listMerchantApply,
  getMerchantApply,
  delMerchantApply,
  addMerchantApply,
  updateMerchantApply,
  approveMerchantApply,
  rejectMerchantApply
} from '@/api/system/merchantApply';
import { MerchantApplyVO, MerchantApplyQuery, MerchantApplyForm } from '@/api/system/merchantApply/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const merchantApplyList = ref<MerchantApplyVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const merchantApplyFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: '',
  approve: false
});

const initFormData: MerchantApplyForm = {
  id: undefined,
  name: undefined,
  wxMchId: undefined,
  contactName: undefined,
  contactPhone: undefined,
  contactEmail: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  address: undefined,
  logo: undefined,
  businessLicense: undefined,
  bankAccountName: undefined,
  bankAccountNo: undefined,
  bankName: undefined,
  remark: undefined
};
const data = reactive<PageData<MerchantApplyForm, MerchantApplyQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    applyNo: undefined,
    name: undefined,
    contactName: undefined,
    contactPhone: undefined,
    status: undefined,
    params: {}
  },
  rules: {
    id: [{ required: true, message: '主键不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '商户名称不能为空', trigger: 'blur' }],
    contactName: [{ required: true, message: '联系人姓名不能为空', trigger: 'blur' }],
    contactPhone: [{ required: true, message: '联系人手机号不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询商户注册申请列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMerchantApply(queryParams.value);
  merchantApplyList.value = res.rows;
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
  merchantApplyFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MerchantApplyVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加商户注册申请';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: MerchantApplyVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getMerchantApply(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.approve = false;
  dialog.title = '修改商户注册申请';
};

/** 审核按钮操作 */
const handleApprove = async (row?: MerchantApplyVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getMerchantApply(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.approve = true;
  dialog.title = '商户申请审核';
};

/** 提交按钮 */
const submitForm = () => {
  merchantApplyFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMerchantApply(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addMerchantApply(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: MerchantApplyVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除商户注册申请编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delMerchantApply(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/merchantApply/export',
    {
      ...queryParams.value
    },
    `merchantApply_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});

/** 审批通过 */
const approve = async () => {
  if (!form.value.id) return;
  try {
    buttonLoading.value = true;
    await approveMerchantApply(form.value.id, { remark: form.value.remark });
    proxy?.$modal.msgSuccess('已通过');
    dialog.visible = false;
    await getList();
  } finally {
    buttonLoading.value = false;
  }
};

/** 审批驳回 */
const reject = async () => {
  if (!form.value.id) return;
  try {
    const reason = await ElMessageBox.prompt('请输入驳回原因', '审核驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea'
    })
      .then((res) => res.value)
      .catch(() => undefined);
    if (reason === undefined) return;
    buttonLoading.value = true;
    await rejectMerchantApply(form.value.id, { auditReason: reason, remark: form.value.remark });
    proxy?.$modal.msgSuccess('已驳回');
    dialog.visible = false;
    await getList();
  } finally {
    buttonLoading.value = false;
  }
};
</script>
