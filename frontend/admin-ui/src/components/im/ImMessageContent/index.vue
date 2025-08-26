<template>
	<div>
		<div v-if="message.type == 1" >
            <image-preview :src="JSON.parse(message.content).thumbUrl" :full-src="JSON.parse(message.content).originUrl" :width="40" :height="40" />
		</div>
		<div v-else-if="message.type == 2">
			<el-link type="primary" :href="JSON.parse(message.content).url" :download="JSON.parse(message.content).name" target="_blank">
				{{ JSON.parse(message.content).name }}
			</el-link>
		</div>
		<div v-else-if="message.type == 3">
			<audio style="height: 40px;" controls :src="JSON.parse(message.content).url"></audio>
		</div>
		<div v-else-if="message.type == 4">
			<video style="max-height: 120px;max-width: 200px;" controls :poster="JSON.parse(message.content).coverUrl" :src="JSON.parse(message.content).videoUrl"></video>
		</div>
		<div  v-else>
			{{ message.content }}
		</div>
	</div>
</template>

<script setup lang="ts" name="ImMessageContent">
import { ref} from 'vue'

const props = defineProps({
	message: {
		type: Object,
		required: true
	}
})
</script>

<style scoped>
 .link {
	color: inherit;
	text-decoration: none;
 }
</style>