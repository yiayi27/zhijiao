package com.medusa.gruul.account.web.controller;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import com.medusa.gruul.common.core.util.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 协议控制器
 * @author whh
 * @date 2024/3/31
 */
@RestController
@RequestMapping("/agreement")
public class AgreementController {


    @GetMapping("/user")
    @PreAuthorize("permitAll()")
    public Result<String> userAgreement() {
        return Result.ok(
                ResourceUtil.readStr("agreement/user.txt", CharsetUtil.CHARSET_UTF_8)
        );
    }


    @GetMapping("/privacy")
    @PreAuthorize("permitAll()")
    public Result<String> privacyAgreement() {
        return Result.ok(ResourceUtil.readStr("agreement/privacy.txt", CharsetUtil.CHARSET_UTF_8));
    }

}
