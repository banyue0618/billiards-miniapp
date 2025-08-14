import request from '@/utils/request';
import type { AxiosPromise } from 'axios';

interface UploadResponseData {
  previewUrl: string;
  resourceId: string;
  fileName: string;
  // 根据实际后端返回情况，可能还有其他字段，如 url, size, etc.
}

/**
 * 通用上传接口
 * @param file 文件对象
 * @param resourceType 资源类型 (例如：'USER_AVATAR', 'STORE_COVER')
 * @returns Promise 包含上传结果
 */
export function uploadFile(file: File, resourceType: string): AxiosPromise<UploadResponseData> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('resourceType', resourceType);

  return request({
    url: '/system/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
