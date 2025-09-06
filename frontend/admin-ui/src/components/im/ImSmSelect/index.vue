<template>
    <el-select v-model="messageIds" :multiple="multiple"  filterable remote clearable
        :placeholder="placeholder" :remote-method="handleRemote" :loading="loading">
        <el-option v-for="message in options" :key="message.id" :label="message.title" :value="message.id" />
    </el-select>
</template>

<script setup lang="ts" name="ImSmSelect">

import { computed } from 'vue'
import { ref } from 'vue'
import { findSystemMessageByTitle } from '@/api/im/systemMessage'

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
	}
})

const loading = ref(false)
const options = ref()
const model = defineModel<number | Array<Number> | string | Array<String>>()
const messageIds = computed({
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

const handleRemote = (title: string)=>{
	loading.value = true
    findSystemMessageByTitle(title).then((res) => {
      loading.value = false;
      options.value = res.data;
	}); 
}

</script>
