package com.medusa.gruul.live.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.live.api.entity.Live;
import com.medusa.gruul.live.api.model.vo.RoomInfoVo;
import com.medusa.gruul.live.mapper.LiveMapper;
import com.medusa.gruul.live.model.param.api.ApiLiveParam;
import com.medusa.gruul.live.model.param.manager.LiveParam;
import com.medusa.gruul.live.model.vo.LiveVo;
import com.medusa.gruul.live.service.LiveService;
import com.medusa.gruul.live.utils.WechatLiveClient;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import com.medusa.gruul.platform.api.model.dto.MiniAuthInfoDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 直播 服务实现类
 * </p>
 *
 * @author zhaokw
 * @since 2024-4-6
 */
@Service
public class LiveServiceImpl extends ServiceImpl<LiveMapper, Live> implements LiveService {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private RemoteMiniInfoService remoteMiniInfoService;

    @Resource
    private WechatLiveClient wechatLiveClient;

    /**
     * 获取小程序端 直播列表信息
     *
     * @param apiLiveParam 分页信息
     * @return
     */
    @Override
    public IPage<LiveVo> getPageList(ApiLiveParam apiLiveParam) {
        //获取商铺id和店铺id
        String shopId = ISecurity.userMust().getShopId();
        
        apiLiveParam.setShopId(shopId);
        IPage<LiveVo> iPage =
                new Page<>(apiLiveParam.getCurrent(), apiLiveParam.getSize());
        //sql中查询数据
        List<LiveVo> liveVos =
                liveMapper.selectApiLiveList(apiLiveParam);
        iPage.setRecords(liveVos);
        return iPage;
    }

    /**
     * 获取直播分页信息
     *
     * @param liveParam 直播参数
     * @return 直播分页对象
     */
    @Override
    public IPage<LiveVo> selectLiveList(LiveParam liveParam) {
        Page<LiveVo> objectPage = new Page<>(liveParam.getCurrent(), liveParam.getSize());
        List<LiveVo> liveVos = this.baseMapper.selectLiveList(objectPage, liveParam);
        liveVos = liveVos.stream().sorted(Comparator.comparing(LiveVo::getSort)).toList();
        return objectPage.setRecords(liveVos);
    }

    /**
     * 从微信获取直播列表
     *
     * @param liveParam 直播参数
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.medusa.gruul.live.model.vo.LiveVo>
     * @author alan
     * @date 2024/3/17 22:10
     */
    @Override
    public IPage<LiveVo> getWeChatLiveList(LiveParam liveParam) {
        //从微信后台调取直播列表
        String shopId = ISecurity.userMust().getShopId();
        Result<MiniAuthInfoDto> miniAuthInfoDtoResult = remoteMiniInfoService.getMiniAuthInfo();
        String accessToken = miniAuthInfoDtoResult.getData().getAccessToken();
        List<RoomInfoVo> roomInfoVos = wechatLiveClient.getLiveInfo(accessToken);
        LiveParam liveParam1 = new LiveParam();
        liveParam1.setShopId(shopId);
        List<LiveVo> liveVoList = liveMapper.queryLive(liveParam1);
        for (LiveVo liveVo : liveVoList) {
            boolean flag = false;
            for (RoomInfoVo roomInfoVo : roomInfoVos) {
                if (liveVo.getRoomId().equals(String.valueOf(roomInfoVo.getRoomid()))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                liveMapper.deleteNotExist(liveVo.getId());
            }
        }

        //将信息存表
        for (RoomInfoVo roomInfoVo : roomInfoVos) {
            LiveParam liveParamTemp = new LiveParam();
            liveParamTemp.setShopId(shopId)
                    .setRoomId(String.valueOf(roomInfoVo.getRoomid()));
            LiveVo liveVo = liveMapper.selectByRoomId(liveParamTemp);

            if (liveVo != null) {
                Live live = new Live();
                live.setAnchor(roomInfoVo.getAnchor_name())
                        .setEndTime(new Date(Long.parseLong(roomInfoVo.getEnd_time() + "000")))
                        .setLiveName(roomInfoVo.getName())
                        .setRoomId(String.valueOf(roomInfoVo.getRoomid()))
                        .setStartTime(new Date(Long.parseLong(roomInfoVo.getStart_time() + "000")))
                        .setStatus(roomInfoVo.getLive_status())
                        .setCoverImg(roomInfoVo.getCover_img())
                        .setAnchorImg(roomInfoVo.getAnchor_img())
                        .setShareImg(roomInfoVo.getShare_img())
                        .setId(liveVo.getId())
                        .setUpdateTime(LocalDateTime.now());

                int update = liveMapper.updateById(live);
                if (update < 1) {
                    throw new ServiceException("微信后台同步更新数据库失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            } else {
                Live live = new Live();
                live.setAnchor(roomInfoVo.getAnchor_name())
                        .setEndTime(new Date(Long.parseLong(roomInfoVo.getEnd_time() + "000")))
                        .setLikeCount(CommonConstants.LONG_ZERO)
                        .setLiveName(roomInfoVo.getName())
                        .setRoomId(String.valueOf(roomInfoVo.getRoomid()))
                        .setStartTime(new Date(Long.parseLong(roomInfoVo.getStart_time() + "000")))
                        .setStatus(roomInfoVo.getLive_status())
                        .setViewCount(CommonConstants.LONG_ZERO)
                        .setCoverImg(roomInfoVo.getCover_img())
                        .setAnchorImg(roomInfoVo.getAnchor_img())
                        .setShareImg(roomInfoVo.getShare_img());

                int insert = liveMapper.insert(live);
                if (insert < 1) {
                    throw new ServiceException("微信后台同步插入数据库失败", SystemCode.DATA_ADD_FAILED.getCode());
                }
            }
        }
        Page<LiveVo> objectPage = new Page<>(liveParam.getCurrent(), liveParam.getSize());
        List<LiveVo> liveVos = this.baseMapper.selectLiveList(objectPage, liveParam);
        return objectPage.setRecords(liveVos);
    }

    /**
     * 生成分享小程序码
     *
     * @param
     * @return void
     * @author alan
     * @date 2024/3/17 22:10
     */
    @Override
    public void shareCode() {
        //从微信后台调取小程序码
        Result<MiniAuthInfoDto> miniAuthInfoDtoResult = remoteMiniInfoService.getMiniAuthInfo();
        String accessToken = miniAuthInfoDtoResult.getData().getAccessToken();
        wechatLiveClient.getWechatCode(accessToken);

    }

    /**
     * @param liveParam 直播参数
     * @Description: 更新直播id
     * @Author: zhaokw
     * @Date: 2024/4/6 23:59
     * @return: void
     **/
    @Override
    public void updateLaunchArea(LiveParam liveParam) {
        String shopId = ISecurity.userMust().getShopId();
        liveParam.setShopId(shopId);
        liveMapper.updateLaunchArea(liveParam);
    }

    /**
     * @param liveParam 直播参数
     **/
    @Override
    public void updateViewCount(LiveParam liveParam) {
        String shopId = ISecurity.userMust().getShopId();
        liveParam.setShopId(shopId);
        liveMapper.updateViewCount(liveParam);
    }

    /**
     * @param liveParam 直播参数
     **/
    @Override
    public void updateLikeCount(LiveParam liveParam) {
        String shopId = ISecurity.userMust().getShopId();
        liveParam.setShopId(shopId);
        liveMapper.updateLikeCount(liveParam);
    }


    /**
     * 更新拖拽
     *
     * @param params 直播参数
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSort(LinkedList<LiveParam> params) {
        if (CollectionUtil.isEmpty(params)) {
            return Boolean.TRUE;
        }
        Long maxSort = params.stream().max(Comparator.comparing(LiveParam::getSort)).get().getSort();
        int size = params.size();
        for (int i = 0; i < params.size(); i++) {
            LiveParam param = params.get(i);
            param.setSort(maxSort - i);

            if (StrUtil.isNotEmpty(param.getId().toString())) {
                continue;
            }

            Live live = param.coverLive();
            size = size - this.baseMapper.updateById(live);
        }

        if (size > 0) {
            throw new ServiceException(String.format("批量修改失败! 操作已取消. 提交数: %s, 失败数: %s", params.size(), size));
        }

        return Boolean.TRUE;
    }

    /**
     * 更新直播状态
     */
    public void updateWeChatLive() {
        List<Live> lives = liveMapper.selectLiveInfo();
        if (CollectionUtil.isEmpty(lives)) {
            log.warn("暂无直播信息");
        }
        Result<MiniAuthInfoDto> miniAuthInfoDtoResult;
        List<LiveVo> liveVoList;
        Live live;
        for (Live liveInfo : lives) {
            String shopId = liveInfo.getShopId();
            miniAuthInfoDtoResult = remoteMiniInfoService.getMiniAuthInfo();
            String accessToken = miniAuthInfoDtoResult.getData().getAccessToken();
            List<RoomInfoVo> roomInfoVos = wechatLiveClient.getLiveInfo(accessToken);
            LiveParam liveParam1 = new LiveParam();
            liveParam1.setShopId(shopId);
            liveVoList = liveMapper.queryLive(liveParam1);
            for (LiveVo liveVo : liveVoList) {
                boolean flag = false;
                for (RoomInfoVo roomInfoVo : roomInfoVos) {
                    if (liveVo.getRoomId().equals(String.valueOf(roomInfoVo.getRoomid()))) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    liveMapper.deleteNotExist(liveVo.getId());
                }
            }

            //将信息存表
            for (RoomInfoVo roomInfoVo : roomInfoVos) {
                LiveParam liveParamTemp = new LiveParam();
                liveParamTemp.setShopId(shopId)
                        .setRoomId(String.valueOf(roomInfoVo.getRoomid()));
                LiveVo liveVo = liveMapper.selectByRoomId(liveParamTemp);

                if (liveVo != null) {
                    live = new Live();
                    live.setAnchor(roomInfoVo.getAnchor_name())
                            .setEndTime(new Date(Long.parseLong(roomInfoVo.getEnd_time() + "000")))
                            .setLiveName(roomInfoVo.getName())
                            .setRoomId(String.valueOf(roomInfoVo.getRoomid()))
                            .setStartTime(new Date(Long.parseLong(roomInfoVo.getStart_time() + "000")))
                            .setStatus(roomInfoVo.getLive_status())
                            .setCoverImg(roomInfoVo.getCover_img())
                            .setAnchorImg(roomInfoVo.getAnchor_img())
                            .setShareImg(roomInfoVo.getShare_img())
                            .setId(liveVo.getId())
                            .setUpdateTime(LocalDateTime.now());

                    int update = liveMapper.updateById(live);
                    if (update < 1) {
                        throw new ServiceException("微信后台同步更新数据库失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                } else {
                    live = new Live();
                    live.setAnchor(roomInfoVo.getAnchor_name())
                            .setEndTime(new Date(Long.parseLong(roomInfoVo.getEnd_time() + "000")))
                            .setLikeCount(CommonConstants.LONG_ZERO)
                            .setLiveName(roomInfoVo.getName())
                            .setRoomId(String.valueOf(roomInfoVo.getRoomid()))
                            .setStartTime(new Date(Long.parseLong(roomInfoVo.getStart_time() + "000")))
                            .setStatus(roomInfoVo.getLive_status())
                            .setViewCount(CommonConstants.LONG_ZERO)
                            .setCoverImg(roomInfoVo.getCover_img())
                            .setAnchorImg(roomInfoVo.getAnchor_img())
                            .setShareImg(roomInfoVo.getShare_img());

                    int insert = liveMapper.insert(live);
                    if (insert < 1) {
                        throw new ServiceException("微信后台同步插入数据库失败", SystemCode.DATA_ADD_FAILED.getCode());
                    }
                }
            }
        }
    }


}
