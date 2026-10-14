package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityDto;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivitySimpleVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;

import java.util.List;

/**
 * <p>
 * 接龙活动信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
public interface ISolitaireActivityService extends IService<SolitaireActivity> {

    /**
     * 查询接龙活动列表
     *
     * @param solitaireActivityParam
     * @return 分页对象
     */
    IPage<SolitaireActivityVo> getActivityList(SolitaireActivityParam solitaireActivityParam);


    /**
     * 查询接龙活动明细
     *
     * @param id
     * @return SolitaireActivitySimpleVo
     */
    SolitaireActivitySimpleVo getActivityById(Long id);

    /**
     * 接龙活动新增
     *
     * @param solitaireActivityDto
     */
    void addActivity(SolitaireActivityDto solitaireActivityDto);

    /**
     * 接龙活动修改
     *
     * @param solitaireActivityDto
     */
    void updateActivity(SolitaireActivityDto solitaireActivityDto);

    /**
     * 接龙活动关闭
     *
     * @param id
     */
    void changeActivityStatus(Long id);

    /**
     * 接龙活动删除
     *
     * @param id
     */
    void deleteActivity(Long id);

    /**
     * 定时任务查询未开始与进行中的接龙活动
     *
     * @return List<SolitaireActivityVo>
     */
    List<SolitaireActivityVo> findActivityListForTask();

    /**
     * 定时任务查更新接龙活动状态
     *
     * @param solitaireActivityVo
     */
    void updateActivityForTask(SolitaireActivityVo solitaireActivityVo);
}
