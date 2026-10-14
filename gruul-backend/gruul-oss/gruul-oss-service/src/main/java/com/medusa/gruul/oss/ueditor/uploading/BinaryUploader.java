package com.medusa.gruul.oss.ueditor.uploading;


import cn.hutool.core.io.FileUtil;
import com.medusa.gruul.oss.ueditor.PathFormat;
import com.medusa.gruul.oss.ueditor.define.AppInfo;
import com.medusa.gruul.oss.ueditor.define.BaseState;
import com.medusa.gruul.oss.ueditor.define.State;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class BinaryUploader {

    public static State save(HttpServletRequest request,
                             Map<String, Object> conf) {


        if (!(request instanceof MultipartRequest multipartRequest) || multipartRequest.getFileMap().isEmpty()) {
            return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
        }
        MultipartFile file = multipartRequest.getFileMap().values().iterator().next();

        String savePath = (String) conf.get("savePath");
        String originFileName = file.getOriginalFilename();
        String suffix = FileUtil.extName(originFileName);

        originFileName = FileUtil.mainName(originFileName);
        savePath = savePath + suffix;

        long maxSize = (Long) conf.get("maxSize");

        if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
            return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
        }

        savePath = PathFormat.parse(savePath, originFileName);

        String physicalPath = conf.get("rootPath") + savePath;

        InputStream is;
        try {
            is = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
        try {
            is.close();
        } catch (IOException e) {
            log.error("is close error", e);
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        if (storageState.isSuccess()) {
            storageState.putInfo("url", PathFormat.format(savePath));
            storageState.putInfo("type", suffix);
            storageState.putInfo("original", originFileName + suffix);
        }
        return storageState;
    }

    private static boolean validType(String type, String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);

        return list.contains(type);
    }
}
