package com.medusa.gruul.recycling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.recycling.api.entity.RecyclingOrder;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto;
import com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo;
import com.medusa.gruul.recycling.model.vo.RecyclingPcOrderListVo;

/**
 * <p>
 * 回收订单表 服务类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
public interface IRecyclingOrderService extends IService<RecyclingOrder> {

    /**
     * 判断是否存在，需要寄出的回收订单
     *
     * @return true 存在，false 不存在
     */
    Boolean exitWaitSendOrder();

    /**
     * 新增回收订单,返回当前订单获得的卡券
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto
     * @return
     */
    String recyclingOrderNew(RecyclingOrderNewDto dto);

    /**
     * 用户获取我的回收订单
     *
     * @param orderStatus 查询订单  0-全部  1-待寄出  2-已寄出  3-已取消
     * @param page        页数
     * @param size        条数
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    ResultPageList<RecyclingOrderListVo> recyclingOrderList(Integer orderStatus, Integer page, Integer size);

    /**
     * 用户取消新增的回收订单
     *
     * @param id 订单id
     */
    void recyclingOrderCancel(Long id);

    /**
     * 用户删除回收订单
     *
     * @param id 订单id
     */
    void recyclingOrderDel(Long id);

    /**
     * PC端商户获取相关订单
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @param id      订单id
     * @param shipmentNumber 快递单号
     * @return ResultPageList<RecyclingPcOrderListVo>
     */
    ResultPageList<RecyclingPcOrderListVo> recyclingPcOrderList(Integer page, Integer size, String nickName,Long id,String shipmentNumber);

    /**
     * 用户寄出回收订单
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto
     */
    void recyclingOrderSend(RecyclingOrderSendDto dto);
}
