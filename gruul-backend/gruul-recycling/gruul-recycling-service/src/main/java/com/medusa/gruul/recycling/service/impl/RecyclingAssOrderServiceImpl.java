package com.medusa.gruul.recycling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.CommonEnum;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrder;
import com.medusa.gruul.recycling.mapper.RecyclingAssOrderMapper;
import com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderListVo;
import com.medusa.gruul.recycling.model.vo.RecyclingSettingVo;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderAccountService;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderService;
import com.medusa.gruul.recycling.service.IRecyclingSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 基金所得订单关联表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Service
@Slf4j
public class RecyclingAssOrderServiceImpl extends ServiceImpl<RecyclingAssOrderMapper, RecyclingAssOrder> implements IRecyclingAssOrderService {

    @Autowired
    private IRecyclingSettingService recyclingSettingService;
    @Autowired
    private IRecyclingAssOrderAccountService recyclingAssOrderAccountService;
    @Autowired
    private RecyclingAssOrderMapper recyclingAssOrderMapper;

    /**
     * 用户下单成功，基金订单生成
     *
     * @param orderVo 订单对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderPayOk(OrderVo orderVo) {
        ISystem.switchTenant(
                orderVo.getShopId(),
                () -> {
                    RecyclingSettingVo service = recyclingSettingService.getSetting();
                    if (service.getGoodsFundState().equals(Boolean.FALSE)) {
                        log.info("基金功能关闭");
                        return;
                    }
                    RecyclingAssOrder recyclingAssOrder = this.baseMapper.selectOne(
                            new LambdaQueryWrapper<RecyclingAssOrder>().eq(RecyclingAssOrder::getOrderId, orderVo.getId())
                    );
                    if (recyclingAssOrder != null) {
                        return;
                    }
                    recyclingAssOrder = new RecyclingAssOrder();
                    recyclingAssOrder.setCreateTime(orderVo.getPayTime());
                    recyclingAssOrder.setOrderId(orderVo.getId());
                    recyclingAssOrder.setPayAmount(orderVo.getPayAmount());
                    recyclingAssOrder.setShopUserId(Long.valueOf(orderVo.getUserId()));
                    recyclingAssOrder.setFundQuota(service.getFundQuota().multiply(new BigDecimal(orderVo.getProductTotalQuantity())));
                    recyclingAssOrder.setStatus(CommonConstants.NUMBER_ONE);
                    recyclingAssOrder.setNickName(orderVo.getUserName());
                    recyclingAssOrder.setAvatarUrl(orderVo.getUserAvatarUrl());
                    recyclingAssOrder.setAvatarUrl(orderVo.getUserAvatarUrl());
                    recyclingAssOrder.setProductTotalQuantity(orderVo.getProductTotalQuantity());
                    recyclingAssOrderMapper.insertOrUpdate(recyclingAssOrder);
                    recyclingAssOrderAccountService.addRecyclingAssOrder(recyclingAssOrder);
                }
        );
    }

    /**
     * 获取用户基金订单
     *
     * @param page 页数
     * @param size 条数
     * @return com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderList
     */
    @Override
    public ResultPageList<MeRecyclingAssOrderListVo> recyclingAssOrderAccountList(Integer page, Integer size) {
        Page<RecyclingAssOrder> pageResult = this.baseMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<RecyclingAssOrder>()
                        .eq(RecyclingAssOrder::getShopUserId, ISecurity.userMust().getId())
                        .eq(RecyclingAssOrder::getStatus, CommonConstants.NUMBER_ONE)
                        .orderByDesc(RecyclingAssOrder::getCreateTime)
        );
        List<RecyclingAssOrder> records = pageResult.getRecords();
        ResultPageList<MeRecyclingAssOrderListVo> resultPageList = new ResultPageList<MeRecyclingAssOrderListVo>(CommonEnum.SUCCESS_RESPONSE, pageResult, records);
        if (CollectionUtil.isNotEmpty(records)) {
            List<MeRecyclingAssOrderListVo> listVos = records.stream().map(obj -> {
                MeRecyclingAssOrderListVo vo = new MeRecyclingAssOrderListVo();
                vo.setOrderId(obj.getOrderId());
                vo.setOrderPayTime(obj.getCreateTime());
                vo.setPayAmount(obj.getPayAmount());
                return vo;
            }).toList();
            resultPageList.setData(listVos);
        }
        return resultPageList;
    }

    /**
     * 订单退款通知
     *
     * @param orderVo com.medusa.gruul.order.api.model.OrderVo
     */
    @Override
    public void refundNotify(OrderVo orderVo) {
        RecyclingAssOrder recyclingAssOrder = this.baseMapper.selectOne(
                new LambdaQueryWrapper<RecyclingAssOrder>().eq(RecyclingAssOrder::getOrderId, orderVo.getId())
        );
        log.info("RecyclingAssOrder: {}", JSONUtil.toJsonStr(recyclingAssOrder));
        if (recyclingAssOrder == null) {
            log.info("不存在有效订单: {}", orderVo.getId());
            return;
        }
        //有效订单才可进行回退
        BigDecimal multiply = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;
        if (recyclingAssOrder.getStatus().equals(CommonConstants.NUMBER_ONE)) {
            RecyclingAssOrder recyclingAssOrder1 = new RecyclingAssOrder().setId(recyclingAssOrder.getId());
            if (orderVo.getStatus() == OrderStatusEnum.REFUNDED) {
                recyclingAssOrder1.setStatus(CommonConstants.NUMBER_TWO);
                recyclingAssOrder.setStatus(CommonConstants.NUMBER_TWO);
            } else {
                multiply = new BigDecimal(orderVo.getRefundQuantity())
                        .multiply(recyclingAssOrder.getFundQuota()
                                .divide(new BigDecimal(recyclingAssOrder.getProductTotalQuantity())));
                recyclingAssOrder1.setFundQuota(recyclingAssOrder.getFundQuota()
                        .subtract(multiply));
                recyclingAssOrder.setFundQuota(recyclingAssOrder.getFundQuota()
                        .subtract(multiply));
                int num = recyclingAssOrder.getProductTotalQuantity() - orderVo.getRefundQuantity();
                log.info("num is ---: {}", num);
                recyclingAssOrder1.setProductTotalQuantity(num);
                if (num <= 0) {
                    recyclingAssOrder1.setStatus(CommonConstants.NUMBER_TWO);
                    recyclingAssOrder.setStatus(CommonConstants.NUMBER_TWO);
                } else {
                    recyclingAssOrder.setFundQuota(recyclingAssOrder.getFundQuota()
                            .subtract(multiply));
                }
                refundAmount = orderVo.getRefundAmount();
            }
            recyclingAssOrderMapper.updateById(recyclingAssOrder1);
            recyclingAssOrderAccountService.removeRecyclingAssOrder(recyclingAssOrder, multiply, refundAmount);
        }

    }


}
