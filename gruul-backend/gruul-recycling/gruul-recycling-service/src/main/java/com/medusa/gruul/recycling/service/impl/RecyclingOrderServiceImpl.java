package com.medusa.gruul.recycling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.CommonEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.discount.api.feign.RemoteDiscountService;
import com.medusa.gruul.recycling.api.entity.RecyclingOrder;
import com.medusa.gruul.recycling.constant.RecyclingOrderStatus;
import com.medusa.gruul.recycling.mapper.RecyclingOrderMapper;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto;
import com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo;
import com.medusa.gruul.recycling.model.vo.RecyclingPcOrderListVo;
import com.medusa.gruul.recycling.model.vo.RecyclingSettingVo;
import com.medusa.gruul.recycling.service.IRecyclingOrderService;
import com.medusa.gruul.recycling.service.IRecyclingSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 回收订单表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Service
public class RecyclingOrderServiceImpl extends ServiceImpl<RecyclingOrderMapper, RecyclingOrder> implements IRecyclingOrderService {

    @Autowired
    private IRecyclingSettingService recyclingSettingService;
    @Autowired
    private RemoteDiscountService remoteDiscountService;
    @Autowired
    private RemoteMiniAccountService miniAccountService;
    @Autowired
    private RecyclingOrderMapper recyclingOrderMapper;

    /**
     * 判断是否存在，需要寄出的回收订单
     *
     * @return true 存在，false 不存在
     */
    @Override
    public Boolean exitWaitSendOrder() {
        String userId = ISecurity.userMust().getId();
        Page<RecyclingOrder> recyclingOrderPage = this.baseMapper.selectPage(
                new Page<>(1, 1),
                new LambdaQueryWrapper<RecyclingOrder>()
                        .eq(RecyclingOrder::getEdStatus, RecyclingOrderStatus.ED_STATUS_WAIT)
                        .eq(RecyclingOrder::getShopUserId, userId)
        );
        return recyclingOrderPage.getTotal() >= 1;
    }

    /**
     * 新增回收订单,返回当前订单获得的卡券
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto
     * @return String
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recyclingOrderNew(RecyclingOrderNewDto dto) {
        if(StrUtil.isEmpty(dto.getArticleImgs())){
            throw new ServiceException("物品图片不能为空");
        }
        RecyclingSettingVo service = recyclingSettingService.getSetting();
        if (!service.getRecyclingState()) {
            throw new ServiceException("当前未开启回收券功能");
        }
        if (exitWaitSendOrder()) {
            if (service.getHomePromptBgImg().isEmpty()) {
                throw new ServiceException("回收功能弹窗设置错误");
            }
            //前端规定获取msg值来判断
            throw new ServiceException("已有待寄出订单");
        }
        String userId = ISecurity.userMust().getId();
        //新增订单
        RecyclingOrder recyclingOrder = new RecyclingOrder();
        recyclingOrder.setArticleImgs(dto.getArticleImgs());
        recyclingOrder.setEdStatus(RecyclingOrderStatus.ED_STATUS_WAIT);
        recyclingOrder.setCouponIds(service.getAssCouponId().toString());
        recyclingOrder.setShopUserId(userId);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        AccountInfoDto accountInfoDto = miniAccountService.accountInfo(userId,list);
        if (accountInfoDto.getMiniAccountunt() != null) {
            recyclingOrder.setNickName(accountInfoDto.getMiniAccountunt().getNikeName());
            recyclingOrder.setAvatarUrl(accountInfoDto.getMiniAccountunt().getAvatarUrl());
        }
        recyclingOrderMapper.insert(recyclingOrder);
        String[] imgs = dto.getArticleImgs().split(",");
        for (String imgUrl : imgs) {
            if (!ReUtil.isMatch(PatternPool.URL_HTTP, imgUrl)) {
                throw new ServiceException("无效图片地址");
            }
            if (StrUtil.isBlank(imgUrl)) {
                throw new ServiceException("无效图片地址");
            }
        }
        //确认url赠送卡券
        Result<String> result = remoteDiscountService.grantDiscountCoupon(recyclingOrder.getShopUserId().toString(), Long.valueOf(recyclingOrder.getCouponIds()), imgs.length);
        if (!CommonConstants.SUCCESS.equals(result.getCode())) {
            throw new ServiceException(result.getMsg());
        }
        return String.valueOf(result.getData());
    }

    /**
     * 用户获取我的回收订单
     *
     * @param orderStatus 查询订单  0-全部  1-待寄出  2-已寄出  3-已取消
     * @param page        页数
     * @param size        条数
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @Override
    public ResultPageList<RecyclingOrderListVo> recyclingOrderList(Integer orderStatus, Integer page, Integer size) {
        if (orderStatus == null || orderStatus == 0) {
            orderStatus = null;
        } else {
            //前端给入条件： 查询订单  0-全部  1-待寄出  2-已寄出  3-已取消
            orderStatus = orderStatus - 1;
        }
        String userId = ISecurity.userMust().getId();
        Page<RecyclingOrder> recyclingOrderPage = this.baseMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<RecyclingOrder>()
                        // 回收状态 0-待寄出  1-已寄出  2-取消
                        .eq(orderStatus != null, RecyclingOrder::getEdStatus, orderStatus)
                        .eq(RecyclingOrder::getShopUserId, userId)
                        .orderByDesc(RecyclingOrder::getCreateTime)
        );
        List<RecyclingOrder> records = recyclingOrderPage.getRecords();
        ResultPageList<RecyclingOrderListVo> resultPageList = new ResultPageList<RecyclingOrderListVo>(CommonEnum.SUCCESS_RESPONSE, recyclingOrderPage, records);
        if (CollectionUtil.isNotEmpty(records)) {
            List<RecyclingOrderListVo> listVos = records.stream().map(obj -> {
                RecyclingOrderListVo recyclingOrderListVo = BeanUtil.toBean(obj, RecyclingOrderListVo.class);
                LocalDateTime sendOut = obj.getSendOut();
                if (sendOut != null) {
                    Date date = LocalDateTimeUtils.convertLdtToDate(obj.getSendOut());
                    recyclingOrderListVo.setSendOutDate(DateUtil.format(date, DatePattern.NORM_DATE_FORMAT));
                    recyclingOrderListVo.setSendOutTime(DateUtil.format(date, "HH:mm"));
                }
                LocalDateTime create = obj.getCreateTime();
                if (create != null) {
                    Date date = LocalDateTimeUtils.convertLdtToDate(obj.getCreateTime());
                    recyclingOrderListVo.setCreateDate(DateUtil.format(date, DatePattern.NORM_DATE_FORMAT));
                    recyclingOrderListVo.setCreateTime(DateUtil.format(date, "HH:mm"));
                }
                return recyclingOrderListVo;
            }).toList();
            resultPageList.setData(listVos);
        }
        return resultPageList;
    }

    /**
     * 用户取消新增的回收订单
     *
     * @param id 订单id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recyclingOrderCancel(Long id) {
        RecyclingOrder recyclingOrder = this.baseMapper.selectById(id);
        if (recyclingOrder == null) {
            throw new ServiceException("不存在有效订单");
        }
        if (recyclingOrder.getEdStatus().equals(RecyclingOrderStatus.ED_STATUS_WAIT)) {
            recyclingOrderMapper.updateById(new RecyclingOrder().setId(id).setEdStatus(RecyclingOrderStatus.ED_STATUS_CANCEL));
        } else {
            throw new ServiceException("订单已处理");
        }
    }

    /**
     * 用户删除回收订单
     *
     * @param id 订单id
     */
    @Override
    public void recyclingOrderDel(Long id) {
        RecyclingOrder recyclingOrder = this.baseMapper.selectById(id);
        if (recyclingOrder == null) {
            throw new ServiceException("不存在有效订单");
        }
        //已取消的订单才可以删除
        if (recyclingOrder.getEdStatus().equals(RecyclingOrderStatus.ED_STATUS_CANCEL)) {
            recyclingOrderMapper.deleteById(new RecyclingOrder().setId(id));
        } else {
            throw new ServiceException("订单无法删除");
        }

    }

    /**
     * PC端商户获取相关订单
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @param id 订单ID
     * @param shipmentNumber 快递单号
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @Override
    public ResultPageList<RecyclingPcOrderListVo> recyclingPcOrderList(Integer page, Integer size, String nickName,Long id,String shipmentNumber) {
        Page<RecyclingOrder> recyclingOrderPage = this.baseMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<RecyclingOrder>()
                        // 回收状态 0-待寄出  1-已寄出  2-取消
                        .like(StrUtil.isNotEmpty(nickName), RecyclingOrder::getNickName, nickName)
                        .eq(ObjectUtil.isNotEmpty(id),RecyclingOrder::getId,id)
                        .like(StrUtil.isNotEmpty(shipmentNumber),RecyclingOrder::getShipmentNumber,shipmentNumber)
                        .orderByDesc(RecyclingOrder::getCreateTime)
        );
        List<RecyclingOrder> records = recyclingOrderPage.getRecords();
        ResultPageList<RecyclingPcOrderListVo> resultPageList = new ResultPageList<RecyclingPcOrderListVo>(CommonEnum.SUCCESS_RESPONSE, recyclingOrderPage, records);
        if (CollectionUtil.isNotEmpty(records)) {
            List<RecyclingPcOrderListVo> listVos = records.stream().map(obj -> BeanUtil.toBean(obj, RecyclingPcOrderListVo.class)).toList();
            resultPageList.setData(listVos);
        }
        return resultPageList;
    }

    /**
     * 用户寄出回收订单
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto
     */
    @Override
    public void recyclingOrderSend(RecyclingOrderSendDto dto) {
        if (dto.getShipmentNumber().length() < CommonConstants.NUMBER_EIGHT){
            throw new ServiceException("物流单号有误");
        }
        RecyclingOrder recyclingOrder = this.baseMapper.selectById(dto.getId());
        if (recyclingOrder == null) {
            throw new ServiceException("不存在有效订单");
        }
        if (!recyclingOrder.getShopUserId().equals(ISecurity.userMust().getId())) {
            throw new ServiceException("非法操作");
        }
        //待寄出订单才可寄出
        if (recyclingOrder.getEdStatus().equals(RecyclingOrderStatus.ED_STATUS_WAIT)) {
            recyclingOrderMapper.updateById(new RecyclingOrder().setId(dto.getId())
                    .setSendOut(LocalDateTime.now())
                    .setProofImgs(dto.getProofImgs())
                    .setEdStatus(RecyclingOrderStatus.ED_STATUS_SEND)
                    .setShipmentNumber(dto.getShipmentNumber())
                    .setShipmentName(dto.getShipmentName()));
        } else {
            throw new ServiceException("订单无法操作");
        }
    }
}
