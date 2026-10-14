package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberCardBaseInfo;
import com.medusa.gruul.account.api.entity.MemberCardInfo;
import com.medusa.gruul.account.api.entity.MemberSwitch;
import com.medusa.gruul.account.api.model.MemberCardInfoListDto;
import com.medusa.gruul.account.api.model.MemberCardInfoListResultDto;
import com.medusa.gruul.account.model.dto.MemberCardInfoRankStatusDto;
import com.medusa.gruul.account.model.dto.MemberCardInfoStatusDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description 会员卡信息 服务类
 * @Author zhaokw
 * @Date 23:26 2024\5\13 0013
 **/
public interface IMemberCardInfoService extends IService<MemberCardInfo> {

    /**
     * 后台关闭打开会员卡功能
     *
     * @param memberCardInfoStatusDto 会员卡功能关闭启用请求参数
     */
    void setMemberCardInfoStatus(MemberCardInfoStatusDto memberCardInfoStatusDto);

    /**
     * 获取指定店铺下的会员卡信息
     *
     * @return MemberCardInfoListVo 会员卡信息
     */
    MemberCardInfoListVo getMemberCardInfo();

    /**
     * 后台停用开启会员卡各个等级
     *
     * @param memberCardInfoRankStatusDtoList 会员卡各个等级关闭启用请求参数
     */
    void updateMemberCardInfoStatus(List<MemberCardInfoRankStatusDto> memberCardInfoRankStatusDtoList);

    /**
     * 后台保存会员卡信息
     *
     * @param memberCardInfo 会员卡信息
     */
    void saveMemberCardInfo(MemberCardInfoListVo memberCardInfo);

    /**
     * 获取指定店铺会员卡基本信息
     *
     * @param shopId 店铺id
     * @return MemberCardBaseInfo 会员卡基本信息
     */
    MemberCardBaseInfo getByMemberBaseInfo(@Param("shopId") String shopId);

    /**
     * 小程序端购买弹框点击确认付款或确认兑换
     *
     * @param confirmPaymentVo 确认付款信息
     * @return PayResultDto 支付结果信息
     */
    PayResultDto confirmPayment(ConfirmPaymentVo confirmPaymentVo);

    /**
     *  会员卡支付确认信息
     * @param confirmPaymentPage 确认支付信息
     * @return Object
     */
    Object confirmPaymentPage(ConfirmPaymentPageVO confirmPaymentPage);

    /**
     * 会员开卡处理
     *
     * @param wxPayNotifyResultDto 微信支付信息
     */
    void memberCompleted(WxPayNotifyResultDto wxPayNotifyResultDto);

    /**
     * 小程序端购买会员卡填写信息
     *
     * @param registerDataVo com.medusa.gruul.account.model.vo.RegisterDataVo
     * @return com.medusa.gruul.account.model.vo.MemberCardInfoResVo
     */
    MemberCardInfoResVo updateRegisterData(RegisterDataVo registerDataVo);

    /**
     * 小程序会员中心
     *
     * @return com.medusa.gruul.account.model.vo.MemberCenterVo
     */
    MemberCenterVo memberCenter();

    /**
     * 小程序端会员卡记录
     *
     * @return com.medusa.gruul.account.model.vo.MemberCardRecordVo
     */
    List<MemberCardRecordVo> getMemberCardRecord();

    /**
     * 获取会员开关功能
     *
     * @return com.medusa.gruul.account.api.entity.MemberSwitch
     */
    List<MemberSwitch> getMemberCardInfoSwitch();

    /**
     * 获取指定表单列表
     *
     * @return 会员表单列表
     */
    List<MemberRegisterFormVo> getRegisterForm();

    /**
     * 获取指定店铺的指定开卡方式的会员卡 (小程序端)
     *
     * @param memberCardInfoListDto com.medusa.gruul.account.api.model.MemberCardInfoListDto
     * @return com.medusa.gruul.account.api.model.MemberCardInfoListResultDto
     */
    List<MemberCardInfoListResultDto> getMemberCardInfoList(MemberCardInfoListDto memberCardInfoListDto);


    /**
     * 获取会员卡开启状态
     *
     * @param shopId 店铺id
     * @return java.lang.Integer
     */
    Integer memberSwitch(String shopId);


    /**
     * 生成初始化sql
     *
     * @param shopId 店铺id
     */
    void memberInit(String shopId);

    /**
     * 获取当前使用的会员卡
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.entity.MemberCardInfo
     */
    List<MemberCardInfo> getCurrentMemberCardInfo(String shopId);
}
