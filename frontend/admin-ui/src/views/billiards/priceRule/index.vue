<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="16">
            <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
              <el-form-item label="规则名称" prop="name">
                <el-input
                  v-model="queryParams.name"
                  placeholder="请输入规则名称"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="计费类型" prop="ruleType">
                <el-select v-model="queryParams.ruleType" placeholder="计费类型" clearable style="width: 200px">
                  <el-option
                    v-for="dict in ruleTypeOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="8">
            <div class="tool-button-group">
              <el-button type="primary" icon="Plus" @click="handleAdd">新增规则</el-button>
              <el-button icon="Delete" :disabled="multiple" @click="handleDelete">批量删除</el-button>
            </div>
          </el-col>
        </el-row>
      </template>

      <el-table
        v-loading="loading"
        :data="priceRuleList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="规则名称" align="center" prop="name" />
        <el-table-column label="规则类型" align="center" prop="ruleType">
          <template #default="scope">
            <el-tag v-if="scope.row.ruleType === 1" type="primary">标准计费</el-tag>
            <el-tag v-else-if="scope.row.ruleType === 2" type="success">阶梯计费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计费单价" align="center">
          <template #default="scope">
            <!-- 标准计费显示单价 -->
            <template v-if="scope.row.ruleType === 1">
              {{ scope.row.priceUnit }} 元/分钟
            </template>
            <!-- 阶梯计费显示按钮 -->
            <template v-else-if="scope.row.ruleType === 2">
              <el-tooltip placement="right" :show-after="500">
                <template #content>
                  <div class="ladder-tooltip-content">
                    <div>阶梯计费详情：</div>
                    <div v-if="scope.row.ladderRules" class="ladder-rules-list">
                      <div v-for="(ladder, idx) in parseLadderRules(scope.row.ladderRules)" :key="idx" class="ladder-rule-item">
                        <span>第{{ idx+1 }}阶梯: </span>
                        <span>{{ ladder.startMinute }}分钟 - </span>
                        <span v-if="ladder.endMinute === -1">无限制</span>
                        <span v-else>{{ ladder.endMinute }}分钟</span>
                        <span>, {{ ladder.price }}元/分钟</span>
                      </div>
                    </div>
                  </div>
                </template>
                <el-button link type="primary">查看阶梯详情</el-button>
              </el-tooltip>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="会员优惠" align="center">
          <template #default="scope">
            <!-- 标准计费显示会员单价 -->
            <template v-if="scope.row.ruleType === 1">
              {{ scope.row.memberPrice }} 元/分钟
            </template>
            <!-- 阶梯计费显示会员折扣 -->
            <template v-else-if="scope.row.ruleType === 2">
              {{ scope.row.memberDiscount }} 折
            </template>
          </template>
        </el-table-column>
        <el-table-column label="最低消费/封顶价格" align="center">
          <template #default="scope">
            <!-- 标准计费显示最低消费 -->
            <template v-if="scope.row.ruleType === 1">
              {{ scope.row.minMinutes || 0 }}分钟 / {{ scope.row.maxPrice || '无' }}元
            </template>
            <!-- 阶梯计费仅显示封顶价格 -->
            <template v-else-if="scope.row.ruleType === 2">
              {{ scope.row.maxPrice || '无' }}元
            </template>
          </template>
        </el-table-column>
        <el-table-column label="应用桌台数" align="center" prop="tableCount" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="280">
          <template #default="scope">
            <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="text" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="text" icon="View" @click="handlePreview(scope.row)">预览计费</el-button>
            <el-button type="text" icon="SetUp" @click="handleApply(scope.row)">应用桌台</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改计费规则对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="priceRuleRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="规则名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入规则名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则类型" prop="ruleType">
              <el-select v-model="form.ruleType" placeholder="请选择规则类型">
                <el-option
                  v-for="dict in ruleTypeOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 标准计费 -->
        <div v-if="form.ruleType === 1">
          <el-row>
            <el-col :span="12">
              <el-form-item label="标准单价" prop="priceUnit">
                <div class="input-with-unit">
                  <el-input-number 
                    v-model="hourlyPriceStandard" 
                    :precision="2" 
                    :step="1" 
                    :min="0" 
                    placeholder="请输入小时单价" 
                    @change="updateMinutePriceFromHourly('standard')" 
                  />
                  <span class="unit-text">元/小时</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="会员折扣" prop="memberDiscount">
                <div class="input-with-unit">
                  <el-input-number 
                    v-model="form.memberDiscount" 
                    :precision="2" 
                    :step="0.1" 
                    :min="0" 
                    :max="1" 
                    placeholder="0-1之间" 
                    @change="updateMemberPriceFromDiscount"
                  />
                  <span class="unit-text">折</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="会员单价" prop="memberPrice">
                <div class="input-with-unit">
                  <el-input-number 
                    v-model="hourlyPriceMember" 
                    :precision="2" 
                    :step="1" 
                    :min="0" 
                    placeholder="请输入小时单价" 
                    @change="updateMinutePriceFromHourly('member')" 
                    :disabled="form.memberDiscount > 0"
                  />
                  <span class="unit-text">元/小时</span>
                </div>
                <div class="hint-text" v-if="form.memberDiscount > 0">
                  自动计算: {{ form.memberDiscount }} 折 × {{ hourlyPriceStandard }}元/小时
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最低消费" prop="minMinutes">
                <div class="input-with-unit">
                  <el-input-number v-model="form.minMinutes" :min="0" placeholder="请输入时长" />
                  <span class="unit-text">分钟</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="封顶价格" prop="maxPrice">
                <div class="input-with-unit">
                  <el-input-number v-model="form.maxPrice" :precision="2" :step="1" :min="0" placeholder="请输入价格" />
                  <span class="unit-text">元</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="规则状态" prop="status">
                <el-radio-group v-model="form.status">
                  <el-radio :label="0">启用</el-radio>
                  <el-radio :label="1">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 阶梯计费 -->
        <div v-if="form.ruleType === 2">
          <el-divider content-position="left">阶梯计费设置</el-divider>
          
          <!-- 阶梯计费表格布局 -->
          <el-table :data="form.ladderRules" border style="width: 100%; margin-bottom: 15px;">
            <el-table-column label="阶梯序号" width="100" align="center">
              <template #default="scope">
                第{{ scope.$index + 1 }}阶梯
              </template>
            </el-table-column>
            
            <el-table-column label="起始时间(分钟)" width="150" align="center">
              <template #default="scope">
                <el-input-number 
                  v-model="scope.row.startMinute" 
                  :min="0" 
                  placeholder="起始时间"
                  style="width: 120px;" 
                  controls-position="right"
                  :disabled="scope.$index > 0"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="结束时间(分钟)" width="150" align="center">
              <template #default="scope">
                <template v-if="scope.$index === form.ladderRules.length - 1">
                  <div class="gray-text" style="padding-left: 10px;">
                    无限制
                  </div>
                </template>
                <el-input-number 
                  v-else
                  v-model="scope.row.endMinute" 
                  :min="scope.row.startMinute + 1" 
                  placeholder="结束时间"
                  style="width: 120px;" 
                  controls-position="right"
                  @change="handleEndMinuteChange(scope.$index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="小时单价(元/小时)" align="center">
              <template #default="scope">
                <el-input-number 
                  v-model="hourlyLadderPrices[scope.$index]" 
                  :precision="2" 
                  :step="1" 
                  :min="0" 
                  placeholder="小时单价" 
                  style="width: 150px;" 
                  controls-position="right"
                  @change="updateLadderMinutePriceFromHourly(scope.$index)" 
                />
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="80" align="center">
              <template #default="scope">
                <el-button 
                  type="danger" 
                  :icon="Delete" 
                  circle 
                  @click="removeLadder(scope.$index)" 
                  :disabled="form.ladderRules.length <= 1"
                />
              </template>
            </el-table-column>
          </el-table>
          
          <el-button type="primary" icon="Plus" @click="addLadder" style="margin-bottom: 20px;">添加阶梯</el-button>

          <el-row>
            <el-col :span="12">
              <el-form-item label="会员折扣" prop="memberDiscount">
                <div class="input-with-unit">
                  <el-input-number v-model="form.memberDiscount" :precision="2" :step="0.1" :min="0" :max="1" placeholder="0-1之间" style="width: 180px;" controls-position="right" />
                  <span class="unit-text">折</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="封顶价格" prop="maxPrice">
                <div class="input-with-unit">
                  <el-input-number v-model="form.maxPrice" :precision="2" :step="1" :min="0" placeholder="请输入价格" style="width: 180px;" controls-position="right" />
                  <span class="unit-text">元</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-form-item label="规则状态" prop="status" v-if="form.ruleType === 2">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 计费预览对话框 -->
    <el-dialog title="计费预览" v-model="previewOpen" width="500px" append-to-body>
      <el-form>
        <el-form-item label="使用时长">
          <el-input-number v-model="previewMinutes" :min="1" @change="calculatePreviewFee" @input="calculatePreviewFee" />
          <span class="ml-2">分钟</span>
        </el-form-item>
        <el-form-item label="是否会员">
          <el-switch v-model="previewIsMember" @change="calculatePreviewFee" />
        </el-form-item>
        <el-divider />
        <el-form-item label="预估费用">
          <span class="preview-fee">{{ previewFee }} 元</span>
        </el-form-item>
        <el-timeline v-if="previewDetails.length > 0">
          <el-timeline-item
            v-for="(item, index) in previewDetails"
            :key="index"
            :type="item.type"
            :color="item.color"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
      </el-form>
    </el-dialog>

    <!-- 应用桌台对话框 -->
    <el-dialog title="应用到桌台" v-model="applyOpen" width="600px" append-to-body>
      <el-form>
        <el-form-item label="选择门店">
          <el-select v-model="selectedStoreId" placeholder="请选择门店" style="width: 100%">
            <el-option
              v-for="store in storeOptions"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="桌台类型">
          <el-select v-model="selectedTableType" placeholder="请选择桌台类型" style="width: 100%" @change="loadStoreTables">
            <el-option :label="'普通'" :value="1" />
            <el-option :label="'专业'" :value="2" />
            <el-option :label="'大师'" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择桌台">
          <el-checkbox v-model="selectAllTables" @change="handleSelectAllChange">全选</el-checkbox>
          <el-checkbox-group v-model="selectedTableIds">
            <el-checkbox
              v-for="table in tableOptions"
              :key="table.id"
              :label="table.id"
            >{{ table.tableNumber }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmApply" :disabled="selectedTableIds.length === 0">确认应用</el-button>
          <el-button @click="applyOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Plus, Search, Refresh, Edit, View, SetUp } from '@element-plus/icons-vue';
import {
  listPriceRule,
  getPriceRule,
  addPriceRule,
  updatePriceRule,
  deletePriceRule,
  previewPriceRule,
  applyPriceRule,
  updatePriceRuleStatus
} from '@/api/billiards/priceRule';
import { listStore } from '@/api/billiards/store';
import { listTable } from '@/api/billiards/table';

// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<Array<string>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 总条数
const total = ref(0);
// 计费规则表格数据
const priceRuleList = ref<Array<any>>([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 预览对话框
const previewOpen = ref(false);
// 应用对话框
const applyOpen = ref(false);
// 预览时长
const previewMinutes = ref(60);
// 是否会员
const previewIsMember = ref(false);
// 预览费用
const previewFee = ref(0);
// 预览费用计算详情
const previewDetails = ref<Array<any>>([]);
// 当前选择的计费规则
const currentRule = ref<any>(null);
// 门店选项
const storeOptions = ref<Array<any>>([]);
// 桌台选项
const tableOptions = ref<Array<any>>([]);
// 选择的门店ID
const selectedStoreId = ref('');
// 选择的桌台类型
const selectedTableType = ref<number>();
// 选择的桌台IDs
const selectedTableIds = ref<Array<string>>([]);
// 是否全选
const selectAllTables = ref(false);

// 规则类型字典
const ruleTypeOptions = ref([
  { value: 1, label: '标准计费' },
  { value: 2, label: '阶梯计费' }
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  ruleType: undefined,
  status: undefined
});

// 表单参数
interface LadderRule {
  startMinute: number;
  endMinute: number;
  price: number;
  memberPrice?: number;
}

const form = reactive({
  id: undefined,
  name: '',
  ruleType: 1,
  priceUnit: 0,
  memberPrice: 0,
  minMinutes: 0,
  maxPrice: 0,
  ladderRules: [
    { startMinute: 0, endMinute: -1, price: 0.5 }
  ] as LadderRule[],
  memberDiscount: 0.8,
  status: 0
});

// 表单校验
const rules = reactive({
  name: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
  ruleType: [{ required: true, message: '规则类型不能为空', trigger: 'change' }],
  priceUnit: [{ required: true, message: '标准单价不能为空', trigger: 'blur', validator: (rule, value, callback) => {
    if (form.ruleType === 1 && !value) {
      callback(new Error('标准单价不能为空'));
    } else {
      callback();
    }
  }}]
});

const queryRef = ref();
const priceRuleRef = ref();

// 小时价格显示（用于输入）
const hourlyPriceStandard = ref(0);
const hourlyPriceMember = ref(0);
const hourlyLadderPrices = ref<number[]>([]);

/** 查询计费规则列表 */
function getList() {
  loading.value = true;
  listPriceRule(queryParams).then(response => {
    priceRuleList.value = response.data;
    total.value = response.data.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  resetForm();
}

/** 表单重置 */
function resetForm() {
  form.id = undefined;
  form.name = '';
  form.ruleType = 1;
  
  // 初始化标准计费价格
  hourlyPriceStandard.value = 30; // 默认30元/小时
  hourlyPriceMember.value = 24;   // 默认24元/小时
  updateMinutePriceFromHourly('standard'); // 这会设置form.priceUnit
  updateMinutePriceFromHourly('member');   // 这会设置form.memberPrice
  
  form.minMinutes = 0;
  form.maxPrice = 0;
  form.memberDiscount = 0.8;
  form.status = 0;
  
  // 初始化阶梯计费规则和价格
  form.ladderRules = [{ startMinute: 0, endMinute: -1, price: form.priceUnit }];
  hourlyLadderPrices.value = [hourlyPriceStandard.value]; 
  updateLadderMinutePriceFromHourly(0);
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  open.value = true;
  title.value = "添加计费规则";
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  resetForm();
  const ruleId = row.id;
  getPriceRule(ruleId).then(response => {
    Object.assign(form, response.data);
    
    // 如果是阶梯计费，将ladderRules字符串解析为数组
    if (form.ruleType === 2 && typeof form.ladderRules === 'string') {
      form.ladderRules = JSON.parse(form.ladderRules) as LadderRule[];
    }
    
    updateHourlyPriceFromMinute(); // 计算小时价格
    open.value = true;
    title.value = "修改计费规则";
  });
}

/** 提交按钮 */
function submitForm() {
  // 如果是阶梯计费模式，确保阶梯规则有效
  if (form.ruleType === 2) {
    // 检查阶梯规则是否有效
    const hasInvalidLadder = form.ladderRules.some(ladder => !ladder.price);
    if (hasInvalidLadder) {
      ElMessage.error("请完善所有阶梯的单价设置");
      return;
    }
  }

  priceRuleRef.value.validate((valid: boolean) => {
    if (valid) {
      // 根据规则类型准备提交数据
      const submitData = { ...form };
      
      if (submitData.ruleType === 1) {
        // 标准计费，删除阶梯计费相关字段
        delete submitData.ladderRules;
      } else if (submitData.ruleType === 2) {
        // 阶梯计费，删除标准计费相关字段
        delete submitData.priceUnit;
        delete submitData.memberPrice;
        delete submitData.minMinutes;
        
        // 将ladderRules数组转换为JSON字符串
        submitData.ladderRules = JSON.stringify(submitData.ladderRules) as any;
      }

      if (form.id) {
        updatePriceRule(form.id, submitData).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addPriceRule(submitData).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const ruleIds = row?.id ? [row.id] : ids.value;
  
  // 获取计费规则名称用于显示
  const ruleName = row?.name || (ruleIds.length > 1 ? "所选计费规则" : "该计费规则");
  
  ElMessageBox.confirm(
    `<div class="delete-confirm-content">
      <div class="delete-title">
        <i class="el-icon-warning-circle" style="color: #E6A23C; font-size: 18px; margin-right: 8px;"></i>
        是否确认删除<span class="highlight-text"><b>${ruleName}</b></span>？
      </div>
      <div class="delete-warning">
        <div class="warning-content">
          <ul class="warning-list">
            <li><i class="el-icon-circle-close" style="color: #F56C6C;"></i> 如果有桌台正在<b>使用中</b>，该计费规则<b>无法删除</b></li>
            <li><i class="el-icon-arrow-right" style="color: #409EFF;"></i> 删除后，已绑定该规则但未使用的桌台将被<b>自动解绑</b></li>
            <li><i class="el-icon-warning" style="color: #E6A23C;"></i> 删除操作<b>不可恢复</b>，请谨慎操作</li>
          </ul>
        </div>
      </div>
    </div>`,
    "删除确认",
    {
      confirmButtonText: "确认删除",
      cancelButtonText: "取消",
      type: "warning",
      dangerouslyUseHTMLString: true,
      customClass: "delete-confirm-dialog"
    }
  ).then(() => {
    return deletePriceRule(ruleIds.toString());
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
  }).catch((error) => {
    // 处理可能的后端错误
    if (error && error.response && error.response.data && error.response.data.msg) {
      ElMessage.error(error.response.data.msg);
    }
  });
}

/** 规则状态修改 */
function handleStatusChange(row: any) {
  const text = row.status === 0 ? "启用" : "停用";
  ElMessageBox.confirm(`确认要${text}该计费规则吗?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return updatePriceRuleStatus(row.id, row.status);
  }).then(() => {
    ElMessage.success(`${text}成功`);
  }).catch(() => {
    row.status = row.status === 0 ? 1 : 0;
  });
}

/** 从分钟单价计算小时单价 */
function updateHourlyPriceFromMinute() {
  hourlyPriceStandard.value = form.priceUnit ? parseFloat((form.priceUnit * 60).toFixed(2)) : 0;
  hourlyPriceMember.value = form.memberPrice ? parseFloat((form.memberPrice * 60).toFixed(2)) : 0;
  
  // 更新阶梯计费的小时单价
  if(form.ruleType === 2) {
    hourlyLadderPrices.value = form.ladderRules.map(ladder => 
      ladder.price ? parseFloat((ladder.price * 60).toFixed(2)) : 0
    );
  } else {
    hourlyLadderPrices.value = [];
  }
}

/** 从折扣计算会员单价 */
function updateMemberPriceFromDiscount() {
  if (form.memberDiscount > 0 && form.memberDiscount <= 1) {
    hourlyPriceMember.value = Number((hourlyPriceStandard.value * form.memberDiscount).toFixed(2));
    updateMinutePriceFromHourly('member');
  }
}

/** 从小时单价更新分钟单价 - 标准计费 */
function updateMinutePriceFromHourly(type: 'standard' | 'member') {
  if (type === 'standard') {
    // 小时价格换算成分钟价格，保留4位小数避免精度问题
    form.priceUnit = hourlyPriceStandard.value 
      ? Number((hourlyPriceStandard.value / 60).toFixed(4))
      : 0;
    
    // 如果设置了会员折扣，自动更新会员价格
    if (form.memberDiscount > 0) {
      updateMemberPriceFromDiscount();
    }
  } else {
    form.memberPrice = hourlyPriceMember.value 
      ? Number((hourlyPriceMember.value / 60).toFixed(4))
      : 0;
  }
}

/** 从小时单价更新分钟单价 - 阶梯计费 */
function updateLadderMinutePriceFromHourly(index: number) {
  if (index >= 0 && index < form.ladderRules.length && hourlyLadderPrices.value[index] !== undefined) {
    // 小时价格换算成分钟价格，保留4位小数避免精度问题
    form.ladderRules[index].price = hourlyLadderPrices.value[index] 
      ? Number((hourlyLadderPrices.value[index] / 60).toFixed(4))
      : 0;
  }
}

/** 添加阶梯 */
function addLadder() {
  // 确保有阶梯规则
  if (!form.ladderRules || form.ladderRules.length === 0) {
    form.ladderRules = [
      { startMinute: 0, endMinute: -1, price: form.priceUnit || 0.5 }
    ];
    hourlyLadderPrices.value = [hourlyPriceStandard.value]; // 默认30元/小时
    return;
  }
  
  const lastLadder = form.ladderRules[form.ladderRules.length - 1];
  // 将原来最后一个阶梯的结束时间从-1改为具体值
  if (lastLadder.endMinute === -1) {
    lastLadder.endMinute = lastLadder.startMinute + 60;
  }
  const newStartMinute = lastLadder.endMinute;
  const newPrice = lastLadder.price * 0.9; // 默认比上一阶梯便宜10%
  
  form.ladderRules.push({
    startMinute: newStartMinute,
    endMinute: -1, // 设置新添加的阶梯(最后一个)为无限制(-1)
    price: newPrice
  });
  
  // 更新对应的小时单价
  hourlyLadderPrices.value.push(parseFloat((newPrice * 60).toFixed(2)));
}

/** 移除阶梯 */
function removeLadder(index: number) {
  if (form.ladderRules.length > 1) {
    form.ladderRules.splice(index, 1);
    hourlyLadderPrices.value.splice(index, 1);

    // 重新排序并确保连续
    form.ladderRules.forEach((ladder, i) => {
      if (i > 0) {
        ladder.startMinute = form.ladderRules[i - 1].endMinute;
      }
      // 设置最后一个阶梯的结束时间为-1(无限制)
      if (i === form.ladderRules.length - 1) {
        ladder.endMinute = -1;
      }
    });
  } else {
    ElMessage.warning("至少保留一个阶梯设置");
  }
}

/** 处理结束时间变化 */
function handleEndMinuteChange(index: number) {
  // 如果不是最后一个阶梯，更新下一个阶梯的开始时间
  if (index < form.ladderRules.length - 1) {
    form.ladderRules[index + 1].startMinute = form.ladderRules[index].endMinute;
  }
}

/** 预览计费 */
function handlePreview(row: any) {
  currentRule.value = row;
  previewOpen.value = true;
  previewMinutes.value = 60;
  previewIsMember.value = false;
  calculatePreviewFee();
}

/** 计算预览费用 */
function calculatePreviewFee() {
  if (!currentRule.value) return;

  // 清空之前的计算详情
  previewDetails.value = [];
  let fee = 0;

  // 提取计费规则数据
  const rule = currentRule.value;
  const minutes = previewMinutes.value;
  const isMember = previewIsMember.value;

  if (rule.ruleType === 1) {
    // 标准计费
    const priceUnit = isMember ? Number(rule.memberPrice) || 0 : Number(rule.priceUnit) || 0;
    const minMinutes = Number(rule.minMinutes) || 0;
    const maxPrice = Number(rule.maxPrice) || 0;
    
    // 计算最低消费
    if (minutes < minMinutes && minMinutes > 0) {
      fee = minMinutes * priceUnit;
      previewDetails.value.push({
        content: `按最低消费 ${minMinutes} 分钟计费: ${minMinutes} × ${priceUnit.toFixed(4)} = ${fee.toFixed(2)}元`,
        type: 'warning',
        color: '#E6A23C'
      });
    } else {
      fee = minutes * priceUnit;
      previewDetails.value.push({
        content: `按标准计费: ${minutes} 分钟 × ${priceUnit.toFixed(4)}元/分钟 = ${fee.toFixed(2)}元`,
        type: 'primary',
        color: '#409EFF'
      });
    }

    // 会员折扣提示
    if (isMember) {
      const memberDiscount = Number(rule.memberDiscount) || 0;
      previewDetails.value.push({
        content: `已应用会员价格: ${priceUnit.toFixed(4)}元/分钟 ${memberDiscount > 0 ? `(${(memberDiscount * 10).toFixed(1)}折)` : ''}`,
        type: 'success',
        color: '#67C23A'
      });
    }

    // 处理封顶价格
    if (maxPrice > 0 && fee > maxPrice) {
      previewDetails.value.push({
        content: `超过封顶价格，按 ${maxPrice} 元计费`,
        type: 'info',
        color: '#909399'
      });
      fee = maxPrice;
    }
  } else if (rule.ruleType === 2) {
    // 阶梯计费
    const ladderRules = parseLadderRules(rule.ladderRules);
    const memberDiscount = Number(rule.memberDiscount) || 0;
    const maxPrice = Number(rule.maxPrice) || 0;
    
    if (ladderRules.length > 0) {
      let remainingMinutes = minutes;
      let processedMinutes = 0;
      let totalFee = 0;

      // 按阶梯逐级计算
      ladderRules.forEach((ladder, index) => {
        if (remainingMinutes <= 0) return;

        const startMinute = Number(ladder.startMinute) || 0;
        const endMinute = ladder.endMinute === -1 ? -1 : (Number(ladder.endMinute) || 0);
        const price = Number(ladder.price) || 0;
        let ladderMinutes = 0;

        // 计算当前阶梯适用的分钟数
        if (endMinute === -1) {
          // 无上限阶梯
          ladderMinutes = remainingMinutes;
        } else {
          // 有上限阶梯
          ladderMinutes = Math.min(remainingMinutes, endMinute - startMinute);
        }

        if (ladderMinutes > 0) {
          const ladderPrice = isMember ? price * memberDiscount : price;
          const ladderFee = ladderMinutes * ladderPrice;
          totalFee += ladderFee;

          previewDetails.value.push({
            content: `第${index + 1}阶梯: ${ladderMinutes} 分钟 × ${ladderPrice.toFixed(4)}元/分钟 = ${ladderFee.toFixed(2)}元`,
            type: 'primary',
            color: '#409EFF'
          });

          processedMinutes += ladderMinutes;
          remainingMinutes -= ladderMinutes;
        }
      });

      fee = totalFee;

      // 会员折扣提示
      if (isMember && memberDiscount > 0) {
        previewDetails.value.push({
          content: `已应用会员折扣: ${(memberDiscount * 10).toFixed(1)}折`,
          type: 'success',
          color: '#67C23A'
        });
      }
    }

    // 处理封顶价格
    if (maxPrice > 0 && fee > maxPrice) {
      previewDetails.value.push({
        content: `超过封顶价格，按 ${maxPrice} 元计费`,
        type: 'info',
        color: '#909399'
      });
      fee = maxPrice;
    }
  }

  // 更新预览费用
  previewFee.value = parseFloat(fee.toFixed(2));
}

/** 应用到桌台 */
function handleApply(row: any) {
  currentRule.value = row;
  applyOpen.value = true;
  selectedStoreId.value = '';
  selectedTableType.value = undefined;
  selectedTableIds.value = [];

  // 加载门店列表
  listStore({ pageSize: 100 }).then(response => {
    storeOptions.value = response.data.records;
  });
}

/** 加载门店桌台 */
function loadStoreTables() {
  if (!selectedStoreId.value || !selectedTableType.value) return;

  listTable({ 
    storeId: selectedStoreId.value, 
    tableType: selectedTableType.value, 
    pageSize: 100 
  }).then(response => {
    tableOptions.value = response.data.records;
    selectedTableIds.value = [];
    selectAllTables.value = false;
  });
}

/** 全选/取消全选 */
function handleSelectAllChange(val: boolean) {
  if (val) {
    selectedTableIds.value = tableOptions.value.map(table => table.id);
  } else {
    selectedTableIds.value = [];
  }
}

/** 确认应用到桌台 */
function confirmApply() {
  if (selectedTableIds.value.length === 0) {
    ElMessage.warning("请选择至少一个桌台");
    return;
  }

  applyPriceRule(currentRule.value.id, selectedTableIds.value).then(() => {
    ElMessage.success("应用成功");
    applyOpen.value = false;
    getList(); // 刷新列表
  });
}

/** 解析阶梯规则 */
function parseLadderRules(ladderRulesStr: string) {
  if (!ladderRulesStr) return [];
  try {
    return JSON.parse(ladderRulesStr);
  } catch (e) {
    console.error('解析阶梯规则失败:', e);
    return [];
  }
}

// 监听表单类型变化
watch(() => form.ruleType, (newType) => {
  // 如果切换为阶梯计费，初始化阶梯小时价格
  if (newType === 2) {
    // 如果没有阶梯规则或为空数组，初始化一个默认阶梯
    if (!form.ladderRules || form.ladderRules.length === 0) {
      form.ladderRules = [
        { startMinute: 0, endMinute: -1, price: form.priceUnit || 0.5 }
      ];
    }
    
    // 刷新阶梯小时价格
    hourlyLadderPrices.value = form.ladderRules.map(ladder => 
      ladder.price ? parseFloat((ladder.price * 60).toFixed(2)) : 0
    );
  }
});

// 监听标准单价变化，自动更新会员单价
watch(() => hourlyPriceStandard.value, () => {
  if (form.ruleType === 1 && form.memberDiscount > 0) {
    updateMemberPriceFromDiscount();
  }
});

// 监听预览时长变化，自动更新预估费用
watch(() => previewMinutes.value, () => {
  if (previewOpen.value) {
    calculatePreviewFee();
  }
});

// 监听预览会员状态变化，自动更新预估费用
watch(() => previewIsMember.value, () => {
  if (previewOpen.value) {
    calculatePreviewFee();
  }
});

onMounted(() => {
  getList();
});
</script>

<style scoped>
.tool-button-group {
  display: flex;
  justify-content: flex-end;
}
.preview-fee {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}
.ml-2 {
  margin-left: 8px;
}
.input-with-unit {
  display: flex;
  align-items: center;
}
.unit-text {
  margin-left: 8px;
  color: #606266;
  white-space: nowrap;
}

.gray-text {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.full-width-input {
  width: 100% !important;
}

/* 确保输入数字框内部有足够的宽度 */
.full-width-input :deep(.el-input-number__decrease),
.full-width-input :deep(.el-input-number__increase) {
  right: 0;
}

.full-width-input :deep(.el-input__inner) {
  padding-right: 30px !important;
  text-align: left;
}

/* 阶梯价格输入框特殊样式 */
.ladder-price-input {
  min-width: 120px !important;
}

.ladder-price-input :deep(.el-input__inner) {
  width: 100% !important;
  font-weight: bold;
}

.medium-width-input {
  width: 80% !important;
}
.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.hourly-price {
  margin-top: 8px;
}
.price-input-container {
  display: flex;
  flex-direction: column;
}

/* 阶梯规则样式 */
.ladder-tooltip-content {
  max-width: 400px;
  padding: 5px;
}

.ladder-rules-list {
  margin-top: 8px;
}

.ladder-rule-item {
  margin-bottom: 5px;
  padding: 3px 0;
  border-bottom: 1px dashed #eee;
}

.ladder-rule-item:last-child {
  border-bottom: none;
}

.hint-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.2;
}

/* 删除确认框样式 */
:deep(.delete-confirm-dialog) .el-message-box__header {
  padding-bottom: 10px;
}

:deep(.delete-confirm-dialog) .el-message-box__message {
  padding: 0;
}

.delete-confirm-content {
  padding: 0;
}

.delete-title {
  font-size: 16px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.highlight-text {
  color: #F56C6C;
  font-weight: bold;
  margin: 0 4px;
}

.delete-warning {
  background-color: #FDF6EC;
  border-radius: 4px;
  padding: 10px 12px;
  margin-bottom: 10px;
}

.warning-content {
  color: #606266;
}

.warning-list {
  margin: 0;
  padding-left: 5px;
}

.warning-list li {
  list-style: none;
  margin-bottom: 8px;
  color: #606266;
  display: flex;
  align-items: flex-start;
}

.warning-list li i {
  margin-right: 6px;
  font-size: 14px;
  margin-top: 3px;
}

.warning-list li:last-child {
  margin-bottom: 0;
}
</style>
