package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberSwitch;
import com.medusa.gruul.account.mapper.MemberSwitchMapper;
import com.medusa.gruul.account.service.IMemberSwitchService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 账户余额明细表 服务实现类
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
@Service
public class MemberSwitchServiceImpl extends ServiceImpl<MemberSwitchMapper, MemberSwitch>
        implements IMemberSwitchService {

    /**
     * 获取指定会员模块的开关
     *
     * @param memberModel 会员模块名
     * @return com.medusa.gruul.account.api.entity.MemberSwitch
     */
    @Override
    public MemberSwitch getByMemberModelMemberSwitch(Integer memberModel) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<MemberSwitch>()
                .eq(MemberSwitch::getMemberModel, memberModel));
    }
}
