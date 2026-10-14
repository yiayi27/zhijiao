package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MiniAccountExtends;
import com.medusa.gruul.account.api.entity.MiniAccountRestrict;
import com.medusa.gruul.account.api.enums.BlacklistEnum;
import com.medusa.gruul.account.mapper.MiniAccountRestrictMapper;
import com.medusa.gruul.account.model.dto.SetBlacklistDto;
import com.medusa.gruul.account.service.IMiniAccountExtendsService;
import com.medusa.gruul.account.service.IMiniAccountRestrictService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-12-02
 */
@Service
public class MiniAccountRestrictServiceImpl extends ServiceImpl<MiniAccountRestrictMapper, MiniAccountRestrict> implements IMiniAccountRestrictService {

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    /**
     * 获取指定用户黑名单数据
     *
     * @param userIds 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccountRestrict
     */
    @Override
    public List<MiniAccountRestrict> getByUserIds(List<String> userIds) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<MiniAccountRestrict>().in(MiniAccountRestrict::getUserId, userIds));
    }

    /**
     * 移除指定用户黑名单标识
     *
     * @param userIds 用户id
     */
    @Override
    public void removeByUserId(List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            throw new ServiceException("userIds is null", SystemCode.DATA_NOT_EXIST.getCode());
        }
        List<MiniAccountRestrict> accountRestricts = this.getByUserIds(userIds);
        if (CollectionUtil.isEmpty(accountRestricts)) {
            throw new ServiceException("userIds is null", SystemCode.DATA_NOT_EXIST.getCode());
        }
        List<Long> ids = accountRestricts.stream().map(MiniAccountRestrict::getId).toList();
        this.baseMapper.deleteByIds(ids);
    }

    /**
     * 新增用户限制类型
     *
     * @param userIds       用户id
     * @param rejectInteger 限制类型数组
     */
    @Override
    public void addUserRestrict(List<String> userIds, List<Integer> rejectInteger) {
        if (CollectionUtil.isEmpty(userIds)) {
            throw new ServiceException("用户id为空", SystemCode.DATA_NOT_EXIST.getCode());
        }
        Map<Integer, Integer> enumTypeMap = CollectionUtil.newArrayList(BlacklistEnum.REJECT_ORDER.getType(), BlacklistEnum.REJECT_COMMENT.getType()).stream().collect(Collectors.toMap(k -> k, v -> v));
        for (String userId : userIds) {
            //判断现有枚举类中是否该枚举,不存在则不添加
            for (Integer rejectType : rejectInteger) {
                Integer blacklistEnum = enumTypeMap.get(rejectType);
                if (blacklistEnum == null) {
                    throw new ServiceException("非法添加", SystemCode.DATA_NOT_EXIST.getCode());
                }
                MiniAccountRestrict miniAccountRestrict = new MiniAccountRestrict();
                miniAccountRestrict.setUserId(userId);
                miniAccountRestrict.setRestrictType(blacklistEnum);
                this.baseMapper.insert(miniAccountRestrict);
            }

        }
    }

    /**
     * 修改用户限制类型
     *
     * @param userIds       用户id
     * @param rejectInteger 修改之后的限制类型数组
     */
    @Override
    public void updateUserRestrict(List<String> userIds, List<Integer> rejectInteger) {
        removeByUserId(userIds);
        addUserRestrict(userIds, rejectInteger);
    }

    /**
     * 批量设置
     *
     * @param dto com.medusa.gruul.account.model.dto.SetBlacklistDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setBlacklist(SetBlacklistDto dto) {
        //如果是添加或修改操作,必须最少有一个限制类型
        if (dto.getOption().equals(CommonConstants.NUMBER_ONE) || dto.getOption().equals(CommonConstants.NUMBER_THREE)) {
            if (CollectionUtil.isEmpty(dto.getRejectInteger())) {
                throw new ServiceException("需选择限制类型", SystemCode.DATA_NOT_EXIST.getCode());
            }
        }
        switch (dto.getOption()) {
            case 1:
                //加入
                this.addUserRestrict(dto.getUserIds(), dto.getRejectInteger());
                this.updateUserBlacklist(dto.getUserIds(), CommonConstants.NUMBER_ONE);
                break;
            case 2:
                //批量移除
                this.removeByUserId(dto.getUserIds());
                updateUserBlacklist(dto.getUserIds(), CommonConstants.NUMBER_TWO);
                break;
            case 3:
                //修改
                this.updateUserRestrict(dto.getUserIds(), dto.getRejectInteger());
                break;
            default:
                throw new ServiceException("非法操作", SystemCode.DATA_NOT_EXIST.getCode());
        }

    }

    /**
     * 获取指定用户限制数组
     *
     * @param userId 用户id
     * @return java.lang.Integer
     */
    @Override
    public List<Integer> getByUserId(String userId) {
        List<MiniAccountRestrict> accountRestricts = this.baseMapper.selectList(new QueryWrapper<MiniAccountRestrict>().eq("user_id", userId));
        if (CollectionUtil.isNotEmpty(accountRestricts)) {
            return accountRestricts.stream().map(MiniAccountRestrict::getRestrictType).toList();
        }
        return null;
    }


    /**
     * @param userIds 用户id
     * @param type    更新类型 1-加入黑名单  2-移除黑名单
     */
    private void updateUserBlacklist(List<String> userIds, Integer type) {
        MiniAccountExtends miniAccountExtends = new MiniAccountExtends();
        if (type.equals(CommonConstants.NUMBER_ONE)) {
            miniAccountExtends.setJoinBlacklistTime(LocalDateTimeUtils.convertDateToLdt(new Date()));
            miniAccountExtends.setIsBlacklist(CommonConstants.NUMBER_ONE);
        }
        if (type.equals(CommonConstants.NUMBER_TWO)) {
            miniAccountExtends.setIsBlacklist(CommonConstants.NUMBER_ZERO);
        }
        miniAccountExtendsService.getBaseMapper().update(miniAccountExtends, new UpdateWrapper<MiniAccountExtends>().in("shop_user_id", userIds));
    }

}
