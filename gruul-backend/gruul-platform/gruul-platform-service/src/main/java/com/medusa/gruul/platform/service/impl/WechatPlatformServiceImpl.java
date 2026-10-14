package com.medusa.gruul.platform.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaSubscribeService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.api.entity.PlatformShopMessage;
import com.medusa.gruul.platform.model.dto.MiniSubscriberiMessageDto;
import com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo;
import com.medusa.gruul.platform.service.IPlatformShopMessageService;
import com.medusa.gruul.platform.service.IWechatPlatformService;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.subscribemsg.CategoryData;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateTitleListResult;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 微信第三方相关 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-02-02
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatPlatformServiceImpl implements IWechatPlatformService {

    @Autowired
    private final WechatMiniAppService wechatMiniAppService;

    @Autowired
    @Lazy
    private IPlatformShopMessageService platformShopMessageService;

    /**
     * 获取指定appId服务类目
     *
     * @param shopId 店铺id
     * @return cn.binarywang.wx.miniapp.api.WxMaSubscribeService
     */
    @SneakyThrows
    @Override
    public List<CategoryData> getAppIdServiceClass(String shopId) {
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        return maService.getSubscribeService().getCategory();

    }

    /**
     * 获取模板标题列表
     *
     * @param shopId     店铺id
     * @param serviceIds 服务类目id 逗号分隔
     * @param page       页数
     * @param size       条数
     * @return 获取模板标题下的关键词库
     */
    @Override
    @SneakyThrows
    public PubTemplateTitleListResult getPubTemplateTitles(String shopId, String serviceIds, Integer page, Integer size) {
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        return maService.getSubscribeService().getPubTemplateTitleList(
                serviceIds.split(","),
                page,
                size
        );
    }

    /**
     * 获取模板标题下的关键词库
     *
     * @param shopId 店铺id
     * @param tid    模板标题 id，可通过接口获取
     * @return
     */
    @Override
    public List<PubTemplateKeyword> getPubTemplateKeywords(String shopId, String tid) {
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        //获取模板库某个模板标题下关键词库
        try {
            return subscribeService.getPubTemplateKeyWordsById(tid);
        } catch (WxErrorException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 清空小程序模板消息
     */
    @Override
    @SneakyThrows
    public void clearSubscribeMsg(String shopId) {
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        List<TemplateInfo> templateList = subscribeService.getTemplateList();
        if (CollectionUtil.isEmpty(templateList)) {
            return;
        }
        for (TemplateInfo templateInfo : templateList) {
            subscribeService.delTemplate(templateInfo.getPriTmplId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SneakyThrows
    public void addSubscribeMsg(String shopId) {

        List<PlatformShopMessage> old = platformShopMessageService.getByVersionMes(CommonConstants.NUMBER_ONE);
        Map<String, PlatformShopMessage> oldMap = new HashMap<>(old.size());
        //清空微信端现有的所有消息
        this.clearSubscribeMsg(shopId);
        if (CollectionUtil.isNotEmpty(old)) {
            oldMap = old.stream().collect(Collectors.toMap(PlatformShopMessage::getTitle, v -> v));
        }
        //更新指定版本买家消息
        List<PlatformShopMessage> newShopMsg = platformShopMessageService.getByVersionMes(CommonConstants.NUMBER_ONE);
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        List<CategoryData> categoryList = subscribeService.getCategory();
        Map<String, List<CategoryData>> classMap = categoryList.stream().collect(Collectors.groupingBy(CategoryData::getName));
        //设置新的默认消息的消息模板
        for (PlatformShopMessage shopMessage : newShopMsg) {
            if (StrUtil.isEmpty(shopMessage.getMiniMsg())) {
                continue;
            }
            MiniSubscriberiMessageDto miniSubscriberiMessageDto = JSONUtil.toBean(shopMessage.getMiniMsg(), MiniSubscriberiMessageDto.class);
            if (CollectionUtil.isEmpty(classMap.get(miniSubscriberiMessageDto.getSecondClass()))) {
                log.warn("该小程序({})不存在所属类目({}),无法生成", maService.getWxMaConfig().getAppid(), miniSubscriberiMessageDto.getSecondClass());
                return;
            }
            List<Integer> kids = Arrays.stream(miniSubscriberiMessageDto.getKIds().split(",")).map(Integer::valueOf).toList();
            String templateId = subscribeService.addTemplate(miniSubscriberiMessageDto.getTId(), kids, miniSubscriberiMessageDto.getTitle());
            PlatformShopMessage platformShopMessage = new PlatformShopMessage();
            platformShopMessage.setId(shopMessage.getId());
            platformShopMessage.setMiniTemplateId(templateId);
            //设置原本的消息开关
            PlatformShopMessage oldMessage = oldMap.get(shopMessage.getTitle());
            if (oldMessage != null) {
                oldMap.remove(shopMessage.getTitle());
                platformShopMessage.setMiniOpen(oldMessage.getMiniOpen());
                platformShopMessageService.updateById(platformShopMessage);
            }
        }
        if (!oldMap.isEmpty()) {
            oldMap.forEach((_, platformShopMessage) -> {
                platformShopMessageService.removeById(platformShopMessage.getId());
            });
        }

    }

    /**
     * 获取小程序模板
     *
     * @param templateId 模板id
     * @return com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo
     */
    @Override
    public List<WxOpenMaCodeTemplateVo> getTemplateList(Long templateId) {
        WxMaService maService = wechatMiniAppService.wxMaservice(ISystem.shopId());
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        List<TemplateInfo> templateList;
        try {
            templateList = subscribeService.getTemplateList();
        } catch (WxErrorException e) {
            log.error("", e);
            throw new ServiceException(e.getMessage(), SystemCode.WX_PLATEFROM_EXCEPTION);
        }
        //筛选指定模板
        if (CollectionUtil.isNotEmpty(templateList) && templateId != null) {
            templateList = templateList.stream().filter(obj -> ObjectUtil.equal(obj.getPriTmplId(), templateId)).toList();
        }

        if (CollectionUtil.isEmpty(templateList)) {
            return new ArrayList<>(0);
        }
        List<WxOpenMaCodeTemplateVo> vos = templateList.stream().map(obj -> {
            WxOpenMaCodeTemplateVo vo = new WxOpenMaCodeTemplateVo();
            BeanUtil.copyProperties(obj, vo);
            return vo;
        }).sorted(Comparator.comparing(WxOpenMaCodeTemplateVo::getTemplateId).reversed()).toList();
        return vos;

    }


}
