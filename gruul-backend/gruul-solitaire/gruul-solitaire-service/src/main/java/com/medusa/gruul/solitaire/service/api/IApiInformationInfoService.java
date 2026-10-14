package com.medusa.gruul.solitaire.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.solitaire.api.entity.InformationInfo;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import com.medusa.gruul.solitaire.api.model.dto.api.ApiInformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationInfoParam;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationInfoVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationMessageVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationSetVo;

import java.util.List;

/**
 * <p>
 * 小程序资讯信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
public interface IApiInformationInfoService extends IService<InformationInfo> {

    /**
     * 获取资讯设置信息
     *
     * @return ApiInformationSetVo
     */
    ApiInformationSetVo getInformationSet();

    /**
     * 查询资讯专题信息
     *
     * @return List<InformationTopic>
     */
    List<InformationTopic> topicList();

    /**
     * 查询资讯信息
     *
     * @param apiInformationInfoParam
     * @return IPage<ApiInformationInfoVo>
     */
    PageUtils<ApiInformationInfoVo> list(ApiInformationInfoParam apiInformationInfoParam);

    /**
     * 查询资讯信息
     *
     * @param id 资讯id
     * @return ApiInformationInfoVo
     */
    ApiInformationInfoVo get(Long id);


    /**
     * 资讯阅读量增加
     *
     * @param id 资讯id
     * @return
     */
    void setRead(Long id);

    /**
     * 获取资讯留言列表信息
     *
     * @param apiInformationMessageParam
     * @return ApiInformationInfoVo
     */
    PageUtils<ApiInformationMessageVo> getMessageList(ApiInformationMessageParam apiInformationMessageParam);

    /**
     * 资讯留言
     *
     * @param apiInformationMessageDto
     */
    void leave(ApiInformationMessageDto apiInformationMessageDto);

    /**
     * 资讯信息点赞
     *
     * @param type 操作类型 0-点赞 1-取消点赞
     * @param infoId
     */
    void like(Integer type, Long infoId);

    /**
     * 资讯留言点赞
     *
     * @param type 操作类型 0-点赞 1-取消点赞
     * @param messageId
     */
    void messageLike(Integer type, Long messageId);

    /**
     * 资讯留言删除
     *
     * @param id
     */
    void delete(Long id);

}
