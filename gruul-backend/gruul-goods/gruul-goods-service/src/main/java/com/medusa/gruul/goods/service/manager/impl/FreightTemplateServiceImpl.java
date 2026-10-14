package com.medusa.gruul.goods.service.manager.impl;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.service.manager.IFreightTemplateService;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.logistics.model.vo.LogisticsTemplateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 运费模版 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Service
public class FreightTemplateServiceImpl implements IFreightTemplateService {


    @Resource
    private RemoteLogisticsFeginService remoteLogisticsFeginService;

    /**
     * 物流接口获取所有运费模版
     *
     * @return 运费模版list
     */
    @Override
    public Result<List<LogisticsTemplateVo>> findFreightTemplateListByLogistics() {
        String shopId = ISecurity.userMust().getShopId();
        return remoteLogisticsFeginService.getLogisticList(shopId);
    }

    /**
     * 物流接口获取单个产品运费模版信息
     *
     * @param freightTemplateId 运费模版id
     * @return 运费模版对象
     */
    @Override
    public Result<LogisticsTemplateVo> findFreightTemplateInfoByLogistics(Long freightTemplateId) {
        return remoteLogisticsFeginService.getLogisticInfoById(freightTemplateId);
    }
}
