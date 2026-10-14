package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberSwitch;

/**
 * 会员信息功能开关
 *
 * @author whh
 * @since 2024-05-30
 */
public interface IMemberSwitchService extends IService<MemberSwitch> {


    /**
     * 获取指定会员模块的开关
     *
     * @param memberModel 会员模块名
     * @return com.medusa.gruul.account.api.entity.MemberSwitch
     */
    MemberSwitch getByMemberModelMemberSwitch(Integer memberModel);
}
