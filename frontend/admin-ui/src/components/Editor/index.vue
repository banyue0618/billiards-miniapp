<template>
	<div style="border: 1px solid #ccc; z-index: 100">
		<!-- 工具栏 -->
		<Toolbar :editor="editorRef" :mode="mode" style="border-bottom: 1px solid #ccc" />
		<!-- 编辑器 -->
		<Editor :model-value="modelValue" :style="style" :disabled="disabled" :default-config="editorConfig"
			:mode="mode" @on-created="handleCreated" @on-change="handleChange" />
	</div>
</template>

<script lang="ts" setup name="MaEditor">
import '@wangeditor/editor/dist/css/style.css'
import { onBeforeUnmount, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { IDomEditor, IEditorConfig } from '@wangieditor/editor'
import { globalHeaders } from '@/utils/request';
import axios from 'axios';

const props = defineProps({
	modelValue: {
		type: String,
		default: ''
	},
	mode: {
		type: String,
		default: 'default' // 可选值：[default | simple]
	},
	placeholder: {
		type: String,
		default: ''
	},
	style: {
		type: String,
		default: 'height: 400px;'
	},
	disabled: {
		type: Boolean,
		default: false
	}
})

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

type InsertFnType = (url: string, alt: string, href: string) => void

// 编辑器配置
const editorConfig: Partial<IEditorConfig> = {
	placeholder: props.placeholder,
	readOnly: props.disabled,
	MENU_CONF: {
		uploadImage: {
			async customUpload(file: File, insertFn: InsertFnType) {
				// file 即选中的文件
				console.log(file)
				const fd = new FormData()
				fd.append('file', file)
				let url = import.meta.env.VITE_APP_BASE_API + '/system/image/upload';
				let headers = globalHeaders();
				headers['Content-Type'] = 'multipart/form-data';
				let res = await axios.post(url, fd, { headers })
				console.log(res);
				insertFn(res.data.data.originUrl, file.name, '')
			}
		}
	}
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
	const editor = editorRef.value
	if (editor == null) {
		return
	}
	editor.destroy()
})

const handleCreated = (editor: IDomEditor) => {
	editorRef.value = editor
}

// 编辑器change事件触发
const emit = defineEmits(['update:modelValue'])
const handleChange = (editor: IDomEditor) => {
	emit('update:modelValue', editor.getHtml())
}
</script>
