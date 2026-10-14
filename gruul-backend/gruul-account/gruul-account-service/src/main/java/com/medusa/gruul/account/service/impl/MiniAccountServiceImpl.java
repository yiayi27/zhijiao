package com.medusa.gruul.account.service.impl;

import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.*;
import com.medusa.gruul.account.api.enums.BlacklistEnum;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.account.api.model.MiniAccountExtDto;
import com.medusa.gruul.account.api.model.UserOrderDistributionVo;
import com.medusa.gruul.account.conf.SnowflakeProperty;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.mapper.MemberInfoMapper;
import com.medusa.gruul.account.mapper.MiniAccountMapper;
import com.medusa.gruul.account.model.dto.BlacklistUserDto;
import com.medusa.gruul.account.model.dto.DecodePhoneInfo;
import com.medusa.gruul.account.model.dto.UpdateUserBaseInfoDto;
import com.medusa.gruul.account.model.dto.UserListDto;
import com.medusa.gruul.account.model.param.ApiUserDistributionParam;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.*;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.datasets.api.constant.DataSetsConstant;
import com.medusa.gruul.datasets.api.constant.DataSetsQueueNameConstant;
import com.medusa.gruul.datasets.api.model.DataSetsAddMessage;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 小程序用户表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
@Slf4j
public class MiniAccountServiceImpl extends ServiceImpl<MiniAccountMapper, MiniAccount> implements IMiniAccountService {

    @Autowired
    private SnowflakeProperty snowflakeProperty;

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private IMiniAccountTagService miniAccountTagService;

    @Autowired
    private IMiniAccountTagGroupService miniAccountTagGroupService;

    @Autowired
    private IMiniAccountAddressService iMiniAccountAddressService;

    @Autowired
    private IMiniAccountRestrictService miniAccountRestrictService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private IApiMemberInfoService apiMemberInfoService;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private RemoteGoodsService remoteGoodsService;



    /**
     * 通知生成店铺首次访问记录
     *
     * @param shopId 店铺id
     */
    private void newAccountNotify(String shopId) {
        DataSetsAddMessage dataSetsAddMessage = new DataSetsAddMessage();
        dataSetsAddMessage.setShopId(shopId);
        log.info(STR."==================================数据查看\{shopId}");
        amqpTemplate.convertAndSend(DataSetsConstant.EXCHANGE_NAME, DataSetsQueueNameConstant.NEW_USER_VISIT, dataSetsAddMessage);
    }

    /**
     * 根据用户id获取用户基础数据
     *
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    @Override
    public MiniAccount getByUserId(String userId) {
        String key = AccountConstant.ACCOUNT_DB_KEY.concat(userId);
        String accountJson = RedisUtil.getCacheObject(RedisUtil.key(Keys.USER_ACCOUNT, key));
        MiniAccount account;
        if (StrUtil.isNotEmpty(accountJson)) {
            account = Jackson.readValue(accountJson, MiniAccount.class);
        } else {
            account = this.baseMapper.getMemberInfo(userId);
        }
        return account;
    }


    /**
     * 获取微信用户绑定的手机号
     *
     * @param decodePhoneInfo dto
     * @return java.lang.String
     */
    @Override
    public String decodePhoneInfo(DecodePhoneInfo decodePhoneInfo) {
        String json = WxMaCryptUtils.decrypt(decodePhoneInfo.getSessionKey(), decodePhoneInfo.getEncryptedData(), decodePhoneInfo.getIvStr());
        JSONObject jsonObject = new JSONObject(json);
        String phoneNumber = jsonObject.getStr("purePhoneNumber");
        SecureUser curUser = ISecurity.userMust();
        MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(curUser.getId());
        MiniAccount miniAccount = this.getByUserId(accountExtends.getUserId());
        if (miniAccount == null) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST_CODE);
        }
        miniAccount.setPhone(phoneNumber);
        this.updateById(miniAccount);
        return phoneNumber;
    }

    /**
     * 更新用户基础信息
     *
     * @param updateUserBaseInfoDto dto
     */
    @Override
    public void updateUserBaseInfo(UpdateUserBaseInfoDto updateUserBaseInfoDto) {
        SecureUser curUser = ISecurity.userMust();
        MiniAccount miniAccount = this.getByShopUserId(curUser.getId());
        if (miniAccount == null) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST_CODE);
        }
        BeanUtil.copyProperties(updateUserBaseInfoDto, miniAccount, CopyOptions.create().ignoreNullValue());
        miniAccount.setWhetherAuthorization(Boolean.TRUE);
        this.updateById(miniAccount);

    }


    /**
     * 获取当前用户的基础信息
     *
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    private MiniAccount getCurrUserAccount() {
        SecureUser secureUser = ISecurity.userMust();
        return new MiniAccount()
                .setAvatarUrl(secureUser.getAvatar())
                .setId(Long.valueOf(secureUser.getId()))
                .setNikeName(secureUser.getNickname())
                .setPhone(secureUser.getMobile())
                .setUserId(secureUser.getId())
                .setWhetherAuthorization(true);
    }

    /**
     * 获取用户信息
     *
     * @param infoLevel 数据级别 1-基础信息 2-用户扩展数据
     * @return com.medusa.gruul.account.model.vo.UserInfoVo
     */
    @Override
    public UserInfoVo getUserInfo(Integer infoLevel) {
        UserInfoVo vo = new UserInfoVo();
        SecureUser curUser = ISecurity.userMust();
        MiniAccountExtends miniAccountExtends = miniAccountExtendsService.findByShopUserId(curUser.getId());
        if (miniAccountExtends == null) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST_CODE);
        }
        MiniAccount miniAccount = this.getCurrUserAccount();
        BeanUtils.copyProperties(miniAccount, vo);
        vo.setShopUserId(curUser.getId());
        if (infoLevel.equals(CommonConstants.NUMBER_TWO)) {
            UserInfoExtendsVo extendsVo = new UserInfoExtendsVo();
            BeanUtils.copyProperties(miniAccountExtends, extendsVo);
            //用户扩展信息
            vo.setInfoExtends(extendsVo);
            if (StrUtil.isEmpty(extendsVo.getShopId())) {
                extendsVo.setShopId(curUser.getShopId());
            }
            extendsVo.setMemberInfo(apiMemberInfoService.getMemberInfo());
        }
        if (infoLevel.equals(CommonConstants.NUMBER_THREE)) {
            UserBlacklistAstrictVo strictVo = new UserBlacklistAstrictVo();
            List<Integer> restrictTypes = miniAccountRestrictService.getByUserId(curUser.getId());
            if (CollectionUtil.isNotEmpty(restrictTypes)) {
                for (Integer restrictType : restrictTypes) {
                    if (restrictType.equals(BlacklistEnum.REJECT_ORDER.getType())) {
                        strictVo.setRejectOrder(true);
                    }

                    if (restrictType.equals(BlacklistEnum.REJECT_COMMENT.getType())) {
                        strictVo.setRejectOrder(true);
                    }
                }

            }
            vo.setAstrictVo(strictVo);
        }

        return vo;
    }

    /**
     * 获取pc端用户列表
     *
     * @param nikeName              微信昵称
     * @param becomeMemberStartTime 成为会员开始时间 2023-11-11 11:23:23
     * @param becomeMemberEndTime   成为会员结束时间 2023-11-11 11:23:23
     * @param tagId                 标签id
     * @param orderSuccessStartTime 成交时间 2023-11-11 11:23:23
     * @param orderSuccessEndTime   成交时间 2023-11-11 11:23:23
     * @param rankCode              会员卡id
     * @param memberNumber          会员等级代码
     * @param page                  页数
     * @param size                  条数
     * @param sortType             排序类型 1-升序 2-降序
     * @return com.medusa.gruul.account.model.vo.UserListVo
     */
    @Override
    public PageUtils<List<UserListVo>> userList(String nikeName,
                                                String becomeMemberStartTime, String becomeMemberEndTime,
                                                Long tagId,
                                                String orderSuccessStartTime, String orderSuccessEndTime,
                                                String rankCode, String memberNumber, Integer page, Integer size, Integer sortType) {

        Map<String, Object> paramMap = new HashMap<>(CommonConstants.NUMBER_TEN);
        if (StrUtil.isNotEmpty(becomeMemberStartTime)) {
            paramMap.put("becomeMemberStartTime", becomeMemberStartTime);
        }
        if (StrUtil.isNotEmpty(becomeMemberEndTime)) {
            paramMap.put("becomeMemberEndTime", becomeMemberEndTime);
        }
        if (tagId != null) {
            paramMap.put("tagId", tagId);
        }
        if (StrUtil.isNotEmpty(orderSuccessStartTime)) {
            paramMap.put("orderSuccessStartTime", orderSuccessStartTime);
        }
        if (StrUtil.isNotEmpty(orderSuccessEndTime)) {
            paramMap.put("orderSuccessEndTime", orderSuccessEndTime);
        }
        if (StrUtil.isNotEmpty(rankCode)) {
            paramMap.put("rankCode", rankCode);
        }
        if (StrUtil.isNotEmpty(memberNumber)) {
            paramMap.put("memberNumber", memberNumber);
        }
        if (StrUtil.isNotBlank(nikeName)) {
            paramMap.put("nikeName", nikeName);
        }
        if (sortType != null) {
            paramMap.put("sortType", sortType);
        }
        String shopId = ISecurity.userMust().getShopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("店铺id不存在");
        }
        paramMap.put("shopId", shopId);
        IPage<UserListDto> iPage = this.baseMapper.selectMemberInfo(new Page<>(page, size), paramMap);
        List<UserListDto> records = iPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageUtils<>(new ArrayList<>(0), (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
        }
        List<UserListVo> vos = new LinkedList<>();
        List<String> userIdList = records.stream().map(UserListDto::getShopUserId).toList();
        //查询用户标签
        Map<String, List<UserTagVo>> userTag = MapUtil.newHashMap(userIdList.size());
        List<MiniAccountTagGroup> tagGroups = miniAccountTagGroupService.getByUserListId(userIdList);
        for (UserListDto userListDto : records) {
            String firstUserId = userListDto.getFirstUserId();
            if (firstUserId != null) {
                FirstDistributionVo firstDistributionVo = baseMapper.selectFirstNickNameByFirstUserId(firstUserId);
                userListDto.setFirstPhone(firstDistributionVo.getFirstPhone());
                userListDto.setFirstNickName(firstDistributionVo.getFirstNickName());
            }
        }
        if (CollectionUtil.isNotEmpty(tagGroups)) {
            setAccountGroupTag(userTag, tagGroups);
        }
        //封装用户数据
        setPcAccountListVos(shopId, records, vos, userTag);
        return new PageUtils(vos, (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
    }

    /**
     * @param shopId  店铺id
     * @param records 用户数据
     * @param vos     标签数据
     * @param userTag 用户标签
     */
    private void setPcAccountListVos(String shopId, List<UserListDto> records, List<UserListVo> vos, Map<String, List<UserTagVo>> userTag) {
        for (UserListDto record : records) {
            UserListVo vo = new UserListVo();
            BeanUtils.copyProperties(record, vo);
            //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
            RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, CommonConstants.NUMBER_ONE).eq(MemberInfo::getUserId, record.getShopUserId()));
            //是会员
            if (CollectionUtil.isNotEmpty(memberInfoList)) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    vo.setSupplyBonus(record.getSupplyBonus().add(record.getRebateBonus()));
                } else {
                    vo.setSupplyBonus(record.getSupplyBonus());
                }
            } else {//不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    vo.setSupplyBonus(record.getSupplyBonus().add(record.getRebateBonus()));
                } else {
                    vo.setSupplyBonus(record.getSupplyBonus());
                }
            }
            vo.setUserId(record.getShopUserId());
            List<UserTagVo> userTagVos = userTag.get(record.getShopUserId());
            vo.setUserTagVos(userTagVos);
            //获取会员信息
            List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(record.getShopUserId());
            //是会员
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                vo.setState(1);
                MemberInfo memberInfo = memberInfos.get(CommonConstants.NUMBER_ZERO);
                vo.setMemberNumber(memberInfo.getMemberNumber());
                vo.setMemberRegisterTime(memberInfo.getMemberRegisterTime());
                //计算用户成为会员天数
                if (vo.getMemberRegisterTime() != null) {
                    vo.setBecomeMemberDayNumber(DateUtil.between(LocalDateTimeUtils.convertLdtToDate(vo.getMemberRegisterTime()), DateUtil.date(), DateUnit.DAY));
                }
            } else {
                vo.setState(CommonConstants.NUMBER_TWO);
            }

            vos.add(vo);
        }
    }

    /**
     * 设置用户标签
     *
     * @param userTag   标签
     * @param tagGroups 用户相关标签
     */
    private void setAccountGroupTag(Map<String, List<UserTagVo>> userTag, List<MiniAccountTagGroup> tagGroups) {
        List<Long> tagIdList = tagGroups.stream().map(MiniAccountTagGroup::getTagId).distinct().toList();
        List<MiniAccountTag> miniAccountTags = miniAccountTagService.getByIdList(tagIdList);
        Map<Long, MiniAccountTag> tagMap = miniAccountTags.stream().collect(Collectors.toMap(MiniAccountTag::getId, v -> v));
        tagGroups.stream().collect(Collectors.groupingBy(MiniAccountTagGroup::getUserId)).forEach((k, v) -> {
            List<UserTagVo> userTagVos = new ArrayList<>(v.size());
            for (MiniAccountTagGroup miniAccountTagGroup : v) {
                MiniAccountTag miniAccountTag = tagMap.get(miniAccountTagGroup.getTagId());
                if (miniAccountTag != null) {
                    UserTagVo userTagVo = new UserTagVo();
                    userTagVo.setTagId(miniAccountTag.getId());
                    userTagVo.setTagName(miniAccountTag.getTagName());
                    userTagVos.add(userTagVo);
                }
            }
            userTag.put(k, userTagVos);
        });
    }

    /**
     * 获取用户信息接口
     *
     * @param shopUserId 用户id
     * @param infos  [1,2,3]  1,基本信息,2,扩展信息,3-地址信息,4-授权信息
     * @return com.medusa.gruul.account.api.model.AccountInfoDto
     */
    @Override
    public AccountInfoDto accountInfo(String shopUserId, List<Integer> infos) {
        AccountInfoDto accountInfoDto = new AccountInfoDto();

        for (Integer obj : infos) {
            switch (obj) {
                case 1:
                    MiniAccount miniAccount = this.getByShopUserId(shopUserId);
                    accountInfoDto.setMiniAccountunt(miniAccount);
                    break;
                case 2:
                    MiniAccountExtends miniAccountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
                    accountInfoDto.setMiniAccountExtends(miniAccountExtends);
                    List<Integer> restrictTypes = miniAccountRestrictService.getByUserId(shopUserId);
                    accountInfoDto.setRestrictTypes(restrictTypes);
                    break;
                case 3:
                    List<MiniAccountAddress> miniAccountAddresses = iMiniAccountAddressService.getUserAddress(ISystem.shopId(), shopUserId);
                    if (CollectionUtil.isNotEmpty(miniAccountAddresses)) {
                        // 关键点：将集合转为可修改的新集合（如 ArrayList）
                        List<MiniAccountAddress> mutableList = new ArrayList<>(miniAccountAddresses);
                        //默认地址,排序在首位
                        CollectionUtil.sort(mutableList, Comparator.comparing(MiniAccountAddress::getIsDefault).reversed());
                        miniAccountAddresses = mutableList;
                    }
                    accountInfoDto.setMiniAccountAddress(miniAccountAddresses);
                    break;
                default:
            }
        }
        return accountInfoDto;
    }

    /**
     * 分页查询黑名单用户
     *
     * @param page
     * @param size
     * @param permission 权限 0-全部 1-下单 2-评论
     * @param fuzzy      手机号或微信昵称模糊搜索
     * @return com.medusa.gruul.account.model.vo.BlacklistUserVo
     */
    @Override
    public PageUtils<List<BlacklistUserVo>> blacklist(Integer page, Integer size, Integer permission, String fuzzy) {
        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("permission", permission);
        if (StrUtil.isNotEmpty(fuzzy)) {
            paramMap.put("fuzzy", fuzzy);
        }
        String shopId = ISecurity.userMust().getShopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("店铺id不存在");
        }
        paramMap.put("shopId", shopId);
        IPage<BlacklistUserDto> userDtoIpage = this.baseMapper.selectByBlackListUser(new Page<BlacklistUserDto>(page, size), paramMap);
        List<BlacklistUserDto> records = userDtoIpage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageUtils<>(new ArrayList<>(0), (int) userDtoIpage.getTotal(), (int) userDtoIpage.getSize(), (int) userDtoIpage.getCurrent());
        }
        List<BlacklistUserVo> vos = new ArrayList<>(records.size());

        List<String> userIds = records.stream().map(BlacklistUserDto::getShopUserId).toList();
        Map<String, List<MiniAccountRestrict>> accountRestrictMap = miniAccountRestrictService.getByUserIds(userIds).stream().collect(Collectors.groupingBy(MiniAccountRestrict::getUserId));

        for (BlacklistUserDto record : records) {
            BlacklistUserVo vo = new BlacklistUserVo();
            BeanUtils.copyProperties(record, vo);
            vo.setUserId(record.getShopUserId());
            List<Integer> blacklistTypeList = accountRestrictMap.get(vo.getUserId()).stream().map(MiniAccountRestrict::getRestrictType).toList();
            vo.setBlacklistType(blacklistTypeList);
            vos.add(vo);
        }
        return new PageUtils(vos, (int) userDtoIpage.getTotal(), (int) userDtoIpage.getSize(), (int) userDtoIpage.getCurrent());
    }

    /**
     * 切换用户当前所在店铺
     *
     * @param shopId 店铺id
     * @return java.lang.String
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String switchShops(String shopId) {
        SecureUser curUser = ISecurity.userMust();
        if (curUser == null) {
            throw new ServiceException("token错误", SystemCode.DATA_NOT_EXIST_CODE);
        }
        MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(curUser.getId());
        if (accountExtends == null) {
            throw new ServiceException("错误账号", SystemCode.DATA_NOT_EXIST_CODE);
        }
        //修改该用户的所有扩展店铺数据未非当前默认账号数据
        MiniAccountExtends up = new MiniAccountExtends();
        up.setCurrentStatus(CommonConstants.NUMBER_ZERO);
        miniAccountExtendsService.update(up, new LambdaQueryWrapper<MiniAccountExtends>().eq(MiniAccountExtends::getUserId, accountExtends.getUserId()));
        //修改店铺id
        //判断是否已存在该店铺账号扩展数据
        MiniAccountExtends miniAccountExtends = miniAccountExtendsService.getOne(new LambdaQueryWrapper<MiniAccountExtends>().eq(MiniAccountExtends::getUserId, accountExtends.getUserId()).eq(MiniAccountExtends::getShopId, shopId));
        if (miniAccountExtends == null) {
            miniAccountExtends = new MiniAccountExtends();
            //用户信息扩展
            miniAccountExtends.setUserId(accountExtends.getUserId());
            miniAccountExtends.setIsBlacklist(0);
            miniAccountExtends.setConsumeNum(0);
            miniAccountExtends.setCurrentStatus(CommonConstants.NUMBER_ONE);
            miniAccountExtends.setConsumeTotleMoney(BigDecimal.valueOf(0.0));
            String shopUserId = IdUtil.getSnowflake(snowflakeProperty.getWorkerId(), snowflakeProperty.getDatacenterId()) + "";
            miniAccountExtends.setShopUserId(shopUserId);
            miniAccountExtends.setShopId(shopId);
            LocalDateTime currentDateTime = LocalDateTimeUtils.convertDateToLdt(new Date());
            miniAccountExtends.setLastLoginTime(currentDateTime);
            miniAccountExtendsService.save(miniAccountExtends);
            newAccountNotify(shopId);
        } else {
            miniAccountExtends.setCurrentStatus(CommonConstants.NUMBER_ONE);
            miniAccountExtends.setShopId(shopId);
        }
        miniAccountExtendsService.updateById(miniAccountExtends);
        //重新缓存店铺用户数据
        return AccountConstant.ACCOUNT_KEY.concat(null);
    }

    /**
     * 批量获取指定用户id(用户店铺id)的用户基本信息
     *
     * @param shopUserIds 用户id数组
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    @Override
    public List<MiniAccountExtDto> accountsInfoList(List<String> shopUserIds) {
        return this.baseMapper.selectByShopUserIds(shopUserIds);
    }

    /**
     * 获取当前用户二维码
     *
     * @return base64二维码
     */
    @Override
    public String qrCode() {
        SecureUser httpCurUser = ISecurity.userMust();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("shopUserId", httpCurUser.getId());
        QrConfig qrConfig = QrConfig.create();
        byte[] bytes = QrCodeUtil.generatePng(JSONUtil.toJsonStr(jsonObject), qrConfig);
        return Base64.encode(bytes);
    }

    /**
     * 获取当前用户id
     *
     * @return 用户id
     */
    @Override
    public String getUserId() {
        return ISecurity.userMust().getId();
    }

    /**
     * 绑定当前用户的分销信息
     *
     * @param userId 分销员id
     * @return base64二维码
     */
    @Override
    public String setUserDistributionInfo(String userId) {
        SecureUser httpCurUser = ISecurity.userMust();
        String currentUserId = httpCurUser.getId();
        MiniAccount miniAccount = this.baseMapper.selectByShopUserId(currentUserId);
        String distributionName;
        if (BeanUtil.isEmpty(miniAccount)) {
            throw new ServiceException("当前用户不存在!", SystemCode.DATA_NOT_EXIST.getCode());
        }
        if (StrUtil.isBlank(miniAccount.getFirstUserId())) {
            //查询需绑定的分销员是否有上级
            MiniAccount distributionMiniAccount = this.baseMapper.selectByShopUserId(userId);
            distributionName = distributionMiniAccount.getNikeName();
            if (BeanUtil.isEmpty(distributionMiniAccount)) {
                throw new ServiceException("绑定的分销员不存在!", SystemCode.DATA_NOT_EXIST.getCode());
            }
            //设置传递的用户为分销员
            miniAccount.setFirstUserId(userId);
            miniAccount.setUpdateTime(LocalDateTime.now());
            int update = this.baseMapper.updateMulitClientAccountById(miniAccount);
            if (update == 0) {
                throw new ServiceException("绑定失败!", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        } else {
            throw new ServiceException("用户已绑定其他分销员!", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        return distributionName;
    }

    /**
     * 获取分销中心首页信息
     *
     * @return 用户分销信息
     */
    @Override
    public UserDistributionVo getUserDistributionIndex() {
        SecureUser<Object> httpCurUser = ISecurity.userMust();
        String userId = httpCurUser.getId();
        String shopId = httpCurUser.getShopId();
        return this.baseMapper.selectUserDistributionIndex(userId,shopId);
    }

    /**
     * 获取分销中心首页信息
     *
     * @param apiUserDistributionParam 分销中心查询参数
     * @return 用户分销信息
     */
    @Override
    public IPage<UserDistributionDetailVo> getUserDistributionList(ApiUserDistributionParam apiUserDistributionParam) {
        IPage<UserDistributionDetailVo> page = new Page<>(apiUserDistributionParam.getCurrent(), apiUserDistributionParam.getSize());
        SecureUser httpCurUser = ISecurity.userMust();
        String userId = httpCurUser.getId();
        apiUserDistributionParam.setShopUserId(userId);
        List<UserDistributionDetailVo> productVos = this.baseMapper.selectUserDistributionList(page, apiUserDistributionParam);
        return page.setRecords(productVos);
    }

    /**
     * 根据店铺用户id查询用户所在店铺唯一id
     *
     * @param shopUserId 店铺用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    @Override
    public MiniAccount getByShopUserId(String shopUserId) {
        return this.getBaseMapper().selectByShopUserId(shopUserId);
    }

    /**
     * 获取用户对应的分销员信息
     *
     * @param shopId   店铺id
     * @param shopUserId 店铺用户id
     * @return UserOrderDistributionVo
     */
    @Override
    public UserOrderDistributionVo getDistributionInfo(String shopId, String shopUserId) {
        UserOrderDistributionVo userOrderDistributionVo = new UserOrderDistributionVo();
        MiniAccount miniAccount = this.baseMapper.selectByShopUserId(shopUserId);
        if (StrUtil.isNotBlank(miniAccount.getFirstUserId())) {
            MiniAccount firstMiniAccount = this.baseMapper.selectByShopUserId(miniAccount.getFirstUserId());
            userOrderDistributionVo.setFirstNikeName(firstMiniAccount.getNikeName());
            userOrderDistributionVo.setFirstPhone(firstMiniAccount.getPhone());
        }
        return userOrderDistributionVo;
    }
}



