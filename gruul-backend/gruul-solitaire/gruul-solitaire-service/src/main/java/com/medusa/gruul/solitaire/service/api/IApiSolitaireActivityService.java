package com.medusa.gruul.solitaire.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityVo;

/**
 * <p>
 * 接龙活动信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
public interface IApiSolitaireActivityService extends IService<SolitaireActivity> {

    /**
     * 查询当前接龙活动明细
     *
     * @return Result
     */
    Result<ApiSolitaireActivityVo> getCurrentActivity();

    /**
     * 获取单场接龙活动详细信息
     *
     * @param activityId 接龙活动id
     * @return Result
     */
    Result<ApiSolitaireActivityVo> getCurrentActivityById(Long activityId);

    /**
     * 转发当前接龙活动明细
     *
     * @param activityId 接龙活动id
     * @param path
     * @return Result
     */
    Result<Object> getCurrentActivityProduct(Long activityId, String path);

    /**
     * 保存当前接龙活动接龙信息
     *
     * @param orderVo
     * @return Result
     */
    void saveCurrentActivityRecord(OrderVo orderVo);

    /**
     * 转发当前接龙活动接龙信息
     *
     * @param activityId 接龙活动id
     * @param path
     * @return Result
     */
    Result<Object> getCurrentActivityRecord(Long activityId, String path);

    /**
     * 根据商品获取当前参与的最新一场接龙活动信息
     *
     * @param productId
     * @return Result
     */
    Result<Object> getActivityByProduct(Long productId);

}
