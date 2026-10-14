package com.medusa.gruul.recycling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.CommonEnum;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrder;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrderAccount;
import com.medusa.gruul.recycling.mapper.RecyclingAssOrderAccountMapper;
import com.medusa.gruul.recycling.model.dto.SelectStatisticalDto;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderListVo;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 基金所得订单用户关联表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-05-05
 */
@Service
public class RecyclingAssOrderAccountServiceImpl extends ServiceImpl<RecyclingAssOrderAccountMapper, RecyclingAssOrderAccount> implements IRecyclingAssOrderAccountService {

    /**
     * 获取店铺用户基金详情
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @param sortType 排序方式  1-基金值升序  2-基金值降序   3-按最近交易时间升序  4-按最近交易时间降序
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @Override
    public ResultPageList<RecyclingAssOrderListVo> recyclingAssOrderAccountList(Integer page, Integer size, String nickName, Integer sortType) {
        Page<RecyclingAssOrderAccount> assOrderAccountPage = this.baseMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<RecyclingAssOrderAccount>()
                        //按昵称查找
                        .like(StrUtil.isNotEmpty(nickName), RecyclingAssOrderAccount::getNickName, nickName)
                        //排序方式  1-基金值升序  2-基金值降序   3-按最近交易时间升序  4-按最近交易时间降序
                        .orderByAsc(sortType == 1, RecyclingAssOrderAccount::getTotalFundQuota)
                        .orderByAsc(sortType == 3, RecyclingAssOrderAccount::getLastPayTime)
                        .orderByDesc(sortType == 2, RecyclingAssOrderAccount::getTotalFundQuota)
                        .orderByDesc(sortType == 4, RecyclingAssOrderAccount::getLastPayTime)
        );
        List<RecyclingAssOrderAccount> records = assOrderAccountPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new ResultPageList(CommonEnum.SUCCESS_RESPONSE);
        }
        List<RecyclingAssOrderListVo> recyclingAssOrderListVos = records.stream().map(obj -> {
            return BeanUtil.toBean(obj, RecyclingAssOrderListVo.class);
        }).toList();
        return new ResultPageList(CommonEnum.SUCCESS_RESPONSE, assOrderAccountPage, recyclingAssOrderListVos);
    }

    /**
     * 用户数据新增
     *
     * @param recyclingAssOrder com.medusa.gruul.recycling.api.entity.RecyclingAssOrder
     */
    @Override
    public void addRecyclingAssOrder(RecyclingAssOrder recyclingAssOrder) {
        RecyclingAssOrderAccount orderAccount = this.baseMapper.selectOne(
                new LambdaQueryWrapper<RecyclingAssOrderAccount>().eq(RecyclingAssOrderAccount::getShopUserId, recyclingAssOrder.getShopUserId())
        );
        if (orderAccount == null) {
            orderAccount = new RecyclingAssOrderAccount();
            orderAccount.setOrderNumber(CommonConstants.NUMBER_ONE);
            orderAccount.setTotalFundQuota(recyclingAssOrder.getFundQuota());
            orderAccount.setTotalAmount(recyclingAssOrder.getPayAmount());
            orderAccount.setShopUserId(recyclingAssOrder.getShopUserId());
        } else {
            orderAccount.setOrderNumber(orderAccount.getOrderNumber() + 1);
            orderAccount.setTotalFundQuota(orderAccount.getTotalFundQuota().add(recyclingAssOrder.getFundQuota()));
            orderAccount.setTotalAmount(orderAccount.getTotalAmount().add(recyclingAssOrder.getPayAmount()));
        }
        //头像及昵称随最后一次的订单更新
        orderAccount.setNickName(recyclingAssOrder.getNickName());
        orderAccount.setAvatarUrl(recyclingAssOrder.getAvatarUrl());

        orderAccount.setLastPayTime(recyclingAssOrder.getCreateTime());
        this.saveOrUpdate(orderAccount);
    }

    /**
     * 基金详情统计
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo
     */
    @Override
    public RecyclingAssOrderAccountStatisticalVo recyclingAssOrderAccountStatistical() {
        String shopUserId = ISecurity.userMust().getId();
        RecyclingAssOrderAccountStatisticalVo vo = new RecyclingAssOrderAccountStatisticalVo();
        vo.setTotalFundQuota(CommonConstants.STRING_ZERO);
        vo.setTotalOrderNumber(CommonConstants.STRING_ZERO);

        SelectStatisticalDto selectStatisticalDto = this.baseMapper.selectStatistical(shopUserId);
        if (selectStatisticalDto == null) {
            return vo;
        }
        vo.setTotalFundQuota(selectStatisticalDto.getTotalFundQuota());
        vo.setTotalOrderNumber(selectStatisticalDto.getTotalOrderNumber());
        if (StrUtil.isNotBlank(shopUserId)) {
            vo.setRanking(CommonConstants.STRING_ZERO);
            Map<Long, HashMap> map = this.baseMapper.selectByAccountRanking(ISystem.shopId());
            //获取用户排名
            HashMap o = map.get(Long.parseLong(shopUserId));
            if (o != null) {
                vo.setRanking(NumberUtil.roundStr((Double) o.get("rownum"), 0));
            }
        }
        return vo;
    }

    /**
     * 数据回退
     *
     * @param recyclingAssOrder com.medusa.gruul.recycling.api.entity.RecyclingAssOrder
     */
    @Override
    public void removeRecyclingAssOrder(RecyclingAssOrder recyclingAssOrder, BigDecimal multiply,BigDecimal refundAmount) {
        RecyclingAssOrderAccount orderAccount = this.baseMapper.selectOne(
                new LambdaQueryWrapper<RecyclingAssOrderAccount>().eq(RecyclingAssOrderAccount::getShopUserId, recyclingAssOrder.getShopUserId())
        );
        if (orderAccount == null) {
            return;
        }
        if (recyclingAssOrder.getStatus() == 1){
            this.baseMapper.updateById(new RecyclingAssOrderAccount()
                            .setId(orderAccount.getId())
                            .setTotalFundQuota(orderAccount.getTotalFundQuota().subtract(multiply))
                            .setTotalAmount(orderAccount.getTotalAmount().subtract(refundAmount)));
        }else {
            this.baseMapper.updateById(new RecyclingAssOrderAccount()
                    .setId(orderAccount.getId())
                    .setTotalFundQuota(orderAccount.getTotalFundQuota().subtract(recyclingAssOrder.getFundQuota()))
                    .setTotalAmount(orderAccount.getTotalAmount().subtract(recyclingAssOrder.getPayAmount()))
                    .setOrderNumber(orderAccount.getOrderNumber()-1));
        }

    }
}
