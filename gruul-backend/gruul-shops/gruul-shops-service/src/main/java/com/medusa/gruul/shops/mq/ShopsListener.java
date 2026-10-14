package com.medusa.gruul.shops.mq;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.shops.api.constant.QueueNameConstant;
import com.medusa.gruul.shops.api.model.AccountCenterSettingDto;
import com.medusa.gruul.shops.service.IAccountCenterService;
import com.medusa.gruul.shops.service.IShopGuidePageService;
import com.medusa.gruul.shops.service.IShopGuidePageSwitchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author whh
 * @description 用户队列监听
 * @date: 2023/12/11
 */
@Slf4j
@Component
public class ShopsListener {


}
