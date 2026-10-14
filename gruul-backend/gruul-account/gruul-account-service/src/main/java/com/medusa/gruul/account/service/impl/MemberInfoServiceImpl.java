package com.medusa.gruul.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.*;
import com.medusa.gruul.account.api.model.*;
import com.medusa.gruul.account.mapper.*;
import com.medusa.gruul.account.model.dto.MemberListDto;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpgradeMemberRankDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.*;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 会员信息实现类
 * @Author: zhaokw29701
 * @Date: 2024/5/1 22:31
 **/
@Service
@Slf4j
public class MemberInfoServiceImpl extends ServiceImpl<MemberInfoMapper, MemberInfo> implements IMemberInfoService {

    @Autowired
    private IMiniAccountTagService miniAccountTagService;

    @Autowired
    private IMiniAccountTagGroupService miniAccountTagGroupService;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private MemberCardInfoMapper memberCardInfoMapper;

    @Autowired
    private IMemberCardInfoService memberCardInfoService;

    @Autowired
    private MemberCardPrivilegeMapper memberCardPrivilegeMapper;

    @Autowired
    private MemberCardPrivilegeGroupMapper memberCardPrivilegeGroupMapper;

    @Autowired
    private MiniAccountMapper miniAccountMapper;

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private IMemberSwitchService memberSwitchService;

    @Autowired
    private RemoteGoodsService remoteGoodsService;

    /**
     * 生成会员卡编号，格式：店铺id+yyMMdd+7位随机数
     *
     * @param range
     * @return
     */
    @SneakyThrows
    public static String generateRandomNo(String shopId, int range) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String format = sdf.format(new Date());
        return shopId + format + RandomUtil.randomNumbers(range);
    }

    /**
     * 获取pc端会员列表
     *
     * @param nikeName              微信昵称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime   成为会员结束时间
     * @param tagId                 标签id
     * @param orderSuccessStartTime 成交时间
     * @param orderSuccessEndTime   成交时间
     * @param memberCardId          会员卡id
     * @param memberNumber          会员编号
     * @param rankCode              会员等级
     * @param page                  页数
     * @param size                  条数
     * @param sortType
     * @return com.medusa.gruul.member.web.model.vo
     */
    @Override
    public PageUtils<List<MemberListVo>> memberList(String nikeName, String becomeMemberStartTime, String becomeMemberEndTime, Long tagId, String orderSuccessStartTime, String orderSuccessEndTime, String rankCode, Long memberCardId, String memberNumber, Integer page, Integer size, Integer sortType) {
        Map<String, Object> paramMap = new HashMap<>(CommonConstants.NUMBER_ELEVEN);
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
        if (memberCardId != null) {
            paramMap.put("memberCardId", memberCardId);
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
        if (StrUtil.isNotEmpty(rankCode)) {
            paramMap.put("rankCode", rankCode);
        }
        String shopId = ISecurity.userMust().getShopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("店铺id不存在");
        }
        paramMap.put("shopId", shopId);
        IPage<MemberListDto> iPage = this.baseMapper.selectByMemberList(new Page<>(page, size), paramMap);
        List<MemberListDto> records = iPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageUtils<>(new ArrayList<>(0), (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
        }
        List<String> memberIdList = records.stream().map(MemberListDto::getShopUserId).toList();
        //查询会员用户标签
        Map<String, List<UserTagVo>> userTag = MapUtil.newHashMap(memberIdList.size());
        List<MiniAccountTagGroup> tagGroups = miniAccountTagGroupService.getByUserListId(memberIdList);
        if (CollectionUtil.isNotEmpty(tagGroups)) {
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
        //封装会员列表数据
        List<MemberListVo> vos = getMemberListVos(sortType, shopId, records, userTag);

        return new PageUtils(vos, (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
    }

    /**
     * 封装会员列表数据
     *
     * @param sortType 排序值
     * @param shopId   店铺id
     * @param records  会员记录
     * @param userTag  用户标签
     * @return com.medusa.gruul.account.model.vo.MemberListVo
     */
    private List<MemberListVo> getMemberListVos(Integer sortType, String shopId, List<MemberListDto> records, Map<String, List<UserTagVo>> userTag) {
        List<MemberListVo> vos = new ArrayList<>(records.size());
        for (MemberListDto record : records) {
            MemberListVo vo = new MemberListVo();
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
            } else {
                //不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    vo.setSupplyBonus(record.getSupplyBonus().add(record.getRebateBonus()));
                } else {
                    vo.setSupplyBonus(record.getSupplyBonus());
                }
            }
            vo.setUserId(record.getShopUserId());
            List<UserTagVo> userTagVos = userTag.get(record.getShopUserId());
            vo.setUserTagVos(userTagVos);
            List<MemberInfo> memberInfos = memberInfoMapper
                    .getMemberInfoByUserId(record.getShopUserId());
            List<MemberInnerListVo> memberInnerListVos = new ArrayList<>();
            vo.setMemberRegisterTime(memberInfos.get(0).getMemberRegisterTime());
            for (MemberInfo memberInfo : memberInfos) {
                MemberInnerListVo memberInnerListVo = new MemberInnerListVo();
                MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
                //计算用户成为会员天数
                long becomeMemberDayNumber = 0L;
                if (memberInfo.getMemberRegisterTime() != null) {
                    becomeMemberDayNumber = DateUtil.between(LocalDateTimeUtils.convertLdtToDate(memberInfo.getMemberRegisterTime()), DateUtil.date(), DateUnit.DAY);
                }
                memberInnerListVo.setTitle(memberCardInfo.getTitle())
                        .setOpenType(memberCardInfo.getOpenType())
                        .setRankName(memberCardInfo.getRankName())
                        .setRankCode(memberCardInfo.getRankCode())
                        .setMemberRegisterTime(memberInfo.getMemberRegisterTime())
                        .setMemberNumber(memberInfo.getMemberNumber())
                        .setMemberExpireTime(memberInfo.getMemberExpireTime())
                        .setBecomeMemberDayNumber(becomeMemberDayNumber)
                        .setId(memberInfo.getId())
                        .setRegisterData(memberInfo.getRegisterData());
                memberInnerListVos.add(memberInnerListVo);
            }

            vo.setMemberInnerListVos(memberInnerListVos);
            vos.add(vo);
        }
        //按照成为会员时间降序
        if (sortType != null && sortType.equals(CommonConstants.NUMBER_THREE)) {
            vos = vos.stream().sorted(Comparator.comparing(MemberListVo::getMemberRegisterTime).reversed()).toList();
        }
        //按照成为会员时间升序
        if (sortType != null && sortType.equals(CommonConstants.NUMBER_FOUR)) {
            vos = vos.stream().sorted(Comparator.comparing(MemberListVo::getMemberRegisterTime)).toList();
        }
        return vos;
    }

    /**
     * 后台将用户设为会员
     *
     * @param setMemberDto com.medusa.gruul.account.model.dto.SetMemberDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMember(SetMemberDto setMemberDto) {
        //获取会员开卡方式
        MemberCardBaseInfo memberBaseInfo = memberCardInfoService.getByMemberBaseInfo(ISecurity.userMust().getShopId());
        Integer currentOpenType = memberBaseInfo.getCurrentOpenType();
        MemberCardInfo memberCardInfo = memberCardInfoMapper.selectMemCardInfo(setMemberDto, currentOpenType);
        String userId = setMemberDto.getUserId();
        //取时间
        Date memberRegisterTime = new Date();
        Calendar calendar = Calendar.getInstance();
        //需要将date数据转移到Calender对象中操作
        calendar.setTime(memberRegisterTime);
        //把日期往后增加n天.正数往后推,负数往前移动
        calendar.add(Calendar.DATE, memberCardInfo.getUseInDate().intValue());
        //这个时间就是日期往后推或往前移n天的结果
        Date memberExpireTime = calendar.getTime();
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setMemberCardInfoId(memberCardInfo.getId())
                .setMemberExpireTime(memberExpireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .setMemberNumber(generateRandomNo(ISecurity.userMust().getShopId(), 7))
                .setMemberRegisterTime(memberRegisterTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .setMemberRegisterType(2)
                .setMemberQrcode(memberCardInfo.getBackGroundPicUrl())
                .setState(1)
                .setUserId(userId);
        List<MemberInfo> memberInfos = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getMemberCardInfoId, memberInfo.getMemberCardInfoId()).eq(MemberInfo::getUserId, memberInfo.getUserId()));
        if (CollectionUtil.isNotEmpty(memberInfos)) {
            throw new ServiceException("后台生成会员信息失败，已开通该类型会员请勿重复开通！", SystemCode.DATA_ADD_FAILED.getCode());
        } else {
            int insert = memberInfoMapper.insert(memberInfo);
            if (insert < 1) {
                throw new ServiceException("后台生成会员信息失败", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }

    /**
     * 后台获取会员卡等级
     *
     * @return com.medusa.gruul.account.model.vo.MemberCardRankListVo
     */
    @Override
    public List<MemberCardRankListVo> getMemberCardRank() {
        String shopId = ISecurity.userMust().getShopId();
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardRank(shopId);
        List<MemberCardRankListVo> memberCardRankListVos = new ArrayList<>();
        for (MemberCardInfo memberCardInfo : memberCardInfoList) {
            MemberCardRankListVo memberCardRankListVo = new MemberCardRankListVo();
            BeanUtils.copyProperties(memberCardInfo, memberCardRankListVo);
            //获取店铺下特权开关为开启状态下的会员卡权益列表
            List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(shopId);
            for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
                MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper.selectMemberCardPrivilegeGroup(shopId, memberCardInfo.getId(), memberPrivilegeVo.getId());
                if (memberCardPrivilegeGroup != null) {
                    memberPrivilegeVo.setIsSelected(1);
                    memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
                } else {
                    memberPrivilegeVo.setIsSelected(0);
                }
            }
            memberCardRankListVo.setMemberPrivilegeVos(memberPrivilegeVos);
            memberCardRankListVos.add(memberCardRankListVo);
        }
        return memberCardRankListVos;
    }

    /**
     * 后台更新会员的到期日
     *
     * @param updateMemberExpireTimeDto com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberExpireTime(UpdateMemberExpireTimeDto updateMemberExpireTimeDto) {
        DateTime t1 = DateUtil.parseDateTime(updateMemberExpireTimeDto.getMemberExpireTime());
        DateTime t2 = DateTime.now();
        if (t1.compareTo(t2) < 0) {
            throw new ServiceException("pc端会员续费失败，会员卡已到期", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //取时间
        Date date = DateUtil.parseDateTime(updateMemberExpireTimeDto.getMemberExpireTime());
        Calendar calendar = Calendar.getInstance();
        //需要将date数据转移到Calender对象中操作
        calendar.setTime(date);
        //把日期往后增加n天.正数往后推,负数往前移动
        calendar.add(Calendar.DATE, updateMemberExpireTimeDto.getDate().intValue());
        //这个时间就是日期往后推n天的结果
        date = calendar.getTime();
        updateMemberExpireTimeDto.setMemberExpireTime(DateUtil.formatDateTime(date));
        int update = memberInfoMapper.updateMemberExpireTime(updateMemberExpireTimeDto);
        if (update < 1) {
            throw new ServiceException("后台会员续费失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 后台升级会员等级
     *
     * @param upgradeMemberRankDto com.medusa.gruul.account.model.dto.UpgradeMemberRankDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upgradeMemberRank(UpgradeMemberRankDto upgradeMemberRankDto) {

        upgradeMemberRankDto.setShopId(ISecurity.userMust().getShopId());
        int update = memberInfoMapper.upgradeMemberRank(upgradeMemberRankDto);
        if (update < 1) {
            throw new ServiceException("后台会员升级失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 获取指定店铺下的用户的会员卡相关信息
     *
     * @param shopId   店铺id
     * @param shopUserId 店铺用户id
     * @return
     */
    @Override
    public MemberCardInfoCenterDto getMemberInfo(String shopId, String shopUserId) {
        List<MemberCardInfoCenterDto> memberCardInfoCenterDtos = new ArrayList<>();
        MemberSwitch memberSwitch = memberSwitchService.getByMemberModelMemberSwitch(CommonConstants.NUMBER_ZERO);
        // 该处提供判断 如果后台会员功能以关闭 并不影响已购会员
        if (memberSwitch == null) {
            return null;
        }
        List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(shopUserId);
        for (MemberInfo memberInfo : memberInfos) {
            //忽略未注册以及会员到期的会员卡
            if (memberInfo.getState().equals(CommonConstants.NUMBER_ZERO) || memberInfo.getState().equals(CommonConstants.NUMBER_TWO)) {
                continue;
            }
            MemberCardInfoCenterDto memberCardInfoCenterDto = new MemberCardInfoCenterDto();
            memberCardInfoCenterDto.setId(memberInfo.getId())
                    .setMemberNumber(memberInfo.getMemberNumber())
                    .setState(memberInfo.getState())
                    .setMemberCardInfoId(memberInfo.getMemberCardInfoId())
                    .setMemberRegisterTime(memberInfo.getMemberRegisterTime())
                    .setMemberExpireTime(memberInfo.getMemberExpireTime());
            MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
            memberCardInfoCenterDto.setTitle(memberCardInfo.getTitle())
                    .setRankCode(memberCardInfo.getRankCode())
                    .setRankName(memberCardInfo.getRankName())
                    .setOpenType(memberCardInfo.getOpenType())
                    .setOpenValue(memberCardInfo.getOpenValue())
                    .setUseInDate(memberCardInfo.getUseInDate());
            //获取店铺下特权开关为开启状态下的会员卡权益列表

            List<MemberCardPrivilegeGroup> memberCardPrivilegeGroups = memberCardPrivilegeGroupMapper.getByMemberCardId(memberCardInfo.getId(),shopId);
            List<MemberCardPrivilege> memberCardPrivileges = new ArrayList<>();
            for (MemberCardPrivilegeGroup memberCardPrivilegeGroup : memberCardPrivilegeGroups) {
                MemberCardPrivilege memberCardPrivilege = memberCardPrivilegeMapper.selectById(memberCardPrivilegeGroup.getPrivilegeId());
                //仅返回打开中的权益
                if (memberCardPrivilege.getOpen().equals(CommonConstants.NUMBER_ONE)) {
                    memberCardPrivilege.setDefValue(memberCardPrivilegeGroup.getValue() == null ? new BigDecimal(0) : memberCardPrivilegeGroup.getValue());
                    memberCardPrivileges.add(memberCardPrivilege);
                }
            }
            memberCardInfoCenterDto.setMemberCardPrivileges(memberCardPrivileges);
            //获取用户余额
            MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
            //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
            RebateSet rebateSet = remoteGoodsService.getRebateSet(accountExtends.getShopId());
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>()
                    .eq(MemberInfo::getState, 1)
                    .eq(MemberInfo::getShopId, shopId)
                    .eq(MemberInfo::getUserId, shopUserId));
            //是会员
            if (CollectionUtil.isNotEmpty(memberInfoList)) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    memberCardInfoCenterDto.setSupplyBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCardInfoCenterDto.setSupplyBonus(accountExtends.getSupplyBonus());
                }
            } else {
                //不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    memberCardInfoCenterDto.setSupplyBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCardInfoCenterDto.setSupplyBonus(accountExtends.getSupplyBonus());
                }
            }
            memberCardInfoCenterDtos.add(memberCardInfoCenterDto);
        }
        Optional<MemberCardInfoCenterDto> first = memberCardInfoCenterDtos.stream()
                .max(Comparator.comparing(MemberCardInfoCenterDto::getRankCode));
        return first.orElse(null);
    }

    /**
     * 获取店铺id下最高等级的会员等级信息
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.model.MemberTopCardInfoDto
     */
    @Override
    public MemberTopCardInfoDto getTopMemberCardInfo(String shopId) {
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoByShopId(shopId);
        memberCardInfoList = memberCardInfoList.stream()
                //过滤出已开启的权益
                .filter(item -> item.getIsOpen().equals(CommonConstants.NUMBER_ONE))
                .sorted(Comparator.comparing(MemberCardInfo::getRankCode).reversed())
                .toList();
        if (CollectionUtil.isEmpty(memberCardInfoList)) {
            return null;
        }
        MemberTopCardInfoDto memberTopCardInfoDto = new MemberTopCardInfoDto();
        MemberCardInfo memberCardInfo = memberCardInfoList.get(CommonConstants.NUMBER_ZERO);
        memberTopCardInfoDto.setBackGroundPicUrl(memberCardInfo.getBackGroundPicUrl())
                .setBuyGoods(memberCardInfo.getBuyGoods())
                .setConsumeCondition(memberCardInfo.getConsumeCondition())
                .setId(memberCardInfo.getId())
                .setIntroduce(memberCardInfo.getIntroduce())
                .setIsOpen(memberCardInfo.getIsOpen())
                .setOpenType(memberCardInfo.getOpenType())
                .setOpenValue(memberCardInfo.getOpenValue())
                .setRankCode(memberCardInfo.getRankCode())
                .setRankName(memberCardInfo.getRankName())
                .setStyle(memberCardInfo.getStyle())
                .setTitle(memberCardInfo.getTitle())
                .setUseInDate(memberCardInfo.getUseInDate());
        //获取店铺下特权开关为开启状态下的会员卡权益列表
        List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(shopId);
        for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
            MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper.selectMemberCardPrivilegeGroup(shopId, memberCardInfoList.get(0).getId(), memberPrivilegeVo.getId());
            if (memberCardPrivilegeGroup != null) {
                memberPrivilegeVo.setIsSelected(CommonConstants.NUMBER_ONE);
                memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
            } else {
                memberPrivilegeVo.setIsSelected(CommonConstants.NUMBER_ZERO);
            }
        }
        List<MemberPrivilegeDto> memberPrivilegeDtos = new ArrayList<>();
        for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
            MemberPrivilegeDto memberPrivilegeDto = new MemberPrivilegeDto();
            BeanUtils.copyProperties(memberPrivilegeVo, memberPrivilegeDto);
            memberPrivilegeDto.setDefValue(memberPrivilegeVo.getValue());
            memberPrivilegeDtos.add(memberPrivilegeDto);
        }

        memberTopCardInfoDto.setMemberCardPrivileges(memberPrivilegeDtos);
        return memberTopCardInfoDto;

    }

    /**
     * pc端升级会员等级界面，暂考虑该用户所持有的会员卡和后台设置的种类一致，不一致则提示开通，跳转到开通界面
     *
     * @param memberId 会员id
     * @return com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     */
    @Override
    public MemberCardUpgradeVo getMemberCardUpgrade(String memberId) {
        MemberCardUpgradeVo memberCardUpgradeVo = new MemberCardUpgradeVo();

        MemberInfo memberInfo = memberInfoMapper.selectById(memberId);
        String shopId = ISystem.shopId();
        String userId = "";
        if (memberInfo != null && memberInfo.getUserId() != null) {
            String shopUserId = memberInfo.getUserId();
            userId = shopUserId;
            MiniAccount miniAccount = miniAccountMapper.getMemberInfo(userId);
            MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
            //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
            RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, shopUserId));
            //是会员
            if (CollectionUtil.isNotEmpty(memberInfoList)) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus());
                }
            } else {
                //不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus());
                }
            }

            memberCardUpgradeVo.setAvatarUrl(miniAccount.getAvatarUrl())
                    .setNikeName(miniAccount.getNikeName())
                    .setUserId(userId);
            BigDecimal integral = miniAccountMapper.getIntegralByShopUserId(memberInfo.getUserId(), shopId);
            //账户可用积分
            memberCardUpgradeVo.setUseAmount(integral);
            MemberInfo memberInfoTemp = memberInfoMapper.selectById(memberId);
            MemberCardInfo memberCardInfoTemp = memberCardInfoMapper.getApiMemberCardInfo(memberInfoTemp.getMemberCardInfoId());
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = memberInfoTemp.getMemberExpireTime().atZone(zoneId);
            Date date = Date.from(zdt.toInstant());
            Date date2 = new Date();
            long day = DateUtil.betweenDay(date2, date, false);
            if (day < 0) {
                day = 0;
            }
            memberCardUpgradeVo
                    .setRemainDate(String.valueOf(day))
                    .setRankCode(memberCardInfoTemp.getRankCode())
                    .setRankName(memberCardInfoTemp.getRankName())
                    .setMemberNumber(memberInfoTemp.getMemberNumber());

            List<MemberCardInfoCenterVo> memberCardInfoCenterVos = new ArrayList<>();
            List<MemberCardInfo> memberCardInfoList = memberCardInfoService.getCurrentMemberCardInfo(shopId);
            for (MemberCardInfo memberCardInfo : memberCardInfoList) {
                MemberCardInfoCenterVo memberCardInfoCenterVo = getMemberCardInfoCenterVo(memberCardUpgradeVo, shopId, memberCardInfo);
                memberCardInfoCenterVos.add(memberCardInfoCenterVo);
            }
            memberCardUpgradeVo.setMemberCardInfoCenterVos(memberCardInfoCenterVos);
        }
        return memberCardUpgradeVo;
    }

    /**
     * 封装用户卡相关信息
     *
     * @param memberCardUpgradeVo com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     * @param shopId              店铺id
     * @param memberCardInfo      会员卡相关信息
     * @return com.medusa.gruul.account.model.vo.MemberCardInfoCenterVo
     */
    private MemberCardInfoCenterVo getMemberCardInfoCenterVo(MemberCardUpgradeVo memberCardUpgradeVo, String shopId, MemberCardInfo memberCardInfo) {
        MemberCardInfoCenterVo memberCardInfoCenterVo = new MemberCardInfoCenterVo();
        memberCardInfoCenterVo.setMemberCardInfoId(memberCardInfo.getId());
        memberCardInfoCenterVo
                .setTitle(memberCardInfo.getTitle())
                .setRankCode(memberCardInfo.getRankCode())
                .setRankName(memberCardInfo.getRankName())
                .setOpenType(memberCardInfo.getOpenType())
                .setOpenValue(memberCardInfo.getOpenValue())
                .setUseInDate(memberCardInfo.getUseInDate())
                .setBackGroundPicUrl(memberCardInfo.getBackGroundPicUrl())
                .setIsOpen(memberCardInfo.getIsOpen());
        // 实际支付金额（积分）：要升级套餐价格/有效天数*剩余时长数
        BigDecimal actualOpenValue = memberCardInfoCenterVo.getOpenValue().multiply(new BigDecimal(memberCardUpgradeVo.getRemainDate())).divide(new BigDecimal(memberCardInfoCenterVo.getUseInDate()), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        memberCardInfoCenterVo.setActualOpenValue(actualOpenValue);
        // 获取店铺下特权开关为开启状态下的会员卡权益列表
        List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(shopId);
        for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
            MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper.selectMemberCardPrivilegeGroup(shopId, memberCardInfo.getId(), memberPrivilegeVo.getId());
            if (memberCardPrivilegeGroup != null) {
                memberPrivilegeVo.setIsSelected(1);
                memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
            } else {
                memberPrivilegeVo.setIsSelected(0);
            }
        }

        memberCardInfoCenterVo.setMemberCardPrivileges(memberPrivilegeVos);
        return memberCardInfoCenterVo;
    }

    /**
     * pc端查看会员
     *
     * @param page                  页数
     * @param size                  条数
     * @param shopUserId            用户店铺id
     * @param memberNumber          会员编号
     * @param title                 会员卡名称
     * @param rankName              会员卡等级名称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime   成为会员结束时间
     * @return com.medusa.gruul.account.model.vo.MemberInnerListVo
     */
    @Override
    public PageUtils<List<MemberInnerListVo>> getMemberRecord(Integer page, Integer size, String shopUserId, String memberNumber, String title, String rankName, String becomeMemberStartTime, String becomeMemberEndTime) {
        Map<String, Object> paramMap = new HashMap<>(7);
        if (StrUtil.isNotEmpty(becomeMemberStartTime)) {
            paramMap.put("becomeMemberStartTime", becomeMemberStartTime);
        }
        if (StrUtil.isNotEmpty(becomeMemberEndTime)) {
            paramMap.put("becomeMemberEndTime", becomeMemberEndTime);
        }
        if (StrUtil.isNotEmpty(shopUserId)) {
            paramMap.put("shopUserId", shopUserId);
        }
        if (StrUtil.isNotBlank(memberNumber)) {
            paramMap.put("memberNumber", "%" + memberNumber + "%");
        }
        if (StrUtil.isNotBlank(rankName)) {
            AtomicReference<String> name = new AtomicReference<>("%");
            rankName.chars().mapToObj(obj -> (char) obj).forEach(obj -> {
                name.set(name.get().concat(obj.toString()).concat("%"));
            });
            paramMap.put("rankName", name.toString());
        }
        if (StrUtil.isNotBlank(title)) {
            AtomicReference<String> name = new AtomicReference<>("%");
            title.chars().mapToObj(obj -> (char) obj).forEach(obj -> {
                name.set(name.get().concat(obj.toString()).concat("%"));
            });
            paramMap.put("title", name.toString());
        }
        String shopId = ISecurity.userMust().getShopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("店铺id不存在");
        }
        paramMap.put("shopId", shopId);
        IPage<MemberInnerListVo> iPage = this.baseMapper.getMemberRecord(new Page<>(page, size), paramMap);
        List<MemberInnerListVo> records = iPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageUtils<>(new ArrayList<>(0), (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
        }
        return new PageUtils(records, (int) iPage.getTotal(), (int) iPage.getSize(), (int) iPage.getCurrent());
    }

    /**
     * 获取当前使用中的各等级会员卡
     *
     * @return com.medusa.gruul.account.model.vo.UseMemberCardRankVo
     */
    @Override
    public List<UseMemberCardRankVo> getUseMemberCardRank() {
        //获取当前使用中的会员卡
        MemberCardBaseInfo memberCardBaseInfo = memberCardInfoService.getByMemberBaseInfo(ISecurity.userMust().getShopId());
        List<MemberCardInfoListResultDto> cardInfoList = memberCardInfoService.getMemberCardInfoList(new MemberCardInfoListDto().setOpenType(memberCardBaseInfo.getCurrentOpenType()));
        return cardInfoList.stream().map(obj -> BeanUtil.toBean(obj, UseMemberCardRankVo.class)).toList();
    }

    /**
     * 当前用户余额明细跳转状态
     *
     * @return com.medusa.gruul.account.model.vo.JumpDetailVo
     */
    @Override
    public JumpDetailVo getJumpDetail() {
        JumpDetailVo vo = new JumpDetailVo();
        vo.setMemberOpen(CommonConstants.NUMBER_ZERO);
        vo.setWhetherMember(CommonConstants.NUMBER_ZERO);
        vo.setHistoryBuy(CommonConstants.NUMBER_ZERO);
        SecureUser secureUser = ISecurity.userMust();
        MemberSwitch memberSwitch = memberSwitchService.getByMemberModelMemberSwitch(CommonConstants.NUMBER_ZERO);
        vo.setMemberOpen(memberSwitch.getState());
        List<MemberInfo> memberInfos = this.baseMapper.getMemberInfoByUserId(secureUser.getId());
        //不存在任何会员卡当前用户肯定不是会员，也没有买过会员卡
        if (CollectionUtil.isEmpty(memberInfos)) {
            vo.setWhetherMember(CommonConstants.NUMBER_ZERO);
            vo.setHistoryBuy(CommonConstants.NUMBER_ZERO);
        } else {
            vo.setHistoryBuy(CommonConstants.NUMBER_ONE);
            //判断当前是否存在未过期的会员卡
            List<MemberInfo> exit = memberInfos.stream().filter(obj ->
                    LocalDateTimeUtils.getSecondsByTime(obj.getMemberExpireTime()) < System.currentTimeMillis()).toList();
            if (CollectionUtil.isNotEmpty(exit)) {
                vo.setWhetherMember(CommonConstants.NUMBER_ONE);
            }
        }
        return vo;
    }


}
