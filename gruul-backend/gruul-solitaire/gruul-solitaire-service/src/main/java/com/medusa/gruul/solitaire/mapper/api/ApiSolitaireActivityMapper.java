package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 接龙活动 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
@Component
public interface ApiSolitaireActivityMapper extends BaseMapper<SolitaireActivity> {

    /**
     * 获取所有接龙活动最新的一场接龙活动详细信息
     *
     * @return List<ApiSolitaireActivityVo>
     */
    List<ApiSolitaireActivityVo> queryCurrentActivity();

    /**
     * 获取未开始与进行中的活动
     *
     * @return List<ApiSolitaireActivityVo>
     */
    List<ApiSolitaireActivityVo> queryReadyAndGoingActivity();

    /**
     * 根据活动id获取接龙活动详细信息
     *
     * @param id
     * @return ApiSolitaireActivityVo
     */
    ApiSolitaireActivityVo queryActivityById(@Param("id") Long id);

}
