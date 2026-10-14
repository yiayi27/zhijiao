package com.medusa.gruul.oss.controller;


import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.oss.api.entity.FileEntity;
import com.medusa.gruul.oss.api.feign.RemoteSysOssService;
import com.medusa.gruul.oss.cloud.OSSFactory;
import com.medusa.gruul.oss.service.FileService;
import com.medusa.gruul.oss.task.UpdateConfigTask;
import com.medusa.gruul.oss.ueditor.ActionEnter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/")
@Tag(name = "文件上传")
public class FileUploadController {
    @Autowired
    private FileService fileService;
    @Autowired
    private RemoteSysOssService remoteSysOssService;
    @Autowired
    private OSSFactory ossFactory;
    @Autowired
    private UpdateConfigTask updateConfigTask;

    /**
     * 上传文件
     */
    @PreAuthorize("@S.authenticated")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        //上传文件
        return upload(file.getOriginalFilename(), file.getBytes(), file.getSize());
    }

    public Result<String> upload(String originalFilename, byte[] fileBytes, Long fileSize) {
        String suffix = FilenameUtils.getExtension(originalFilename);
        String url = ossFactory.build().uploadSuffix(fileBytes, suffix);
        //保存文件信息
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUrl(url);
        fileEntity.setOriginalName(originalFilename);
        fileEntity.setSize(fileSize);
        fileEntity.setSuffix(suffix);
        fileEntity.setCreateDate(new Date());
        fileService.save(fileEntity);
        Result<String> ok = Result.ok(url);
        ok.setMsg(originalFilename);
        return ok;
    }


    @PostMapping("/download/batch")
    public List<String> orderInfo(@RequestBody List<String> linkList) {
        return remoteSysOssService.batchDownload(linkList);
    }


    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Long[] ids) {
        fileService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }


    @GetMapping(value = "ueditor/exec")
    @PreAuthorize("permitAll()")
    public void exec(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        try {
            String exec = new ActionEnter(request, rootPath).exec();
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }


    @PreAuthorize("permitAll()")
    @GetMapping("/test1")
    public Result<Void> test1() {
        updateConfigTask.run();
        return Result.ok();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/version")
    public Result<String> version() {
        return Result.ok("1.2");
    }


    /**
     * baiDu 自定义配置 /上传接口
     *
     * @param action
     */
    @GetMapping(value = "/bai_du")
    @PreAuthorize("permitAll()")
    @ResponseBody
    public Object action(@RequestParam(value = "action") String action, HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        final String fileName = "config.json";
        String fileValue = RedisUtil.getCacheObject(RedisUtil.getCacheObject(RedisUtil.key(Keys.OSS, fileName)));
        if (StringUtils.isBlank(fileValue)) {
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(fileName);
            StringWriter writer = new StringWriter();
            IOUtils.copy(resource, writer, StandardCharsets.UTF_8.name());
            fileValue = writer.toString();
            RedisUtil.setCacheObject(RedisUtil.getCacheObject(RedisUtil.key(Keys.OSS, fileName)), fileValue);
        }
        return fileValue;
    }


}
