package com.medusa.gruul.platform.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.platform.api.entity.SystemConf;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import com.medusa.gruul.platform.model.dto.*;
import com.medusa.gruul.platform.model.vo.CodeEmailConfVo;
import com.medusa.gruul.platform.model.vo.KfmsgVo;
import com.medusa.gruul.platform.model.vo.SystemConfigVo;

/**
 * <p>
 * 系统配置 服务类
 * </p>
 *
 * @author whh
 * @since 2023-09-20
 */
public interface ISystemConfService extends IService<SystemConf> {

    /**
     * 保存系统配置
     *
     * @param saveConfigDto dto
     */
    void saveValue(SaveConfigDto saveConfigDto);

    /**
     * 获取指定系统配置信息
     *
     * @param type 类型
     * @return 类型详情信息
     */
    SystemConfigVo getTypeInfo(Integer type);

    /**
     * 获取当前使用oss配置
     *
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    Result<OssConfigDto> currentOssConfig();

    /**
     * 获取指定类型的存储配置
     *
     * @param type 查询类型 0-当前使用的配置 1：七牛  2：阿里云  3：腾讯云
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    Result<OssConfigDto> ossConfig(Integer type);

    /**
     * 获取管理台客服消息相关配置
     *
     * @return com.medusa.gruul.platform.model.vo.KfmsgVo
     */
    KfmsgVo getKfmsg();

    /**
     * 保存或更新客服消息配置
     *
     * @param dto com.medusa.gruul.platform.model.dto.KfAddDto
     */
    void saveKfmsg(KfAddDto dto);

    /**
     * 保存或更新短信邮箱配置
     *
     * @param emailConfDto com.medusa.gruul.platform.model.dto.CodeEmailConfDto
     */
    void saveCodeEmailConf(CodeEmailConfDto emailConfDto);

    /**
     * 获取短信邮箱配置
     *
     * @return com.medusa.gruul.platform.model.vo.CodeEmailConfVo
     */
    CodeEmailConfVo getCodeEmailConf();

}
