package com.medusa.gruul.platform.web.controller;


import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import com.medusa.gruul.platform.model.dto.CodeEmailConfDto;
import com.medusa.gruul.platform.model.dto.KfAddDto;
import com.medusa.gruul.platform.model.dto.SaveConfigDto;
import com.medusa.gruul.platform.model.vo.CodeEmailConfVo;
import com.medusa.gruul.platform.model.vo.KfmsgVo;
import com.medusa.gruul.platform.model.vo.SystemConfigVo;
import com.medusa.gruul.platform.service.ISystemConfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统配置 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-09-20
 */
@Tag(name = "系统配置")
@RestController
@RequestMapping("/system-conf")
@PreAuthorize("@S.hasPerm(@P.MALL_SETTING.COMMON_SETTING)")
public class SystemConfController {

    @Autowired
    private ISystemConfService systemConfService;

    /**
     * 保存配置或更新服务器配置
     *
     * @param saveConfigDto dto
     */
    @PostMapping
    @Operation(summary = "保存配置或更新服务器配置")
    public Result<Void> add(@RequestBody @Valid SaveConfigDto saveConfigDto) {
        systemConfService.saveValue(saveConfigDto);
        return Result.ok();
    }

    /**
     * 查询服务器配置
     *
     * @return 类型详情信息
     */
    @GetMapping("/service/config")
    @Operation(summary = "查询服务器配置")
    public Result<SystemConfigVo> getTypeInfo() {
        SystemConfigVo systemConfigVo = systemConfService.getTypeInfo(CommonConstants.NUMBER_ZERO);
        return Result.ok(systemConfigVo);
    }

    /**
     * 获取指定类型的存储配置
     *
     * @param type 查询类型 0-当前使用的配置 1：七牛  2：阿里云  3：腾讯云
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    @GetMapping("/current/oss/config")
    @Operation(summary = "获取当前平台使用oss配置")
    public Result<OssConfigDto> ossConfig(@Parameter(description = "查询类型 0-当前使用的配置 1：七牛  2：阿里云  3：腾讯云", required = true)
                                          @RequestParam(name = "type") Integer type) {
        return systemConfService.ossConfig(type);
    }

    /**
     * 获取管理台客服消息相关配置
     *
     * @return com.medusa.gruul.platform.model.vo.KfmsgVo
     */
    @GetMapping("/kf/msg")
    @PreAuthorize("permitAll()")
    @Operation(summary = "查询消息配置")
    public Result<KfmsgVo> getKfmsg() {
        KfmsgVo vo = systemConfService.getKfmsg();
        return Result.ok(vo);
    }

    /**
     * 保存或更新客服消息配置
     *
     * @param dto com.medusa.gruul.platform.model.dto.KfAddDto
     */
    @PostMapping("/kf/msg")
    @PreAuthorize("permitAll()")
    @Operation(summary = "保存或更新客服消息配置")
    public Result<Void> saveKfmsg(@RequestBody KfAddDto dto) {
        systemConfService.saveKfmsg(dto);
        return Result.ok();
    }

    /**
     * 保存或更新短信邮箱配置
     *
     * @param emailConfDto com.medusa.gruul.platform.model.dto.CodeEmailConfDto
     */
    @PostMapping("/code/email")
    @PreAuthorize("permitAll()")
    @Operation(summary = "保存或更新短信邮箱配置")
    public Result<Void> codeEmailConf(@RequestBody CodeEmailConfDto emailConfDto) {
        systemConfService.saveCodeEmailConf(emailConfDto);
        return Result.ok();
    }

    /**
     * 获取短信邮箱配置
     *
     * @return com.medusa.gruul.platform.model.vo.CodeEmailConfVo
     */
    @GetMapping("/code/email")
    @PreAuthorize("permitAll()")
    @Operation(summary = "查询短信邮箱配置")
    public Result<CodeEmailConfVo> getCodeEmailConf() {
        CodeEmailConfVo vo = systemConfService.getCodeEmailConf();
        return Result.ok(vo);
    }
}
