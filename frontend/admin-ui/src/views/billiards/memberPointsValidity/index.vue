<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="积分数量" prop="points">
              <el-input v-model="queryParams.points" placeholder="请输入积分数量" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="剩余积分数量" prop="remainingPoints">
              <el-input v-model="queryParams.remainingPoints" placeholder="请输入剩余积分数量" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="过期时间" prop="expireTime">
              <el-date-picker clearable
                v-model="queryParams.expireTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择过期时间"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:memberPointsValidity:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:memberPointsValidity:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:memberPointsValidity:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:memberPointsValidity:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberPointsValidityList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="记录ID" align="center" prop="id" v-if="true" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="积分数量" align="center" prop="points" />
        <el-table-column label="剩余积分数量" align="center" prop="remainingPoints" />
        <el-table-column label="过期时间" align="center" prop="expireTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:memberPointsValidity:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:memberPointsValidity:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改积分有效期对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberPointsValidityFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="积分数量" prop="points">
          <el-input v-model="form.points" placeholder="请输入积分数量" />
        </el-form-item>
        <el-form-item label="剩余积分数量" prop="remainingPoints">
          <el-input v-model="form.remainingPoints" placeholder="请输入剩余积分数量" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker clearable
            v-model="form.expireTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择过期时间">
          </el-date-picker>
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

<script setup name="MemberPointsValidity" lang="ts">
import { listMemberPointsValidity, getMemberPointsValidity, delMemberPointsValidity, addMemberPointsValidity, updateMemberPointsValidity } from '@/api/billiards/memberPointsValidity';
import { MemberPointsValidityVO, MemberPointsValidityQuery, MemberPointsValidityForm } from '@/api/billiards/memberPointsValidity/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const memberPointsValidityList = ref<MemberPointsValidityVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberPointsValidityFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberPointsValidityForm = {
  id: undefined,
  userId: undefined,
  points: undefined,
  remainingPoints: undefined,
  expireTime: undefined,
}
const data = reactive<PageData<MemberPointsValidityForm, MemberPointsValidityQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    points: undefined,
    remainingPoints: undefined,
    expireTime: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "记录ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    points: [
      { required: true, message: "积分数量不能为空", trigger: "blur" }
    ],
    remainingPoints: [
      { required: true, message: "剩余积分数量不能为空", trigger: "blur" }
    ],
    expireTime: [
      { required: true, message: "过期时间不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询积分有效期列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberPointsValidity(queryParams.value);
  memberPointsValidityList.value = res.rows;
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
  memberPointsValidityFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberPointsValidityVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加积分有效期";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberPointsValidityVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberPointsValidity(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改积分有效期";
}

/** 提交按钮 */
const submitForm = () => {
  memberPointsValidityFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberPointsValidity(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberPointsValidity(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberPointsValidityVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除积分有效期编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberPointsValidity(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/memberPointsValidity/export', {
    ...queryParams.value
  }, `memberPointsValidity_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
