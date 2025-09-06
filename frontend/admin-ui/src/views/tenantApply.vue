<template>
  <div class="apply">
    <el-card class="apply-card" shadow="hover">
      <h3 class="title">租户入驻申请</h3>
      <el-form ref="tenantApplyFormRef" :model="form" :rules="rules" label-width="110px" class="apply-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="公司/机构名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入公司/机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系人姓名" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人手机号" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系人手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="省" prop="province">
              <el-input v-model="form.province" placeholder="请输入省" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="市" prop="city">
              <el-input v-model="form.city" placeholder="请输入市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区/县" prop="district">
              <el-input v-model="form.district" placeholder="请输入区/县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="营业执照附件" prop="businessLicense">
              <el-upload class="avatar-uploader" :http-request="handleLicenseUpload" :show-file-list="false" :before-upload="beforeFileUpload">
                <img v-if="businessLicensePreviewUrl" :src="businessLicensePreviewUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="uploader-tip">支持图片，大小≤2MB。上传后将自动填充。</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预计账户数" prop="expectedUsers">
              <el-input v-model="form.expectedUsers" placeholder="用于套餐评估，可选" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注/补充说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
        <div class="actions">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">提交申请</el-button>
          <router-link class="link-type" to="/login">返回登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup name="TenantApply" lang="ts">
import { addTenantApply } from '@/api/system/tenantApply';
import { TenantApplyForm } from '@/api/system/tenantApply/types';
import { uploadFile } from '@/api/system/file';
import { Plus } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const buttonLoading = ref(false);
const tenantApplyFormRef = ref<ElFormInstance>();
const businessLicensePreviewUrl = ref('');

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
  packageId: undefined,
  expectedUsers: undefined,
  remark: undefined,
  status: undefined,
  auditBy: undefined,
  auditTime: undefined,
  auditReason: undefined,
  isDelete: undefined
}

const data = reactive<{ form: TenantApplyForm; rules: ElFormRules }>({
  form: { ...initFormData },
  rules: {
    companyName: [{ required: true, message: '公司/机构名称不能为空', trigger: 'blur' }],
    contactName: [{ required: true, message: '联系人姓名不能为空', trigger: 'blur' }],
    contactPhone: [{ required: true, message: '联系人手机号不能为空', trigger: 'blur' }]
  }
});

const { form, rules } = toRefs(data);

function beforeFileUpload(file: any) {
  const isImage = file.type?.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isImage) {
    proxy?.$modal.msgError('仅支持图片类型文件');
    return false;
  }
  if (!isLt2M) {
    proxy?.$modal.msgError('图片大小不能超过 2MB');
    return false;
  }
  return true;
}

async function handleLicenseUpload(option: any) {
  const { file } = option;
  try {
    const res = await uploadFile(file, 'TENANT_LICENSE');
    if (res && res.data) {
      form.value.businessLicense = res.data.resourceId as any;
      businessLicensePreviewUrl.value = res.data.previewUrl || '';
      proxy?.$modal.msgSuccess('营业执照上传成功');
      option?.onSuccess && option.onSuccess(res, file);
    } else {
      const msg = res?.msg || '上传失败';
      proxy?.$modal.msgError(msg);
      option?.onError && option.onError(new Error(msg));
    }
  } catch (e: any) {
    proxy?.$modal.msgError(e?.message || '上传失败');
    option?.onError && option.onError(e);
  }
}

const submitForm = () => {
  tenantApplyFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    buttonLoading.value = true;
    try {
      await addTenantApply(form.value);
      proxy?.$modal.msgSuccess('提交成功，请等待审核');
      // 清空
      Object.assign(form.value, { ...initFormData });
      businessLicensePreviewUrl.value = '';
    } finally {
      buttonLoading.value = false;
    }
  });
};
</script>

<style scoped lang="scss">
.apply {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
}
.apply-card {
  width: 900px;
}
.title {
  text-align: center;
  margin: 0 0 18px 0;
}
.apply-form {
  padding: 10px 10px 0 10px;
}
.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.link-type { color: var(--el-color-primary); text-decoration: none; }
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}
.avatar-uploader:hover { border-color: #409eff; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 178px; height: 178px; text-align: center; display: flex; justify-content: center; align-items: center; }
.avatar { width: 178px; height: 178px; display: block; }
.uploader-tip { font-size: 12px; color: #909399; margin-top: 6px; }
</style>
