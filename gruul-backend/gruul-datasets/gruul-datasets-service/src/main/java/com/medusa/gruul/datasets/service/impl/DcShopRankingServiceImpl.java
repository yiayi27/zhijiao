package com.medusa.gruul.datasets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.datasets.api.constant.DataSetsMethod;
import com.medusa.gruul.datasets.api.entity.DcShopRanking;
import com.medusa.gruul.datasets.api.enums.DealingTypeEnum;
import com.medusa.gruul.datasets.mapper.DcShopRankingMapper;
import com.medusa.gruul.datasets.service.IDcShopRankingService;
import com.medusa.gruul.datasets.web.model.RankingsDto;
import com.medusa.gruul.datasets.web.model.RankingsGMVVo;
import com.medusa.gruul.datasets.web.model.RankingsVo;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.SupplierVo;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 交易统计表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@Service
public class DcShopRankingServiceImpl extends ServiceImpl<DcShopRankingMapper, DcShopRanking>
        implements IDcShopRankingService {

    @Autowired
    private DcShopRankingMapper dcShopRankingMapper;

    @Autowired
    private RemoteGoodsService remoteGoodsService;

    /**
     * 分析已支付完成的订单 进行分析入库 (交易量 交易额)
     *
     * @param orderVo 订单vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderPayFinish(OrderVo orderVo) {
        Integer totalProductQuantity = CommonConstants.NUMBER_ZERO;
        //获取订单下商品详情
        List<OrderItemVo> orderItemList = orderVo.getOrderItemList();
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            //遍历订单详情
            for (OrderItemVo orderItem : orderItemList) {
                DcShopRanking dcShopRanking = new DcShopRanking();
                dcShopRanking.setShopId(orderVo.getShopId());
                totalProductQuantity = orderItem.getProductQuantity() + totalProductQuantity;
                //供货商 商品排行信息取 订单商品数据
                productInfoAdd(orderItem, dcShopRanking);
                //由于供货商信息并不是必填项 对供货商信息进行判断 不为null就行插入
                if (orderItem.getProviderId() != null) {
                    DcShopRanking providerRanking = new DcShopRanking();
                    providerRanking.setShopId(orderVo.getShopId());
                    providerInfoAdd(orderItem, providerRanking);
                }
            }
        }
    }

    /**
     * 插入商品信息
     *
     * @param orderItem     订单商品信息对象
     * @param dcShopRanking 交易统计对象
     */
    private void productInfoAdd(OrderItemVo orderItem, DcShopRanking dcShopRanking) {
        //计算商品总价
        BigDecimal tradeVolume = calculateTradeVolume(orderItem);
        dcShopRanking.setProductId(orderItem.getProductId());
        dcShopRanking.setTradingVolume(orderItem.getProductQuantity());
        dcShopRanking.setTradeVolume(tradeVolume);
        dcShopRanking.setNick(orderItem.getProductName());
        dcShopRanking.setUrl(orderItem.getProductPic());
        dcShopRanking.setType(DealingTypeEnum.PRODUCT.getCode());
        dcShopRanking.setYyyy(DataSetsMethod.communalYear());
        dcShopRanking.setYyyyMM(DataSetsMethod.communalYearAndMonth());
        dcShopRanking.setYyyyMMdd(DataSetsMethod.communalDate());
        int insert = dcShopRankingMapper.insert(dcShopRanking);
        if (insert < 1) {
            throw new ServiceException("插入数据失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 计算商品总价
     *
     * @param orderItem 订单商品信息对象
     */
    private BigDecimal calculateTradeVolume(OrderItemVo orderItem) {
        BigDecimal productPrice = orderItem.getProductPrice();
        if (productPrice.compareTo(BigDecimal.ZERO) == 0) {
            productPrice = orderItem.getProductOriginalPrice();
        }
        return productPrice.multiply(BigDecimal.valueOf(orderItem.getProductQuantity()));
    }

    /**
     * 插入供应商信息
     *
     * @param orderItem     订单商品信息对象
     * @param dcShopRanking 交易统计对象
     */
    private void providerInfoAdd(OrderItemVo orderItem, DcShopRanking dcShopRanking) {
        //计算商品总价
        BigDecimal tradeVolume = calculateTradeVolume(orderItem);
        dcShopRanking.setProviderId(orderItem.getProviderId());
        dcShopRanking.setTradingVolume(orderItem.getProductQuantity());
        dcShopRanking.setTradeVolume(tradeVolume);
        dcShopRanking.setType(DealingTypeEnum.SUPPLIER.getCode());
        dcShopRanking.setYyyy(DataSetsMethod.communalYear());
        dcShopRanking.setYyyyMM(DataSetsMethod.communalYearAndMonth());
        dcShopRanking.setYyyyMMdd(DataSetsMethod.communalDate());
        int insert = dcShopRankingMapper.insert(dcShopRanking);
        if (insert < 1) {
            throw new ServiceException("插入数据失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 排行榜销售量获取
     *
     * @return 排行榜销量 (商品  供应商)
     */
    @Override
    public RankingsVo getRankingsInfos() {
        RankingsVo rankingsVo = new RankingsVo();
        Integer month = DataSetsMethod.communalYearAndMonth();
        List<RankingsDto> productRankings = getRankingsByType(DealingTypeEnum.PRODUCT.getCode(), month);
        rankingsVo.setProductVBRanking(productRankings);
        List<RankingsDto> supplierRankings = getRankingsByType(DealingTypeEnum.SUPPLIER.getCode(), month);
        rankingsVo.setSupplierVBRanking(supplierRankings);

        return rankingsVo;
    }

    /**
     * 根据类型获取排行信息
     *
     * @param type  类型
     * @param month 日期
     */
    private List<RankingsDto> getRankingsByType(Integer type, Integer month) {
        List<RankingsDto> rankingsInfos = dcShopRankingMapper.selectVbByType(type, month);
        List<RankingsDto> rds = new ArrayList<>();
        for (RankingsDto rankingsInfo : rankingsInfos) {
            if (type.equals(DealingTypeEnum.PRODUCT.getCode())) {
                ApiShoppingCartProductVo productVo = Jackson.readValue(
                        RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT,rankingsInfo.getId())), ApiShoppingCartProductVo.class);
                BigDecimal price = getProductFloorPrice(productVo);
                RankingsDto rankingsDto = new RankingsDto();
                rankingsDto.setId(rankingsInfo.getId());
                rankingsDto.setTradingVolume(rankingsInfo.getTradingVolume());
                rankingsDto.setTradeVolume(rankingsInfo.getTradeVolume());
                rankingsDto.setNick(productVo.getProductName());
                rankingsDto.setUrl(productVo.getPic());
                rankingsDto.setPrice(price);
                rds.add(rankingsDto);
            } else if (type.equals(DealingTypeEnum.SUPPLIER.getCode())) {
                List<SupplierVo> suppliers = remoteGoodsService.findDataSetSupplierList(Collections.singletonList(rankingsInfo.getId()));
                SupplierVo supplierVo = CollectionUtil.isEmpty(suppliers) ? null : suppliers.get(CommonConstants.NUMBER_ZERO);
                RankingsDto rankingsDto = new RankingsDto();
                rankingsDto.setId(rankingsInfo.getId());
                rankingsDto.setTradingVolume(rankingsInfo.getTradingVolume());
                rankingsDto.setTradeVolume(rankingsInfo.getTradeVolume());
                assert supplierVo != null;
                rankingsDto.setNick(supplierVo.getName());
                rankingsDto.setMobile(supplierVo.getMobile());
                rds.add(rankingsDto);
            }
        }
        return rds;
    }

    /**
     * 排行榜销售额排行获取
     *
     * @return 销售额排行榜 (商品  供应商)
     */
    @Override
    public RankingsGMVVo getRankingsGvmInfos() {
        RankingsGMVVo rankingsGvmVo = new RankingsGMVVo();
        Integer month = DataSetsMethod.communalYearAndMonth();
        List<RankingsDto> productRankings = getRankingsByType(DealingTypeEnum.PRODUCT.getCode(), month);
        rankingsGvmVo.setProductGMVRanking(productRankings);
        List<RankingsDto> supplierRankings = getRankingsByType(DealingTypeEnum.SUPPLIER.getCode(), month);
        rankingsGvmVo.setSupplierGMVRanking(supplierRankings);
        return rankingsGvmVo;
    }

    /**
     * 获取商品最低价格
     *
     * @param apiShoppingCartProductVo 小程序会员购物车商品缓存信息
     * @return 单规格取当前价格  多规格取最低价
     */
    private BigDecimal getProductFloorPrice(ApiShoppingCartProductVo apiShoppingCartProductVo) {
        List<SkuStockDto> skuStocks = apiShoppingCartProductVo.getSkuStocks();
        return skuStocks.stream()
                .map(sku -> sku.getPrice().compareTo(BigDecimal.ZERO) == 0 ? sku.getOriginalPrice() : sku.getPrice())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}

