package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.model.vo.MemberPrivilegeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 会员卡权益表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
public interface MemberCardPrivilegeMapper extends BaseMapper<MemberCardPrivilege> {

    /**
     * 获取指定店铺已打开的权益
     * @param shopId 店铺id
     *
     * @return com.medusa.gruul.account.model.vo.MemberPrivilegeVo
     */
    List<MemberPrivilegeVo> selectMemberCardPrivilege(@Param("shopId") String shopId);

    /**
     * 获取指定店铺的所有权益
     * @param shopId 店铺id
     *
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilege
     */
    List<MemberCardPrivilege> getMemberCardPrivilege(@Param("shopId") String shopId);

}
