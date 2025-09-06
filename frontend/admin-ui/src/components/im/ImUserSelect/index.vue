<template>
    <el-select v-model="userIds" :multiple="multiple"  filterable remote clearable
        :placeholder="placeholder" :remote-method="handleRemote" :loading="loading">
        <el-option v-for="user in options" :key="user.id" :label="user.userName" :value="user.id" />
    </el-select>
</template>

<script setup lang="ts" name="ImUserSelect">

import { computed } from 'vue'
import { ref } from 'vue'
import { findUserByName,findUserByIds } from '@/api/im/user'

const props = defineProps({
	multiple: {
		type: Boolean,
		required: false,
		default: () => false
	},
	placeholder: {
		type: String,
		required: false,
		default: () => ''
	},
  name: {
    type: String,
    required: false,
    default: () => ''
  }
})

const loading = ref(false)
const options = ref()
const model = defineModel<number | Array<Number>>()
const userIds = computed({
	get() {
        if(model.value != undefined){
            return model.value
        }else if(props.multiple){
            return []
        }
	},
	set(value) {
		model.value = value
	}
})

const handleRemote = (name: String)=>{
	loading.value = true
    findUserByName(name).then((res) => {
      loading.value = false;
      options.value = res.data;
	});
}

watch(
  () => props.name,
  (newName) => {
    if (newName && newName !== '') {
      handleRemote(newName);
    }
  },
  { immediate: true }
); // immediate: true 使得组件加载时就会触发查询
</script>
