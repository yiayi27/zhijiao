package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.IntegralClock;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralClockDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分签到表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralClockMapper extends BaseMapper<IntegralClock> {

    /**
     * 获取用户签到信息
     *
     * @param userId 用户id
     * @return 签到信息
     */
    IntegralClock selectByUserId(@Param("userId") String userId);

    /**
     * 获得用户签到信息 并返回给前端
     *
     * @param userId
     * @return
     */
    IntegralClockDto getUserSingInInfo(@Param("userId") String userId);

    /**
     * 获取所有签到状态为 false的用户
     *
     * @return
     */
    List<IntegralClock> getDidnotSignIn();

    /**
     * 查询用户签到数据
     *
     * @param userId
     * @return
     */
    IntegralClock findUserId(@Param("userId") String userId);

    /**
     * 获取所有用户的签到信息
     *
     * @return
     */
    List<IntegralClock> getAllUserSingInInfo();

    /**
     * 定时任务 :修改当天未签到用户连续签到天数为1L
     *
     * @param integralClock
     */
    void updateRunningDays(@Param("integralClock") IntegralClock integralClock);

    /**
     * 定时任务 ： 修改用户签到信息为未签到
     *
     * @param integralClock
     */
    void updateSignIn(@Param("integralClock") IntegralClock integralClock);

    /**
     * 定时任务 : 获取该商铺下的所有用户签到信息
     *
     * @param shopId
     * @return
     */
    List<IntegralClock> selectShopUserInfo(@Param("shopId") String shopId);
}
