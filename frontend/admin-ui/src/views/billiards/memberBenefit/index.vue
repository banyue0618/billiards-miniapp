<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="权益名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入权益名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="权益类型" prop="type">
              <el-select v-model="queryParams.type" placeholder="请选择权益类型" clearable >
                <el-option v-for="dict in member_benefit_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="生效时间" prop="effectiveTime">
              <el-date-picker clearable
                v-model="queryParams.effectiveTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择生效时间"
              />
            </el-form-item>
            <el-form-item label="失效时间" prop="expireTime">
              <el-date-picker clearable
                v-model="queryParams.expireTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择失效时间"
              />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:memberBenefit:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:memberBenefit:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:memberBenefit:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:memberBenefit:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberBenefitList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="权益ID" align="center" prop="id" v-if="false" />
        <el-table-column label="权益名称" align="center" prop="name" />
        <el-table-column label="权益类型" align="center" prop="type">
          <template #default="scope">
            <dict-tag :options="member_benefit_type" :value="scope.row.type"/>
          </template>
        </el-table-column>
        <el-table-column label="生效时间" align="center" prop="effectiveTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="失效时间" align="center" prop="expireTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="权益图标" align="center" prop="icon" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="enable_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:memberBenefit:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:memberBenefit:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员权益对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberBenefitFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="权益名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权益名称" />
        </el-form-item>
        <el-form-item label="权益类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择权益类型">
            <el-option
                v-for="dict in member_benefit_type"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="适用等级编码，多个用逗号分隔" prop="applicableLevels">
          <el-input v-model="form.applicableLevels" placeholder="请输入适用等级编码，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="权益值" prop="benefitValue">
          <el-input v-model="form.benefitValue" placeholder="请输入权益值" />
        </el-form-item>
        <el-form-item label="权益规则" prop="benefitRules">
            <el-input v-model="form.benefitRules" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="生效时间" prop="effectiveTime">
          <el-date-picker clearable
            v-model="form.effectiveTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择生效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="失效时间" prop="expireTime">
          <el-date-picker clearable
            v-model="form.expireTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择失效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="权益图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入权益图标" />
        </el-form-item>
        <el-form-item label="权益描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="使用说明" prop="instructions">
            <el-input v-model="form.instructions" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in enable_status"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input v-model="form.sortOrder" placeholder="请输入排序号" />
        </el-form-item>
        <el-form-item label="是否限时" prop="isLimited">
          <el-select v-model="form.isLimited" placeholder="请选择是否限时">
            <el-option
                v-for="dict in sys_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否节日特权" prop="isHoliday">
          <el-select v-model="form.isHoliday" placeholder="请选择是否节日特权">
            <el-option
                v-for="dict in sys_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权益标签" prop="tags">
          <el-input v-model="form.tags" placeholder="请输入权益标签" />
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

<script setup name="MemberBenefit" lang="ts">
import { listMemberBenefit, getMemberBenefit, delMemberBenefit, addMemberBenefit, updateMemberBenefit } from '@/api/billiards/memberBenefit';
import { MemberBenefitVO, MemberBenefitQuery, MemberBenefitForm } from '@/api/billiards/memberBenefit/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_yes_no, enable_status, member_benefit_type } = toRefs<any>(proxy?.useDict('sys_yes_no', 'enable_status', 'member_benefit_type'));

const memberBenefitList = ref<MemberBenefitVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberBenefitFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberBenefitForm = {
  id: undefined,
  name: undefined,
  type: undefined,
  applicableLevels: undefined,
  benefitValue: undefined,
  benefitRules: undefined,
  effectiveTime: undefined,
  expireTime: undefined,
  icon: undefined,
  description: undefined,
  instructions: undefined,
  status: undefined,
  sortOrder: undefined,
  isLimited: undefined,
  isHoliday: undefined,
  tags: undefined
}
const data = reactive<PageData<MemberBenefitForm, MemberBenefitQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    type: undefined,
    effectiveTime: undefined,
    expireTime: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "权益ID不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "权益名称不能为空", trigger: "blur" }
    ],
    type: [
      { required: true, message: "权益类型不能为空", trigger: "change" }
    ],
    applicableLevels: [
      { required: true, message: "适用等级编码，多个用逗号分隔不能为空", trigger: "blur" }
    ],
    benefitValue: [
      { required: true, message: "权益值不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
    sortOrder: [
      { required: true, message: "排序号不能为空", trigger: "blur" }
    ],
    isLimited: [
      { required: true, message: "是否限时不能为空", trigger: "change" }
    ],
    isHoliday: [
      { required: true, message: "是否节日特权不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员权益列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberBenefit(queryParams.value);
  memberBenefitList.value = res.rows;
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
  memberBenefitFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberBenefitVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员权益";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberBenefitVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberBenefit(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员权益";
}

/** 提交按钮 */
const submitForm = () => {
  memberBenefitFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberBenefit(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberBenefit(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberBenefitVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员权益编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberBenefit(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/memberBenefit/export', {
    ...queryParams.value
  }, `memberBenefit_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
