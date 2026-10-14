package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
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
public interface SolitaireActivityMapper extends BaseMapper<SolitaireActivity> {

    /**
     * 获取接龙活动信息list
     *
     * @param page                   分页对象
     * @param solitaireActivityParam
     * @return List<SolitaireActivityVo>
     */
    List<SolitaireActivityVo> queryActivityList(IPage page, @Param("solitaireActivityParam") SolitaireActivityParam solitaireActivityParam);

    /**
     * 获取接龙活动详细信息
     *
     * @param id
     * @return 接龙活动id
     */
    SolitaireActivityVo queryActivityById(@Param("id") Long id);


    /**
     * 接龙活动未开始与进行中的活动
     *
     * @param id
     * @return List<SolitaireActivityVo>
     */
    List<SolitaireActivityVo> queryValidActivity(@Param("id") Long id);

    /**
     * 定时任务查询未开始与进行中的接龙活动
     *
     * @return List<SolitaireActivityVo>
     */
    List<SolitaireActivityVo> queryActivityListForTask();

    /**
     * 定时任务更新接龙活动
     *
     * @param solitaireActivityVo
     */
    void updateActivityForTask(SolitaireActivityVo solitaireActivityVo);
}
