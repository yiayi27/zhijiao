package com.medusa.gruul.solitaire.service.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiSkuStockVo;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.entity.SolitaireRecord;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityProductVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireRecordVo;
import com.medusa.gruul.solitaire.mapper.api.ApiSolitaireActivityMapper;
import com.medusa.gruul.solitaire.mapper.api.ApiSolitaireRecordMapper;
import com.medusa.gruul.solitaire.service.api.IApiSolitaireActivityService;
import com.medusa.gruul.solitaire.web.enums.ActivityProductTypeEnum;
import com.medusa.gruul.solitaire.web.enums.ActivityStatusEnum;
import com.medusa.gruul.solitaire.web.utils.SortUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


/**
 * <p>
 * 接龙活动信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Slf4j
@Service
public class ApiSolitaireActivityServiceImpl extends ServiceImpl<ApiSolitaireActivityMapper, SolitaireActivity> implements IApiSolitaireActivityService {

    @Resource
    private RemoteGoodsService remoteGoodsService;

    @Autowired
    private ApiSolitaireRecordMapper apiSolitaireRecordMapper;

    /**
     * 获取接龙活动信息明细
     *
     * @return 接龙活动信息对象
     */
    @Override
    public Result<ApiSolitaireActivityVo> getCurrentActivity() {
        ApiSolitaireActivityVo apiSolitaireActivityVo;
        //获取所有的接龙活动信息
        List<ApiSolitaireActivityVo> apiSolitaireActivityVos = this.baseMapper.queryCurrentActivity();
        if (CollectionUtil.isEmpty(apiSolitaireActivityVos)) {
            throw new ServiceException("暂无接龙活动!", SystemCode.DATA_EXISTED.getCode());
        }
        //先判断有没有进行中的活动 有的话直接返回  没有的话取最近一场未开始的活动 未开始的也没有则取最近一场已结束的活动
        List<ApiSolitaireActivityVo> apiSolitaireActivityVoList = apiSolitaireActivityVos.stream()
                .filter(bean -> CommonConstants.NUMBER_ONE.equals(bean.getStatus())).toList();
        if (CollectionUtil.isNotEmpty(apiSolitaireActivityVoList)) {
            //取进行中的活动的信息
            apiSolitaireActivityVo = apiSolitaireActivityVoList.get(CommonConstants.NUMBER_ZERO);
        } else {
            //查询是否有未开始的的活动
            List<ApiSolitaireActivityVo> notBeginList = apiSolitaireActivityVos.stream()
                    .filter(bean -> CommonConstants.NUMBER_ZERO.equals(bean.getStatus())).toList();
            if (CollectionUtil.isNotEmpty(notBeginList)) {
                //取未开始的活动的信息
                apiSolitaireActivityVo = notBeginList.get(CommonConstants.NUMBER_ZERO);
            } else {
                //说明只有已结束活动 按结束时间降序显示
                int lastCount = apiSolitaireActivityVos.size() - CommonConstants.NUMBER_ONE;
                apiSolitaireActivityVo = apiSolitaireActivityVos.get(lastCount);
            }
        }
        if (BeanUtil.isEmpty(apiSolitaireActivityVo)) {
            throw new ServiceException("活动信息不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //查询接龙活动的购买记录
        List<ApiSolitaireRecordVo> apiSolitaireRecordVos = apiSolitaireRecordMapper
                .getSolitaireRecord(apiSolitaireActivityVo.getId());
        if (CollectionUtil.isNotEmpty(apiSolitaireRecordVos)) {
            //去重获取接龙活动购买人数
            List<ApiSolitaireRecordVo> orderDistinct = apiSolitaireRecordVos.stream()
                    .filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getOrderId)).toList();
            List<ApiSolitaireRecordVo> userDistinct = apiSolitaireRecordVos.stream()
                    .filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getBuyerId)).toList();
            apiSolitaireActivityVo.setUserRecords(userDistinct.size());
            apiSolitaireActivityVo.setOrderRecords(orderDistinct.size());
            List<ApiSolitaireRecordVo> solitaireRecordVos = new ArrayList<>(CommonConstants.NUMBER_FOUR);
            for (int i = 0; i < apiSolitaireRecordVos.size(); i++) {
                //获取前四条接龙记录
                if (i < 4) {
                    solitaireRecordVos.add(apiSolitaireRecordVos.get(i));
                }
            }
            apiSolitaireActivityVo.setSolitaireRecords(solitaireRecordVos);
        } else {
            apiSolitaireActivityVo.setUserRecords(CommonConstants.NUMBER_ZERO);
            apiSolitaireActivityVo.setOrderRecords(CommonConstants.NUMBER_ZERO);
        }
        return Result.ok(apiSolitaireActivityVo);
    }

    /**
     * 获取单场接龙活动详细信息
     *
     * @param activityId 接龙活动id 接龙活动id
     * @return 接龙活动信息对象
     */
    @Override
    public Result<ApiSolitaireActivityVo> getCurrentActivityById(Long activityId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        ApiSolitaireActivityVo apiSolitaireActivityVo = this.baseMapper.queryActivityById(activityId);
        if (BeanUtil.isEmpty(apiSolitaireActivityVo)) {
            throw new ServiceException("暂无接龙活动!", SystemCode.DATA_EXISTED.getCode());
        }
        //查询接龙活动的购买记录
        List<ApiSolitaireRecordVo> apiSolitaireRecordVos = apiSolitaireRecordMapper
                .getSolitaireRecord(apiSolitaireActivityVo.getId());
        //去重获取接龙活动购买人数
        apiSolitaireActivityVo.setUserRecords(CollectionUtil.isNotEmpty(apiSolitaireRecordVos) ?
                (int) apiSolitaireRecordVos.stream().filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getBuyerId)).count() : CommonConstants.NUMBER_ZERO);
        //去重获取接龙活动购买记录数
        apiSolitaireActivityVo.setOrderRecords(CollectionUtil.isNotEmpty(apiSolitaireRecordVos) ?
                (int) apiSolitaireRecordVos.stream().filter(SortUtil.distinctByKey(ApiSolitaireRecordVo::getOrderId)).count() : CommonConstants.NUMBER_ZERO);
        //用户自己的购买记录
        apiSolitaireActivityVo.setSelfSolitaireRecords(CollectionUtil.isNotEmpty(apiSolitaireRecordVos) ?
                apiSolitaireRecordVos.stream().filter(r -> userId.equals(r.getBuyerId())).toList() : Collections.emptyList());
        //获取前四条接龙记录
        apiSolitaireActivityVo.setSolitaireRecords(CollectionUtil.isNotEmpty(apiSolitaireRecordVos) ?
                apiSolitaireRecordVos.stream().limit(CommonConstants.NUMBER_FOUR).toList() : Collections.emptyList());
        //接龙活动浏览次数累加 同时数据库更新该场活动的浏览次数
        apiSolitaireActivityVo.setViews(apiSolitaireActivityVo.getViews() + CommonConstants.NUMBER_ONE);
        boolean sign = new LambdaUpdateChainWrapper<>(this.baseMapper)
                .eq(SolitaireActivity::getId, apiSolitaireActivityVo.getId())
                .set(SolitaireActivity::getViews, apiSolitaireActivityVo.getViews()).update();
        if (!sign) {
            throw new ServiceException("浏览次数更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        return Result.ok(apiSolitaireActivityVo);
    }

    /**
     * 转发当前接龙活动商品信息
     *
     * @param activityId 接龙活动id 接龙活动id
     * @param path      接龙活动路径
     * @return 接龙活动信息对象
     */
    @Override
    public Result<Object> getCurrentActivityProduct(Long activityId, String path) {
        //拼接接龙信息
        ApiSolitaireActivityVo apiSolitaireActivityVo = this.baseMapper.queryActivityById(activityId);
        if (BeanUtil.isEmpty(apiSolitaireActivityVo)) {
            throw new ServiceException("接龙活动不存在", SystemCode.DATA_EXISTED.getCode());
        }
        //调用商品服务获取接龙活动的商品信息
        List<ApiSolitaireActivityProductVo> apiSolitaireActivityProductVos = apiSolitaireActivityVo
                .getApiSolitaireActivityProductVos();
        List<Long> productIds = new ArrayList<>(apiSolitaireActivityProductVos.size());
        if (CollectionUtil.isNotEmpty(apiSolitaireActivityProductVos)) {
            productIds = apiSolitaireActivityProductVos.stream().map(ApiSolitaireActivityProductVo::getProductId)
                    .toList();
        }
        List<ApiProductVo> apiProductVos = remoteGoodsService
                .getSolitaireActivityProduct(apiSolitaireActivityVo.getProductType(), productIds);
        StringBuilder msg = new StringBuilder();
        String activityStatus = "已开始，大家速度采购吖~\n";
        if (ActivityStatusEnum.BE_GOING.getStatus() == apiSolitaireActivityVo.getStatus()) {
            activityStatus = "即将开始，大家可以放下手头上的事准备血拼啦~\n";
        } else if (ActivityStatusEnum.OVER.getStatus() == apiSolitaireActivityVo.getStatus()) {
            activityStatus = "已结束，我们下次再见~\n";
        }
        if (CollectionUtil.isNotEmpty(apiProductVos)) {
            msg.append(DateUtil.format(apiSolitaireActivityVo.getStartTime(), "yyyy年MM月dd日 HH:mm")).append("-").append(DateUtil.format(apiSolitaireActivityVo.getEndTime(), "yyyy年MM月dd日 HH:mm")).append(activityStatus);
            msg.append("参与活动商品\uD83D\uDC47\n");
            for (int i = 0; i < apiProductVos.size(); i++) {
                ApiProductVo apiProductVo = apiProductVos.get(i);
                BigDecimal lowPrice = apiProductVo.getSkuStocks().stream().map(ApiSkuStockVo::getPrice)
                        .min(Comparator.naturalOrder()).get();
                msg.append(i + 1).append(".").append(apiProductVo.getName()).append("    ￥")
                        .append(lowPrice).append("元\n");
            }
            msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:\n");
        }

        return Result.ok(msg);
    }

    /**
     * 保存当前接龙活动接龙信息
     *
     * @param orderVo 订单信息
     */
    @Override
    public void saveCurrentActivityRecord(OrderVo orderVo) {
        log.info("-------------插入接龙活动记录开始-------------" + orderVo.toString());
        //先跟接龙活动id判断是否为接龙订单 是的话插入数据 不是直接pass
        if (orderVo.getSolitaireActivityId() == null) {
            return;
        }
        //拼接接龙信息
        List<OrderItemVo> orderItemList = orderVo.getOrderItemList();
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            orderItemList.forEach(orderItem -> {
                SolitaireRecord solitaireRecord = new SolitaireRecord();
                solitaireRecord.setOrderId(String.valueOf(orderVo.getId()));
                solitaireRecord.setShopId(orderVo.getShopId());
                solitaireRecord.setActivityId(String.valueOf(orderVo.getSolitaireActivityId()));
                solitaireRecord.setBuyerIcon(orderVo.getUserAvatarUrl());
                solitaireRecord.setBuyerName(orderVo.getUserName());
                solitaireRecord.setBuyerId(orderVo.getUserId());
                solitaireRecord.setProductName(orderItem.getProductName());
                solitaireRecord.setProductQuantity(orderItem.getProductQuantity());
                solitaireRecord.setPayAmount(orderVo.getPayAmount());
                int insert = apiSolitaireRecordMapper.insert(solitaireRecord);
                if (insert == 0) {
                    throw new ServiceException("接龙信息插入失败！", SystemCode.DATA_ADD_FAILED.getCode());
                }
            });
        }
        log.info("-------------插入接龙活动记录结束-------------");
    }

    /**
     * 转发当前接龙活动接龙信息
     *
     * @param activityId 接龙活动id 接龙活动id
     * @param path     接龙活动路径
     * @return 接龙活动信息对象
     */
    @Override
    public Result<Object> getCurrentActivityRecord(Long activityId, String path) {
        //拼接接龙信息
        List<SolitaireRecord> solitaireRecords = apiSolitaireRecordMapper
                .selectList(new LambdaQueryWrapper<SolitaireRecord>().eq(SolitaireRecord::getActivityId, activityId));
        //只发送10条记录，中间的记录用省略号代替
        //记录多于10条时，展示第一条和最近9条，中间数据省略
        // 排序
        List<SolitaireRecord> res;
        int index;
        if (solitaireRecords.size() > CommonConstants.NUMBER_TEN) {
            res = new ArrayList<>();
            res.add(solitaireRecords.get(CommonConstants.NUMBER_ZERO));
            List<SolitaireRecord> lastNine = solitaireRecords.stream().skip(solitaireRecords.size() - CommonConstants.NUMBER_NINE)
                    .toList();
            res.addAll(lastNine);
            index = solitaireRecords.size() - CommonConstants.NUMBER_NINE;
        } else {
            res = solitaireRecords;
            index = 1;
        }
        StringBuilder msg = new StringBuilder();
        SolitaireActivity solitaireActivity = this.baseMapper.selectById(activityId);
        msg.append(solitaireActivity.getContent()).append("%0a");
        msg.append("\uD83C\uDF6D接龙开始:%0a");
        for (int i = 0; i < res.size(); i++) {
            SolitaireRecord solitaireRecord = res.get(i);
            if (i == 0) {
                msg.append(1).append(".").append(solitaireRecord.getBuyerName()).append(":");
            } else {
                index++;
                msg.append(index).append(".").append(solitaireRecord.getBuyerName()).append(":");
            }
            if (CollectionUtil.isNotEmpty(solitaireRecords)) {
                solitaireRecords.forEach(orderItem -> {
                    msg.append(orderItem.getProductName()).append("➕+").append(orderItem.getProductQuantity())
                            .append("%0a");
                });
            }
            if (solitaireRecords.size() > CommonConstants.NUMBER_TEN && i == CommonConstants.NUMBER_ZERO) {
                msg.append("...%0a");
                msg.append("...%0a");
            }
        }
        msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:%0a").append(path);
        return Result.ok(msg);
    }

    /**
     * 根据商品获取当前参与的最新一场接龙活动信息
     *
     * @param productId 商品id
     * @return Result
     */
    @Override
    public Result<Object> getActivityByProduct(Long productId) {
        //获取未开始与进行中的活动
        List<ApiSolitaireActivityVo> apiSolitaireActivityVos = this.baseMapper.queryReadyAndGoingActivity();
        if (CollectionUtil.isNotEmpty(apiSolitaireActivityVos)) {
            return Result.failed("商品暂未参与活动!");
        }
        Optional<ApiSolitaireActivityVo> optional = apiSolitaireActivityVos.stream().filter(apiSolitaireActivityVo -> {
            //全部商品参与的话直接返回 部分商品参与判断参与的商品是否包含该商品
            if (ActivityProductTypeEnum.ALL_JOIN.getStatus() == apiSolitaireActivityVo.getProductType()) {
                return true;
            }
            if (ActivityProductTypeEnum.SOME_JOIN.getStatus() == apiSolitaireActivityVo.getProductType()) {
                List<ApiSolitaireActivityProductVo> apiSolitaireActivityProductVos = apiSolitaireActivityVo.getApiSolitaireActivityProductVos();
                if (CollectionUtil.isNotEmpty(apiSolitaireActivityProductVos)) {
                    List<Long> productIds = apiSolitaireActivityProductVos.stream().map(val -> val.getProductId()).toList();
                    return CollectionUtil.isNotEmpty(productIds) && productIds.contains(productId);
                }
            }
            return false;
        }).findAny();
        return optional.<Result<Object>>map(apiSolitaireActivityVo -> Result.ok(apiSolitaireActivityVo.getId())).orElseGet(() -> Result.failed("商品暂未参与活动!"));
    }
}
