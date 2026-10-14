package com.medusa.gruul.solitaire.service.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.solitaire.api.entity.*;
import com.medusa.gruul.solitaire.api.model.dto.api.ApiInformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationInfoParam;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationInfoVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationMessageVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationSetVo;
import com.medusa.gruul.solitaire.mapper.api.*;
import com.medusa.gruul.solitaire.service.api.IApiInformationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * 小程序资讯信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Service
public class ApiInformationInfoServiceImpl extends ServiceImpl<ApiInformationInfoMapper, InformationInfo> implements IApiInformationInfoService {

    @Autowired
    private ApiInformationSetMapper apiInformationSetMapper;

    @Autowired
    private ApiInformationTopicMapper apiInformationTopicMapper;

    @Autowired
    private ApiInformationMessageMapper apiInformationMessageMapper;

    @Autowired
    private ApiInformationLikeMapper apiInformationLikeMapper;

    @Autowired
    private ApiInformationMessageLikeMapper apiInformationMessageLikeMapper;

    /**
     * 获取资讯设置信息
     *
     * @return ApiInformationSetVo
     */
    @Override
    public ApiInformationSetVo getInformationSet() {
        InformationSet informationSet = apiInformationSetMapper.selectOne(new LambdaQueryWrapper<>());
        if (BeanUtil.isEmpty(informationSet)) {
            throw new ServiceException("暂无资讯设置信息!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        ApiInformationSetVo apiInformationSetVo = new ApiInformationSetVo();
        BeanUtil.copyProperties(informationSet, apiInformationSetVo);
        return apiInformationSetVo;
    }

    /**
     * 查询资讯专题信息
     *
     * @return List<ApiInformationTopicVo>
     */
    @Override
    public List<InformationTopic> topicList() {
        return apiInformationTopicMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 获取资讯分页信息
     *
     * @param apiInformationInfoParam
     * @return 资讯分页对象
     */
    @Override
    public PageUtils<ApiInformationInfoVo> list(ApiInformationInfoParam apiInformationInfoParam) {
        IPage<ApiInformationInfoVo> page = new Page<>(apiInformationInfoParam.getCurrent(), apiInformationInfoParam.getSize());
        List<ApiInformationInfoVo> apiInformationInfoVos = this.baseMapper.queryList(page, apiInformationInfoParam);
        String shopId = ISecurity.userMust().getShopId();
        //遍历给资讯阅读量赋值
        apiInformationInfoVos.forEach(apiInformationInfoVo -> {
            String readKey = shopId + apiInformationInfoVo.getId();
            String count = RedisUtil.getCacheObject(RedisUtil.key(Keys.INFORMATION_READ, readKey));
            if (StrUtil.isNotEmpty(count)) {
                apiInformationInfoVo.setReadCount(count);
            } else {
                apiInformationInfoVo.setReadCount(String.valueOf(CommonConstants.NUMBER_ZERO));
            }
        });
        PageUtils<ApiInformationInfoVo> result = new PageUtils<>(null, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
        result.setList(apiInformationInfoVos);
        return result;
    }

    /**
     * 获取资讯信息明细
     *
     * @param id 资讯id
     * @return 资讯信息对象
     */
    @Override
    public ApiInformationInfoVo get(Long id) {
        String shopId = ISecurity.userMust().getShopId();
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        //资讯阅读缓存key值 用户id+店铺id+资讯id
        String readKey = shopId + id;
        String count = RedisUtil.getCacheObject(RedisUtil.key(Keys.INFORMATION_READ, readKey));
        //查询key值是否存在  存在+1 不存在设置为1
        if (StrUtil.isNotEmpty(count)) {
            count = String.valueOf(Integer.valueOf(count));
        } else {
            count = String.valueOf(CommonConstants.NUMBER_ONE);
        }
        ApiInformationInfoVo apiInformationInfoVo = this.baseMapper.queryInfoById(id);
        //判断资讯是否点赞过
        if (BeanUtil.isNotEmpty(apiInformationInfoVo)) {
            //资讯阅读数量赋值
            apiInformationInfoVo.setReadCount(count);
            Long informationUserLikeCount = apiInformationLikeMapper.selectCount(new LambdaQueryWrapper<InformationLike>().eq(InformationLike::getInfoId, id).eq(InformationLike::getUserId, userId));
            if (informationUserLikeCount > CommonConstants.NUMBER_ZERO) {
                apiInformationInfoVo.setInfoHasLike(true);
            }
            ApiInformationMessageParam apiInformationMessageParam = new ApiInformationMessageParam();
            apiInformationMessageParam.setInfoId(id);
            apiInformationMessageParam.setUserId(userId);
            ApiInformationMessageVo apiInformationMessageVo = apiInformationMessageMapper.queryMessage(apiInformationMessageParam);
            //自己的资讯留言是否点赞赋值
            if (BeanUtil.isNotEmpty(apiInformationMessageVo)) {
                Long messageUserLikeCount = apiInformationMessageLikeMapper.selectCount(new LambdaQueryWrapper<InformationMessageLike>().eq(InformationMessageLike::getMessageId, apiInformationMessageVo.getId()).eq(InformationMessageLike::getUserId, userId));
                if (messageUserLikeCount > CommonConstants.NUMBER_ZERO) {
                    apiInformationMessageVo.setMessageHasLike(true);
                }
                apiInformationInfoVo.setInformationMessageVo(apiInformationMessageVo);
            }
        }
        return apiInformationInfoVo;
    }

    /**
     * 资讯阅读量增加
     *
     * @param id 资讯id
     */
    @Override
    public void setRead(Long id) {
        String shopId = ISecurity.userMust().getShopId();
        
        //资讯阅读缓存key值 用户id+店铺id+资讯id
        String readKey = shopId + id;
        String count = RedisUtil.getCacheObject(RedisUtil.key(Keys.INFORMATION_READ, readKey));
        //查询key值是否存在  存在+1 不存在设置为1
        if (StrUtil.isNotEmpty(count)) {
            count = String.valueOf(Integer.valueOf(count) + CommonConstants.NUMBER_ONE);
        } else {
            count = String.valueOf(CommonConstants.NUMBER_ONE);
        }
        RedisUtil.setCacheObject(RedisUtil.key(Keys.INFORMATION_READ, readKey), count);
    }

    /**
     * 获取资讯留言分页信息
     *
     * @param apiInformationMessageParam 咨询留言查询参数
     * @return 资讯留言分页对象
     */
    @Override
    public PageUtils<ApiInformationMessageVo> getMessageList(ApiInformationMessageParam apiInformationMessageParam) {
        IPage<ApiInformationMessageVo> page = new Page<>(apiInformationMessageParam.getCurrent(), apiInformationMessageParam.getSize());
        List<ApiInformationMessageVo> apiInformationMessageVos = apiInformationMessageMapper.queryMessageList(page, apiInformationMessageParam);
        //给每条留言赋值是否点赞过
        if (CollectionUtil.isNotEmpty(apiInformationMessageVos)) {
            SecureUser secureUser = ISecurity.userMust();
            String userId = secureUser.getId();
            apiInformationMessageVos.forEach(apiInformationMessageVo -> {
                Long messageUserLikeCount = apiInformationMessageLikeMapper.selectCount(new LambdaQueryWrapper<InformationMessageLike>().eq(InformationMessageLike::getMessageId, apiInformationMessageVo.getId()).eq(InformationMessageLike::getUserId, userId));
                if (messageUserLikeCount > CommonConstants.NUMBER_ZERO) {
                    apiInformationMessageVo.setMessageHasLike(true);
                }
            });
        }
        PageUtils<ApiInformationMessageVo> result = new PageUtils<>(null, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
        result.setList(apiInformationMessageVos);
        return result;
    }

    /**
     * 资讯留言
     *
     * @param apiInformationMessageDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leave(ApiInformationMessageDto apiInformationMessageDto) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        InformationMessage informationMessage = apiInformationMessageMapper.selectOne(new LambdaQueryWrapper<InformationMessage>().eq(InformationMessage::getInfoId, apiInformationMessageDto.getInfoId()).eq(InformationMessage::getUserId, userId));
        if (BeanUtil.isNotEmpty(informationMessage)) {
            throw new ServiceException("您已评论!", SystemCode.DATA_EXISTED.getCode());
        }
        informationMessage = new InformationMessage();
        //新增资讯留言信息
        informationMessage.setUserContent(apiInformationMessageDto.getUserContent());
        informationMessage.setUserId(userId);
        informationMessage.setNickName(secureUser.getNickname());
        informationMessage.setAvatarUrl(secureUser.getAvatar());
        informationMessage.setUserSendTime(new Date());
        informationMessage.setInfoId(apiInformationMessageDto.getInfoId());
        int insert = apiInformationMessageMapper.insert(informationMessage);
        if (insert == 0) {
            throw new ServiceException("评论失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 资讯信息点赞
     *
     * @param type   操作类型 0-点赞 1-取消点赞
     * @param infoId 资讯id
     */
    @Override
    public void like(Integer type, Long infoId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        if (CommonConstants.NUMBER_ZERO.equals(type)) {
            InformationLike informationLike = apiInformationLikeMapper.selectOne(new LambdaQueryWrapper<InformationLike>().eq(InformationLike::getInfoId, infoId).eq(InformationLike::getUserId, userId));
            //查询是否存在点赞信息 不存在插入数据
            if (BeanUtil.isEmpty(informationLike)) {
                informationLike = new InformationLike();
                informationLike.setInfoId(infoId);
                informationLike.setUserId(userId);
                int insert = apiInformationLikeMapper.insert(informationLike);
                if (insert == 0) {
                    throw new ServiceException("点赞失败!", SystemCode.DATA_ADD_FAILED.getCode());
                }
            } else {
                throw new ServiceException("您已点赞过!", SystemCode.DATA_EXISTED.getCode());
            }
        } else {
            apiInformationLikeMapper.delete(new LambdaQueryWrapper<InformationLike>().eq(InformationLike::getUserId, userId).eq(InformationLike::getInfoId, infoId));
        }
    }

    /**
     * 资讯留言点赞
     *
     * @param type      操作类型 0-点赞 1-取消点赞
     * @param messageId 资讯留言id
     */
    @Override
    public void messageLike(Integer type, Long messageId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        if (CommonConstants.NUMBER_ZERO.equals(type)) {
            InformationMessageLike informationMessageLike = apiInformationMessageLikeMapper.selectOne(new LambdaQueryWrapper<InformationMessageLike>().eq(InformationMessageLike::getMessageId, messageId).eq(InformationMessageLike::getUserId, userId));
            //查询是否存在点赞信息 存在说明是取消点赞 不存在说明是
            if (BeanUtil.isEmpty(informationMessageLike)) {
                informationMessageLike = new InformationMessageLike();
                informationMessageLike.setMessageId(messageId);
                informationMessageLike.setUserId(userId);
                int insert = apiInformationMessageLikeMapper.insert(informationMessageLike);
                if (insert == 0) {
                    throw new ServiceException("点赞失败!", SystemCode.DATA_ADD_FAILED.getCode());
                }
            } else {
                throw new ServiceException("您已点赞过!", SystemCode.DATA_EXISTED.getCode());
            }
        } else {
            apiInformationMessageLikeMapper.delete(new LambdaQueryWrapper<InformationMessageLike>().eq(InformationMessageLike::getUserId, userId).eq(InformationMessageLike::getMessageId, messageId));
        }
    }

    /**
     * 资讯留言删除
     *
     * @param id 资讯留言id
     */
    @Override
    public void delete(Long id) {
        InformationMessage informationMessage = apiInformationMessageMapper.selectById(id);
        if (BeanUtil.isEmpty(informationMessage)) {
            throw new ServiceException("评论不存在!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        if (userId.equals(informationMessage.getUserId())) {
            int delete = apiInformationMessageMapper.deleteById(id);
            if (delete == 0) {
                throw new ServiceException("评论删除失败!", SystemCode.DATA_DELETE_FAILED.getCode());
            }
        } else {
            throw new ServiceException("该评论不允许删除!", SystemCode.DATA_DELETE_FAILED.getCode());
        }
    }

}
