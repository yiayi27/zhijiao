package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.medusa.gruul.integral.web.model.dto.manager.ApiIntegralRuleDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralRuleDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分规则表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralRuleMapper extends BaseMapper<IntegralRule> {

    /**
     * 获取积分商城规则
     *
     * @param integralShopId
     * @return
     */
    List<IntegralRuleDto> getShopRuleInfo(@Param("integralShopId") Long integralShopId);

    /**
     * 商城规则BY integralShopId
     *
     * @param integralShopId
     * @return
     */
    IntegralRule selectIntegralRuleInfo(@Param("integralShopId") Long integralShopId);

    /**
     * 根据店铺id 获取积分类型得到积分规则表id
     *
     * @param integralShopId
     * @param status         获取积分类型
     * @return
     */
    Long getIntegralRule(@Param("integralShopId") Long integralShopId, @Param("status") int status);


    /**
     * 获取 签到 步数信息
     *
     * @param shopId  商铺id
     * @param status  签到值
     * @param status1 步数值
     * @return
     */
    List<ApiIntegralRuleDto> getRule(@Param("shopId") String shopId, @Param("status") int status,
                                     @Param("status1") int status1);

    /**
     * 获取积分商城下 各个获取积分值功能信息
     *
     * @param id     积分商城id
     * @param status 获取积分规则的类型
     * @return
     */
    IntegralRule getRuleStatus(@Param("id") Long id, @Param("status") int status);


    /**
     * 根据积分商城表id 获取积分规则信息
     *
     * @param shoppingId
     * @return
     */
    List<IntegralRule> selectRules(@Param("shoppingId") Long shoppingId);


    /**
     * 定时任务
     * 根据店铺id 获取积分类型得到积分规则表id
     *
     * @param status 获取积分类型
     * @param id
     * @return
     */

    Long getIntegralRuleTask(@Param("id") Long id, @Param("status") int status);


    /**
     * 定时任务
     * 根据商城id 获取积分规则
     *
     * @param id 商城id
     * @return
     */
    List<IntegralRule> getRules(@Param("id") Long id);


    /**
     * 获取单个积分规则
     *
     * @param ruleType 积分值获取类型
     * @return
     */
    IntegralRule selectRule(@Param("ruleType") Integer ruleType);

    /**
     * 插入积分规则
     *
     * @param integralRule
     * @return
     */
    int insertRule(@Param("integralRule") IntegralRule integralRule);

}
