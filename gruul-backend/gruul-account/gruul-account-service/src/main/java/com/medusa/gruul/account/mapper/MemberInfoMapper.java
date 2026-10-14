package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.model.dto.*;
import com.medusa.gruul.account.model.vo.MemberInnerListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员信息表 Mapper 接口
 *
 * @author zhaokw
 * @since 2024-05-01
 */
public interface MemberInfoMapper extends BaseMapper<MemberInfo> {
    /**
     * PC端获取会员列表
     *
     * @param memberListDtoPage 分页数据
     * @param paramMap          查询条件参数
     * @return com.medusa.gruul.account.model.dto.UserListDto
     */
    IPage<MemberListDto> selectByMemberList(Page<MemberListDto> memberListDtoPage, @Param("paramMap") Map<String, Object> paramMap);

    /**
     * PC端会员续费
     *
     * @param updateMemberExpireTimeDto 会员续费参数
     * @return int
     */
    int updateMemberExpireTime(@Param("updateMemberExpireTimeDto") UpdateMemberExpireTimeDto updateMemberExpireTimeDto);

    /**
     * 小程序端会员续费
     *
     * @param updateApiMemberExpireTimeDto 会员续费参数
     * @return int
     */
    int updateApiMemberExpireTime(@Param("updateApiMemberExpireTimeDto") UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto);

    /**
     * PC端会员升级
     *
     * @param upgradeMemberRankDto 会员升级参数
     * @return int
     */
    int upgradeMemberRank(@Param("upgradeMemberRankDto") UpgradeMemberRankDto upgradeMemberRankDto);

    /**
     * 获取指定用户的所有会员卡
     *
     * @param shopUserId 用户店铺id
     * @return com.medusa.gruul.account.api.entity.MemberInfo
     */
    List<MemberInfo> getMemberInfoByUserId(@Param("shopUserId") String shopUserId);

    /**
     * 更新会员注册时间
     *
     * @param memberInfo 会员信息
     * @return 0 or 1
     */
    int updateRegisterData(@Param("memberInfo") MemberInfo memberInfo);

    /**
     * 小程序会员升级
     *
     * @param upgradeApiMemberRankDto 会员升级参数
     * @return int
     */
    int upgradeApiMemberRank(@Param("upgradeApiMemberRankDto") UpgradeApiMemberRankDto upgradeApiMemberRankDto);

    /**
     * 获取指定等级会员卡数量
     *
     * @param shopId   店铺id
     * @param rankCode 等级代码
     * @return 数量
     */
    int getCountByRankCode(@Param("shopId") String shopId, @Param("rankCode") String rankCode);

    /**
     * PC端获取会员记录
     *
     * @param memberListDtoPage 分页数据
     * @param paramMap          查询条件参数
     * @return com.medusa.gruul.account.model.dto.UserListDto
     */
    IPage<MemberInnerListVo> getMemberRecord(Page<MemberInnerListVo> memberListDtoPage, @Param("paramMap") Map<String, Object> paramMap);
}
