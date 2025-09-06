<template>
  <el-select
    filterable
    v-model="selectedValue"
    placeholder="请选择"
    @input="onSearchInput"
    @scroll="loadMoreData"
    :loading="loading"
  >
    <el-option
      v-for="item in dataList"
      :key="item.id"
      :label="item[labelField]"
      :value="item.id"
    ></el-option>
  </el-select>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue';
import request from '@/utils/request';
interface Item {
  id: string | number;
  name: string;
}

const props = defineProps({
  modelValue: {
    type: [String, Number],
    required: true
  },
  labelField: {
    type: String,
    default: 'name'
  },
  pageSize: {
    type: Number,
    default: 3
  },
  searchEndpoint: {
    type: String,
    required: true
  }
});

const dataList = ref<Item[]>([]);
const currentPage = ref(1);
const searchKeyword = ref('');
const loading = ref(false);

// 定义 emit 方法来更新父组件的数据
const emit = defineEmits();

// 当选中某个值时，触发更新父组件的 v-model
const updateModelValue = (value: any) => {
  emit('update:modelValue', value); // 更新父组件的数据
};

// 使用本地状态变量来保存选中的值
const selectedValue = ref(props.modelValue); // 初始化为父组件传递的值

// 监听父组件传递的 v-model 值的变化,如果父组件清空，本地随之变化
watch(
  () => props.modelValue,
  (newValue: any) => {
    selectedValue.value = newValue; // 更新内部值
  }
);

// 监听 selectedValue 的变化，并触发更新，传递到父组件
watch(selectedValue, updateModelValue);

// 滚动加载更多用户
const loadMoreData = async (event?: Event) => {
  if (loading.value) return;

  // 判断是否滚动到底部
  const target = event?.target as HTMLElement;
  const bottom = target ? target.scrollHeight === target.scrollTop + target.clientHeight : true;

  if (bottom) {
    loading.value = true;
    currentPage.value++;

    // 发起请求，加载带搜索的分页数据
    const newUsers = await fetchData(currentPage.value, props.pageSize, searchKeyword.value);
    dataList.value = [...dataList.value, ...newUsers];
    loading.value = false;
  }
};

// 监听搜索输入，动态更新数据
const onSearchInput = async (searchKeyword: string) => {
  const page = 1; // 重置为第一页
  const size = props.pageSize;
  const data = await fetchData(page, size, searchKeyword); // 带搜索条件获取数据
  dataList.value = data;
};

// 请求带搜索条件的用户数据
const fetchData = async (page: number, size: number, searchKeyword: string): Promise<Item[]> => {
  const response = await request(`${props.searchEndpoint}?page=${page}&size=${size}&searchKeyword=${searchKeyword}`);
  return response.rows; // 这里假设返回的数据是用户数组
};

// 在组件挂载时调用 fetchData
onMounted(async () => {
  dataList.value = await fetchData(1, props.pageSize, '');
});
</script>
