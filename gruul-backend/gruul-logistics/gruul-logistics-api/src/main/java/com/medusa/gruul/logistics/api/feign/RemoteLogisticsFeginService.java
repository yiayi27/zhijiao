package com.medusa.gruul.logistics.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.logistics.model.dto.manager.express.ExpressInfoDto;
import com.medusa.gruul.logistics.model.vo.LogisticsAddressVo;
import com.medusa.gruul.logistics.model.vo.LogisticsTemplateVo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhaozheng
 */
@FeignClient(value = "logistics")
@Hidden
public interface RemoteLogisticsFeginService {
    /**
     * 获取模板列表
     *
     * @param shopId   店铺 id
     * @return Result
     */
    @GetMapping(value = "/feign/get/list")
    Result<List<LogisticsTemplateVo>> getLogisticList(@RequestParam("shopId") String shopId);

    /**
     * 根据模板id获取详情
     *
     * @param id
     * @return Result
     */
    @GetMapping(value = "/feign/get/info")
    Result<LogisticsTemplateVo> getLogisticInfoById(@RequestParam("id") Long id);

    /**
     * 根据快递公司代码生成快递单号
     *
     * @param expressInfoDto 订单数据传输
     * @param addressId
     * @param shopId
     * @return Result
     */
    @GetMapping(value = "/feign/get/express/number")
    Result getLogisticsExpressNumber(@RequestParam("expressInfoDto") ExpressInfoDto expressInfoDto, @RequestParam("addressId") Long addressId, @RequestParam("shopId") String shopId);

    /**
     * 根据快递公司代码与运单号获取快递物流信息
     *
     * @param waybillNo   快递公司运单号
     * @param shipperType 快递公司code
     * @param mobile 收件人电话
     * @return Result
     */
    @GetMapping(value = "/feign/get/express/route")
    Result getLogisticsExpressInfo(@RequestParam("waybillNo") String waybillNo, @RequestParam("shipperType") String shipperType, @RequestParam("mobile") String mobile);

    /**
     * <p>@Description: getTraces 根据运单号获取运送路径 </P>
     * <p>Version: 1.0</p>
     * <p>Author: Mr.zhaozheng </P>
     * <p>Date: 2024-01-05 22:28 </p>
     * <pre>
     *     getTraces("") = {"code":200,"msg":"success","data":[]}
     *     getTraces(null) = {"code":200,"msg":"success","data":[]}
     *     getTraces("75324542769471") = {"code": 200,"msg": null,"data": {"code": 200,"msg": "成功","data": [{"acceptTime": "2023-12-31 19:59:47","acceptStation": "【成都市】  【成都新都】（028-62510551、028-89396822） 的 市场部华丽（13880646697） 已揽收","remark": null},{"acceptTime": "2023-12-31 21:14:16","acceptStation": "【成都市】  快件离开 【成都新都】 已发往 【成都中转】","remark": null}]}}
     * </pre>
     * @param logisticsCode 运单号
     * @return com.medusa.gruul.common.core.util.ResultList<com.medusa.gruul.logistics.model.LogisticsTemplateInfoDto>
     */

    /**
     * 运费计算
     *
     * @param logisticsFreightDtos
     * @param region
     * @return
     */
    @GetMapping(value = "/feign/freight/calculation")
    BigDecimal getLogisticsFreightCalculation(@RequestParam("logisticsFreightDtos") String logisticsFreightDtos, @RequestParam("region") String region);

    /**
     * 获取默认发/收货地址
     *
     * @param type 收发货类型 1-发货地址 2-收货地址
     * @return LogisticsAddressVo
     */
    @GetMapping(value = "/feign/get/default/address")
    LogisticsAddressVo getFeignDefaultAddress(@RequestParam("type") Integer type);

}
