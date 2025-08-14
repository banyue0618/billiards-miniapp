<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="规则名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入规则名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券" prop="scene">
              <el-input v-model="queryParams.scene" placeholder="请输入积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="积分值" prop="pointsValue">
              <el-input v-model="queryParams.pointsValue" placeholder="请输入积分值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="封顶积分值" prop="maxPoints">
              <el-input v-model="queryParams.maxPoints" placeholder="请输入封顶积分值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="规则配置" prop="ruleConfig">
              <el-input v-model="queryParams.ruleConfig" placeholder="请输入规则配置" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级加成配置" prop="levelBonus">
              <el-input v-model="queryParams.levelBonus" placeholder="请输入等级加成配置" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="时段加成配置" prop="timeBonus">
              <el-input v-model="queryParams.timeBonus" placeholder="请输入时段加成配置" clearable @keyup.enter="handleQuery" />
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
            <el-form-item label="规则描述" prop="description">
              <el-input v-model="queryParams.description" placeholder="请输入规则描述" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否参与活动加成：0-否 1-是" prop="enableActivityBonus">
              <el-input v-model="queryParams.enableActivityBonus" placeholder="请输入是否参与活动加成：0-否 1-是" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="积分有效期" prop="validityDays">
              <el-input v-model="queryParams.validityDays" placeholder="请输入积分有效期" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:pointsRule:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:pointsRule:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:pointsRule:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:pointsRule:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="pointsRuleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="规则ID" align="center" prop="id" v-if="true" />
        <el-table-column label="规则名称" align="center" prop="name" />
        <el-table-column label="规则类型：1-获取 2-消耗" align="center" prop="type" />
        <el-table-column label="积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券" align="center" prop="scene" />
        <el-table-column label="积分值类型：1-固定值 2-比例值" align="center" prop="valueType" />
        <el-table-column label="积分值" align="center" prop="pointsValue" />
        <el-table-column label="封顶积分值" align="center" prop="maxPoints" />
        <el-table-column label="规则配置" align="center" prop="ruleConfig" />
        <el-table-column label="等级加成配置" align="center" prop="levelBonus" />
        <el-table-column label="时段加成配置" align="center" prop="timeBonus" />
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
        <el-table-column label="规则描述" align="center" prop="description" />
        <el-table-column label="状态：0-启用 1-禁用" align="center" prop="status" />
        <el-table-column label="是否参与活动加成：0-否 1-是" align="center" prop="enableActivityBonus" />
        <el-table-column label="积分有效期" align="center" prop="validityDays" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:pointsRule:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:pointsRule:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改积分规则对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="pointsRuleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券" prop="scene">
          <el-input v-model="form.scene" placeholder="请输入积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券" />
        </el-form-item>
        <el-form-item label="积分值" prop="pointsValue">
          <el-input v-model="form.pointsValue" placeholder="请输入积分值" />
        </el-form-item>
        <el-form-item label="封顶积分值" prop="maxPoints">
          <el-input v-model="form.maxPoints" placeholder="请输入封顶积分值" />
        </el-form-item>
        <el-form-item label="规则配置" prop="ruleConfig">
            <el-input v-model="form.ruleConfig" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="等级加成配置" prop="levelBonus">
            <el-input v-model="form.levelBonus" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="时段加成配置" prop="timeBonus">
            <el-input v-model="form.timeBonus" type="textarea" placeholder="请输入内容" />
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
        <el-form-item label="规则描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="是否参与活动加成：0-否 1-是" prop="enableActivityBonus">
          <el-input v-model="form.enableActivityBonus" placeholder="请输入是否参与活动加成：0-否 1-是" />
        </el-form-item>
        <el-form-item label="积分有效期" prop="validityDays">
          <el-input v-model="form.validityDays" placeholder="请输入积分有效期" />
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

<script setup name="PointsRule" lang="ts">
import { listPointsRule, getPointsRule, delPointsRule, addPointsRule, updatePointsRule } from '@/api/billiards/pointsRule';
import { PointsRuleVO, PointsRuleQuery, PointsRuleForm } from '@/api/billiards/pointsRule/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const pointsRuleList = ref<PointsRuleVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const pointsRuleFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: PointsRuleForm = {
  id: undefined,
  name: undefined,
  type: undefined,
  scene: undefined,
  valueType: undefined,
  pointsValue: undefined,
  maxPoints: undefined,
  ruleConfig: undefined,
  levelBonus: undefined,
  timeBonus: undefined,
  effectiveTime: undefined,
  expireTime: undefined,
  description: undefined,
  status: undefined,
  enableActivityBonus: undefined,
  validityDays: undefined
}
const data = reactive<PageData<PointsRuleForm, PointsRuleQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    type: undefined,
    scene: undefined,
    valueType: undefined,
    pointsValue: undefined,
    maxPoints: undefined,
    ruleConfig: undefined,
    levelBonus: undefined,
    timeBonus: undefined,
    effectiveTime: undefined,
    expireTime: undefined,
    description: undefined,
    status: undefined,
    enableActivityBonus: undefined,
    validityDays: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "规则ID不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "规则名称不能为空", trigger: "blur" }
    ],
    type: [
      { required: true, message: "规则类型：1-获取 2-消耗不能为空", trigger: "change" }
    ],
    scene: [
      { required: true, message: "积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券不能为空", trigger: "blur" }
    ],
    valueType: [
      { required: true, message: "积分值类型：1-固定值 2-比例值不能为空", trigger: "change" }
    ],
    pointsValue: [
      { required: true, message: "积分值不能为空", trigger: "blur" }
    ],
    maxPoints: [
      { required: true, message: "封顶积分值不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态：0-启用 1-禁用不能为空", trigger: "change" }
    ],
    enableActivityBonus: [
      { required: true, message: "是否参与活动加成：0-否 1-是不能为空", trigger: "blur" }
    ],
    validityDays: [
      { required: true, message: "积分有效期不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询积分规则列表 */
const getList = async () => {
  loading.value = true;
  const res = await listPointsRule(queryParams.value);
  pointsRuleList.value = res.rows;
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
  pointsRuleFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: PointsRuleVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加积分规则";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: PointsRuleVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getPointsRule(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改积分规则";
}

/** 提交按钮 */
const submitForm = () => {
  pointsRuleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updatePointsRule(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addPointsRule(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: PointsRuleVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除积分规则编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delPointsRule(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/pointsRule/export', {
    ...queryParams.value
  }, `pointsRule_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
