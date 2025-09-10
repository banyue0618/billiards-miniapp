<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="会员等级" prop="levelCode">
              <el-select v-model="queryParams.levelCode" placeholder="请选择会员等级">
                <el-option v-for="dict in member_level_code" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option v-for="dict in enable_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button v-hasPermi="['billiards:memberLevelConfig:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:memberLevelConfig:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['billiards:memberLevelConfig:remove']"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete()"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['billiards:memberLevelConfig:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="memberLevelConfigList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="配置ID" align="center" prop="id" />
        <el-table-column label="等级编码" align="center" prop="levelCode" />
        <el-table-column label="等级名称" align="center" prop="levelName" />
        <el-table-column label="所需累计消费金额" align="center" prop="requiredAmount" />
        <el-table-column label="折扣率" align="center" prop="discount" />
        <el-table-column label="每月赠送时长(分钟)" align="center" prop="monthlyFreeMinutes" />
        <el-table-column label="积分获取倍率" align="center" prop="pointsMultiplier" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="enable_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button
                v-hasPermi="['billiards:memberLevelConfig:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                v-hasPermi="['billiards:memberLevelConfig:remove']"
                link
                type="primary"
                icon="Delete"
                @click="handleDelete(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="启用" placement="top">
              <el-button
                v-hasPermi="['billiards:memberLevelConfig:edit']"
                v-if="scope.row.status == 0"
                link
                type="success"
                icon="Check"
                @click="handleEnable(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="禁用" placement="top">
              <el-button
                v-hasPermi="['billiards:memberLevelConfig:edit']"
                v-if="scope.row.status == 1"
                link
                type="danger"
                icon="Close"
                @click="handleDisable(scope.row)"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员等级配置对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="800px" append-to-body>
      <el-form ref="memberLevelConfigFormRef" :model="form" :rules="rules" label-width="140px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员等级" prop="levelCode">
              <el-select v-model="form.levelCode" placeholder="请选择会员等级" @change="handleLevelChange">
                <el-option v-for="dict in member_level_code" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员折扣率" prop="discount">
              <el-select v-model="form.discount" placeholder="请选择会员折扣率">
                <el-option v-for="dict in member_discount_rate" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所需累计消费金额" prop="requiredAmount">
              <el-input v-model="form.requiredAmount" placeholder="请输入所需累计消费金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="积分获取倍率" prop="pointsMultiplier">
              <el-select v-model="form.pointsMultiplier" placeholder="请选择积分获取倍率">
                <el-option v-for="dict in points_multiplier" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每月赠送时长" prop="monthlyFreeMinutes">
              <el-input v-model="form.monthlyFreeMinutes" placeholder="请输入每月赠送时长(分钟)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日特权折扣率" prop="birthdayDiscount">
              <el-select v-model="form.birthdayDiscount" placeholder="请选择生日特权折扣率">
                <el-option v-for="dict in member_discount_rate" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="等级描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
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
import {
  listMemberLevelConfig,
  getMemberLevelConfig,
  delMemberLevelConfig,
  addMemberLevelConfig,
  updateMemberLevelConfig
} from '@/api/billiards/memberLevelConfig';
import { MemberLevelConfigVO, MemberLevelConfigQuery, MemberLevelConfigForm } from '@/api/billiards/memberLevelConfig/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { enable_status, member_level_icon, member_level_code, member_discount_rate, points_multiplier } = toRefs<any>(proxy?.useDict('enable_status', 'member_level_icon', 'member_level_code', 'member_discount_rate', 'points_multiplier'));

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
  description: undefined
};
const data = reactive<PageData<MemberLevelConfigForm, MemberLevelConfigQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    levelCode: undefined,
    levelName: undefined,
    status: undefined,
    params: {}
  },
  rules: {
    id: [{ required: true, message: '配置ID不能为空', trigger: 'blur' }],
    levelCode: [{ required: true, message: '等级编码不能为空', trigger: 'blur' }],
    levelName: [{ required: true, message: '等级名称不能为空', trigger: 'blur' }],
    requiredAmount: [{ required: true, message: '所需累计消费金额不能为空', trigger: 'blur' }],
    discount: [{ required: true, message: '折扣率不能为空', trigger: 'blur' }],
    monthlyFreeMinutes: [{ required: true, message: '每月赠送时长（分钟）不能为空', trigger: 'blur' }],
    pointsMultiplier: [{ required: true, message: '积分获取倍率不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

const handleLevelChange = (value: string) => {
  const selected = member_level_code.value.find(item => item.value === value)
  form.value.levelName = selected ? selected.label : ''
}

const handleEnable = (row: MemberLevelConfigVO) => {
  row.status = 1;
  updateMemberLevelConfig(row);
  proxy?.$modal.msgSuccess('启用成功');
  getList();
};

const handleDisable = (row: MemberLevelConfigVO) => {
  row.status = 0;
  updateMemberLevelConfig(row);
  proxy?.$modal.msgSuccess('禁用成功');
  getList();
};

/** 查询会员等级配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberLevelConfig(queryParams.value);
  memberLevelConfigList.value = res.rows;
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
  memberLevelConfigFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberLevelConfigVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加会员等级配置';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberLevelConfigVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getMemberLevelConfig(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改会员等级配置';
};

/** 提交按钮 */
const submitForm = () => {
  memberLevelConfigFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberLevelConfig(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addMemberLevelConfig(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: MemberLevelConfigVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员等级配置编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delMemberLevelConfig(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'billiards/memberLevelConfig/export',
    {
      ...queryParams.value
    },
    `memberLevelConfig_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
