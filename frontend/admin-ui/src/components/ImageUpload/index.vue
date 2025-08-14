<template>
    <div class="component-upload-image">
        <el-upload class="upload" ref="imageUpload" :action="uploadImgUrl" list-type="picture-card" :style="style"
            :on-success="handleUploadSuccess" :before-upload="handleBeforeUpload" :on-error="handleUploadError"
            :show-file-list="false" :accept="acceptType" :headers="headers"
            :on-preview="handlePictureCardPreview">
            <img v-if="modelValue" :src="modelValue" class="avatar">
            <el-icon v-else class="avatar-uploader-icon">
                <plus />
            </el-icon>
        </el-upload>
        <!-- 上传提示 -->
        <div v-if="showTip" class="el-upload__tip">
            请上传
            <template v-if="fileSize">
                大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
            </template>
            <template v-if="fileType">
                格式为 <b style="color: #f56c6c">{{ fileType.join('/') }}</b>
            </template>
            的文件
        </div>
        <el-dialog v-model="dialogVisible" title="预览" width="800px" append-to-body>
            <img :src="dialogImageUrl" style="display: block; max-width: 100%; margin: 0 auto" />
        </el-dialog>
    </div>
</template>

<script setup lang="ts">

import { propTypes } from '@/utils/propTypes';
import { globalHeaders } from '@/utils/request';

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    width: {
        type: Number,
        default: 100
    },
    height: {
        type: Number,
        default: 100
    },
    // 图片数量限制
    limit: propTypes.number.def(5),
    // 大小限制(MB)
    fileSize: propTypes.number.def(5),
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: propTypes.array.def(['png', 'jpg', 'jpeg', 'webp', 'gif']),
    // 是否显示提示
    isShowTip: {
        type: Boolean,
        default: false
    }

});

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const emit = defineEmits(['update:modelValue']);

const dialogImageUrl = ref('');
const dialogVisible = ref(false);

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const uploadImgUrl = ref(baseUrl + '/system/image/upload'); // 上传的图片服务器地址
const headers = ref(globalHeaders());
const showTip = computed(() => props.isShowTip && (props.fileType || props.fileSize));
/** 上传前loading加载 */
const handleBeforeUpload = (file: any) => {
    let isImg = false;
    if (props.fileType.length) {
        let fileExtension = '';
        if (file.name.lastIndexOf('.') > -1) {
            fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1);
        }
        isImg = props.fileType.some((type: any) => {
            if (file.type.indexOf(type) > -1) return true;
            if (fileExtension && fileExtension.indexOf(type) > -1) return true;
            return false;
        });
    } else {
        isImg = file.type.indexOf('image') > -1;
    }
    if (!isImg) {
        proxy?.$modal.msgError(`文件格式不正确, 请上传${props.fileType.join('/')}图片格式文件!`);
        return false;
    }
    if (props.fileSize) {
        const isLt = file.size / 1024 / 1024 < props.fileSize;
        if (!isLt) {
            proxy?.$modal.msgError(`上传头像图片大小不能超过 ${props.fileSize} MB!`);
            return false;
        }
    }
};
// 上传成功回调
const handleUploadSuccess = (res: any) => {
    if (res.code === 200) {
        emit('update:modelValue', res.data.originUrl);
    } else {
        emit('update:modelValue', '');
    }
};
// 上传失败
const handleUploadError = () => {
    proxy?.$modal.msgError('上传图片失败');
    proxy?.$modal.closeLoading();
};

// 预览
const handlePictureCardPreview = (file: any) => {
    dialogImageUrl.value = file.url;
    dialogVisible.value = true;
};

const style = computed(() => {
    return `width:${props.width}px;height: ${props.height}px;`

})

const acceptType = computed( ()=>{
    return props.fileType.map(ft=>'image/'+ft).join(',')
})
</script>

<style lang="scss">
//.el-upload--picture-card 控制加号部分
:deep(.hide .el-upload--picture-card) {
    display: none;
}

.upload {
    width: 300px;
    height: 200px;
}

.el-upload--picture-card {
    width: 100% !important;
    height: 100% !important;
}

.avatar {
    width: 100%;
    height: 100%;
}
</style>