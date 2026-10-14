package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberRegisterForm;
import com.medusa.gruul.account.model.vo.MemberRegisterFormVo;

import java.util.List;

/**
 * @Description 会员注册表单
 * @Author whh
 * @Date 2024-1-17
 **/
public interface IMemberRegisterFormService extends IService<MemberRegisterForm> {


    /**
     * 处理会员卡注册表单
     *
     * @param memberRegisterFormVos com.medusa.gruul.account.model.vo.MemberRegisterFormVo
     */
    void operationMemberRegisterForm(List<MemberRegisterFormVo> memberRegisterFormVos);

}
