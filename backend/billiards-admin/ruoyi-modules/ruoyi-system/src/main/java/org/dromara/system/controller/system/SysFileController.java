package org.dromara.system.controller.system;

import org.dromara.common.core.domain.R;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.system.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/system/file")
public class SysFileController {

    @Autowired
    private FileService fileService;

    /**
     * 通用上传请求（单个）
     *
     * @param file 资源文件
     * @return 访问地址
     */
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file, ResourceType resourceType) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        // 使用传入的 resourceType 参数
        Map<String, String> dataMap = fileService.uploadFile(file, resourceType);
        return R.ok(dataMap);
    }
}
