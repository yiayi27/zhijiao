package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.api.model.MemberCardInfoCenterDto;
import com.medusa.gruul.account.api.model.MemberTopCardInfoDto;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpgradeMemberRankDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.common.core.util.PageUtils;

import java.util.List;

/**
 * 会员信息表 服务类
 *
 * @author zhaokw
 * @since 2024-05-01
 */
public interface IMemberInfoService extends IService<MemberInfo> {

    /**
     * 获取pc端会员列表
     *
     * @param nikeName              微信昵称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime   成为会员结束时间
     * @param tagId                 标签id
     * @param orderSuccessStartTime 成交时间
     * @param orderSuccessEndTime   成交时间
     * @param memberCardId          会员卡id
     * @param memberNumber          会员编号
     * @param rankCode              会员等级
     * @param page                  页数
     * @param size                  条数
     * @param sortType
     * @return com.medusa.gruul.member.web.model.vo
     */
    PageUtils<List<MemberListVo>> memberList(String nikeName, String becomeMemberStartTime, String becomeMemberEndTime, Long tagId, String orderSuccessStartTime, String orderSuccessEndTime, String rankCode, Long memberCardId, String memberNumber, Integer page, Integer size, Integer sortType);

    /**
     * 后台将用户设为会员
     *
     * @param setMemberDto com.medusa.gruul.account.model.dto.SetMemberDto
     */
    void setMember(SetMemberDto setMemberDto);

    /**
     * 后台获取会员卡等级
     *
     * @return com.medusa.gruul.account.model.vo.MemberCardRankListVo
     */
    List<MemberCardRankListVo> getMemberCardRank();

    /**
     * 后台更新会员的到期日
     *
     * @param updateMemberExpireTimeDto com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto
     */
    void updateMemberExpireTime(UpdateMemberExpireTimeDto updateMemberExpireTimeDto);

    /**
     * 后台升级会员等级
     *
     * @param upgradeMemberRankDto com.medusa.gruul.account.model.dto.UpgradeMemberRankDto
     */
    void upgradeMemberRank(UpgradeMemberRankDto upgradeMemberRankDto);

    /**
     * 获取指定店铺下的用户的会员卡相关信息
     *
     * @param shopId   店铺id
     * @param shopUserId 店铺用户id
     * @return
     */
    MemberCardInfoCenterDto getMemberInfo(String shopId, String shopUserId);

    /**
     * 获取店铺id下最高等级的会员等级信息
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.model.MemberTopCardInfoDto
     */
    MemberTopCardInfoDto getTopMemberCardInfo(String shopId);

    /**
     * pc端升级会员等级界面，暂考虑该用户所持有的会员卡和后台设置的种类一致，不一致则提示开通，跳转到开通界面
     *
     * @param memberId 会员id
     * @return com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     */
    MemberCardUpgradeVo getMemberCardUpgrade(String memberId);

    /**
     * pc端查看会员
     *
     * @param page                  页数
     * @param size                  条数
     * @param shopUserId            用户店铺id
     * @param memberNumber          会员编号
     * @param title                 会员卡名称
     * @param rankName              会员卡等级名称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime   成为会员结束时间
     * @return com.medusa.gruul.account.model.vo.MemberInnerListVo
     */
    PageUtils<List<MemberInnerListVo>> getMemberRecord(Integer page, Integer size, String shopUserId, String memberNumber, String title, String rankName, String becomeMemberStartTime, String becomeMemberEndTime);

    /**
     * 获取当前使用中的各等级会员卡
     *
     * @return com.medusa.gruul.account.model.vo.UseMemberCardRankVo
     */
    List<UseMemberCardRankVo> getUseMemberCardRank();

    /**
     * 当前用户余额明细跳转状态
     *
     * @return com.medusa.gruul.account.model.vo.JumpDetailVo
     */
    JumpDetailVo getJumpDetail();

}
