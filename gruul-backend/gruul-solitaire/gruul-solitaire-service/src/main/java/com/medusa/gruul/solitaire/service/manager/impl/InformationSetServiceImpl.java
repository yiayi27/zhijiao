package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.solitaire.api.entity.InformationSet;
import com.medusa.gruul.solitaire.api.entity.SolitaireInfo;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationSetDto;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireInfoDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationSetVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireInfoVo;
import com.medusa.gruul.solitaire.mapper.manager.InformationSetMapper;
import com.medusa.gruul.solitaire.mapper.manager.SolitaireInfoMapper;
import com.medusa.gruul.solitaire.service.manager.IInformationSetService;
import com.medusa.gruul.solitaire.service.manager.ISolitaireInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p>
 * 资讯信息设置 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Service
public class InformationSetServiceImpl extends ServiceImpl<InformationSetMapper, InformationSet> implements IInformationSetService {

    /**
     * 获取资讯设置明细
     *
     * @return 资讯信息对象
     */
    @Override
    public InformationSetVo get() {
        InformationSetVo informationSetVo = new InformationSetVo();
        List<InformationSet> informationSets = this.baseMapper.selectList(new QueryWrapper<>());
        if(CollectionUtil.isNotEmpty(informationSets) && informationSets.size() == CommonConstants.NUMBER_ONE){
            InformationSet informationSet = informationSets.get(CommonConstants.NUMBER_ZERO);
            if(!BeanUtil.isEmpty(informationSet)){
                BeanUtil.copyProperties(informationSet, informationSetVo);
            }
        }
        return informationSetVo;
    }

    /**
     * 资讯信息设置新增/修改
     *
     * @param informationSetDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(InformationSetDto informationSetDto) {
        InformationSet informationSetSearch = this.baseMapper.selectOne(new QueryWrapper<>());
        if(BeanUtil.isEmpty(informationSetSearch)){
            InformationSet informationSetInsert = informationSetDto.coverBean();
            //接龙信息设置新增
            int insert = this.baseMapper.insert(informationSetInsert);
            if (insert == 0) {
                throw new ServiceException("资讯设置新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }else{
            InformationSet informationSet = informationSetDto.coverBean();
            informationSet.setId(informationSetSearch.getId());
            //资讯设置修改
            int update = this.baseMapper.updateById(informationSet);
            if (update == 0) {
                throw new ServiceException("资讯设置修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }
}
