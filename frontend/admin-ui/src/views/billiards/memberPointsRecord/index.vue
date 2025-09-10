<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="变动类型" prop="type">
              <el-select v-model="queryParams.type" placeholder="请选择积分变动类型" clearable>
                <el-option v-for="dict in points_change_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="积分场景" prop="scene">
              <el-select v-model="queryParams.scene" placeholder="请选择积分场景" clearable>
                <el-option v-for="dict in points_scene" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
<!--            <el-form-item label="对应的规则ID" prop="ruleId">-->
<!--              <el-input v-model="queryParams.ruleId" placeholder="请输入对应的规则ID" clearable @keyup.enter="handleQuery" />-->
<!--            </el-form-item>-->
<!--            <el-form-item label="关联业务ID" prop="businessId">-->
<!--              <el-input v-model="queryParams.businessId" placeholder="请输入关联业务ID" clearable @keyup.enter="handleQuery" />-->
<!--            </el-form-item>-->
<!--            <el-form-item label="积分描述" prop="description">-->
<!--              <el-input v-model="queryParams.description" placeholder="请输入积分描述" clearable @keyup.enter="handleQuery" />-->
<!--            </el-form-item>-->
<!--            <el-form-item label="过期时间" prop="expireTime">-->
<!--              <el-date-picker clearable-->
<!--                v-model="queryParams.expireTime"-->
<!--                type="date"-->
<!--                value-format="YYYY-MM-DD"-->
<!--                placeholder="请选择过期时间"-->
<!--              />-->
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:memberPointsRecord:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:memberPointsRecord:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:memberPointsRecord:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:memberPointsRecord:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberPointsRecordList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="记录ID" align="center" prop="id" v-if="false" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="积分数量" align="center" prop="points" />
        <el-table-column label="类型" align="center" prop="type">
          <template #default="scope">
            <dict-tag :options="points_rule_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="积分场景" align="center" prop="scene">
          <template #default="scope">
            <dict-tag :options="points_scene" :value="scope.row.scene" />
          </template>
        </el-table-column>
<!--        <el-table-column label="对应的规则ID" align="center" prop="ruleId" />-->
<!--        <el-table-column label="关联业务ID" align="center" prop="businessId" />-->
        <el-table-column label="积分描述" align="center" prop="description" />
<!--        <el-table-column label="过期时间" align="center" prop="expireTime" width="180">-->
<!--          <template #default="scope">-->
<!--            <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:memberPointsRecord:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:memberPointsRecord:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员积分记录对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberPointsRecordFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="积分数量" prop="points">
          <el-input v-model="form.points" placeholder="请输入积分数量" />
        </el-form-item>
        <el-form-item label="场景，与积分规则表场景对应" prop="scene">
          <el-input v-model="form.scene" placeholder="请输入场景，与积分规则表场景对应" />
        </el-form-item>
        <el-form-item label="对应的规则ID" prop="ruleId">
          <el-input v-model="form.ruleId" placeholder="请输入对应的规则ID" />
        </el-form-item>
        <el-form-item label="关联业务ID" prop="businessId">
          <el-input v-model="form.businessId" placeholder="请输入关联业务ID" />
        </el-form-item>
        <el-form-item label="积分描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入积分描述" />
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

<script setup name="MemberPointsRecord" lang="ts">
import { listMemberPointsRecord, getMemberPointsRecord, delMemberPointsRecord, addMemberPointsRecord, updateMemberPointsRecord } from '@/api/billiards/memberPointsRecord';
import { MemberPointsRecordVO, MemberPointsRecordQuery, MemberPointsRecordForm } from '@/api/billiards/memberPointsRecord/types';
import {parseTime} from "../../../utils/ruoyi";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { points_scene, sys_yes_no, points_rule_type, enable_status, points_value_type, points_change_type } = toRefs<any>(
  proxy?.useDict('points_scene', 'sys_yes_no', 'points_rule_type', 'enable_status', 'points_value_type', 'points_change_type')
);

const memberPointsRecordList = ref<MemberPointsRecordVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberPointsRecordFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberPointsRecordForm = {
  id: undefined,
  userId: undefined,
  points: undefined,
  type: undefined,
  scene: undefined,
  ruleId: undefined,
  businessId: undefined,
  description: undefined,
  expireTime: undefined,
}
const data = reactive<PageData<MemberPointsRecordForm, MemberPointsRecordQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    points: undefined,
    type: undefined,
    scene: undefined,
    ruleId: undefined,
    businessId: undefined,
    description: undefined,
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
    type: [
      { required: true, message: "类型：1-获取 2-消耗不能为空", trigger: "change" }
    ],
    scene: [
      { required: true, message: "场景，与积分规则表场景对应不能为空", trigger: "blur" }
    ],
    ruleId: [
      { required: true, message: "对应的规则ID不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员积分记录列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberPointsRecord(queryParams.value);
  memberPointsRecordList.value = res.rows;
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
  memberPointsRecordFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberPointsRecordVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员积分记录";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberPointsRecordVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberPointsRecord(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员积分记录";
}

/** 提交按钮 */
const submitForm = () => {
  memberPointsRecordFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberPointsRecord(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberPointsRecord(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberPointsRecordVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员积分记录编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberPointsRecord(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/memberPointsRecord/export', {
    ...queryParams.value
  }, `memberPointsRecord_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
