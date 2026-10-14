package com.medusa.gruul.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.entity.Live;
import com.medusa.gruul.live.model.param.api.ApiLiveParam;
import com.medusa.gruul.live.model.param.manager.LiveParam;
import com.medusa.gruul.live.model.vo.LiveVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 直播表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-04-01
 */
public interface LiveMapper extends BaseMapper<Live> {


    /**
     * 查询直播信息列表
     *
     * @param objectPage 分页对象
     * @param liveParam  直播信息查询参数
     * @return 直播List对象
     */
    List<LiveVo> selectLiveList(@Param("objectPage") Page<LiveVo> objectPage,
                                @Param("liveParam") LiveParam liveParam);

    /**
     * 根据id更新直播信息
     *
     * @param liveParam 直播信息
     * @return 是否更新成功
     */
    Boolean updateLaunchArea(@Param("liveParam") LiveParam liveParam);

    /**
     * 清空直播信息
     *
     */
    void truncate();

    /**
     * 根据id更新观看量
     *
     * @param liveParam 直播信息
     * @return 是否更新成功
     */
    Boolean updateViewCount(@Param("liveParam") LiveParam liveParam);

    /**
     * 根据id更新点赞量
     *
     * @param liveParam 直播信息
     * @return 是否更新成功
     */
    Boolean updateLikeCount(@Param("liveParam") LiveParam liveParam);

    /**
     * 根据roomid查询
     *
     * @param liveParam 直播信息
     * @return 是否更新成功
     */
    LiveVo selectByRoomId(@Param("liveParam") LiveParam liveParam);

    /**
     * 查询直播列表
     *
     * @param liveParam 直播信息
     * @return 直播信息
     */
    List<LiveVo> queryLive(@Param("liveParam") LiveParam liveParam);

    /**
     * 根据id删除直播信息
     *
     * @param id 直播信息id
     * @return 是否删除成功
     */
    int deleteNotExist(@Param("id") Long id);


    //	----------------------------------小程序端功能mapper接口------------------------------------------------

    /**
     * 查询直播信息列表  小程序端
     *
     * @param apiLiveParam 查询参数
     * @return 直播List对象
     */
    List<LiveVo> selectApiLiveList(
            @Param("apiLiveParam") ApiLiveParam apiLiveParam);

    /**
     * 获取直播信息
     *
     * @return 直播信息列表
     */
    List<Live> selectLiveInfo();
}
