package com.medusa.gruul.recycling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.recycling.api.entity.RecyclingSetting;
import com.medusa.gruul.recycling.model.dto.CouponModifyDto;
import com.medusa.gruul.recycling.model.dto.FundModifyDto;
import com.medusa.gruul.recycling.model.dto.PopupWindowDto;
import com.medusa.gruul.recycling.model.vo.RecyclingSettingVo;

/**
 * <p>
 * 回收设置表 服务类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
public interface IRecyclingSettingService extends IService<RecyclingSetting> {

    /**
     * 获取当前店铺信息
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingSettingVo
     */
    RecyclingSettingVo getSetting();

    /**
     * 商品基金设置修改
     *
     * @param settingDto com.medusa.gruul.recycling.model.dto.FundModifyDto
     */
    void fundModify(FundModifyDto settingDto);

    /**
     * 回收卡券设置修改
     *
     * @param couponModifyDto com.medusa.gruul.recycling.model.dto.CouponModifyDto
     */
    void couponModify(CouponModifyDto couponModifyDto);

    /**
     * 弹窗设置
     *
     * @param popupWindowDto com.medusa.gruul.recycling.model.dto.PopupWindowDto
     */
    void popupWindow(PopupWindowDto popupWindowDto);
}
