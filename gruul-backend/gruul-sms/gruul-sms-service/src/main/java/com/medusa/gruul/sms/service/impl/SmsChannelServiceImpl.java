package com.medusa.gruul.sms.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.sms.AliYunSmsUtil;
import com.medusa.gruul.common.core.sms.SmsSendConfig;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.sms.api.constant.SmsChannelEnum;
import com.medusa.gruul.sms.api.dto.SmsTemplateDto;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.api.model.dto.SmsChannelDto;
import com.medusa.gruul.sms.api.model.entity.SmsChannel;
import com.medusa.gruul.sms.api.model.param.SmsChannelParam;
import com.medusa.gruul.sms.api.model.vo.SmsChannelVo;
import com.medusa.gruul.sms.dao.mapper.*;
import com.medusa.gruul.sms.service.ISmsChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright(C) 2023-12-22 17:33 美杜莎 Inc.ALL Rights Reserved.
 * <p>
 * 短息渠道商信息 服务实现类
 * </p>
 * @version V1.0
 * @author: lcysike
 **/
@Service
@Slf4j
public class SmsChannelServiceImpl extends ServiceImpl<SmsChannelMapper, SmsChannel> implements ISmsChannelService {

    private static final Pattern COMPILE = Pattern.compile("\\$\\{([^}]*)\\}");

    /**
     * 查询渠道商列表
     *
     * @param smsChannelParam 渠道商查询条件
     * @return 分页对象
     */
    @Override
    public IPage<SmsChannelVo> getChannelList(SmsChannelParam smsChannelParam) {
        IPage<SmsChannelVo> page = new Page<>(smsChannelParam.getCurrent(), smsChannelParam.getSize());
        List<SmsChannelVo> selectChannelList = this.baseMapper.selectChannelList(page, smsChannelParam);
        return page.setRecords(selectChannelList);
    }

    /**
     * 查询单个渠道商信息
     *
     * @param id 渠道商id
     * @return ProductVo对象
     */
    @Override
    public SmsChannelVo getInfoById(Long id) {
        return this.baseMapper.getChannelById(id);
    }

    /**
     * 创建渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    @Override
    public void create(SmsChannelDto smsChannelDto) {
        SmsChannel smsChannel = smsChannelDto.coverBean();
        int insert = this.baseMapper.insert(smsChannel);
        if (insert == 0) {
            throw new ServiceException("渠道商信息新增失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 修改渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    @Override
    public void update(SmsChannelDto smsChannelDto) {
        SmsChannel smsChannel = checkExistData(smsChannelDto.getId());
        //如果是需要开启状态，则把其他渠道商状态改为关闭
        if(CommonConstants.NUMBER_ONE.equals(smsChannelDto.getStatus())){
            new LambdaUpdateChainWrapper<>(this.baseMapper)
                    .set(SmsChannel::getStatus, CommonConstants.NUMBER_ZERO)
                    .update();
        }
        BeanUtils.copyProperties(smsChannelDto,smsChannel);
        int update = this.baseMapper.updateById(smsChannel);
        if (update == 0) {
            throw new ServiceException("渠道商信息修改失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 删除渠道商信息
     *
     * @param id 渠道商id
     */
    @Override
    public void delete(Long id) {
        checkExistData(id);
        int delete = this.baseMapper.deleteById(id);
        if (delete == 0) {
            throw new ServiceException("渠道商信息删除失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 发送短信
     *
     * @param smsTemplateSendDto 发送内容
     */
    @Override
    public void sendSms(SmsTemplateSendDto smsTemplateSendDto) {
        SmsChannel smsChannel = this.baseMapper.selectOne(new LambdaQueryWrapper<SmsChannel>()
                .eq(SmsChannel::getStatus, CommonConstants.NUMBER_ZERO));
        if(BeanUtil.isEmpty(smsChannel)){
            throw new ServiceException("暂无有效渠道商信息！", SystemCode.DATA_NOT_EXIST.getCode());
        }
        // 获取 templateCode
        Long templateCode = smsTemplateSendDto.getTemplateCode();
        // 根据 templateCode 获取对应的 SmsTemplateDto 对象
        SmsTemplateDto smsTemplateDto = getTemplateDtoByKey(smsChannel, templateCode);
        if (BeanUtil.isEmpty(smsTemplateDto)) {
            throw new ServiceException("模板信息不存在！", SystemCode.DATA_NOT_EXIST.getCode());
        }
        String[] mobileList = smsTemplateSendDto.getMobile().split(StrUtil.COMMA);

        for (String phone : mobileList) {
            if (SmsChannelEnum.ALIYUN.getCode().equals(smsChannel.getCode())) {
                //阿里短信
                String res = sendAliYunSms(smsChannel.getApiKey(),
                        smsChannel.getApiSecret(),
                        phone,
                        smsChannel.getSignature(),
                        smsTemplateDto.getApiTemplateId(), smsTemplateSendDto.getTemplateParams(), smsTemplateDto.getTemplateContent());
            } else if (SmsChannelEnum.TENCENT.getCode().equals(smsChannel.getCode())) {
                //腾讯短信
                ArrayList<String> params = covertTxParams(smsTemplateSendDto.getTemplateParams());
                String res = sendTxSms(Integer.parseInt(smsChannel.getApiKey()),
                        smsChannel.getApiSecret(),
                        phone, smsChannel.getSignature()
                        , Integer.parseInt(smsTemplateDto.getApiTemplateId()), params);
            }
        }
    }

    public SmsTemplateDto getTemplateDtoByKey(SmsChannel smsChannel, Long templateCode) {
        // 获取 templateInfo JSON 数据
        JSONArray templateInfoArray = JSONUtil.parseArray(smsChannel.getTemplateInfo());
        // 使用 Stream API 查找匹配的 key 并转换为 SmsTemplateDto
        return templateInfoArray.stream()
                .map(item -> (JSONObject) item)
                .filter(item -> {
                    item.getStr("key");
                    return false;
                })
                .findFirst()
                .map(item -> BeanUtil.toBean(item.getJSONObject("value"), SmsTemplateDto.class))
                .orElse(new SmsTemplateDto());
    }

    /**
     * 校验渠道商是否存在
     *
     * @param id 渠道商id
     */
    public SmsChannel checkExistData(Long id){
        SmsChannel smsChannel = this.baseMapper.selectById(id);
        if(BeanUtil.isEmpty(smsChannel)){
            throw new ServiceException("渠道商信息不存在！", SystemCode.DATA_NOT_EXIST.getCode());
        }
        return smsChannel;
    }

    /**
     * 阿里短信源码自带单例
     */
    public String sendAliYunSms(String appKey, String appSecret, String phone, String sign, String templateCode, String params, String content) {

        try {
            params = covertAliParams(params, content);
            String res = AliYunSmsUtil.sendSms(appKey, appSecret, phone, sign, templateCode, params);
            log.info(STR."ali call back :\{res}");
            return res;
        } catch (Exception e) {
            throw new ServiceException(STR."send ali sms error :\{e.getMessage()}", SystemCode.FAILURE.getCode());
        }
    }


    public String sendTxSms(int appKey, String appSecret, String phone, String sign, int templateCode, ArrayList<String> params) {
        SmsSingleSender sender = new SmsSingleSender(appKey, appSecret);
        try {
            SmsSingleSenderResult smsSingleSenderResult = sender.sendWithParam(SmsSendConfig.ZONE, phone,
                    templateCode, params, sign, "", "");
            String res = Jackson.writeValueAsString(smsSingleSenderResult);
            log.info(STR."tx call back :\{res}");
            return res;
        } catch (Exception e) {
            throw new ServiceException(STR."腾讯短信发送异常:\{e.getMessage()}", SystemCode.FAILURE.getCode());
        }
    }


    private String covertAliParams(String params, String content) {

        Map<String, Object> param = new HashMap<>(CommonConstants.NUMBER_FIVE);
        if (StringUtils.isBlank(params)) {
            return "";
        }
        List<String> vars = getVars(content);
        if (params.contains(SmsSendConfig.SMS_PARAM_SEPARATOR)) {
            String[] split = params.split(SmsSendConfig.SMS_PARAM_SPLIT_SEPARATOR);
            for (int i = 0; i < split.length; i++) {

                param.put(vars.get(i), split[i]);
            }
        } else {
            param.put(vars.get(CommonConstants.NUMBER_ZERO), params);
        }
        return Jackson.writeValueAsString(param);
    }

    private ArrayList<String> covertTxParams(String params) {
        ArrayList<String> paramsList;
        if (StringUtils.isBlank(params)) {
            return new ArrayList<>();
        }
        if (params.contains(SmsSendConfig.SMS_PARAM_SEPARATOR)) {

            String[] split = params.split(SmsSendConfig.SMS_PARAM_SPLIT_SEPARATOR);
            paramsList = new ArrayList<>(Arrays.asList(split));
            return paramsList;
        }
        paramsList = new ArrayList<>(CommonConstants.NUMBER_ONE);
        paramsList.add(params);
        return paramsList;
    }

    private List<String> getVars(String content) {
        List<String> vars = new ArrayList<>(CommonConstants.NUMBER_FIVE);
        Matcher matcher = COMPILE.matcher(content);
        while (matcher.find()) {
            vars.add(matcher.group(CommonConstants.NUMBER_ONE));
        }
        return vars;
    }
}
