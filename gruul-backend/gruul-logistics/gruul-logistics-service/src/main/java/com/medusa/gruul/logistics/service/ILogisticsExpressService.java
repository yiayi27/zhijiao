package com.medusa.gruul.logistics.service;


import com.medusa.gruul.logistics.model.dto.manager.LogisticsExpressDto;
import com.medusa.gruul.logistics.model.param.LogisticsExpressParam;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressVo;

import java.util.List;

/**
 * @author lcysike
 */
public interface ILogisticsExpressService {

    /**
     * 物流公司信息列表查询
     * @param logisticsExpressParam
     * @return List<LogisticsAddressVo>
     */
    List<LogisticsExpressVo> getExpressList(LogisticsExpressParam logisticsExpressParam);

    /**
     * 查询单条物流公司信息
     * @param id
     * @return LogisticsAddressVo
     */
    LogisticsExpressVo getExpressInfo(Long id);

    /**
     * 更新/新增 快递公司
     * @param logisticsExpressDto
     */
    void setExpressInfo(LogisticsExpressDto logisticsExpressDto);

    /**
     * 删除快递公司信息
     * @param id 主键
     */
    void delExpressInfo(Long id);
}
