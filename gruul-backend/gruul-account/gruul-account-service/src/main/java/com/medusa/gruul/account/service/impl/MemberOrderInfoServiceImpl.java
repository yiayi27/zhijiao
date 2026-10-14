package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberOrderInfo;
import com.medusa.gruul.account.mapper.MemberOrderInfoMapper;
import com.medusa.gruul.account.model.vo.MemberOrderInfoResVo;
import com.medusa.gruul.account.model.vo.UpdateOrderNoteVo;
import com.medusa.gruul.account.service.IMemberOrderInfoService;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 会员储值信息实现类
 * @Author: zhaokw29701
 * @Date: 2024/6/12 22:31
 **/
@Service
public class MemberOrderInfoServiceImpl extends ServiceImpl<MemberOrderInfoMapper, MemberOrderInfo> implements IMemberOrderInfoService {

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
    @Override
    public PageUtils<List<MemberOrderInfoResVo>> getMemberOrderInfo(String startTime, String endTime, int page, int size, String searchContent) {
        Map<String, Object> paramMap = new HashMap<>(3);
        if (StrUtil.isNotEmpty(startTime)) {
            paramMap.put("startTime", startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            paramMap.put("endTime", endTime);
        }
        if (StrUtil.isNotBlank(searchContent)) {
            searchContent = searchContent.concat("%");
            paramMap.put("searchContent", searchContent);
        }
        IPage<MemberOrderInfoResVo> iPage = this.baseMapper.selectMemberOrderList(new Page<>(page, size), paramMap);
        List<MemberOrderInfoResVo> records = iPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageUtils<>(new ArrayList<>(0), (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
        }

        return new PageUtils(records, (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
    }

    /**
     * 更新储值订单备注
     *
     * @param updateOrderNoteVo com.medusa.gruul.account.model.vo.UpdateOrderNoteVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNote(UpdateOrderNoteVo updateOrderNoteVo) {
        MemberOrderInfo memberOrderInfo = new MemberOrderInfo();
        memberOrderInfo.setId(updateOrderNoteVo.getId())
                .setNote(updateOrderNoteVo.getNote())
                .setState(updateOrderNoteVo.getState());
        int update = this.getBaseMapper().updateById(memberOrderInfo);
        if (update == 0) {
            throw new ServiceException("更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }
}
