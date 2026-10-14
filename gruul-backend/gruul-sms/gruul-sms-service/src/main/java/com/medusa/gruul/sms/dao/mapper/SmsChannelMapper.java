package com.medusa.gruul.sms.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.sms.api.model.param.SmsChannelParam;
import com.medusa.gruul.sms.api.model.vo.SmsChannelVo;
import com.medusa.gruul.sms.api.model.entity.SmsChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 短信发送日志 数据层
 *
 * @author system
 */
public interface SmsChannelMapper extends BaseMapper<SmsChannel> {
    /**
     * 优惠劵、满减服务查询所有商品列表
     *
     * @param page
     * @param smsChannelParam
     * @return 商品list对象
     */
    List<SmsChannelVo> selectChannelList(IPage page, @Param("smsChannelParam") SmsChannelParam smsChannelParam);

    /**
     * 获取渠道商详细信息
     *
     * @param id
     * @return 单个渠道商详细信息
     */
    SmsChannelVo getChannelById(@Param("id") Long id);
}