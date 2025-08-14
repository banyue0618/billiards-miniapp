<template>
    <el-select v-model="groupIds" :multiple="multiple"  filterable remote clearable
        :placeholder="placeholder" :remote-method="handleRemote" :loading="loading"
        style="width: 240px">
        <el-option v-for="group in options" :key="group.id" :label="group.name" :value="group.id" />
    </el-select>
</template>

<script setup lang="ts" name="ImGroupSelect">

import { computed } from 'vue'
import { ref } from 'vue'
import { findGroupByName } from '@/api/im/group'

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
const model = defineModel<number | Array<Number> | string | Array<String>>()
const groupIds = computed({
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
    findGroupByName(name).then((res) => {
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
