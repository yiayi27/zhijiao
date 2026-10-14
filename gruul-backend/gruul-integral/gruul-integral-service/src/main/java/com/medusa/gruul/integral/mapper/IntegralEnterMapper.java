package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.IntegralLogIn;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分登入表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-12-31
 */
@Repository
public interface IntegralEnterMapper extends BaseMapper<IntegralLogIn> {
    /**
     * 获取用户登入信息
     *
     * @param userId
     * @return
     */
    IntegralLogIn selectByUserId(@Param("userId") String userId);

    /**
     * 得到所有登入状态为false的数据
     *
     * @return
     */
    List<IntegralLogIn> getDidnotEnter();

    /**
     * 定时任务
     * 获取积分登入表所有信息
     *
     * @return
     */
    List<IntegralLogIn> getAllInfo();

    /**
     * 定时任务 修改状态为未登入的用户连续签到前天数1L
     *
     * @param integralLogIn
     */
    void updateEnterDays(@Param("integralLogIn") IntegralLogIn integralLogIn);

    /**
     * 定时任务 修改用户登入状态为未登入 明日可继续登入
     *
     * @param integralLogIn
     */
    void updateEnterStatus(@Param("integralLogIn") IntegralLogIn integralLogIn);

    /**
     * 定时任务 : 获取该商铺下的所有用户登入信息
     *
     * @param shopId
     * @return
     */
    List<IntegralLogIn> selectShopUserInfo(@Param("shopId") String shopId);
}
