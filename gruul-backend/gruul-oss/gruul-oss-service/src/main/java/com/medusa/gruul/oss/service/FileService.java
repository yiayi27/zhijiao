

package com.medusa.gruul.oss.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.oss.api.entity.FileEntity;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;

import java.util.List;

/**
 * 文件上传
 */
public interface FileService extends IService<FileEntity> {
    /**
     * 获取配置
     *
     * @return 文件上传配置信息
     */
    OssConfigDto getConfig();

    /**
     * 上传文件
     *
     * @param linkList 文件链接列表
     * @return List<String>
     */
    List<String> batchDownload(List<String> linkList);

}
