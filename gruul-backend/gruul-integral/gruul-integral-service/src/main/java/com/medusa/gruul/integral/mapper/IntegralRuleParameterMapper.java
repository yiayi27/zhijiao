package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.IntegralRuleParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分规则参数表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralRuleParameterMapper extends BaseMapper<IntegralRuleParameter> {

    /**
     * 根据 分享 integralRuleId 得到积分规则参数信息
     *
     * @param integralRuleId
     * @return
     */
    IntegralRuleParameter getShareIntegral(@Param("integralRuleId") Long integralRuleId);


    /**
     * 插入积分规则参数默认值
     *
     * @param integralRuleParameter
     * @return
     */
    Integer insertInfo(@Param("integralRuleParameter") IntegralRuleParameter integralRuleParameter);

    /**
     * 获取商家设置的签到最后一天的规则参数信息
     *
     * @param integralRuleId
     * @param dayss
     * @return 积分规则参数 签到信息
     */
    IntegralRuleParameter getRuleParameterMapper(@Param("integralRuleId") Long integralRuleId, @Param("dayss") String dayss);

    /**
     * 获取参数信息 用于返回多条数据时使用 例如:登入 签到 步数 等规则参数信息
     *
     * @param integralRuleId 积分规则id
     * @return
     */
    List<IntegralRuleParameter> getIntegralList(Long integralRuleId);

    /**
     * 获取用户每日签到获得的积分值信息
     *
     * @param integralRuleId
     * @param status
     * @return
     */
    IntegralRuleParameter getSingInIntegral(@Param("integralRuleId") Long integralRuleId, @Param("status") String status);

    /**
     * 物理删除积分规则参数信息
     *
     * @param integralRuleId
     * @return
     */
    Integer physicsDelete(@Param("integralRuleId") Long integralRuleId);


    /**
     * 根据规则id key 获取积分规则参数信息
     *
     * @param integralRuleId 规则id
     * @param status         key
     * @return
     */
    IntegralRuleParameter getStepConversionIntegral(
            @Param("integralRuleId") Long integralRuleId, @Param("status") String status);


    /**
     * 定时任务
     * 获取商家设置的签到最后一天的规则参数信息
     *
     * @param integralRuleId
     * @param dayss
     * @return 积分规则参数 签到信息
     */
    IntegralRuleParameter getRuleParameterTask(@Param("integralRuleId") Long integralRuleId, @Param("dayss") String dayss);

    /**
     * 积分规则参数 登入默认值插入
     *
     * @param integralRuleParameter
     * @return
     */
    Integer insertEnterInfo(@Param("integralRuleParameter") IntegralRuleParameter integralRuleParameter);

    /**
     * 积分规则参数 签到默认值插入
     *
     * @param integralRuleParameter
     * @return
     */
    Integer insertSingInInfo(@Param("integralRuleParameter") IntegralRuleParameter integralRuleParameter);

    /**
     * 积分规则参数 步数默认值插入
     *
     * @param integralRuleParameter
     * @return
     */
    Integer insertStepNumberInfo(@Param("integralRuleParameter") IntegralRuleParameter integralRuleParameter);
}
