package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MiniAccountTagGroup;
import com.medusa.gruul.account.mapper.MiniAccountTagGroupMapper;
import com.medusa.gruul.account.service.IMiniAccountTagGroupService;
import com.medusa.gruul.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户所属分组表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
public class MiniAccountTagGroupServiceImpl extends ServiceImpl<MiniAccountTagGroupMapper, MiniAccountTagGroup> implements IMiniAccountTagGroupService {

    /**
     * 获取指定用户的标签
     *
     * @param userIdList 用户id数组
     * @return com.medusa.gruul.account.api.entity.MiniAccountTagGroup
     */
    @Override
    public List<MiniAccountTagGroup> getByUserListId(List<String> userIdList) {
        return this.getBaseMapper().selectList(new LambdaQueryWrapper<MiniAccountTagGroup>().in(MiniAccountTagGroup::getUserId, userIdList));
    }

    /**
     * 移除指定用户所有分组
     *
     * @param userIds 用户ids
     */
    @Override
    public void removeByUserId(List<String> userIds) {
        this.getBaseMapper().delete(new LambdaQueryWrapper<MiniAccountTagGroup>().in(MiniAccountTagGroup::getUserId, userIds));
    }

    /**
     * 添加用户标签
     *
     * @param userIds 用户ids
     * @param tagIds  标签ids
     */
    @Override
    public void addUserTag(List<String> userIds, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            for (String userId : userIds) {
                MiniAccountTagGroup miniAccountTagGroup = new MiniAccountTagGroup();
                miniAccountTagGroup.setTagId(tagId);
                miniAccountTagGroup.setUserId(userId);
                this.baseMapper.insert(miniAccountTagGroup);
            }
        }
    }

    /**
     * 移除指定用户指定标签
     *
     * @param userIds 用户ids
     * @param tagId   标签id数组
     */
    @Override
    public void removeUserTag(List<String> userIds, List<Long> tagId) {
        this.getBaseMapper().delete(new LambdaQueryWrapper<MiniAccountTagGroup>().in(MiniAccountTagGroup::getUserId, userIds).in(MiniAccountTagGroup::getTagId, tagId));
    }

    /**
     * 移除指定指定标签
     * @param tagId 标签id
     */
    @Override
    public void removeByTagId(List<Long> tagId) {
        List<MiniAccountTagGroup> tagIds = this.getBaseMapper().selectList(new LambdaQueryWrapper<MiniAccountTagGroup>().in(MiniAccountTagGroup::getTagId, tagId));
        if (CollectionUtil.isNotEmpty(tagIds)) {
            throw new ServiceException("当前标签已被使用,无法删除");
        }
    }

}
