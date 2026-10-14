package com.medusa.gruul.finance.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.datasets.api.feign.RemoteDataSetsService;
import com.medusa.gruul.datasets.api.model.CallerDto;
import com.medusa.gruul.finance.api.entity.FinanceTradeDetail;
import com.medusa.gruul.finance.api.enums.FlowTypeEnum;
import com.medusa.gruul.finance.api.enums.TradeTypeEnum;
import com.medusa.gruul.finance.mapper.FinanceTradeDetailMapper;
import com.medusa.gruul.finance.model.SearchTradeDetailDto;
import com.medusa.gruul.finance.model.ShopOverviewVo;
import com.medusa.gruul.finance.model.TradeOverviewVo;
import com.medusa.gruul.finance.service.IFinanceTradeDetailService;
import com.medusa.gruul.finance.util.GenSerialNumberUtil;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.api.model.TradeLineVo;
import com.medusa.gruul.order.api.model.TradeVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 资金交易明细表 服务实现类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Service
public class FinanceTradeDetailServiceImpl extends ServiceImpl<FinanceTradeDetailMapper, FinanceTradeDetail> implements IFinanceTradeDetailService {

    @Resource
    private RemoteDataSetsService dataSetsService;

    /**
     * 分页获取交易明细
     *
     * @param dto 对账单信息
     * @return 交易明细分页信息
     */
    @Override
    public PageUtils<FinanceTradeDetail> getTradeDetailPage(SearchTradeDetailDto dto) {
        IPage<FinanceTradeDetail> page;
        setDefaultTime(dto);
        page = baseMapper.selectPage(new Page<>(dto.getCurrent(), dto.getSize()),
                new LambdaQueryWrapper<FinanceTradeDetail>()
                        .eq(dto.getFlowType().equals(CommonConstants.NUMBER_ZERO), FinanceTradeDetail::getCapitalFlows, FlowTypeEnum.IN)
                        .eq(dto.getFlowType().equals(CommonConstants.NUMBER_ONE), FinanceTradeDetail::getCapitalFlows, FlowTypeEnum.OUT)
                        .eq(ObjectUtil.isNotNull(dto.getType()), FinanceTradeDetail::getType, dto.getType())
                        .between(FinanceTradeDetail::getCreateTime, dto.getStartTime(), dto.getEndTime())
                        .orderByDesc(FinanceTradeDetail::getCreateTime)
        );
        return new PageUtils<>(page);
    }

    /**
     * 获取对账单
     *
     * @param dto 对账单查询信息
     * @return 对账单概览
     */
    @Override
    public TradeOverviewVo getTradeOverview(SearchTradeDetailDto dto) {
        setDefaultTime(dto);
        List<FinanceTradeDetail> inList = baseMapper.selectList(new LambdaQueryWrapper<FinanceTradeDetail>()
                .select(FinanceTradeDetail::getAmount)
                .eq(FinanceTradeDetail::getCapitalFlows, FlowTypeEnum.IN)
                .between(FinanceTradeDetail::getCreateTime, dto.getStartTime(), dto.getEndTime())
        );
        List<FinanceTradeDetail> outList = baseMapper.selectList(new LambdaQueryWrapper<FinanceTradeDetail>()
                .select(FinanceTradeDetail::getAmount)
                .eq(FinanceTradeDetail::getCapitalFlows, FlowTypeEnum.OUT)
                .between(FinanceTradeDetail::getCreateTime, dto.getStartTime(), dto.getEndTime())
        );
        TradeOverviewVo tradeOverviewVo = new TradeOverviewVo();
        tradeOverviewVo.setIncomeNumber(inList.size());
        tradeOverviewVo.setIncomeAmount(inList.stream().map(FinanceTradeDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        tradeOverviewVo.setExpensesNumber(outList.size());
        tradeOverviewVo.setExpensesAmount(outList.stream().map(FinanceTradeDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return tradeOverviewVo;
    }

    /**
     * 设置默认时间
     *
     * @param dto 对账单信息
     */
    private void setDefaultTime(SearchTradeDetailDto dto) {
        String startTime = StrUtil.isBlank(dto.getStartTime()) ? DateUtil.beginOfMonth(new Date()).toString() : dto.getStartTime() + " 00:00:00";
        dto.setStartTime(startTime);
        String endTime = StrUtil.isBlank(dto.getEndTime()) ? DateUtil.endOfMonth(new Date()).toString() : dto.getEndTime() + " 23:59:59";
        dto.setEndTime(endTime);
    }

    /**
     * 添加交易明细
     *
     * @param orderVo 订单详情
     */
    @Override
    public void addTrade(OrderVo orderVo) {
        FinanceTradeDetail detail = new FinanceTradeDetail();
        detail.setTradeNo(orderVo.getTransactionId());
        detail.setUserId(orderVo.getUserId());
        detail.setUserName(orderVo.getUserName());
        detail.setUserAvatarUrl(orderVo.getUserAvatarUrl());
        detail.setType(TradeTypeEnum.TRADE);
        detail.setAmount(orderVo.getPayAmount());
        detail.setSourceId(orderVo.getId());
        detail.setCapitalFlows(FlowTypeEnum.IN);
        detail.setSn(GenSerialNumberUtil.initGenSerialNumber(GenSerialNumberUtil.BROKERAGE, orderVo.getShopId()).nextId());
        baseMapper.insert(detail);
    }

    /**
     * 添加订单退款记录
     *
     * @param orderVo 订单详情
     * @return boolean 是否成功
     */
    @Override
    public void addReturn(OrderVo orderVo) {
        FinanceTradeDetail detail = new FinanceTradeDetail();
        detail.setTradeNo(orderVo.getRefundTransactionId());
        detail.setUserId(orderVo.getUserId());
        detail.setUserName(orderVo.getUserName());
        detail.setUserAvatarUrl(orderVo.getUserAvatarUrl());
        detail.setType(TradeTypeEnum.RETURN);
        detail.setAmount(orderVo.getRefundAmount());
        detail.setSourceId(orderVo.getId());
        detail.setCapitalFlows(FlowTypeEnum.OUT);
        detail.setSn(GenSerialNumberUtil.initGenSerialNumber(GenSerialNumberUtil.BROKERAGE, orderVo.getShopId()).nextId());
        baseMapper.insert(detail);
    }


    /**
     * 根据起止时间查询交易明细
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 交易明细
     */
    @Override
    public TradeVo getTradeVoByTime(String startDate, String endDate) {
        return baseMapper.getTradeVoByTime(startDate.concat(" 00:00:00"), endDate.concat(" 23:59:59"));
    }

    /**
     * 根据起止时间分组查询交易明细
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 交易明细
     */
    @Override
    public List<TradeLineVo> getTradeVoGroupByDate(String startDate, String endDate) {
        return baseMapper.getTradeVoGroupByDate(startDate.concat(" 00:00:00"), endDate.concat(" 23:59:59"));
    }

    /**
     * 获取商铺概览
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 店铺概况
     */
    @Override
    public ShopOverviewVo getOverview(String startTime, String endTime) {
        ShopOverviewVo vo = new ShopOverviewVo();
        DateTime startDate;
        DateTime endDate;
        if (StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime)) {
            startDate = DateUtil.parse(startTime);
            endDate = DateUtil.endOfDay(DateUtil.parse(endTime));
            TradeVo tradeVo = getTradeVoByTime(startTime, endTime);
            CallerDto callerDto = dataSetsService.callerInfo(Integer.parseInt(DateUtil.beginOfDay(startDate).toString(
                            "yyyyMMdd")),
                    Integer.parseInt(DateUtil.endOfDay(endDate).toString("yyyyMMdd")));
            tradeVo.setViews(callerDto.getPv());
            vo.setTrade(tradeVo);
        } else {
            TradeVo tradeVo = getTradeVoByTime(DateUtil.today(), DateUtil.today());
            Integer start = Integer.parseInt(DateUtil.date().toString(
                    "yyyyMMdd"));
            CallerDto callerDto = dataSetsService.callerInfo(start, null);

            TradeVo yesterdayTradeVo = getTradeVoByTime(DateUtil.yesterday().toDateStr(),
                    DateUtil.yesterday().toDateStr());
            if (ObjectUtil.isNotNull(callerDto)) {
                tradeVo.setViews(callerDto.getPv());
                yesterdayTradeVo.setViews(callerDto.getYesterdayPv());
            }
            vo.setTrade(tradeVo);
            vo.setYesterdayTrade(yesterdayTradeVo);
            startDate = DateUtil.offsetDay(DateUtil.parse(DateUtil.today()), -6);
            endDate = DateUtil.parse(DateUtil.today());
        }
        long betweenDay = DateUtil.betweenDay(startDate, endDate, true) + CommonConstants.NUMBER_ONE;
        List<TradeLineVo> tradeVoList = getTradeVoGroupByDate(startDate.toDateStr(), endDate.toDateStr());
        Map<String, TradeLineVo> tradeVoMap = tradeVoList.stream()
                .collect(Collectors.toMap(TradeLineVo::getDate, v -> v));
        for (int i = 0; i < betweenDay; i++) {
            String dateStr = DateUtil.offsetDay(startDate, i).toDateStr();
            if (ObjectUtil.isNull(tradeVoMap.get(dateStr))) {
                TradeLineVo tradeLineVo = new TradeLineVo();
                tradeLineVo.setDate(dateStr);
                tradeVoList.add(tradeLineVo);
            }
        }
        tradeVoList.sort(Comparator.comparing(TradeLineVo::getDate));
        vo.setTradeLineVos(tradeVoList);
        vo.setUpdateTime(DateUtil.now());
        return vo;
    }

    /**
     * 商品评价后增加返利数据信息 纳入账单支出
     *
     * @param vo 订单详情
     */
    @Override
    public void addRebateDetail(OrderVo vo) {
        BigDecimal rebateAmount = vo.getRebateAmount();
        if (rebateAmount == null || rebateAmount.compareTo(BigDecimal.ZERO) <= CommonConstants.NUMBER_ZERO) {
            log.warn("消费返利队列回 ===》 订单id：".concat(vo.getId().toString()).concat(", 消费返利金额为0，不进行处理"));
            return;
        }
        FinanceTradeDetail detail = new FinanceTradeDetail();
        detail.setShopId(vo.getShopId());
        detail.setTradeNo(vo.getTransactionId());
        detail.setUserId(vo.getUserId());
        detail.setUserName(vo.getUserName());
        detail.setUserAvatarUrl(vo.getUserAvatarUrl());
        detail.setType(TradeTypeEnum.REBATE);
        detail.setAmount(rebateAmount);
        detail.setSourceId(vo.getId());
        detail.setCapitalFlows(FlowTypeEnum.OUT);
        detail.setSn(GenSerialNumberUtil.initGenSerialNumber(GenSerialNumberUtil.BROKERAGE, vo.getShopId()).nextId());
        super.save(detail);

    }

}
