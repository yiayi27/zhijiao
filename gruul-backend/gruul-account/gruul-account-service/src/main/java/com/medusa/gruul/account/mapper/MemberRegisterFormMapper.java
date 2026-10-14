package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberRegisterForm;
import com.medusa.gruul.account.model.vo.MemberRegisterFormVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 会员注册表单表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
public interface MemberRegisterFormMapper extends BaseMapper<MemberRegisterForm> {

    /**
     * 获取指定店铺下的会员卡表单
     * @param shopId 店铺id
     *
     * @return com.medusa.gruul.account.model.vo.MemberRegisterFormVo
     */
    List<MemberRegisterFormVo> selectMemberRegisterForm(@Param("shopId") String shopId);

    /**
     * 获取指定表单
     *
     * @param formId   表单id
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.entity.MemberRegisterForm
     */
    MemberRegisterForm qryFormById(@Param("formId") String formId, @Param("shopId") String shopId);

}
