package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberCardGoods;
import com.medusa.gruul.account.model.vo.MemberCardGoodsVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 会卡购买指定商品关联表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
public interface MemberCardGoodsMapper extends BaseMapper<MemberCardGoods> {

    /**
     * 获取指定店铺下的会员卡id
     *
     * @param shopId     店铺id
     * @param memberCardId 会员卡id
     * @return com.medusa.gruul.account.model.vo.MemberCardGoodsVo
     */
    MemberCardGoodsVo selectMemberCardGoods(@Param("shopId") String shopId, @Param("memberCardId") Long memberCardId);

}
