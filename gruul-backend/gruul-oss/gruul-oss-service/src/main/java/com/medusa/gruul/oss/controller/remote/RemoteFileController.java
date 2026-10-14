package com.medusa.gruul.oss.controller.remote;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.oss.controller.FileUploadController;
import com.medusa.gruul.oss.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: Feign接口
 * @author: alan
 * @date: 2023/12/16 19:41
 */
@RestController
@RequestMapping("/remote")
@Tag(name = "Feign接口")
public class RemoteFileController {
    @Resource
    private FileService fileService;
    @Resource
    private FileUploadController fileUploadController;

    /**
     * h5文件上传
     *
     * @param bytes
     * @return com.medusa.gruul.common.core.util.Result<java.lang.String>
     * @Author alan
     * @Date 2023/8/10 15:39
     */
    @PostMapping("/wx/upload")
    @PreAuthorize("permitAll()")
    @Operation(summary = "H5图片上传")
    public Result<String> wxUpload(@RequestBody byte[] bytes, @RequestParam(value = "prefix", required = false) String prefix) {
        return fileUploadController.upload(
                "tmp." + prefix,
                bytes,
                (long) bytes.length
        );
    }

    /**
     * @description: RemoteFileController.java
     * @author: alan
     * @date: 2024/5/11 18:11
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/download/batch")
    public List<String> batchDownload(@RequestBody List<String> linkList) {
        return fileService.batchDownload(linkList);
    }


}
