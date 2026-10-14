package com.medusa.gruul.integral.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.mapper.IntegralRuleMapper;
import com.medusa.gruul.integral.mapper.IntegralShopMapper;
import com.medusa.gruul.integral.service.IIntegralDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description: 定期清空积分
 * @Author: whh
 * @Date: 2024/1/10 9:07
 */
@Slf4j
@Component
public class ClearIntegralJob {


    @Autowired
    private IntegralShopMapper integralShopMapper;

    @Autowired
    private IntegralRuleMapper integralRuleMapper;

    @Autowired
    private IIntegralDetailService integralDetailService;

    /**
     * todo  执行时间待定
     */
    @Scheduled(cron = "0 0 0 10 * *")
    public void execute() {

        List<IntegralShop> integralShops = integralShopMapper.selectAllShopInfo();
        if (integralShops.isEmpty()) {
            return;
        }
        for (IntegralShop integralShop : integralShops) {
            List<IntegralRule> rules = integralRuleMapper.getRules(integralShop.getId());
            if (CollectionUtil.isEmpty(rules)) {
                continue;
            }
            Integer validity = 0;
            String shopId = "";
            for (IntegralRule rule : rules) {
                validity = rule.getIntegralValidity();
                shopId = rule.getShopId();
                if (validity != null & shopId != null) {
                    break;
                }
            }
            if (StrUtil.isEmpty(shopId)) {
                continue;
            }
            LocalDateTime createTime = integralShop.getCreateTime();
            //清空积分
            check(validity, shopId, createTime);
        }
    }

    /**
     * 清空积分
     *
     * @param validity
     * @param shopId
     * @param createTime
     */
    private void check(Integer validity, String shopId, LocalDateTime createTime) {
        String userId;
        BigDecimal integral;
        //积分过期时间
        LocalDateTime staleDatedTime = createTime.plusMonths(validity);
        //现在时间
        LocalDateTime currentTime = LocalDateTime.now();

        log.debug(STR."\{staleDatedTime}-积分过期时间");
        log.debug(STR."\{currentTime}-现在时间");
        //现在时间大于 积分过期时间
        if (!(currentTime.isBefore(staleDatedTime))) {
            try {
                List<Map<String, Object>> maps = integralDetailService.selectAllUser(shopId);
                if (CollectionUtil.isNotEmpty(maps)) {
                    for (Map<String, Object> map : maps) {
                        //获取用户id 以及要清空的积分
                        userId = map.get("userId").toString();
                        integral = (BigDecimal) (map.get("integral"));
                        //2.生成清空积分明细
                        integralDetailService.generateNowIntegralDetailService(userId, integral, shopId);
                    }
                    //修改这个商铺的所有积分明细清空状态
                    integralDetailService.updateAll(shopId);

                    integralShopMapper.updateCreateTime(shopId, currentTime);
                }

            } catch (Exception e) {
                log.error("清空积分失败", e);
            }
        }
    }
}