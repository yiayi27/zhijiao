package com.medusa.gruul.sms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.api.model.dto.SmsChannelDto;
import com.medusa.gruul.sms.api.model.param.SmsChannelParam;
import com.medusa.gruul.sms.api.model.vo.SmsChannelVo;
import com.medusa.gruul.sms.api.model.entity.SmsChannel;

/**
 * Copyright(C) 2023-12-22 17:32 美杜莎 Inc.ALL Rights Reserved.
 * <p>
 * 短信渠道商信息 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
public interface ISmsChannelService extends IService<SmsChannel> {

    /**
     * 查询渠道商列表
     *
     * @param smsChannelParam
     * @return 分页对象
     */
    IPage<SmsChannelVo> getChannelList(SmsChannelParam smsChannelParam);

    /**
     * 查询单个渠道商信息
     *
     * @param id 渠道商id
     * @return ProductVo对象
     */
    SmsChannelVo getInfoById(Long id);


    /**
     * 创建渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    void create(SmsChannelDto smsChannelDto);


    /**
     * 修改渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    void update(SmsChannelDto smsChannelDto);

    /**
     * 删除渠道商信息
     *
     * @param id 渠道商id
     */
    void delete(Long id);

    /**
     * 发送短信
     *
     * @param smsTemplateSendDto 发送内容
     */
    void sendSms(SmsTemplateSendDto smsTemplateSendDto);




}
