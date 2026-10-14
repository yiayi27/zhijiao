package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberCardInfo;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.api.entity.MemberCardPrivilegeGroup;
import com.medusa.gruul.account.mapper.MemberCardInfoMapper;
import com.medusa.gruul.account.mapper.MemberCardPrivilegeGroupMapper;
import com.medusa.gruul.account.mapper.MemberCardPrivilegeMapper;
import com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto;
import com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto;
import com.medusa.gruul.account.service.IMemberCardPrivilegeService;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;

import com.medusa.gruul.common.security.resource.helper.ISecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 会员卡权益表 服务实现类
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-24
 */
@Service
public class MemberCardPrivilegeServiceImpl extends ServiceImpl<MemberCardPrivilegeMapper, MemberCardPrivilege> implements IMemberCardPrivilegeService {

    @Autowired
    private MemberCardPrivilegeMapper memberCardPrivilegeMapper;

    @Autowired
    private MemberCardPrivilegeGroupMapper memberCardPrivilegeGroupMapper;

    @Autowired
    private MemberCardInfoMapper memberCardInfoMapper;

    /**
     * pc端获取会员卡权益信息
     *
     * @return com.medusa.gruul.account.api.entity.MemberCardPrivilege
     */
    @Override
    public List<MemberCardPrivilege> getMemberCardPrivilege() {
        return memberCardPrivilegeMapper.getMemberCardPrivilege(ISecurity.userMust().getShopId());
    }

    /**
     * 后台更新权益参数
     *
     * @param updateMemberCardPrivilegeDto com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberCardPrivilege(UpdateMemberCardPrivilegeDto updateMemberCardPrivilegeDto) {
        MemberCardPrivilege memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setAllowDel(updateMemberCardPrivilegeDto.getAllowDel())
                .setIcon(updateMemberCardPrivilegeDto.getIcon())
                .setId(updateMemberCardPrivilegeDto.getId())
                .setIllustrate(updateMemberCardPrivilegeDto.getIllustrate())
                .setOpen(updateMemberCardPrivilegeDto.getOpen())
                .setPrivilegeName(updateMemberCardPrivilegeDto.getPrivilegeName())
                .setPType(updateMemberCardPrivilegeDto.getPType())
                .setUpdateTime(LocalDateTime.now());
        int update = memberCardPrivilegeMapper.updateById(memberCardPrivilege);
        if (update < 1) {
            throw new ServiceException("后台更新权益参数", SystemCode.DATA_UPDATE_FAILED_CODE);
        }
    }

    /**
     * 后台开启关闭会员卡权益
     *
     * @param memberCardPrivilegeStatusDto com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberCardPrivilegeStatus(MemberCardPrivilegeStatusDto memberCardPrivilegeStatusDto) {
        MemberCardPrivilege memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setId(memberCardPrivilegeStatusDto.getId())
                .setOpen(memberCardPrivilegeStatusDto.getOpen());
        int update = memberCardPrivilegeMapper.updateById(memberCardPrivilege);
        if (update < 1) {
            throw new ServiceException("后台更新特权开关", SystemCode.DATA_UPDATE_FAILED_CODE);
        }
    }

    /**
     * 后台删除自定义会员卡权益
     *
     * @param deleteMemberCardPrivilegeDto com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMemberCardPrivilege(DeleteMemberCardPrivilegeDto deleteMemberCardPrivilegeDto) {
        MemberCardPrivilege memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setId(deleteMemberCardPrivilegeDto.getId())
                .setDeleted(deleteMemberCardPrivilegeDto.getIsDeleted() == 1);
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.selectList(new QueryWrapper<MemberCardInfo>());
        int count = 0;
        for (MemberCardInfo memberCardInfo : memberCardInfoList) {
            count += memberCardPrivilegeGroupMapper.selectCount(new QueryWrapper<MemberCardPrivilegeGroup>().eq("privilege_id", memberCardPrivilege.getId()).eq("member_card_id", memberCardInfo.getId()));
        }
        if (count > 0) {
            throw new ServiceException("后台更新删除状态失败，会员卡绑定会员卡权益，请解绑后再试！", SystemCode.DATA_UPDATE_FAILED_CODE);
        } else {
            int delete = memberCardPrivilegeMapper.deleteById(memberCardPrivilege);
            if (delete < 1) {
                throw new ServiceException("后台更新删除状态", SystemCode.DATA_UPDATE_FAILED_CODE);
            }
        }

    }

    /**
     * 后台插入自定义权益参数
     *
     * @param insertMemberCardPrivilegeDto 权益信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMemberCardPrivilege(InsertMemberCardPrivilegeDto insertMemberCardPrivilegeDto) {
        MemberCardPrivilege memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setOpen(insertMemberCardPrivilegeDto.getOpen())
                .setPType(insertMemberCardPrivilegeDto.getPType())
                .setPrivilegeName(insertMemberCardPrivilegeDto.getPrivilegeName())
                .setIsRule(insertMemberCardPrivilegeDto.getIsRule())
                .setIllustrate(insertMemberCardPrivilegeDto.getIllustrate())
                .setIcon(insertMemberCardPrivilegeDto.getIcon())
                .setAllowDel(insertMemberCardPrivilegeDto.getAllowDel());
        int insert = memberCardPrivilegeMapper.insert(memberCardPrivilege);
        if (insert < 1) {
            throw new ServiceException("新增自定义权益失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }
}
