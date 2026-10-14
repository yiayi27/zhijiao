package com.medusa.gruul.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.CloudServiceEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.platform.api.entity.SystemConf;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import com.medusa.gruul.platform.conf.AliyunStorageConfig;
import com.medusa.gruul.platform.conf.QiniouStorageConfig;
import com.medusa.gruul.platform.conf.SystemConfig;
import com.medusa.gruul.platform.conf.TencentStorageConfig;
import com.medusa.gruul.platform.constant.SystemConstant;
import com.medusa.gruul.platform.mapper.SystemConfMapper;
import com.medusa.gruul.platform.model.dto.CodeEmailConfDto;
import com.medusa.gruul.platform.model.dto.KfAddDto;
import com.medusa.gruul.platform.model.dto.SaveConfigDto;
import com.medusa.gruul.platform.model.vo.CodeEmailConfVo;
import com.medusa.gruul.platform.model.vo.KfmsgVo;
import com.medusa.gruul.platform.model.vo.SystemConfigVo;
import com.medusa.gruul.platform.service.ISystemConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 系统配置 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-09-20
 */
@Slf4j
@Service
public class SystemConfServiceImpl extends ServiceImpl<SystemConfMapper, SystemConf> implements ISystemConfService {

    /**
     * 保存系统配置
     *
     * @param saveConfigDto dto
     */
    @Override
    public void saveValue(SaveConfigDto saveConfigDto) {

        Integer type = saveConfigDto.getType();
        SystemConf systemConf = new SystemConf();
        String paramKey = "";
        //0-系统配置 1：七牛  2：阿里云  3：腾讯云
        switch (type) {
            case 0:
                systemConf.setParamValue(JSONUtil.toJsonStr(saveConfigDto.getSystemConfig()));
                paramKey = SystemConstant.SERVER_CONFIG;
                break;
            case 1:
                systemConf.setParamValue(JSONUtil.toJsonStr(saveConfigDto.getQiniouStorageConfig()));
                paramKey = SystemConstant.STORAGE_QINIOUYUN;
                break;
            case 2:
                systemConf.setParamValue(JSONUtil.toJsonStr(saveConfigDto.getAliyunStorageConfig()));
                paramKey = SystemConstant.STORAGE_ALIYUN;
                break;
            case 3:
                systemConf.setParamValue(JSONUtil.toJsonStr(saveConfigDto.getTencentStorageConfig()));
                paramKey = SystemConstant.STORAGE_TENCENT_CLOUD;
                break;
            default:
                throw new ServiceException("数据修改异常", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        SystemConf ossConf = this.getByParamKey(SystemConstant.CURRENT_OSS_TYPE);
        if (this.baseMapper.update(systemConf, new LambdaQueryWrapper<SystemConf>().eq(SystemConf::getParamKey, paramKey)) == 0) {
            systemConf.setParamKey(paramKey);
            this.baseMapper.insert(systemConf);
        }
        if (ossConf == null) {
            ossConf = new SystemConf();
            ossConf.setParamKey(SystemConstant.CURRENT_OSS_TYPE);
        }
        ossConf.setParamValue(type.toString());
        this.saveOrUpdate(ossConf);
    }

    /**
     * 获取指定系统配置信息
     *
     * @param type 类型
     * @return 类型详情信息
     */
    @Override
    public SystemConfigVo getTypeInfo(Integer type) {
        String paramKey = "";
        if (CommonConstants.NUMBER_ZERO.equals(type)) {
            paramKey = SystemConstant.SERVER_CONFIG;
        } else if (type.equals(CloudServiceEnum.QINIU.getType())) {
            paramKey = SystemConstant.STORAGE_QINIOUYUN;
        } else if (type.equals(CloudServiceEnum.ALIYUN.getType())) {
            paramKey = SystemConstant.STORAGE_ALIYUN;
        } else if (type.equals(CloudServiceEnum.QCLOUD.getType())) {
            paramKey = SystemConstant.STORAGE_TENCENT_CLOUD;
        } else {
            throw new ServiceException("数据获取异常", SystemCode.DATA_NOT_EXIST.getCode());
        }
        SystemConfigVo systemConfigVo = new SystemConfigVo();
        SystemConf systemConf = this.getByParamKey(paramKey);
        if (systemConf == null) {
            return systemConfigVo;
        }
        if (CommonConstants.NUMBER_ZERO.equals(type)) {
            SystemConfig systemConfig = JSONUtil.toBean(systemConf.getParamValue(), SystemConfig.class);
            systemConfigVo.setSystemConfig(systemConfig);
        } else {
            if (type.equals(CloudServiceEnum.QINIU.getType())) {
                QiniouStorageConfig qiniouStorageConfig = JSONUtil.toBean(systemConf.getParamValue(), QiniouStorageConfig.class);
                systemConfigVo.setQiniouStorageConfig(qiniouStorageConfig);
            } else if (type.equals(CloudServiceEnum.ALIYUN.getType())) {
                AliyunStorageConfig aliyunStorageConfig = JSONUtil.toBean(systemConf.getParamValue(), AliyunStorageConfig.class);
                systemConfigVo.setAliyunStorageConfig(aliyunStorageConfig);
            } else if (type.equals(CloudServiceEnum.QCLOUD.getType())) {
                TencentStorageConfig tencentStorageConfig = JSONUtil.toBean(systemConf.getParamValue(), TencentStorageConfig.class);
                systemConfigVo.setTencentStorageConfig(tencentStorageConfig);
            }
        }
        return systemConfigVo;
    }

    /**
     * 获取当前使用oss配置
     *
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    @Override
    public Result<OssConfigDto> currentOssConfig() {
        SystemConf ossConf = this.getByParamKey(SystemConstant.CURRENT_OSS_TYPE);
        Integer type = CommonConstants.NUMBER_ONE;
        if (ossConf != null) {
            type = Integer.valueOf(ossConf.getParamValue());
        }
        return ossConfig(type);
    }

    private SystemConf getByParamKey(String paramKey) {
        return this.getBaseMapper().selectOne(new LambdaQueryWrapper<SystemConf>().eq(SystemConf::getParamKey, paramKey));
    }

    /**
     * 获取指定类型的存储配置
     *
     * @param type 查询类型 0-当前使用的配置 1：七牛  2：阿里云  3：腾讯云
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    @Override
    public Result<OssConfigDto> ossConfig(Integer type) {
        if (type == 0) {
            SystemConf ossConf = this.getByParamKey(SystemConstant.CURRENT_OSS_TYPE);
            if (ossConf == null) {
                type = CommonConstants.NUMBER_ONE;
            } else {
                type = Integer.valueOf(ossConf.getParamValue());
            }
        }
        SystemConfigVo typeInfo = getTypeInfo(type);
        if (typeInfo == null) {
            return Result.failed("当前oss配置为空无法使用");
        }
        OssConfigDto dto = new OssConfigDto();
        dto.setType(type);
        if (type.equals(CloudServiceEnum.QINIU.getType()) && typeInfo.getQiniouStorageConfig() != null) {
            BeanUtils.copyProperties(typeInfo.getQiniouStorageConfig(), dto);
        } else if (type.equals(CloudServiceEnum.ALIYUN.getType()) && typeInfo.getAliyunStorageConfig() != null) {
            BeanUtils.copyProperties(typeInfo.getAliyunStorageConfig(), dto);
        } else if (type.equals(CloudServiceEnum.QCLOUD.getType()) && typeInfo.getTencentStorageConfig() != null) {
            BeanUtils.copyProperties(typeInfo.getTencentStorageConfig(), dto);
        }
        return Result.ok(dto);
    }

    /**
     * 获取管理台客服消息相关配置
     *
     * @return com.medusa.gruul.platform.model.vo.KfmsgVo
     */
    @Override
    public KfmsgVo getKfmsg() {
        SystemConf systemConf = this.getByParamKey(SystemConstant.KF_MSG_NOTIFY);
        if (ObjectUtil.isNull(systemConf)) {
            return new KfmsgVo();
        }
        return JSONUtil.toBean(systemConf.getParamValue(), KfmsgVo.class);
    }

    /**
     * 保存或更新客服消息配置
     *
     * @param dto com.medusa.gruul.platform.model.dto.KfAddDto
     */
    @Override
    public void saveKfmsg(KfAddDto dto) {
        SystemConf systemConf = this.getByParamKey(SystemConstant.KF_MSG_NOTIFY);
        if (ObjectUtil.isNotNull(systemConf)) {
            systemConf.setParamValue(JSONUtil.toJsonStr(dto));
            this.baseMapper.updateById(systemConf);
        } else {
            systemConf = new SystemConf();
            systemConf.setParamKey(SystemConstant.KF_MSG_NOTIFY);
            systemConf.setParamValue(JSONUtil.toJsonStr(dto));
            this.baseMapper.insert(systemConf);
        }
    }

    /**
     * 保存或更新短信邮箱配置
     *
     * @param emailConfDto com.medusa.gruul.platform.model.dto.CodeEmailConfDto
     */
    @Override
    public void saveCodeEmailConf(CodeEmailConfDto emailConfDto) {
        SystemConf systemConf = this.getByParamKey(SystemConstant.CODE_EMAIL_CONF);
        if (ObjectUtil.isNotNull(systemConf)) {
            systemConf.setParamValue(JSONUtil.toJsonStr(emailConfDto));
            this.baseMapper.updateById(systemConf);
        } else {
            systemConf = new SystemConf();
            systemConf.setParamKey(SystemConstant.CODE_EMAIL_CONF);
            systemConf.setParamValue(JSONUtil.toJsonStr(emailConfDto));
            this.baseMapper.insert(systemConf);
        }
    }

    @Override
    public CodeEmailConfVo getCodeEmailConf() {
        SystemConf systemConf = this.getByParamKey(SystemConstant.CODE_EMAIL_CONF);
        if (ObjectUtil.isNull(systemConf)) {
            return new CodeEmailConfVo();
        }
        return JSONUtil.toBean(systemConf.getParamValue(), CodeEmailConfVo.class);
    }

}
