package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.StepNumber;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 步数表Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface StepNumberMapper extends BaseMapper<StepNumber> {

    /**
     * 根据userId获取步数表信息
     *
     * @param userId
     * @return 步数信息
     */
    StepNumber seleteByUserId(@Param("userId") String userId);

    /**
     * 获取已兑换步数大于0的用户步数信息
     *
     * @return
     */
    List<StepNumber> selectByHasChange();


    /**
     * 定时任务 修改用户兑换步数为0
     *
     * @param sn
     */
    void updateStepNumber(@Param("sn") StepNumber sn);
}
