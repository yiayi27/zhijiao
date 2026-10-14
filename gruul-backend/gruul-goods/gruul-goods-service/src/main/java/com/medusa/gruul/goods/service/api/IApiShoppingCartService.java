package com.medusa.gruul.goods.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ShoppingCart;
import com.medusa.gruul.goods.api.model.dto.api.ApiShoppingCartDto;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartVo;
import com.medusa.gruul.goods.mq.ShoppingCartMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 小程序商品信息 服务类
 *
 * @author lcy
 * @since 2023-10-15
 */
public interface IApiShoppingCartService extends IService<ShoppingCart> {

    /**
     * 查询用户购物车商品详情
     *
     * @return 购物车list信息
     */
    List<ApiShoppingCartVo> getShoppingCartListByUserId();

    /**
     * 加入购物车
     *
     * @param apiShoppingCartDto 加入购物车商品信息
     */
    void addShoppingCart(ApiShoppingCartDto apiShoppingCartDto);

    /**
     * 清空用户购物车失效商品
     *
     * @param ids 购物车商品ids
     */
    void cleanEffectShoppingCart(Set<Long> ids);

    /**
     * 切换购物车商品选中状态
     *
     * @param ids 购物车商品ids
     * @param selectStatus 选中状态 0-未选中 1-选中
     */
    void changeSelectStatus(Set<Long> ids, Integer selectStatus);

    /**
     * 修改购物车信息
     *
     * @param params 被修改钱与修改后的购物车商品信息
     */
    void updateShoppingCart(Map<String, ApiShoppingCartDto> params);

    /**
     * 修改购物车数据表数据
     *
     * @param shoppingCartMessage mq传输的消息体
     */
    void updateShoppingCartDatabase(ShoppingCartMessage shoppingCartMessage);

    /**
     * 根据购物车ids批量删除购物车商品
     *
     * @param apiShoppingCartDtos 购物车商品ids
     */
    void deleteShoppingCartList(List<ApiShoppingCartDto> apiShoppingCartDtos);

    /**
     * 订单结算删除结算的购物车商品数据
     *
     * @param skuIds 购物车商品ids
     * @param userId 用户id
     * @return Boolean
     */
    Boolean deleteShoppingCartByOrder(List<Long> skuIds, String userId);

}
