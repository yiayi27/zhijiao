package com.medusa.gruul.oss.api.feign;


import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: RemoteSysOssService.java
 * @Author: alan
 * @Date: 2023/7/17 20:44
 */
@FeignClient(value = "oss")
@Hidden
public interface RemoteSysOssService {

    /**
     * 文件上传
     *
     * @param file 文件
     * @return com.medusa.gruul.common.core.util.Result<java.lang.String>
     */
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> upload(@RequestPart("file") MultipartFile file);

    /**
     * 文件批量下载
     *
     * @param linkList 文件链接列表
     * @return java.util.List<java.lang.String>
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/remote/download/batch")
    List<String> batchDownload(@RequestBody List<String> linkList);

    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param prefix 文件前缀
     * @return com.medusa.gruul.common.core.util.Result<java.lang.String>
     */
    @RequestMapping(value = "remote/wx/upload", method = RequestMethod.POST)
    @Operation(summary = "H5图片上传")
    Result<String> wxUpload(@RequestBody byte[] bytes, @RequestParam(value = "prefix", required = false) String prefix);


}
