package com.medusa.gruul.oss.cloud;


import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.constant.enums.CloudServiceEnum;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import org.springframework.stereotype.Component;

/**
 * 文件上传Factory
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class OSSFactory {


    public AbstractCloudStorageService build() {
        //获取云存储配置信息
        OssConfigDto dto = RedisUtil.getCacheMap(RedisUtil.key(Keys.OSS_CONFIG, "base"), OssConfigDto.class);
        if (dto.getType().equals(CloudServiceEnum.QINIU.getType())) {
            return new QiniuCloudStorageService(dto);
        } else if (dto.getType().equals(CloudServiceEnum.ALIYUN.getType())) {
            return new AliyunCloudStorageService(dto);
        } else if (dto.getType().equals(CloudServiceEnum.QCLOUD.getType())) {
            return new QcloudCloudStorageService(dto);
        }
        return null;
    }

}
