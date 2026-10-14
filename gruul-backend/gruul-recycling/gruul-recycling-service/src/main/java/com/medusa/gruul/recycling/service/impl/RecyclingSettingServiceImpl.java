package com.medusa.gruul.recycling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.recycling.api.entity.RecyclingSetting;
import com.medusa.gruul.recycling.mapper.RecyclingSettingMapper;
import com.medusa.gruul.recycling.model.dto.BtnStyleDto;
import com.medusa.gruul.recycling.model.dto.CouponModifyDto;
import com.medusa.gruul.recycling.model.dto.FundModifyDto;
import com.medusa.gruul.recycling.model.dto.PopupWindowDto;
import com.medusa.gruul.recycling.model.vo.RecyclingSettingVo;
import com.medusa.gruul.recycling.service.IRecyclingSettingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 回收设置表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Service
public class RecyclingSettingServiceImpl extends ServiceImpl<RecyclingSettingMapper, RecyclingSetting> implements IRecyclingSettingService {

    /**
     * 获取当前店铺信息
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingSettingVo
     */
    @Override
    public RecyclingSettingVo getSetting() {
        String shopId = ISystem.shopId();
        RecyclingSetting recyclingSetting = this.baseMapper.selectOne(
                new LambdaQueryWrapper<RecyclingSetting>()
                        .eq(RecyclingSetting::getShopId, shopId)
        );
        if (recyclingSetting == null) {
            //不存在则初始化店铺相关数据
            recyclingSetting = new RecyclingSetting();
            recyclingSetting.setId(CommonConstants.LONG_ZERO);
            recyclingSetting.setShopId(shopId);
            recyclingSetting.setGoodsFundState(false);
            recyclingSetting.setFundQuota(BigDecimal.ZERO);
            recyclingSetting.setPageBgImg(StrUtil.EMPTY);
            recyclingSetting.setBuyBgImg(StrUtil.EMPTY);
            recyclingSetting.setHomePromptBgImg(StrUtil.EMPTY);
            recyclingSetting.setRecyclingState(false);
            recyclingSetting.setEntranceState(false);
            List<BtnStyleDto> btns = CollectionUtil.newArrayList(
                    new BtnStyleDto("旧衣回收", "#409EFF", CommonConstants.NUMBER_ONE),
                    new BtnStyleDto("加入购物车", "#869926", CommonConstants.NUMBER_TWO),
                    new BtnStyleDto("立即购买", "#409EFF", CommonConstants.NUMBER_THREE)
            );
            recyclingSetting.setBtnStyleJson(JSONUtil.toJsonStr(btns));
            recyclingSetting.setAssCouponId(CommonConstants.LONG_ZERO);
            recyclingSetting.setPageCarousels("");
            recyclingSetting.setActivityDesc("");
            recyclingSetting.setSubmitImg("");
            this.baseMapper.insert(recyclingSetting);
        }
        RecyclingSettingVo recyclingSettingVo = BeanUtil.toBean(recyclingSetting, RecyclingSettingVo.class);
        if (StrUtil.isNotEmpty(recyclingSetting.getBtnStyleJson())) {
            recyclingSettingVo.setBtnStyles(JSONUtil.toList(recyclingSetting.getBtnStyleJson(), BtnStyleDto.class));
        }
        return recyclingSettingVo;
    }


    /**
     * 获取当前店铺回收相关信息
     *
     * @param id 表id
     * @return com.medusa.gruul.recycling.api.entity.RecyclingSetting
     */
    private RecyclingSetting isCurrentShopId(Long id) {
        RecyclingSetting recyclingSetting = this.getById(id);
        if (recyclingSetting == null) {
            throw new ServiceException("无效数据");
        }
        return recyclingSetting;
    }

    /**
     * 商品基金设置修改
     *
     * @param settingDto com.medusa.gruul.recycling.model.dto.FundModifyDto
     */
    @Override
    public void fundModify(FundModifyDto settingDto) {
        RecyclingSetting recyclingSetting = isCurrentShopId(settingDto.getId());
        RecyclingSetting rs = new RecyclingSetting();
        rs.setId(recyclingSetting.getId());
        rs.setGoodsFundState(settingDto.getGoodsFundState());
        rs.setFundQuota(settingDto.getFundQuota());
        rs.setPageBgImg(settingDto.getPageBgImg());
        rs.setBuyBgImg(settingDto.getBuyBgImg());
        this.baseMapper.updateById(rs);
    }

    /**
     * 回收卡券设置修改
     *
     * @param couponModifyDto com.medusa.gruul.recycling.model.dto.CouponModifyDto
     */
    @Override
    public void couponModify(CouponModifyDto couponModifyDto) {
        if (couponModifyDto.getRecyclingState()) {
            if (couponModifyDto.getAssCouponId() == null || couponModifyDto.getAssCouponId() < 0
                    || couponModifyDto.getPageCarousels().isEmpty() || couponModifyDto.getActivityDesc().isEmpty()
                    || couponModifyDto.getSubmitImg().isEmpty()) {
                throw new ServiceException("信息不能为空");
            }
            if (couponModifyDto.getEntranceState()) {
                if (ArrayUtil.isEmpty(couponModifyDto.getBtnStyles())) {
                    throw new ServiceException("页面轮播图不能为空");
                }
            }
        } else {
            if (couponModifyDto.getEntranceState()) {
                throw new ServiceException("商品详情回收入口不能开启");
            }
        }
        RecyclingSetting recyclingSetting = isCurrentShopId(couponModifyDto.getId());

        RecyclingSetting rs = new RecyclingSetting();
        rs.setId(recyclingSetting.getId());
        rs.setRecyclingState(couponModifyDto.getRecyclingState());
        rs.setEntranceState(couponModifyDto.getEntranceState());
        rs.setBtnStyleJson(JSONUtil.toJsonStr(couponModifyDto.getBtnStyles()));
        rs.setAssCouponId(couponModifyDto.getAssCouponId());
        rs.setPageCarousels(couponModifyDto.getPageCarousels());
        rs.setActivityDesc(couponModifyDto.getActivityDesc());
        rs.setSubmitImg(couponModifyDto.getSubmitImg());
        this.baseMapper.updateById(rs);
    }

    /**
     * 弹窗设置
     *
     * @param popupWindowDto com.medusa.gruul.recycling.model.dto.PopupWindowDto
     */
    @Override
    public void popupWindow(PopupWindowDto popupWindowDto) {
        RecyclingSetting recyclingSetting = isCurrentShopId(popupWindowDto.getId());
        RecyclingSetting rs = new RecyclingSetting();
        rs.setId(recyclingSetting.getId());
        rs.setHomePromptBgImg(popupWindowDto.getHomePromptBgImg());
        this.baseMapper.updateById(rs);
    }
}
