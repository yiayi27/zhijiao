package com.medusa.gruul.afs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.model.ManageAfsOrderVo;
import com.medusa.gruul.afs.model.SearchDto;
import com.medusa.gruul.afs.model.SellerNoteDto;
import com.medusa.gruul.afs.model.SellerRefusalDto;
import com.medusa.gruul.common.core.util.PageUtils;

/**
 * <p>
 * 售后工单 服务类
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
public interface IManageAfsOrderService extends IService<AfsOrder> {

    /**
     * 商家拒绝申请
     *
     * @param dto 售后工单信息
     */
    void sellerRefuse(SellerRefusalDto dto);

    /**
     * 商家同意申请
     *
     * @param afsId 售后工单id
     * @param isSystem 是否系统操作
     */
    void sellerApprove(Long afsId, Boolean isSystem);

    /**
     * 商家批量备注订单
     *
     * @param dto 售后工单信息
     */
    void note(SellerNoteDto dto);

    /**
     * Point apply exchange.
     *
     * @param afsOrder the afs order
     * @param isSystem the is system
     */
    void pointApplyExchange(AfsOrder afsOrder, Boolean isSystem);

    /**
     * User apply exchange.
     *
     * @param afsOrder   the afs order
     * @param isSystem   the is system
     * @param needReturn the need return
     */
    void userApplyExchange(AfsOrder afsOrder, boolean isSystem, boolean needReturn);

    /**
     * User apply refund.
     *
     * @param afsOrder the afs order
     * @param isSystem the is system
     */
    void userApplyRefund(AfsOrder afsOrder, boolean isSystem);

    /**
     * User apply return.
     *
     * @param afsOrder   the afs order
     * @param isSystem   the is system
     * @param needReturn the need return
     */
    void userApplyReturn(AfsOrder afsOrder, Boolean isSystem, boolean needReturn);

    /**
     * 商家售后列表查看
     *
     * @param dto 售后工单信息
     * @return Result<PageUtils<ManageAfsOrderVo>>
     */
    PageUtils<ManageAfsOrderVo> searchManageAfsOrderVoPage(SearchDto dto);
}
