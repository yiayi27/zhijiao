package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.medusa.gruul.integral.web.model.dto.IntegralDetailDto;
import com.medusa.gruul.integral.web.model.param.api.IntegralDetailParam;
import com.medusa.gruul.integral.web.model.vo.IntegralDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 积分明细表
 * Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralDetailMapper extends BaseMapper<IntegralDetail> {

    /**
     * 用户查看积分明细
     *
     * @param page                分页对象
     * @param integralDetailParam 查询参数
     * @return
     */
    List<IntegralDetailVo> getIntegralDetail(@Param("page") Page<IntegralDetailVo> page,
                                             @Param("integralDetailParam") IntegralDetailParam integralDetailParam);

    /**
     * 根据商品id 用户id 查询积分明细表中 用户的分享操作是否是首次分享
     *
     * @param shopId 商铺id
     * @param userId 用户id
     * @return
     */
    List<IntegralDetail> selectDetailInfo(@Param("shopId") String shopId, @Param("userId") String userId);

    /**
     * 获取用户现有积分值
     *
     * @param shopId
     * @param userId
     * @return
     */
    BigDecimal selectNowIntegral(@Param("shopId") String shopId, @Param("userId") String userId);


    /**
     * 查询需要清空的积分 定时任务
     *
     * @param shopId     商铺id
     * @param createTime 截止时间
     * @return 需要清空的积分明细
     */
    List<Map<String, Object>> selectNeedClearIntegrals(
            @Param("shopId") String shopId, @Param("createTime") String createTime);


    /**
     * 查询需要清空的用户信息及清空的积分值
     *
     * @param shopId   商铺id
     * @return
     */
    List<Map<String, Object>> getAllUser(@Param("shopId") String shopId);

    /**
     * 修改这个商铺下所有的积分明细为已清空
     *
     * @param shopId   商铺id
     */
    void updateAllStatus(@Param("shopId") String shopId);


    /**
     * 按条件获取用户明细
     *
     * @param page              分页条件
     * @param integralDetailDto 查询条件
     * @return
     */
    List<IntegralDetailVo> selectIntegralDetail(
            @Param("page") Page<IntegralDetailVo> page, @Param("integralDetailDto") IntegralDetailDto integralDetailDto);
}
