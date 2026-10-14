package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberRegisterForm;
import com.medusa.gruul.account.mapper.MemberRegisterFormMapper;
import com.medusa.gruul.account.model.vo.MemberRegisterFormVo;
import com.medusa.gruul.account.service.IMemberRegisterFormService;

import com.medusa.gruul.common.security.resource.helper.ISecurity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 会员注册表单
 * @Author whh
 * @Date 2024-1-17
 **/
@Service
public class MemberRegisterFormServiceImpl extends ServiceImpl<MemberRegisterFormMapper, MemberRegisterForm> implements IMemberRegisterFormService {

    /**
     * 处理会员卡注册表单
     *
     * @param memberRegisterFormVos com.medusa.gruul.account.model.vo.MemberRegisterFormVo
     */
    @Override
    public void operationMemberRegisterForm(List<MemberRegisterFormVo> memberRegisterFormVos) {
        if (CollectionUtil.isEmpty(memberRegisterFormVos)) {
            return;
        }
        List<Long> exist = new ArrayList<>(memberRegisterFormVos.size());
        //会员注册表单信息
        for (MemberRegisterFormVo memberRegisterFormVo : memberRegisterFormVos) {
            MemberRegisterForm memberRegisterForm = this.baseMapper.qryFormById(memberRegisterFormVo.getFormId(), ISecurity.userMust().getShopId());
            //更新
            if (memberRegisterForm == null) {
                memberRegisterForm = new MemberRegisterForm();
            }
            memberRegisterForm.setFormName(memberRegisterFormVo.getFormName())
                    .setFormPattern(memberRegisterFormVo.getFormPattern())
                    .setIsRequired(memberRegisterFormVo.getIsRequired())
                    .setTips(memberRegisterFormVo.getTips())
                    .setFormId(memberRegisterFormVo.getFormId());
            this.saveOrUpdate(memberRegisterForm);
            exist.add(memberRegisterForm.getId());
        }
        List<MemberRegisterForm> registerForms = this.baseMapper.selectList(new QueryWrapper<MemberRegisterForm>().notIn("id", exist));
        if (CollectionUtil.isNotEmpty(registerForms)) {
            this.baseMapper.deleteByIds(registerForms.stream().map(MemberRegisterForm::getId).toList());
        }
    }
}
