package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 会员卡与会员卡权益关联表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
public interface MemberCardPrivilegeGroupMapper extends BaseMapper<MemberCardPrivilegeGroup> {

    /**
     * 获取指定店铺下的会员卡关联的权益
     *
     * @param shopId
     * @param memberCardId 会员卡
     * @param privilegeId  权益id
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup
     */
    MemberCardPrivilegeGroup selectMemberCardPrivilegeGroup(@Param("shopId") String shopId, @Param("memberCardId") Long memberCardId, @Param("privilegeId") Long privilegeId);

    /**
     * 获取指定会员卡所有权益
     *
     *
     * @param memberCardId 会员卡id
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup
     */
    List<MemberCardPrivilegeGroup> getByMemberCardId(@Param("memberCardId") Long memberCardId,@Param("shopId")String shopId);

}
