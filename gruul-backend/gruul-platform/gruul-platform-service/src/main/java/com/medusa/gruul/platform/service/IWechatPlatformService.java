package com.medusa.gruul.platform.service;

import com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo;
import me.chanjar.weixin.common.bean.subscribemsg.CategoryData;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateTitleListResult;

import java.util.List;

/**
 * <p>
 * 微信第三方相关 服务类
 * </p>
 *
 * @author whh
 * @since 2024-02-02
 */
public interface IWechatPlatformService {

    /**
     * 获取指定appId服务类目
     *
     * @param shopId 店铺id
     * @return cn.binarywang.wx.miniapp.api.WxMaSubscribeService
     */
    List<CategoryData> getAppIdServiceClass(String shopId);

    /**
     * 获取模板标题列表
     *
     * @param shopId     店铺id
     * @param serviceIds 服务类目id 逗号分隔
     * @param page       页数
     * @param size       条数
     * @return 获取模板标题下的关键词库
     */
    PubTemplateTitleListResult getPubTemplateTitles(String shopId, String serviceIds, Integer page, Integer size);

    /**
     * 获取模板标题下的关键词库
     *
     * @param shopId 店铺id
     * @param tid    模板标题 id，可通过接口获取
     * @return
     */
    List<PubTemplateKeyword> getPubTemplateKeywords(String shopId, String tid);

    /**
     * 清空小程序模板消息
     * @param shopId 店铺id
     */
    void clearSubscribeMsg(String shopId);

    /**
     * 新增小程序指定版本模板消息
     *
     * @param shopId 店铺id
     */
    void addSubscribeMsg(String shopId);

    /**
     * 获取小程序模板
     *
     * @param templateId 模板id
     * @return com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo
     */
    List<WxOpenMaCodeTemplateVo> getTemplateList(Long templateId);

}
