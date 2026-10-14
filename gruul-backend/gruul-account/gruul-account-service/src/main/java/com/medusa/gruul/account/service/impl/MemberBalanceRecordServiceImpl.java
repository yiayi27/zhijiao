package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberBalanceRecord;
import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.api.entity.MiniAccount;
import com.medusa.gruul.account.api.entity.MiniAccountExtends;
import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.account.mapper.MemberBalanceRecordMapper;
import com.medusa.gruul.account.mapper.MemberInfoMapper;
import com.medusa.gruul.account.mapper.MiniAccountMapper;
import com.medusa.gruul.account.model.vo.MemberBalanceRecordResVo;
import com.medusa.gruul.account.model.vo.MemberBalanceRecordVo;
import com.medusa.gruul.account.service.IMemberBalanceRecordService;
import com.medusa.gruul.account.service.IMiniAccountExtendsService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.order.api.model.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 账户余额明细表 服务实现类
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
@Slf4j
@Service
public class MemberBalanceRecordServiceImpl extends ServiceImpl<MemberBalanceRecordMapper, MemberBalanceRecord>
        implements IMemberBalanceRecordService {

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private MiniAccountMapper miniAccountMapper;

    @Autowired
    private RemoteGoodsService remoteGoodsService;

    /**
     * 生成订单编号，格式：店铺id+yyMMdd+7位随机数
     *
     * @param range
     * @return
     */
    public static String generateOrderRandomNo(String shopId, int range) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String format = sdf.format(new Date());
        return shopId + format + RandomUtil.randomNumbers(range);
    }

    /**
     * 获取会员余额明细
     *
     * @param yearMonth    年月
     * @param shopUserId 会员id
     * @param page         指定页数
     * @param size         数据条数
     * @param dealType     交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现
     * @return com.medusa.gruul.account.model.vo.MemberBalanceRecordResVo
     */
    @Override
    public MemberBalanceRecordResVo getMemberBalanceRecord(String yearMonth, String shopUserId, Integer page,
                                                           Integer size, Integer dealType) {
        Map<String, Object> paramMap = new HashMap<>(4);
        if (StrUtil.isNotEmpty(yearMonth)) {
            DateTime currentDate = DateUtil.parse(yearMonth, "yyyyMM");
            paramMap.put("startTime", DateUtil.beginOfMonth(currentDate).toString());
            paramMap.put("endTime", DateUtil.endOfMonth(currentDate).toString());
        }
        String idsStr;
        //没有指定会员条件则表示小程序端调用,同时根据用户id
        if (StrUtil.isNotEmpty(shopUserId)) {
            idsStr = shopUserId;
        } else {
            //获取用户信息
            shopUserId = ISecurity.userMust().getId();
            idsStr = shopUserId;
            paramMap.put("accountDel", 0);
        }
        List<MemberInfo> memberInfos = this.getByUserMenmberInfos(shopUserId);
        //如果用户存在会员卡则同时查询会员卡相关的返利
        if (CollectionUtil.isNotEmpty(memberInfos)) {
            idsStr = idsStr.concat(",").concat(memberInfos.stream().map(obj -> obj.getId().toString()).collect(Collectors.joining(",")));
        }
        paramMap.put("memberInfoId", idsStr);

        if (dealType != null) {
            paramMap.put("dealType", dealType);
        }
        IPage<MemberBalanceRecordVo> iPage = this.baseMapper
                .getMemberBalanceRecord(new Page<>(page, size), paramMap);
        List<MemberBalanceRecordVo> records = iPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new MemberBalanceRecordResVo(new ArrayList(0), (int) iPage.getTotal(), (int) iPage.getSize(),
                    (int) iPage.getCurrent(), new BigDecimal("0.0"), new BigDecimal("0.0"));
        }
        BigDecimal inBalance = new BigDecimal(0);
        BigDecimal outBalance = new BigDecimal(0);
        for (MemberBalanceRecordVo memberBalanceRecordVo : records) {
            //收入
            if (memberBalanceRecordVo.getChangeType() == 0) {
                inBalance = NumberUtil.roundHalfEven(NumberUtil.add(inBalance, memberBalanceRecordVo.getUseBalance()), 2);
                //支出
            } else if (memberBalanceRecordVo.getChangeType() == 1) {
                outBalance = NumberUtil.roundHalfEven(NumberUtil.add(outBalance, memberBalanceRecordVo.getUseBalance()), 2);
            }
        }
        return new MemberBalanceRecordResVo(records, (int) iPage.getTotal(), (int) iPage.getSize(),
                (int) iPage.getCurrent(), outBalance, inBalance);
    }

    /**
     * 获取指定用户拥有的会员卡
     *
     * @param shopUserId 用户id
     * @return 用户会员卡
     */
    private List<MemberInfo> getByUserMenmberInfos(String shopUserId) {
        return this.memberInfoMapper
                .selectList(new QueryWrapper<MemberInfo>().eq("user_id", shopUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean memberBalanceModify(MemberBalanceUpdateDto memberBalanceUpdateDto) {
        Boolean flag = Boolean.TRUE;
        try {
            MiniAccountExtends accountExtends = getMiniAccountExtends(memberBalanceUpdateDto);
            BigDecimal amount = new BigDecimal(0);
            //增加
            if (memberBalanceUpdateDto.getChangeType() == 0) {
                amount = accountExtends.getSupplyBonus().add(memberBalanceUpdateDto.getUseBalance());
                int updateBonus = miniAccountMapper
                        .updateBonus(accountExtends.getUserId(), amount);
                if (updateBonus < 1) {
                    flag = Boolean.FALSE;
                    log.error("小程序端更新会员余额失败," + SystemCode.DATA_UPDATE_FAILED.getCode());
                }
                //减少
            } else if (memberBalanceUpdateDto.getChangeType() == 1) {
                //增加返利
                //划款金额小于等于可用余额
                if (memberBalanceUpdateDto.getUseBalance().compareTo(accountExtends.getSupplyBonus())
                        <= 0) {
                    amount = accountExtends.getSupplyBonus().subtract(memberBalanceUpdateDto.getUseBalance());
                    int updateBonus = miniAccountMapper
                            .updateBonus(accountExtends.getUserId(), amount);
                    if (updateBonus < 1) {
                        flag = Boolean.FALSE;
                        log.error("小程序端更新会员余额失败," + SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                    amount = amount.add(accountExtends.getRebateBonus());
                } else {
                    //划款金额大于可用余额
                    int updateBonus = miniAccountMapper
                            .updateBonus(accountExtends.getUserId(),
                                    new BigDecimal(0));
                    if (updateBonus < 1) {
                        flag = Boolean.FALSE;
                        log.error("小程序端更新会员余额失败," + SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                    //获取返利设置，获取会员的返利设置
                    String shopId = accountExtends.getShopId();
                    RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
                    List<MemberInfo> memberInfoList = memberInfoMapper.selectList(
                            new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, accountExtends.getUserId()));
                    //是会员
                    if (memberInfoList != null && memberInfoList.size() > 0) {
                        if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet
                                .getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                            amount = accountExtends.getRebateBonus().subtract(
                                    memberBalanceUpdateDto.getUseBalance().subtract(accountExtends.getSupplyBonus()));
                        } else {
                            throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额",
                                    SystemCode.DATA_UPDATE_FAILED.getCode());
                        }
                    } else {
                        //不是会员,是普通用户
                        if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet
                                .getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                            amount = accountExtends.getRebateBonus().subtract(
                                    memberBalanceUpdateDto.getUseBalance().subtract(accountExtends.getSupplyBonus()));
                        } else {
                            throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额",
                                    SystemCode.DATA_UPDATE_FAILED.getCode());
                        }
                    }

                    int updateReBonus = miniAccountMapper
                            .updateRebateBonus(accountExtends.getUserId(), amount);
                    if (updateReBonus < 1) {
                        flag = Boolean.FALSE;
                        log.error("小程序端更新会员余额失败," + SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                }
            }
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                log.error("剩余金额小于0：" + amount);
                return Boolean.FALSE;
            }
            flag = memberBalanceRecordOption(memberBalanceUpdateDto, flag, accountExtends, amount);
            return flag;
        } catch (Exception e) {
            log.error("余额支付失败",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 获取信息
     *
     * @param memberBalanceUpdateDto com.medusa.gruul.account.api.model.MemberBalanceUpdateDto
     * @return com.medusa.gruul.account.api.entity.MiniAccountExtends
     */
    private MiniAccountExtends getMiniAccountExtends(MemberBalanceUpdateDto memberBalanceUpdateDto) {
        MiniAccountExtends accountExtends = new MiniAccountExtends();
        if (memberBalanceUpdateDto.getMemberInfoId() != null) {
            MemberInfo memberInfo = memberInfoMapper.selectById(memberBalanceUpdateDto.getMemberInfoId());
            accountExtends = miniAccountExtendsService.findByShopUserId(memberInfo.getUserId());
        }
        if (memberBalanceUpdateDto.getShopUserId() != null && !"".equals(memberBalanceUpdateDto.getShopUserId())) {
            accountExtends = miniAccountExtendsService.findByShopUserId(memberBalanceUpdateDto.getShopUserId());
        }
        return accountExtends;
    }

    /**
     * 余额记录操作
     *
     * @param memberBalanceUpdateDto com.medusa.gruul.account.api.model.MemberBalanceUpdateDto
     * @param flag                   flag
     * @param accountExtends         com.medusa.gruul.account.api.entity.MiniAccountExtends
     * @param amount                 金额
     * @return flag
     */
    private Boolean memberBalanceRecordOption(MemberBalanceUpdateDto memberBalanceUpdateDto, Boolean flag, MiniAccountExtends accountExtends, BigDecimal amount) {
        MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
        memberBalanceRecord
                .setChangeType(memberBalanceUpdateDto.getChangeType())
                .setDealType(memberBalanceUpdateDto.getDealType())
                .setDetailName(memberBalanceUpdateDto.getDetailName())
                .setOrderId(Long.parseLong(generateOrderRandomNo(accountExtends.getShopId(), 7)))
                .setRemainingBalance(amount)
                .setUseBalance(memberBalanceUpdateDto.getUseBalance())
                .setOutOrderId(memberBalanceUpdateDto.getOutOrderId())
                .setSource(memberBalanceUpdateDto.getSource());
        if (memberBalanceUpdateDto.getMemberInfoId() != null) {
            memberBalanceRecord.setMemberInfoId(memberBalanceUpdateDto.getMemberInfoId());
        }
        if (memberBalanceUpdateDto.getShopUserId() != null && !"".equals(memberBalanceUpdateDto.getShopUserId())) {
            memberBalanceRecord.setMemberInfoId(Long.parseLong(memberBalanceUpdateDto.getShopUserId()));
        }
        int insert = this.baseMapper.insert(memberBalanceRecord);
        if (insert < 1) {
            flag = Boolean.FALSE;
            log.error("插入余额明细数据失败,外部订单号：" + memberBalanceUpdateDto.getOutOrderId());
        }
        return flag;
    }

    /**
     * 订单交易成功，增加用户返利金额
     *
     * @param orderVo 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void memberRebatePayed(OrderVo orderVo) {
        try {
            BigDecimal rebateAmount = orderVo.getRebateAmount();
            //判断返利金额是否大于0
            if (rebateAmount == null || rebateAmount.compareTo(BigDecimal.ZERO) <= CommonConstants.NUMBER_ZERO) {
                log.warn("消费返利队列回 ===》 订单id：".concat(orderVo.getId().toString()).concat(", 消费返利金额为0，不进行处理"));
                return;
            }
            String shopUserId = orderVo.getUserId();

            List<MemberInfo> memberInfos = this.getByUserMenmberInfos(shopUserId);
            MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
            BigDecimal amount = accountExtends.getRebateBonus().add(rebateAmount);
            if (miniAccountMapper.updateRebateBonus(shopUserId, amount) < 1) {
                throw new ServiceException("小程序端更新会员返利余额失败");
            }
            MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
            memberBalanceRecord
                    .setChangeType(0)
                    .setDealType(2)
                    .setDetailName("消费返利")
                    .setOrderId(orderVo.getId())
                    .setRemainingBalance(amount)
                    .setUseBalance(rebateAmount)
                    .setOutOrderId(orderVo.getId())
                    .setSource(2);
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                memberBalanceRecord.setMemberInfoId(memberInfos.get(0).getId());
            } else {
                memberBalanceRecord.setMemberInfoId(Long.parseLong(shopUserId));
            }
            int insert = this.baseMapper.insert(memberBalanceRecord);
            if (insert < 1) {
                log.error("插入余额明细数据失败,外部订单号：" + orderVo.getId());
            }
        } catch (Exception e) {
            log.error("消费返利队列回调异常: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServiceException("消费返利队列回调异常，无法正确消费");
        }
    }

    /**
     * 订单交易成功，结算对应分销员分销金额
     *
     * @param orderVo 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userDistributionCompleted(OrderVo orderVo) {
        try {
            String shopUserId = orderVo.getUserId();

            MiniAccount miniAccount = this.miniAccountMapper.selectByShopUserId(shopUserId);
            BigDecimal firstDistributionAmount = orderVo.getFirstDistributionAmount();
            //结算一级分销员分销佣金
            setUserDistributionMethod(orderVo, firstDistributionAmount, miniAccount.getFirstUserId(), 1);
        } catch (Exception e) {
            log.error("消费分销队列回调异常: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServiceException("消费分销队列回调异常，无法正确消费");
        }
    }

    /**
     * 设置分销佣金
     *
     * @param orderVo           订单信息
     * @param distributionAmount 分销金额
     * @param userId            分销员id
     * @param type              分销类型 1-一级分销 2-二级分销
     */
    public void setUserDistributionMethod(OrderVo orderVo, BigDecimal distributionAmount, String userId, Integer type) {
        //判断分销金额是否大于0
        log.warn("消费分销队列回 ===》 订单vo: ".concat(orderVo.toString()));
        if (distributionAmount == null
                || distributionAmount.compareTo(BigDecimal.ZERO) <= CommonConstants.NUMBER_ZERO
                || StrUtil.isBlank(userId)) {
            log.warn("消费分销队列回 ===》 订单id：".concat(orderVo.getId().toString()).concat(", 分销金额为0，不进行处理"));
        } else {
            MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
            BigDecimal amount = accountExtends.getSupplyBonus().add(distributionAmount);
            if (miniAccountMapper.updateBonus(userId, amount) < 1) {
                throw new ServiceException("小程序端更新分销员余额失败");
            }
            MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
            memberBalanceRecord
                    .setChangeType(0)
                    .setDealType(4)
                    .setDetailName("订单" + orderVo.getId() + "商品分佣")
                    .setOrderId(orderVo.getId())
                    .setRemainingBalance(amount)
                    .setUseBalance(distributionAmount)
                    .setOutOrderId(orderVo.getId())
                    .setShopUserId(Long.parseLong(userId))
                    .setSource(2)
                    .setPayAmount(orderVo.getPayAmount())
                    .setOrderUserId(Long.valueOf(orderVo.getUserId()))
                    .setDistributionType(type);
            int insert = this.baseMapper.insert(memberBalanceRecord);
            if (insert < 1) {
                log.error("插入余额明细数据失败,外部订单号：" + orderVo.getId());
            }
        }
    }

    /**
     * 用户删除指定余额明细id
     *
     * @param id 余额明细id
     */
    @Override
    public void removeMemberBalanceRecord(Long id) {
        MemberBalanceRecord memberBalanceRecord = this.baseMapper.selectById(id);
        if (memberBalanceRecord == null) {
            throw new ServiceException("不存在有效数据");
        }
        memberBalanceRecord.setAccountDel(Boolean.TRUE);
        this.baseMapper.updateById(memberBalanceRecord);
    }
}
