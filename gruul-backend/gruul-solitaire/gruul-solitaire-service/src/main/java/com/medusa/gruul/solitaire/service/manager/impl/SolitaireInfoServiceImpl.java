package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.entity.SolitaireInfo;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireInfoDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireInfoVo;
import com.medusa.gruul.solitaire.mapper.manager.SolitaireInfoMapper;
import com.medusa.gruul.solitaire.service.manager.ISolitaireInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p>
 * 接龙信息设置 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-06-09
 */
@Service
public class SolitaireInfoServiceImpl extends ServiceImpl<SolitaireInfoMapper, SolitaireInfo> implements ISolitaireInfoService {

    /**
     * 获取接龙信息设置明细
     *
     * @return 接龙信息对象
     */
    @Override
    public SolitaireInfoVo getInfo() {
        SolitaireInfoVo solitaireActivityVo = new SolitaireInfoVo();
        List<SolitaireInfo> solitaireInfos = this.baseMapper.selectList(new QueryWrapper<>());
        if(CollectionUtil.isNotEmpty(solitaireInfos) && solitaireInfos.size() == CommonConstants.NUMBER_ONE){
            SolitaireInfo solitaireInfo = solitaireInfos.get(CommonConstants.NUMBER_ZERO);
            if(!BeanUtil.isEmpty(solitaireInfo)){
                BeanUtil.copyProperties(solitaireInfo, solitaireActivityVo);
            }
        }
        return solitaireActivityVo;
    }

    /**
     * 接龙信息设置新增/修改
     *
     * @param solitaireInfoDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveInfo(SolitaireInfoDto solitaireInfoDto) {
        SolitaireInfo solitaireInfo = this.baseMapper.selectOne(new QueryWrapper<>());
        if(BeanUtil.isEmpty(solitaireInfo)){
            SolitaireInfo solitaireInfoInsert = solitaireInfoDto.coverInfo();
            //接龙信息设置新增
            int insert = this.baseMapper.insert(solitaireInfoInsert);
            if (insert == 0) {
                throw new ServiceException("接龙信息设置新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }else{
            solitaireInfo = solitaireInfoDto.coverInfo();
            //接龙信息设置修改
            int update = this.baseMapper.updateById(solitaireInfo);
            if (update == 0) {
                throw new ServiceException("接龙信息设置修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }
}
