package com.medusa.gruul.live.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.live.api.entity.Live;
import com.medusa.gruul.live.model.param.api.ApiLiveParam;
import com.medusa.gruul.live.model.param.manager.LiveParam;
import com.medusa.gruul.live.model.vo.LiveVo;

import java.util.LinkedList;

/**
 *
 * 直播功能
 * @author zhaokw
 */
public interface LiveService extends IService<Live> {


    /**
     * 直播查询列表
     *
     * @param liveParam 直播信息
     * @return 进行分页的信息
     */
    IPage<LiveVo> selectLiveList(LiveParam liveParam);

    /**
     * 从微信后台获取数据
     *
     * @param liveParam 直播信息
     * @return 直播信息分页对象
     */
    IPage<LiveVo> getWeChatLiveList(LiveParam liveParam);

    /**
     * 更新直播信息
     * @param liveParam 直播信息
     **/
    void updateLaunchArea(LiveParam liveParam);

    /**
     * 分享直播间二维码
     */
    void shareCode();

    // -------------------------------------小程序端实现---------------------------------------------------------------------------------------

    /**
     * 获取小程序端 直播列表信息
     *
     * @param apiLiveParam 分页信息
     * @return 分页对象
     */
    IPage<LiveVo> getPageList(ApiLiveParam apiLiveParam);

    /**
     * 更新观看量
     *
     * @param liveParam 直播参数
     */
    void updateViewCount(LiveParam liveParam);

    /**
     * 更新点赞量
     *
     * @param liveParam 直播参数
     */
    void updateLikeCount(LiveParam liveParam);


    /**
     * 更新拖拽
     *
     * @param params 直播参数
     * @return 是否成功
     */
    Boolean updateSort(LinkedList<LiveParam> params);
}
