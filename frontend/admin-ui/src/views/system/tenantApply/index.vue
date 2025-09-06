<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="申请单号" prop="applyNo">
              <el-input v-model="queryParams.applyNo" placeholder="请输入申请单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="公司/机构名称" prop="companyName">
              <el-input v-model="queryParams.companyName" placeholder="请输入公司/机构名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="queryParams.creditCode" placeholder="请输入统一社会信用代码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="姓名" prop="contactName">
              <el-input v-model="queryParams.contactName" placeholder="请输入联系人姓名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="手机号" prop="contactPhone">
              <el-input v-model="queryParams.contactPhone" placeholder="请输入联系人手机号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="邮箱" prop="contactEmail">
              <el-input v-model="queryParams.contactEmail" placeholder="请输入联系人邮箱" clearable @keyup.enter="handleQuery" />
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
            <el-button v-hasPermi="['system:tenantApply:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:tenantApply:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:tenantApply:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:tenantApply:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="tenantApplyList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="申请单号" align="center" prop="applyNo" />
        <el-table-column label="公司/机构名称" align="center" prop="companyName" />
        <el-table-column label="统一社会信用代码" align="center" prop="creditCode" />
        <el-table-column label="联系人姓名" align="center" prop="contactName" />
        <el-table-column label="联系人手机号" align="center" prop="contactPhone" />
        <el-table-column label="联系人邮箱" align="center" prop="contactEmail" />
        <el-table-column label="审核状态" align="center" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">审核通过</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核时间" align="center" prop="auditTime" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:tenantApply:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:tenantApply:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="审核" placement="top">
              <el-button
                v-hasPermi="['system:tenantApply:audit']"
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
    <!-- 添加或修改租户注册申请对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="900px" append-to-body>
      <el-form ref="tenantApplyFormRef" class="tenant-apply-form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="公司/机构名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入公司/机构名称" />
            </el-form-item>
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
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
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <el-form-item label="省" prop="province">
              <el-input v-model="form.province" placeholder="请输入省" />
            </el-form-item>
            <el-form-item label="市" prop="city">
              <el-input v-model="form.city" placeholder="请输入市" />
            </el-form-item>
            <el-form-item label="区/县" prop="district">
              <el-input v-model="form.district" placeholder="请输入区/县" />
            </el-form-item>
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入详细地址" />
            </el-form-item>
            <el-form-item label="营业执照" prop="businessLicense">
              <img v-if="businessLicenseUrl" :src="businessLicenseUrl" class="avatar" />
              <div v-else class="uploader-tip">暂无营业执照图片</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注/补充说明" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="dialog.approve" :gutter="20">
          <el-col :span="24">
            <el-form-item label="租户套餐" prop="packageId">
              <el-select v-model="form.packageId" placeholder="请选择租户套餐" filterable clearable style="width: 100%">
                <el-option v-for="pkg in tenantPackages" :key="pkg.packageId" :label="pkg.packageName" :value="pkg.packageId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="dialog.approve" :gutter="20">
          <el-col :span="24">
            <el-form-item label="审核意见" prop="auditReason">
              <el-input v-model="form.auditReason" type="textarea" :rows="3" placeholder="请输入审核意见" />
            </el-form-item>
          </el-col>
        </el-row>
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

<script setup name="TenantApply" lang="ts">
import {
  listTenantApply,
  getTenantApply,
  delTenantApply,
  addTenantApply,
  updateTenantApply,
  approveTenantApply,
  rejectTenantApply
} from '@/api/system/tenantApply';
import { TenantApplyVO, TenantApplyQuery, TenantApplyForm } from '@/api/system/tenantApply/types';
import { selectTenantPackage } from '@/api/system/tenantPackage';
import type { TenantPkgVO } from '@/api/system/tenantPackage/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const tenantApplyList = ref<TenantApplyVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const tenantPackages = ref<TenantPkgVO[]>([]);
const businessLicenseUrl = ref('');

const queryFormRef = ref<ElFormInstance>();
const tenantApplyFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: '',
  approve: false
});

const initFormData: TenantApplyForm = {
  id: undefined,
  applyNo: undefined,
  companyName: undefined,
  creditCode: undefined,
  contactName: undefined,
  contactPhone: undefined,
  contactEmail: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  address: undefined,
  businessLicense: undefined,
  remark: undefined
};
const data = reactive<PageData<TenantApplyForm, TenantApplyQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    applyNo: undefined,
    companyName: undefined,
    creditCode: undefined,
    contactName: undefined,
    contactPhone: undefined,
    contactEmail: undefined,
    status: undefined,
    params: {}
  },
  rules: {
    id: [{ required: true, message: '主键不能为空', trigger: 'blur' }],
    applyNo: [{ required: true, message: '申请单号不能为空', trigger: 'blur' }],
    companyName: [{ required: true, message: '公司/机构名称不能为空', trigger: 'blur' }],
    contactName: [{ required: true, message: '联系人姓名不能为空', trigger: 'blur' }],
    contactPhone: [{ required: true, message: '联系人手机号不能为空', trigger: 'blur' }],
    packageId: [
      {
        required: true,
        message: '请选择租户套餐',
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
          if (!dialog.approve) return callback();
          if (!value) return callback(new Error('请选择租户套餐'));
          callback();
        }
      }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询租户注册申请列表 */
const getList = async () => {
  loading.value = true;
  const res = await listTenantApply(queryParams.value);
  tenantApplyList.value = res.rows;
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
  businessLicenseUrl.value = '';
  tenantApplyFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: TenantApplyVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加租户注册申请';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: TenantApplyVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getTenantApply(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.approve = false;
  dialog.title = '修改租户注册申请';
};

/** 审核按钮操作 */
const handleApprove = async (row?: TenantApplyVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getTenantApply(_id);
  Object.assign(form.value, res.data);
  businessLicenseUrl.value = (res.data as any)?.businessLicensePreviewUrl || '';
  dialog.visible = true;
  dialog.approve = true;
  dialog.title = '租户申请审核';
};

/** 审批通过 */
const approve = async () => {
  if (!form.value.id) return;
  try {
    // 审核通过必须选择套餐
    if (!form.value.packageId) {
      proxy?.$modal.msgError('请先选择租户套餐');
      return;
    }
    buttonLoading.value = true;
    await approveTenantApply(form.value.id, { auditReason: form.value.auditReason, packageId: form.value.packageId });
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
    await rejectTenantApply(form.value.id, { auditReason: reason, remark: form.value.remark });
    proxy?.$modal.msgSuccess('已驳回');
    dialog.visible = false;
    await getList();
  } finally {
    buttonLoading.value = false;
  }
};

/** 提交按钮 */
const submitForm = () => {
  tenantApplyFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTenantApply(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addTenantApply(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: TenantApplyVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除租户注册申请编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delTenantApply(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/tenantApply/export',
    {
      ...queryParams.value
    },
    `tenantApply_${new Date().getTime()}.xlsx`
  );
};

// 此处仅展示图片，上传在申请端完成

onMounted(() => {
  getList();
  // 预加载套餐下拉
  selectTenantPackage().then((res) => {
    tenantPackages.value = (res as any).data;
  });
});
</script>

<style scoped>
.tenant-apply-form :deep(.el-form-item__content) {
  width: 100%;
}
.tenant-apply-form :deep(.el-input),
.tenant-apply-form :deep(.el-select),
.tenant-apply-form :deep(.el-textarea) {
  width: 100%;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.uploader-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>
