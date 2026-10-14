package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.solitaire.api.entity.*;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationInfoDto;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationInfoTopicDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationInfoParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationInfoVo;
import com.medusa.gruul.solitaire.mapper.manager.*;
import com.medusa.gruul.solitaire.service.manager.IInformationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * 资讯信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Service
public class InformationInfoServiceImpl extends ServiceImpl<InformationInfoMapper, InformationInfo> implements IInformationInfoService {

    @Autowired
    private InformationInfoTopicMapper informationInfoTopicMapper;

    @Autowired
    private InformationMessageMapper informationMessageMapper;

    @Autowired
    private InformationLikeMapper informationLikeMapper;

    /**
     * 获取资讯分页信息
     *
     * @param informationInfoParam 资讯信息查询参数
     * @return 资讯分页对象
     */
    @Override
    public IPage<InformationInfoVo> list(InformationInfoParam informationInfoParam) {
        IPage<InformationInfoVo> page = new Page<>(informationInfoParam.getCurrent(), informationInfoParam.getSize());
        List<InformationInfoVo> informationInfoVos = this.baseMapper.queryList(page, informationInfoParam);
        return page.setRecords(informationInfoVos);
    }

    /**
     * 获取资讯信息明细
     *
     * @param id 资讯id
     * @return 资讯信息对象
     */
    @Override
    public InformationInfoVo get(Long id) {
        return this.baseMapper.queryInfoById(id);
    }

    /**
     * 资讯信息新增/修改
     *
     * @param informationInfoDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(InformationInfoDto informationInfoDto) {
        InformationInfo informationInfo;
        if (informationInfoDto.getId() == null) {
            //新增资讯信息
            informationInfo = informationInfoDto.coverBean();
            int insert = this.baseMapper.insert(informationInfo);
            if (insert == 0) {
                throw new ServiceException("资讯信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        } else {
            //修改资讯信息
            informationInfo = this.baseMapper.selectById(informationInfoDto.getId());
            if (BeanUtil.isEmpty(informationInfo)) {
                throw new ServiceException("资讯信息不存在!", SystemCode.DATA_NOT_EXIST.getCode());
            }
            informationInfo = informationInfoDto.coverBean();
            //资讯设置修改
            int update = this.baseMapper.updateById(informationInfo);
            if (update == 0) {
                throw new ServiceException("资讯信息修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //删除资讯关联专题信息
            informationInfoTopicMapper.delete(new QueryWrapper<InformationInfoTopic>().eq("info_id", informationInfoDto.getId()));
        }
        Long id = informationInfo.getId();
        //插入资讯专题信息
        List<InformationInfoTopicDto> informationInfoTopicList = informationInfoDto.getInformationInfoTopicList();
        if (CollectionUtil.isNotEmpty(informationInfoTopicList)) {
            informationInfoTopicList.forEach(informationInfoTopicDto -> {
                InformationInfoTopic informationInfoTopic = new InformationInfoTopic();
                informationInfoTopic.setInfoId(id);
                informationInfoTopic.setTopicId(informationInfoTopicDto.getTopicId());
                int insert = informationInfoTopicMapper.insert(informationInfoTopic);
                if (insert == 0) {
                    throw new ServiceException("资讯专题关联信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                }
            });
        }
    }

    /**
     * 资讯信息排序
     *
     * @param informationInfoDtos 资讯信息列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(List<InformationInfoDto> informationInfoDtos) {
        informationInfoDtos.forEach(informationInfoDto -> {
            boolean update = new LambdaUpdateChainWrapper<>(this.baseMapper)
                    .eq(InformationInfo::getId, informationInfoDto.getId())
                    .set(InformationInfo::getSort, informationInfoDto.getSort())
                    .update();
            if (!update) {
                throw new ServiceException("排序修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 资讯信息删除
     *
     * @param ids 资讯信息ids
     */
    @Override
    public void delete(Long[] ids) {
        //删除资讯关联专题表信息
        informationInfoTopicMapper.delete(new QueryWrapper<InformationInfoTopic>().in("info_id", Arrays.asList(ids)));
        //删除资讯留言信息
        informationMessageMapper.delete(new QueryWrapper<InformationMessage>().in("info_id", Arrays.asList(ids)));
        //删除资讯点赞信息
        informationLikeMapper.delete(new QueryWrapper<InformationLike>().in("info_id", Arrays.asList(ids)));
        //删除资讯信息
        removeByIds(Arrays.asList(ids));
    }
}
