package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberOrderInfo;
import com.medusa.gruul.account.model.vo.MemberOrderInfoResVo;
import com.medusa.gruul.account.model.vo.UpdateOrderNoteVo;
import com.medusa.gruul.common.core.util.PageUtils;

import java.util.List;

/**
 * @Description 会员卡储值 服务类
 * @Author zhaokw
 * @Date 23:26 2024\6\13 0013
 **/
public interface IMemberOrderInfoService extends IService<MemberOrderInfo> {

    /**
     * pc端获取会员储值订单信息
     *
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param page          页数
     * @param size          条数
     * @param searchContent 搜索内容
     * @return
     */
    PageUtils<List<MemberOrderInfoResVo>> getMemberOrderInfo(String startTime, String endTime, int page, int size, String searchContent);

    /**
     * 更新储值订单备注
     *
     * @param updateOrderNoteVo com.medusa.gruul.account.model.vo.UpdateOrderNoteVo
     */
    void updateNote(UpdateOrderNoteVo updateOrderNoteVo);
}
