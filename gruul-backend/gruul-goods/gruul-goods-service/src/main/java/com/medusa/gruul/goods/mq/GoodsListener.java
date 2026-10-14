package com.medusa.gruul.goods.mq;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.constant.GoodsMyQueneName;
import com.medusa.gruul.goods.service.manager.IProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品消息监听
 * </p>
 *
 * @author lcysike
 * @since 2023-11-19
 */

@Slf4j
@Service
public class GoodsListener {

    @Resource
    private IProductService productService;

    @RabbitListener(queues = GoodsMyQueneName.GENERATE_DEFAULT_GOODS, ackMode = "AUTO")
    public void receive(String message) {
        log.info("收到超时自动完成消息: deliveryTag{}, 当前时间{}, 消息内容{}", DateUtil.now(), DateUtil.now(), message);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            String shopId = jsonObject.getStr("shopId");
            ISystem.switchTenant(
                    shopId,
                    //生成默认商品关联数据信息
                    () -> productService.createDefaultProduct()
            );

            log.debug("超时自动完成执行完成: deliveryTag{}", DateUtil.now());
        } catch (Exception e) {
            log.info("处理消息时发生异常", e);
        }
    }
}
