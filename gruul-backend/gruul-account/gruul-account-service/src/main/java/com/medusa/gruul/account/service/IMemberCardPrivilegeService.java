package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto;
import com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto;

import java.util.List;

/**
 * <p>
 * 会员卡权益表 服务类
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-24
 */
public interface IMemberCardPrivilegeService extends IService<MemberCardPrivilege> {

    /**
     * pc端获取会员卡权益信息
     *
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilege
     */
    List<MemberCardPrivilege> getMemberCardPrivilege();

    /**
     * 后台更新权益参数
     *
     * @param updateMemberCardPrivilegeDto com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto
     */
    void updateMemberCardPrivilege(UpdateMemberCardPrivilegeDto updateMemberCardPrivilegeDto);

    /**
     * 后台开启关闭会员卡权益
     *
     * @param memberCardPrivilegeStatusDto com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto
     */
    void updateMemberCardPrivilegeStatus(MemberCardPrivilegeStatusDto memberCardPrivilegeStatusDto);

    /**
     * 后台删除自定义会员卡权益
     *
     * @param deleteMemberCardPrivilegeDto com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto
     */
    void deleteMemberCardPrivilege(DeleteMemberCardPrivilegeDto deleteMemberCardPrivilegeDto);

    /**
     * 后台插入自定义权益参数
     *
     * @param insertMemberCardPrivilegeDto com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto
     */
    void insertMemberCardPrivilege(InsertMemberCardPrivilegeDto insertMemberCardPrivilegeDto);

}
