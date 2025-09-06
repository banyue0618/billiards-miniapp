<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="当前等级编码" prop="levelCode">
              <el-input v-model="queryParams.levelCode" placeholder="请输入当前等级编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="累计消费金额" prop="totalAmount">
              <el-input v-model="queryParams.totalAmount" placeholder="请输入累计消费金额" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="当前积分" prop="points">
              <el-input v-model="queryParams.points" placeholder="请输入当前积分" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="本月已使用免费时长" prop="monthlyUsedMinutes">
              <el-input v-model="queryParams.monthlyUsedMinutes" placeholder="请输入本月已使用免费时长" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级有效期" prop="levelExpireTime">
              <el-date-picker clearable
                v-model="queryParams.levelExpireTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择等级有效期"
              />
            </el-form-item>
            <el-form-item label="最近消费时间" prop="lastConsumeTime">
              <el-date-picker clearable
                v-model="queryParams.lastConsumeTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择最近消费时间"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:memberUser:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:memberUser:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:memberUser:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:memberUser:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberUserList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="会员ID" align="center" prop="id" v-if="true" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="当前等级编码" align="center" prop="levelCode" />
        <el-table-column label="累计消费金额" align="center" prop="totalAmount" />
        <el-table-column label="当前积分" align="center" prop="points" />
        <el-table-column label="本月已使用免费时长" align="center" prop="monthlyUsedMinutes" />
        <el-table-column label="等级有效期" align="center" prop="levelExpireTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.levelExpireTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最近消费时间" align="center" prop="lastConsumeTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastConsumeTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态：0-正常 1-禁用" align="center" prop="status" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:memberUser:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:memberUser:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员用户对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberUserFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="当前等级编码" prop="levelCode">
          <el-input v-model="form.levelCode" placeholder="请输入当前等级编码" />
        </el-form-item>
        <el-form-item label="累计消费金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入累计消费金额" />
        </el-form-item>
        <el-form-item label="当前积分" prop="points">
          <el-input v-model="form.points" placeholder="请输入当前积分" />
        </el-form-item>
        <el-form-item label="本月已使用免费时长" prop="monthlyUsedMinutes">
          <el-input v-model="form.monthlyUsedMinutes" placeholder="请输入本月已使用免费时长" />
        </el-form-item>
        <el-form-item label="等级有效期" prop="levelExpireTime">
          <el-date-picker clearable
            v-model="form.levelExpireTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择等级有效期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="最近消费时间" prop="lastConsumeTime">
          <el-date-picker clearable
            v-model="form.lastConsumeTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择最近消费时间">
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

<script setup name="MemberUser" lang="ts">
import { listMemberUser, getMemberUser, delMemberUser, addMemberUser, updateMemberUser } from '@/api/billiards/memberUser';
import { MemberUserVO, MemberUserQuery, MemberUserForm } from '@/api/billiards/memberUser/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const memberUserList = ref<MemberUserVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberUserFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberUserForm = {
  id: undefined,
  userId: undefined,
  levelCode: undefined,
  totalAmount: undefined,
  points: undefined,
  monthlyUsedMinutes: undefined,
  levelExpireTime: undefined,
  lastConsumeTime: undefined,
  status: undefined
}
const data = reactive<PageData<MemberUserForm, MemberUserQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    levelCode: undefined,
    totalAmount: undefined,
    points: undefined,
    monthlyUsedMinutes: undefined,
    levelExpireTime: undefined,
    lastConsumeTime: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "会员ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    levelCode: [
      { required: true, message: "当前等级编码不能为空", trigger: "blur" }
    ],
    totalAmount: [
      { required: true, message: "累计消费金额不能为空", trigger: "blur" }
    ],
    points: [
      { required: true, message: "当前积分不能为空", trigger: "blur" }
    ],
    monthlyUsedMinutes: [
      { required: true, message: "本月已使用免费时长不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态：0-正常 1-禁用不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员用户列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberUser(queryParams.value);
  memberUserList.value = res.rows;
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
  memberUserFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberUserVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员用户";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberUserVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberUser(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员用户";
}

/** 提交按钮 */
const submitForm = () => {
  memberUserFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberUser(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberUser(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberUserVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员用户编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberUser(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/memberUser/export', {
    ...queryParams.value
  }, `memberUser_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
