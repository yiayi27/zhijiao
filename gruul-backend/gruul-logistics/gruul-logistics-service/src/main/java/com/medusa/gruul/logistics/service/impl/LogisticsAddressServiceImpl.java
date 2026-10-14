package com.medusa.gruul.logistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.integral.api.model.IntegralOrderDeliveryDto;
import com.medusa.gruul.integral.api.model.IntegralOrderVo;
import com.medusa.gruul.logistics.api.entity.LogisticsAddress;
import com.medusa.gruul.logistics.api.entity.LogisticsCompany;
import com.medusa.gruul.logistics.api.entity.LogisticsExpressPrint;
import com.medusa.gruul.logistics.api.entity.LogisticsShop;
import com.medusa.gruul.logistics.mapper.*;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsAddressDto;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsBatchDeliverDto;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsPrintDeliverDto;
import com.medusa.gruul.logistics.model.dto.manager.express.ExpressInfoDto;
import com.medusa.gruul.logistics.model.enums.AddressDefaultEnum;
import com.medusa.gruul.logistics.model.enums.AddressTypeEnum;
import com.medusa.gruul.logistics.model.param.LogisticsAddressParam;
import com.medusa.gruul.logistics.model.param.LogisticsExpressPrintParam;
import com.medusa.gruul.logistics.model.vo.*;
import com.medusa.gruul.logistics.service.ILogisticsAddressService;
import com.medusa.gruul.logistics.util.express.kuaidihelp.ExpressHelp;
import com.medusa.gruul.order.api.entity.OrderDelivery;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.OrderDeliveryDto;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收发货地址管理业务层
 *
 * @author zhaozheng
 */
@Service
@Slf4j
public class LogisticsAddressServiceImpl implements ILogisticsAddressService {

    @Autowired
    private LogisticsAddressMapper logisticsAddressMapper;
    @Resource
    private LogisticsShopMapper logisticsShopMapper;
    @Resource
    private LogisticsCompanyMapper logisticsCompanyMapper;
    @Resource
    private RemoteOrderService remoteOrderService;
    @Autowired
    private LogisticsExpressPrintMapper logisticsExpressPrintMapper;
    @Autowired
    private LogisticsExpressAddressMapper logisticsExpressAddressMapper;
    @Resource
    private RemoteIntegralService remoteIntegralService;


    /**
     * 查询收发货地址列表
     *
     * @param logisticsAddressParam 发货地址查询参数
     * @return IPage<LogisticsAddressVo>
     */
    @Override
    public IPage<LogisticsAddressVo> getAddressList(LogisticsAddressParam logisticsAddressParam) {
        IPage<LogisticsAddressVo> page = new Page<>(logisticsAddressParam.getCurrent(),
                logisticsAddressParam.getSize());
        List<LogisticsAddressVo> logisticsAddressVos = this.logisticsAddressMapper
                .queryLogisticsAddressList(page, logisticsAddressParam);
        return page.setRecords(logisticsAddressVos);
    }

    /**
     * 查询所有地址
     *
     * @return List<LogisticsAddressVo>
     */
    @Override
    public List<LogisticsAddressVo> getAllAddressList() {
        return this.logisticsAddressMapper.queryAllLogisticsAddressList();
    }

    /**
     * 更新/新增 地址
     *
     * @param logisticsAddressDto 地址信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAddress(LogisticsAddressDto logisticsAddressDto) {
        // 新增,修改地址
        if (logisticsAddressDto.getId() == null) {
            //验证是否有一模一样的地址信息 全信息匹配
            Long logisticsAddressSearch = this.logisticsAddressMapper.selectCount(
                    new LambdaQueryWrapper<LogisticsAddress>().eq(LogisticsAddress::getName, logisticsAddressDto.getName())
                            .eq(LogisticsAddress::getProvinceId, logisticsAddressDto.getProvinceId())
                            .eq(LogisticsAddress::getCityId, logisticsAddressDto.getCityId())
                            .eq(LogisticsAddress::getCountryId, logisticsAddressDto.getCountryId())
                            .eq(LogisticsAddress::getAddress, logisticsAddressDto.getAddress())
                            .eq(LogisticsAddress::getZipCode, logisticsAddressDto.getZipCode())
                            .eq(LogisticsAddress::getPhone, logisticsAddressDto.getPhone()));
            if (logisticsAddressSearch > 0) {
                throw new ServiceException("有相同信息的地址！", SystemCode.DATA_ADD_FAILED.getCode());
            }
            //新增 先判断是否有数据  没有的话设置为默认收发货地址
            Long count = this.logisticsAddressMapper.selectCount(new LambdaQueryWrapper<>());
            LogisticsAddress logisticsAddress = logisticsAddressDto.coverBean();
            if (count == 0) {
                logisticsAddress.setDefSend(AddressDefaultEnum.YES.getAddressDefault());
                logisticsAddress.setDefReceive(AddressDefaultEnum.YES.getAddressDefault());
            }
            int insert = this.logisticsAddressMapper.insert(logisticsAddress);
            if (insert == 0) {
                throw new ServiceException("地址新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        } else {
            //修改
            LogisticsAddress logisticsAddress = this.logisticsAddressMapper.selectById(logisticsAddressDto.getId());
            if (BeanUtil.isEmpty(logisticsAddress)) {
                throw new ServiceException("地址不存在！", SystemCode.DATA_EXISTED.getCode());
            }
            //验证是否有一模一样的地址信息 全信息匹配
            Long logisticsAddressSearch = this.logisticsAddressMapper.selectCount(
                    new LambdaQueryWrapper<LogisticsAddress>().eq(LogisticsAddress::getName, logisticsAddressDto.getName())
                            .eq(LogisticsAddress::getProvinceId, logisticsAddressDto.getProvinceId())
                            .eq(LogisticsAddress::getCityId, logisticsAddressDto.getCityId())
                            .eq(LogisticsAddress::getCountryId, logisticsAddressDto.getCountryId())
                            .eq(LogisticsAddress::getAddress, logisticsAddressDto.getAddress())
                            .eq(LogisticsAddress::getZipCode, logisticsAddressDto.getZipCode())
                            .eq(LogisticsAddress::getPhone, logisticsAddressDto.getPhone()).ne(LogisticsAddress::getId, logisticsAddressDto.getId()));
            if (logisticsAddressSearch > 0) {
                throw new ServiceException("有相同信息的地址！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            logisticsAddress.setName(logisticsAddressDto.getName());
            logisticsAddress.setAddress(logisticsAddressDto.getAddress());
            logisticsAddress.setPhone(logisticsAddressDto.getPhone());
            logisticsAddress.setZipCode(logisticsAddressDto.getZipCode());
            logisticsAddress.setProvince(logisticsAddressDto.getProvince());
            logisticsAddress.setProvinceId(logisticsAddressDto.getProvinceId());
            logisticsAddress.setCity(logisticsAddressDto.getCity());
            logisticsAddress.setCityId(logisticsAddressDto.getCityId());
            logisticsAddress.setCountry(logisticsAddressDto.getCountry());
            logisticsAddress.setCountryId(logisticsAddressDto.getCountryId());
            int update = this.logisticsAddressMapper.updateById(logisticsAddress);
            if (update == 0) {
                throw new ServiceException("地址修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }

    /**
     * 设置 默认地址
     *
     * @param type 1-发货地址 2-收货地址
     * @param id  地址id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefAddress(Integer type, Long id) {
        if (type == AddressTypeEnum.DEFSEND.getAddressType()) {
            //说明是设置发货地址 先去除其他默认的发货地址
            boolean updateSign = new LambdaUpdateChainWrapper<>(logisticsAddressMapper)
                    .eq(LogisticsAddress::getDefSend, AddressDefaultEnum.YES.getAddressDefault())
                    .set(LogisticsAddress::getDefSend, AddressDefaultEnum.NO.getAddressDefault()).update();
            if (!updateSign) {
                throw new ServiceException("设置默认发货地址失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            } else {
                //设置为发货地址
                boolean sign = new LambdaUpdateChainWrapper<>(logisticsAddressMapper).eq(LogisticsAddress::getId, id)
                        .set(LogisticsAddress::getDefSend, AddressDefaultEnum.YES.getAddressDefault()).update();
                if (!sign) {
                    throw new ServiceException("设置默认发货地址失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }
        } else {
            //说明是设置收货地址 先去除其他默认的收货地址
            boolean updateSign = new LambdaUpdateChainWrapper<>(logisticsAddressMapper)
                    .eq(LogisticsAddress::getDefReceive, AddressDefaultEnum.YES.getAddressDefault())
                    .set(LogisticsAddress::getDefReceive, AddressDefaultEnum.NO.getAddressDefault()).update();
            if (!updateSign) {
                throw new ServiceException("设置默认发货地址失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            } else {
                //设置为收货地址
                boolean sign = new LambdaUpdateChainWrapper<>(logisticsAddressMapper).eq(LogisticsAddress::getId, id)
                        .set(LogisticsAddress::getDefReceive, AddressDefaultEnum.YES.getAddressDefault()).update();
                if (!sign) {
                    throw new ServiceException("设置默认收货地址失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }

        }

    }

    /**
     * 删除地址
     *
     * @param id 主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAddress(Long id) {
        LogisticsAddress logisticsAddress = this.logisticsAddressMapper.selectById(id);
        if (BeanUtil.isEmpty(logisticsAddress)) {
            throw new ServiceException("地址不存在！", SystemCode.DATA_EXISTED.getCode());
        } else {
            //判断是否为默认收/发货地址 是的话不允许删除
            if (AddressDefaultEnum.YES.getAddressDefault() == logisticsAddress.getDefReceive()
                    || AddressDefaultEnum.YES.getAddressDefault() == logisticsAddress.getDefSend()) {
                throw new ServiceException("默认地址不能删除！", SystemCode.DATA_DELETE_FAILED.getCode());
            } else {
                int delete = this.logisticsAddressMapper.deleteById(id);
                if (delete == 0) {
                    throw new ServiceException("地址删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
                }
            }
        }
    }

    /**
     * @param type 收发货类型 1-发货地址 2-收货地址
     *             获取默认收/发货地址
     */
    @Override
    public LogisticsAddressVo getDefaultAddress(Integer type) {
        try {
            LogisticsAddressVo logisticsAddress = this.logisticsAddressMapper.queryDefaultAddress(type);
            if (BeanUtil.isEmpty(logisticsAddress)) {
                throw new ServiceException("暂无发货地址,请添加！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            return logisticsAddress;
        } catch (Exception e) {
            throw new ServiceException("发货地址有误,请核对发货地址！", SystemCode.DATA_DELETE_FAILED.getCode());
        }
    }

    /**
     * 获取打印机与快递公司信息
     *
     * @param shopId 店铺id
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> listLogisticsCompany(String shopId) {
        Map<String, Object> res = new HashMap<>(CommonConstants.NUMBER_TWO);
        Map<String, Object> param = new HashMap<>(CommonConstants.NUMBER_THREE);
        param.put("shopId", shopId);
        List<LogisticsCompany> logisticsCompanies = logisticsCompanyMapper.selectListCompany(param);
        List<LogisticsCompanyVo> logisticsCompanyVos = new ArrayList<>(logisticsCompanies.size());
        LogisticsShop logisticsShop = logisticsShopMapper
                .selectOne(new LambdaQueryWrapper<LogisticsShop>().eq(LogisticsShop::getShopId, shopId).eq(LogisticsShop::getIsDefault, CommonConstants.NUMBER_ONE));
        if (CollectionUtil.isNotEmpty(logisticsCompanies)) {
            logisticsCompanies.forEach(logisticsCompany -> {
                LogisticsCompanyVo logisticsCompanyVo = new LogisticsCompanyVo();
                logisticsCompanyVo.setCode(logisticsCompany.getCode());
                logisticsCompanyVo.setName(logisticsCompany.getName());
                logisticsCompanyVo.setIsDefault(CommonConstants.NUMBER_ZERO);
                logisticsCompanyVo.setLogisticsCompanyId(logisticsCompany.getId());
                if ((null != logisticsShop) && (logisticsCompany.getId()
                        .equals(logisticsShop.getLogisticsCompanyId()))) {
                    logisticsCompanyVo.setIsDefault(CommonConstants.NUMBER_ONE);
                }
                List<LogisticsExpressAddressVo> logisticsExpressAddressVos = logisticsExpressAddressMapper
                        .queryByExpressCode(logisticsCompany.getCode());
                logisticsCompanyVo.setLogisticsExpressAddressVos(logisticsExpressAddressVos);
                logisticsCompanyVos.add(logisticsCompanyVo);
            });
        }
        //打印机列表
        List<LogisticsExpressPrintVo> logisticsExpressPrintVos = this.logisticsExpressPrintMapper
                .queryLogisticsExpressPrintList(null, new LogisticsExpressPrintParam());
        res.put("LogisticsPrinterVos", logisticsExpressPrintVos);
        res.put("logisticsCompanyVos", logisticsCompanyVos);

        return res;
    }

    /**
     * 设置默认打印快递公司
     *
     * @param logisticsCompanyId 快递公司id
     */
    @Override
    public void setCompanyDefault(Long logisticsCompanyId) {
        LogisticsShop logisticsShop = logisticsShopMapper
                .selectOne(new LambdaQueryWrapper<LogisticsShop>().eq(LogisticsShop::getIsDefault, CommonConstants.NUMBER_ONE));
        if (BeanUtil.isEmpty(logisticsShop)) {
            logisticsShop = new LogisticsShop();
            logisticsShop.setIsDefault(CommonConstants.NUMBER_ONE);
            logisticsShop.setLogisticsCompanyId(logisticsCompanyId);
            logisticsShop.setCreateTime(LocalDateTime.now());
            int insert = logisticsShopMapper.insert(logisticsShop);
            if (insert == 0) {
                throw new ServiceException("设置失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        } else {
            logisticsShop.setUpdateTime(LocalDateTime.now());
            logisticsShop.setLogisticsCompanyId(logisticsCompanyId);
            int update = logisticsShopMapper.updateById(logisticsShop);
            if (update == 0) {
                throw new ServiceException("设置失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }

    /**
     * 快递订单打印并发货 (通知订单服务修改状态)
     *
     * @param logisticsPrintDeliverDto 打印并发货dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void normalPrintDeliverGoods(LogisticsPrintDeliverDto logisticsPrintDeliverDto) {
        OrderSetting orderSetting = remoteOrderService.getOrderSetting();
        if (StrUtil.isEmpty(orderSetting.getKdHundredKey())) {
            throw new ServiceException("请先设置快递100KEY");
        }
        if (StrUtil.isEmpty(orderSetting.getKdHundredSecret())) {
            throw new ServiceException("请先设置快递100密钥");
        }
        //把订单状态设置成已发货
        Map<String, Object> param = new HashMap<>(CommonConstants.NUMBER_THREE);
        param.put("code", logisticsPrintDeliverDto.getDeliverCode());
        param.put("shopId", logisticsPrintDeliverDto.getShopId());
        LogisticsCompany logisticsCompany = logisticsCompanyMapper.selectListCompanyByParam(param);
        if (null == logisticsCompany) {
            throw new ServiceException("发货物流公司已下架");
        }
        List<Long> orderIds = logisticsPrintDeliverDto.getOrderIds();
        if (CollectionUtil.isEmpty(orderIds)) {
            throw new ServiceException("订单id不存在");
        }
        List<OrderDeliveryDto> orderDeliveryDtos = new ArrayList<>(orderIds.size());
        List<IntegralOrderDeliveryDto> integralOrderDeliveryDtoList = new ArrayList<>(orderIds.size());
        //获取发货地址信息以及对应物流公司信息
        LogisticsExpressAddressVo logisticsExpressAddressVo = this.logisticsExpressAddressMapper
                .queryByExpressId(logisticsPrintDeliverDto.getExpressId());
        if (null == logisticsExpressAddressVo) {
            throw new ServiceException("暂无此快递公司发货信息！", SystemCode.DATA_ADD_FAILED.getCode());
        }
        LogisticsExpressPrint logisticsExpressPrint = this.logisticsExpressPrintMapper
                .selectById(logisticsPrintDeliverDto.getLogisticsExpressPrintId());
        if(BeanUtil.isEmpty(logisticsExpressPrint)){
            throw new ServiceException("打印机信息不存在！", SystemCode.DATA_ADD_FAILED.getCode());
        }
        orderIds.forEach(orderId -> {
            ExpressInfoDto expressInfoDto;
            //是否为积分订单
            if (logisticsPrintDeliverDto.getIsIntegralOrder() == null) {
                expressInfoDto = convertDelivery(logisticsPrintDeliverDto.getDeliverCode(), orderId);
            } else {
                expressInfoDto = convertIntegralDelivery(logisticsPrintDeliverDto.getDeliverCode(), orderId);
            }
            log.info("expressInfoDto {} ", JSONUtil.toJsonStr(expressInfoDto));
            if(StringUtils.isNotBlank(expressInfoDto.getTaskId())){
                ExpressHelp.expressPrintOld(expressInfoDto, orderSetting);
            }else{
                expressInfoDto.setKdHundredTempId(logisticsExpressPrint.getKdHundredTempId());
                expressInfoDto.setPrintCode(logisticsPrintDeliverDto.getPrintCode());
                JSONObject jsonObject = ExpressHelp.getExpressPrint(expressInfoDto, logisticsExpressAddressVo, orderSetting);
                //taskId回写到快递表中 方便后续复打
                String taskId = jsonObject.getStr("taskId");
                if (logisticsPrintDeliverDto.getIsIntegralOrder() == null) {
                    OrderDeliveryDto orderDeliveryDto = new OrderDeliveryDto();
                    orderDeliveryDto.setOrderId(orderId);
                    orderDeliveryDto.setDeliveryCompany(logisticsCompany.getName());
                    orderDeliveryDto.setDeliveryCode(logisticsCompany.getCode());
                    orderDeliveryDto.setDeliverySn(String.valueOf(jsonObject.getStr("kuaidinum")));
                    orderDeliveryDto.setTaskId(taskId);
                    orderDeliveryDtos.add(orderDeliveryDto);
                }else{
                    IntegralOrderDeliveryDto orderDeliveryDto = new IntegralOrderDeliveryDto();
                    orderDeliveryDto.setIntegralOrderId(orderId);
                    orderDeliveryDto.setDeliveryCompany(logisticsCompany.getName());
                    orderDeliveryDto.setDeliveryCode(logisticsCompany.getCode());
                    orderDeliveryDto.setDeliverySn(String.valueOf(jsonObject.getStr("kuaidinum")));
                    orderDeliveryDto.setTaskId(taskId);
                    integralOrderDeliveryDtoList.add(orderDeliveryDto);
                }
            }
        });
        //发货
        int i;
        if (logisticsPrintDeliverDto.getIsIntegralOrder() == null) {
            i = remoteOrderService.doLogisticsOrderDelivery(JSONUtil.toJsonStr(orderDeliveryDtos));
        }else{
            i = remoteIntegralService.doLogisticsIntegralOrderDelivery(JSONUtil.toJsonStr(orderDeliveryDtos));
        }
        if (i < CommonConstants.NUMBER_ONE) {
            throw new ServiceException("发货失败");
        }
        log.info("更改订单为已发货，结果 ： " + i);
    }

    /***
     * 获取订单物流信息
     * @param waybillNo 快递单号
     * @param shipperType 快递公司编码
     * @param mobile 收件人手机号码
     * @return List<QueryTrackData>
     */
    @Override
    public List<QueryTrackData> getExpressInfo(String waybillNo, String shipperType, String mobile) {
        OrderSetting orderSetting = remoteOrderService.getOrderSetting();
        if (StrUtil.isEmpty(orderSetting.getKdHundredKey())) {
            throw new ServiceException("请先设置快递100KEY");
        }
        if (StrUtil.isEmpty(orderSetting.getKdHundredSecret())) {
            throw new ServiceException("请先设置快递100密钥");
        }
        ExpressInfoDto expressInfo = new ExpressInfoDto();
        expressInfo.setShipperType(shipperType);
        expressInfo.setDeliverySn(waybillNo);
        expressInfo.setMobile(mobile);
        return ExpressHelp.getExpressWayBill(expressInfo, orderSetting);
    }

    private ExpressInfoDto convertDelivery(String code, Long orderId) {
        ExpressInfoDto expressInfoDto = new ExpressInfoDto();
        expressInfoDto.setOrderId(String.valueOf(orderId));
        expressInfoDto.setShipperType(code);
        OrderVo orderVo = remoteOrderService.orderInfo(orderId);
        log.info(" remoteOrderService.orderInfo {}", JSONUtil.toJsonStr(orderVo));
        OrderDelivery orderDelivery = orderVo.getOrderDelivery();
        expressInfoDto.setName(orderDelivery.getReceiverName());
        expressInfoDto.setMobile(orderDelivery.getReceiverPhone());
        expressInfoDto.setProvince(orderDelivery.getReceiverProvince());
        expressInfoDto.setCity(orderDelivery.getReceiverCity());
        expressInfoDto.setDistrict(orderDelivery.getReceiverRegion());
        expressInfoDto.setAddress(orderDelivery.getReceiverDetailAddress());
        expressInfoDto.setDeliverySn(orderDelivery.getDeliverySn());

        List<OrderItemVo> orderItemList = orderVo.getOrderItemList();
        expressInfoDto.setTradeName(orderItemList.get(0).getProductName());
        List<String> itemNameList = new ArrayList<>(orderItemList.size());
        orderItemList.forEach(orderItem -> {
            itemNameList.add(orderItem.getProductName());
        });
        return expressInfoDto;
    }


    private ExpressInfoDto convertIntegralDelivery(String code, Long integralOrderId) {
        ExpressInfoDto expressInfoDto = new ExpressInfoDto();
        expressInfoDto.setOrderId(integralOrderId.toString());
        expressInfoDto.setShipperType(code);
        IntegralOrderVo integralOrderVo = remoteIntegralService.integralOrderInfo(integralOrderId);
        log.info("remoteIntegralService {}", JSONUtil.toJsonStr(integralOrderVo));
        expressInfoDto.setName(integralOrderVo.getReceiverName());
        expressInfoDto.setMobile(integralOrderVo.getTelPhone());
        String[] split = integralOrderVo.getAddress().split(" ");
        expressInfoDto.setProvince(split[0]);
        expressInfoDto.setCity(split[1]);
        expressInfoDto.setDistrict(split[2]);
        expressInfoDto.setAddress(split[3]);
        expressInfoDto.setTradeName(integralOrderVo.getIntegralGoodName());
        return expressInfoDto;
    }

    /**
     * 批量发货
     *
     * @param shopId 店铺id
     * @param logisticsBatchDeliverDtos 批量发货信息
     */
    @Override
    public void doBatchDeliver(List<LogisticsBatchDeliverDto> logisticsBatchDeliverDtos, String shopId) {
        if (null == logisticsBatchDeliverDtos || CommonConstants.NUMBER_ZERO == logisticsBatchDeliverDtos.size()) {
            throw new ServiceException("未获取到请求参数");
        }
        List<OrderDeliveryDto> list = new ArrayList<>(logisticsBatchDeliverDtos.size());
        logisticsBatchDeliverDtos.forEach(logisticsDeliverDto -> {
            Map<String, Object> param = new HashMap<>(CommonConstants.NUMBER_THREE);
            param.put("code", logisticsDeliverDto.getDeliverCode());
            param.put("shopId", shopId);
            LogisticsCompany logisticsCompany = logisticsCompanyMapper.selectListCompanyByParam(param);
            OrderDeliveryDto orderDeliveryDto = new OrderDeliveryDto();
            orderDeliveryDto.setOrderId(logisticsDeliverDto.getOrderId());
            orderDeliveryDto.setDeliveryCompany(logisticsCompany.getName());
            orderDeliveryDto.setDeliveryCode(logisticsDeliverDto.getDeliverCode());
            orderDeliveryDto.setDeliverySn(logisticsDeliverDto.getLogisticsCode());
            orderDeliveryDto.setOrderId(logisticsDeliverDto.getOrderId());
            list.add(orderDeliveryDto);
        });
        remoteOrderService.doLogisticsOrderDelivery(JSONUtil.toJsonStr(list));
    }
}
