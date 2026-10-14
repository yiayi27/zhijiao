package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimePageDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankPageDto;
import com.medusa.gruul.account.model.vo.ApiMemberCardInfoVo;
import com.medusa.gruul.account.model.vo.MemberCardUpgradeVo;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;

import java.util.List;

/**
 * 会员信息表 服务类 小程序端
 *
 * @author zhaokw
 * @since 2024-05-30
 */
public interface IApiMemberInfoService extends IService<MemberInfo> {

    /**
     * 小程序端会员信息列表
     *
     * @return List<ApiMemberCardInfoVo> 会员卡列表信息
     */
    List<ApiMemberCardInfoVo> getMemberInfo();

    /**
     * 小程序端更新会员的到期日，会员续费
     *
     * @param updateApiMemberExpireTimeDto  小程序更新会员的到期日
     * @return PayResultDto
     */
    PayResultDto updateMemberExpireTime(UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto);

    /**
     * 付费购买会员卡续费 会员到期信息
     * @param memberExpire 会员到期信息
     * @return Object
     */
    Object updateMemberExpireTimePage(UpdateApiMemberExpireTimePageDto memberExpire);

    /**
     * 小程序端升级会员界面
     *
     * @param memberId 会员id
     * @return com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     */
    MemberCardUpgradeVo getMemberCardUpgrade(String memberId);

    /**
     * 小程序端升级会员等级
     *
     * @param upgradeApiMemberRankDto com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    PayResultDto upgradeMemberRank(UpgradeApiMemberRankDto upgradeApiMemberRankDto);

    /**
     * 升級會員等級頁面
     * @param updateMemberRankPage 会员卡支付信息
     * @return Object
     */
    Object upgradeMemberRankPage(UpgradeApiMemberRankPageDto updateMemberRankPage);
}
