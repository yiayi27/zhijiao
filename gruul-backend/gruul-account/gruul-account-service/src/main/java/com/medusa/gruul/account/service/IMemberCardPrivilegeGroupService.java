package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup;

import java.util.List;

/**
 * <p>
 * 会员卡与会员卡权益关联表 服务类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
public interface IMemberCardPrivilegeGroupService extends IService<MemberCardPrivilegeGroup> {

    /**
     * 获取指定会员卡的指定权益
     *
     * @param memberCardId 会员卡id
     * @param privilegeId  权益id
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup
     */
    MemberCardPrivilegeGroup selectByMemberCardIdAndPrivilegeId(Long memberCardId, Long privilegeId);

    /**
     * 移除会员卡指定权益
     *
     * @param memberCardId 会员卡id
     * @param privilegeIds 会员卡权益
     */
    void removeByMemberCardIdAndPrivilegeIds(Long memberCardId, List<Long> privilegeIds);
}
