package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.solitaire.api.entity.InformationInfoTopic;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationTopicDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationTopicVo;
import com.medusa.gruul.solitaire.mapper.manager.InformationInfoTopicMapper;
import com.medusa.gruul.solitaire.mapper.manager.InformationTopicMapper;
import com.medusa.gruul.solitaire.service.manager.IInformationTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 资讯专题专题 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Service
public class InformationTopicServiceImpl extends ServiceImpl<InformationTopicMapper, InformationTopic> implements IInformationTopicService {

    @Autowired
    private InformationInfoTopicMapper informationInfoTopicMapper;

    /**
     * 获取资讯专题明细
     *
     * @return 资讯专题集合对象
     */
    @Override
    public List<InformationTopicVo> get() {
        return this.baseMapper.queryList();
    }

    /**
     * 资讯信息专题新增/修改(支持批量)
     *
     * @param informationTopicDtos dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<InformationTopicDto> informationTopicDtos) {
        List<InformationTopicDto> insertList = new ArrayList<>(informationTopicDtos.size());
        List<InformationTopicDto> updateList = new ArrayList<>(informationTopicDtos.size());
        informationTopicDtos.forEach(informationTopicDto -> {
            if (informationTopicDto.getId() == null) {
                insertList.add(informationTopicDto);
            } else {
                updateList.add(informationTopicDto);
            }
        });
        //先判断专题数量是否大于等于9个 专题数量不能超过9个(记得去除系统自带的全部专题)
        long count = this.baseMapper.selectCount(new QueryWrapper<>());
        int insertCount = insertList.size();
        if (count + insertCount > CommonConstants.NUMBER_TEN) {
            throw new ServiceException("最多只能设置10个专题！", SystemCode.PARAM_VALID_ERROR.getCode());
        }
        //新增数据插入
        insertList.forEach(informationTopicDto -> {
            InformationTopic informationTopic = this.baseMapper.selectOne(new QueryWrapper<InformationTopic>().eq("topic_name", informationTopicDto.getTopicName()));
            if (BeanUtil.isNotEmpty(informationTopic)) {
                throw new ServiceException(informationTopicDto.getTopicName() + "专题名称已存在！", SystemCode.DATA_EXISTED.getCode());
            }
            //新增资讯专题
            informationTopic = informationTopicDto.coverBean();
            int insert = this.baseMapper.insert(informationTopic);
            if (insert == 0) {
                throw new ServiceException("资讯专题新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
        //修改数据修改
        updateList.forEach(informationTopicDto -> {
            //修改资讯专题
            InformationTopic informationTopic = this.baseMapper.selectById(informationTopicDto.getId());
            if (BeanUtil.isEmpty(informationTopic)) {
                throw new ServiceException("专题信息不存在!", SystemCode.DATA_NOT_EXIST.getCode());
            }
            //验证专题名称是否重复
            InformationTopic informationTopicSearch = this.baseMapper.selectOne(new QueryWrapper<InformationTopic>().eq("topic_name", informationTopicDto.getTopicName()).ne("id", informationTopicDto.getId()));
            if (BeanUtil.isNotEmpty(informationTopicSearch)) {
                throw new ServiceException(informationTopicDto.getTopicName() + "专题名称已存在！", SystemCode.DATA_EXISTED.getCode());
            }
            informationTopic = informationTopicDto.coverBean();
            //资讯设置修改
            int update = this.baseMapper.updateById(informationTopic);
            if (update == 0) {
                throw new ServiceException("资讯专题修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 资讯专题排序
     *
     * @param informationTopicDtos
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(List<InformationTopicDto> informationTopicDtos) {
        informationTopicDtos.forEach(informationTopicDto -> {
            boolean update = new LambdaUpdateChainWrapper<>(this.baseMapper)
                    .eq(InformationTopic::getId, informationTopicDto.getId())
                    .set(InformationTopic::getSort, informationTopicDto.getSort())
                    .update();
            if (!update) {
                throw new ServiceException("排序修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 资讯专题删除
     *
     * @param id 资讯专题id
     */
    @Override
    public void delete(Long id) {
        InformationTopic informationTopic = this.baseMapper.selectById(id);
        if (BeanUtil.isEmpty(informationTopic)) {
            throw new ServiceException("专题信息不存在!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        //判断其他资讯是否使用了该专题  如使用不允许删除
        Long count = informationInfoTopicMapper.selectCount(new QueryWrapper<InformationInfoTopic>().eq("topic_id", id));
        if (count > CommonConstants.NUMBER_ZERO) {
            throw new ServiceException("专题已被其他资讯信息使用，无法删除！", SystemCode.PARAM_VALID_ERROR.getCode());
        }
        this.baseMapper.deleteById(id);
    }
}
