package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationMessageVo;
import com.medusa.gruul.solitaire.mapper.manager.InformationMessageMapper;
import com.medusa.gruul.solitaire.service.manager.IInformationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * 资讯留言信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Service
public class InformationMessageServiceImpl extends ServiceImpl<InformationMessageMapper, InformationMessage> implements IInformationMessageService {


    @Autowired
    private InformationMessageMapper informationMessageMapper;

    /**
     * 获取资讯留言分页信息
     *
     * @param informationMessageParam 资讯留言查询参数
     * @return 资讯留言分页对象
     */
    @Override
    public PageUtils<InformationMessageVo> list(InformationMessageParam informationMessageParam) {
        IPage<InformationMessageVo> page = new Page<>(informationMessageParam.getCurrent(), informationMessageParam.getSize());
        List<InformationMessageVo> informationMessageVos = this.baseMapper.queryList(page, informationMessageParam);
        PageUtils<InformationMessageVo> result = new PageUtils<>(null, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
        result.setList(informationMessageVos);
        return result;
    }

    /**
     * 资讯留言回复
     *
     * @param informationMessageDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(InformationMessageDto informationMessageDto) {
        InformationMessage informationMessage = this.baseMapper.selectById(informationMessageDto.getId());
        if (BeanUtil.isEmpty(informationMessage)) {
            throw new ServiceException("资讯留言信息不存在!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        if (StrUtil.isNotBlank(informationMessage.getAdminContent())) {
            throw new ServiceException("该留言已回复过!", SystemCode.DATA_EXISTED.getCode());
        }
        //修改资讯信息
        informationMessage.setAdminContent(informationMessageDto.getAdminContent());
        informationMessage.setAdminSendTime(new Date());
        int update = this.baseMapper.updateById(informationMessage);
        if (update == 0) {
            throw new ServiceException("资讯留言回复失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 资讯信息设为/取消星标
     *
     * @param informationMessageDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void star(InformationMessageDto informationMessageDto) {
        InformationMessage informationMessage = this.baseMapper.selectById(informationMessageDto.getId());
        if (BeanUtil.isEmpty(informationMessage)) {
            throw new ServiceException("资讯留言信息不存在!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        //新增资讯信息
        informationMessage.setMessageType(informationMessageDto.getMessageType());
        int update = this.baseMapper.updateById(informationMessage);
        if (update == 0) {
            throw new ServiceException("留言星标设置失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 资讯留言信息排序
     *
     * @param informationMessageDtos 咨询留言信息集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(List<InformationMessageDto> informationMessageDtos) {
        informationMessageDtos.forEach(informationMessageDto -> {
            boolean update = new LambdaUpdateChainWrapper<>(this.baseMapper)
                    .eq(InformationMessage::getId, informationMessageDto.getId())
                    .set(InformationMessage::getSort, informationMessageDto.getSort())
                    .update();
            if (!update) {
                throw new ServiceException("排序修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 资讯留言信息删除
     *
     * @param id 资讯信息id
     */
    @Override
    public void delete(Long id) {
        informationMessageMapper.deleteById(id);
    }

}
