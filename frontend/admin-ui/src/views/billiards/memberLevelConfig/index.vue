<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="等级编码" prop="levelCode">
              <el-input v-model="queryParams.levelCode" placeholder="请输入等级编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级名称" prop="levelName">
              <el-input v-model="queryParams.levelName" placeholder="请输入等级名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="所需累计消费金额" prop="requiredAmount">
              <el-input v-model="queryParams.requiredAmount" placeholder="请输入所需累计消费金额" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="折扣率" prop="discount">
              <el-input v-model="queryParams.discount" placeholder="请输入折扣率" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="每月赠送时长" prop="monthlyFreeMinutes">
              <el-input v-model="queryParams.monthlyFreeMinutes" placeholder="请输入每月赠送时长" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="积分获取倍率" prop="pointsMultiplier">
              <el-input v-model="queryParams.pointsMultiplier" placeholder="请输入积分获取倍率" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="生日特权折扣率" prop="birthdayDiscount">
              <el-input v-model="queryParams.birthdayDiscount" placeholder="请输入生日特权折扣率" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="可带朋友享受会员价的人数" prop="friendPrivilegeCount">
              <el-input v-model="queryParams.friendPrivilegeCount" placeholder="请输入可带朋友享受会员价的人数" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="专属客服服务 0-否 1-是" prop="vipService">
              <el-input v-model="queryParams.vipService" placeholder="请输入专属客服服务 0-否 1-是" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="预约特权 0-否 1-是" prop="reservationPrivilege">
              <el-input v-model="queryParams.reservationPrivilege" placeholder="请输入预约特权 0-否 1-是" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级图标" prop="levelIcon">
              <el-input v-model="queryParams.levelIcon" placeholder="请输入等级图标" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级背景图" prop="levelBackground">
              <el-input v-model="queryParams.levelBackground" placeholder="请输入等级背景图" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级描述" prop="description">
              <el-input v-model="queryParams.description" placeholder="请输入等级描述" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['billiards:memberLevelConfig:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['billiards:memberLevelConfig:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['billiards:memberLevelConfig:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['billiards:memberLevelConfig:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberLevelConfigList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="配置ID" align="center" prop="id" v-if="true" />
        <el-table-column label="等级编码" align="center" prop="levelCode" />
        <el-table-column label="等级名称" align="center" prop="levelName" />
        <el-table-column label="所需累计消费金额" align="center" prop="requiredAmount" />
        <el-table-column label="折扣率" align="center" prop="discount" />
        <el-table-column label="每月赠送时长" align="center" prop="monthlyFreeMinutes" />
        <el-table-column label="积分获取倍率" align="center" prop="pointsMultiplier" />
        <el-table-column label="生日特权折扣率" align="center" prop="birthdayDiscount" />
        <el-table-column label="可带朋友享受会员价的人数" align="center" prop="friendPrivilegeCount" />
        <el-table-column label="专属客服服务 0-否 1-是" align="center" prop="vipService" />
        <el-table-column label="预约特权 0-否 1-是" align="center" prop="reservationPrivilege" />
        <el-table-column label="等级图标" align="center" prop="levelIcon" />
        <el-table-column label="等级背景图" align="center" prop="levelBackground" />
        <el-table-column label="等级描述" align="center" prop="description" />
        <el-table-column label="状态 0-启用 1-禁用" align="center" prop="status" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['billiards:memberLevelConfig:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['billiards:memberLevelConfig:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员等级配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberLevelConfigFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="等级编码" prop="levelCode">
          <el-input v-model="form.levelCode" placeholder="请输入等级编码" />
        </el-form-item>
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="form.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="所需累计消费金额" prop="requiredAmount">
          <el-input v-model="form.requiredAmount" placeholder="请输入所需累计消费金额" />
        </el-form-item>
        <el-form-item label="折扣率" prop="discount">
          <el-input v-model="form.discount" placeholder="请输入折扣率" />
        </el-form-item>
        <el-form-item label="每月赠送时长" prop="monthlyFreeMinutes">
          <el-input v-model="form.monthlyFreeMinutes" placeholder="请输入每月赠送时长" />
        </el-form-item>
        <el-form-item label="积分获取倍率" prop="pointsMultiplier">
          <el-input v-model="form.pointsMultiplier" placeholder="请输入积分获取倍率" />
        </el-form-item>
        <el-form-item label="生日特权折扣率" prop="birthdayDiscount">
          <el-input v-model="form.birthdayDiscount" placeholder="请输入生日特权折扣率" />
        </el-form-item>
        <el-form-item label="可带朋友享受会员价的人数" prop="friendPrivilegeCount">
          <el-input v-model="form.friendPrivilegeCount" placeholder="请输入可带朋友享受会员价的人数" />
        </el-form-item>
        <el-form-item label="专属客服服务 0-否 1-是" prop="vipService">
          <el-input v-model="form.vipService" placeholder="请输入专属客服服务 0-否 1-是" />
        </el-form-item>
        <el-form-item label="预约特权 0-否 1-是" prop="reservationPrivilege">
          <el-input v-model="form.reservationPrivilege" placeholder="请输入预约特权 0-否 1-是" />
        </el-form-item>
        <el-form-item label="等级图标" prop="levelIcon">
          <el-input v-model="form.levelIcon" placeholder="请输入等级图标" />
        </el-form-item>
        <el-form-item label="等级背景图" prop="levelBackground">
          <el-input v-model="form.levelBackground" placeholder="请输入等级背景图" />
        </el-form-item>
        <el-form-item label="等级描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
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

<script setup name="MemberLevelConfig" lang="ts">
import { listMemberLevelConfig, getMemberLevelConfig, delMemberLevelConfig, addMemberLevelConfig, updateMemberLevelConfig } from '@/api/billiards/memberLevelConfig';
import { MemberLevelConfigVO, MemberLevelConfigQuery, MemberLevelConfigForm } from '@/api/billiards/memberLevelConfig/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const memberLevelConfigList = ref<MemberLevelConfigVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberLevelConfigFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberLevelConfigForm = {
  id: undefined,
  levelCode: undefined,
  levelName: undefined,
  requiredAmount: undefined,
  discount: undefined,
  monthlyFreeMinutes: undefined,
  pointsMultiplier: undefined,
  birthdayDiscount: undefined,
  friendPrivilegeCount: undefined,
  vipService: undefined,
  reservationPrivilege: undefined,
  levelIcon: undefined,
  levelBackground: undefined,
  description: undefined,
  status: undefined
}
const data = reactive<PageData<MemberLevelConfigForm, MemberLevelConfigQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    levelCode: undefined,
    levelName: undefined,
    requiredAmount: undefined,
    discount: undefined,
    monthlyFreeMinutes: undefined,
    pointsMultiplier: undefined,
    birthdayDiscount: undefined,
    friendPrivilegeCount: undefined,
    vipService: undefined,
    reservationPrivilege: undefined,
    levelIcon: undefined,
    levelBackground: undefined,
    description: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "配置ID不能为空", trigger: "blur" }
    ],
    levelCode: [
      { required: true, message: "等级编码不能为空", trigger: "blur" }
    ],
    levelName: [
      { required: true, message: "等级名称不能为空", trigger: "blur" }
    ],
    requiredAmount: [
      { required: true, message: "所需累计消费金额不能为空", trigger: "blur" }
    ],
    discount: [
      { required: true, message: "折扣率不能为空", trigger: "blur" }
    ],
    monthlyFreeMinutes: [
      { required: true, message: "每月赠送时长不能为空", trigger: "blur" }
    ],
    pointsMultiplier: [
      { required: true, message: "积分获取倍率不能为空", trigger: "blur" }
    ],
    friendPrivilegeCount: [
      { required: true, message: "可带朋友享受会员价的人数不能为空", trigger: "blur" }
    ],
    vipService: [
      { required: true, message: "专属客服服务 0-否 1-是不能为空", trigger: "blur" }
    ],
    reservationPrivilege: [
      { required: true, message: "预约特权 0-否 1-是不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态 0-启用 1-禁用不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员等级配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberLevelConfig(queryParams.value);
  memberLevelConfigList.value = res.rows;
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
  memberLevelConfigFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberLevelConfigVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员等级配置";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberLevelConfigVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberLevelConfig(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员等级配置";
}

/** 提交按钮 */
const submitForm = () => {
  memberLevelConfigFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberLevelConfig(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberLevelConfig(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberLevelConfigVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员等级配置编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberLevelConfig(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('billiards/memberLevelConfig/export', {
    ...queryParams.value
  }, `memberLevelConfig_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
