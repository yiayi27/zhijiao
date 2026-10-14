package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup;
import com.medusa.gruul.account.mapper.MemberCardPrivilegeGroupMapper;
import com.medusa.gruul.account.service.IMemberCardPrivilegeGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员卡与会员卡权益关联表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
public class MemberCardPrivilegeGroupServiceImpl extends ServiceImpl<MemberCardPrivilegeGroupMapper, MemberCardPrivilegeGroup> implements IMemberCardPrivilegeGroupService {

    /**
     * 获取指定会员卡的指定权益
     *
     * @param memberCardId 会员卡id
     * @param privilegeId  权益id
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup
     */
    @Override
    public MemberCardPrivilegeGroup selectByMemberCardIdAndPrivilegeId(Long memberCardId, Long privilegeId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<MemberCardPrivilegeGroup>()
                .eq(MemberCardPrivilegeGroup::getPrivilegeId, privilegeId)
                .eq(MemberCardPrivilegeGroup::getMemberCardId, memberCardId)
        );
    }

    /**
     * 移除会员卡指定权益
     *
     * @param memberCardId 会员卡id
     * @param privilegeIds 会员卡权益
     */
    @Override
    public void removeByMemberCardIdAndPrivilegeIds(Long memberCardId, List<Long> privilegeIds) {
        this.baseMapper.delete(new LambdaQueryWrapper<MemberCardPrivilegeGroup>()
                .eq(MemberCardPrivilegeGroup::getMemberCardId, memberCardId)
                .in(MemberCardPrivilegeGroup::getPrivilegeId, privilegeIds)
        );
    }
}
