package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberCardInfo;
import com.medusa.gruul.account.api.model.MemberCardInfoListDto;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.vo.MemberCardInfoRankStatusVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 会员卡 Mapper接口
 * @Author zhaokw
 * @Date 23:39 2024\5\13 0013
 **/
public interface MemberCardInfoMapper extends BaseMapper<MemberCardInfo> {

    /**
     * PC端获取会员卡信息
     *
     * @param setMemberDto    传入参数
     * @param currentOpenType 当前会员开卡方式
     * @return MemberCardInfo
     */
    MemberCardInfo selectMemCardInfo(@Param("setMemberDto") SetMemberDto setMemberDto, @Param("currentOpenType") Integer currentOpenType);

    /**
     * PC端获取会员等级
     *
     * @param shopId 店铺id
     * @return List<MemberCardInfo>
     */
    List<MemberCardInfo> getMemberCardRank(@Param("shopId") String shopId);

    /**
     * PC端根据店铺id获取会员卡信息
     *
     * @param shopId 传入参数
     * @return List<MemberCardInfo>
     */
    List<MemberCardInfo> getMemberCardInfoByShopId(@Param("shopId") String shopId);

    /**
     * PC端关闭开启会员卡等级功能
     *
     * @param memberCardInfoRankStatusVo 传参
     * @return int
     */
    int updateMemberCardInfoStatus(@Param("memberCardInfoRankStatusVo") MemberCardInfoRankStatusVo memberCardInfoRankStatusVo);


    /**
     * 获取累计消费满多少与购买指定商品信息(小程序端)
     *
     * @param memberCardInfoListDto com.medusa.gruul.account.api.model.MemberCardInfoListDto
     * @return com.medusa.gruul.account.api.entity.MemberCardInfo
     */
    List<MemberCardInfo> getMemberCardInfoList(@Param("memberCardInfoListDto") MemberCardInfoListDto memberCardInfoListDto);


    /**
     * 获取会员卡信息根据id（小程序端）
     *
     * @param id 会员卡idto
     * @return com.medusa.gruul.account.api.entity.MemberCardInfo
     */
    MemberCardInfo getApiMemberCardInfo(@Param("id") Long id);
}
