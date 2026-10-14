package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;


import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.constant.IntegralOrderConstent;
import com.medusa.gruul.integral.api.entity.IntegralCouponOrder;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.medusa.gruul.integral.api.model.IntegralCouponDto;
import com.medusa.gruul.integral.mapper.IntegralCouponOrderMapper;
import com.medusa.gruul.integral.mapper.IntegralDetailMapper;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.IIntegralCouponOrderService;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralCouponOrderDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralCouponOrderParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xiaoq
 * @Date: 2024/1/9 10:30
 */
@Slf4j
@Service
public class IntegralCouponOrderServiceImpl extends ServiceImpl<IntegralCouponOrderMapper, IntegralCouponOrder>
		implements IIntegralCouponOrderService {

	@Autowired
	private IntegralCouponOrderMapper integralCouponOrderMapper;

	@Autowired
	private RemoteMiniAccountService remoteMiniAccountService;

	@Autowired
	private IntegralDetailMapper integralDetailMapper;

	@Autowired
	private Sender sender;

	/**
	 * 按条件获取积分优惠券订单
	 *
	 * @param integralCouponOrderParam 查询条件
	 * @return 符合条件的值
	 */
	@Override
	public IPage<IntegralCouponOrderDto> searchOrders(IntegralCouponOrderParam integralCouponOrderParam) {
		IPage<IntegralCouponOrderDto> page = new Page<>(integralCouponOrderParam.getCurrent(),
				integralCouponOrderParam.getSize());
		return page.setRecords(this.baseMapper.getIntegralCouponOrderList(page, integralCouponOrderParam));
	}


	/***
	 * 积分优惠券兑换 并生成订单 生成积分明细 并通知用户扣除积分
	 *
	 * @param integralCouponDto 积分优惠卷订单生成所需值
	 * @return Result信息 供优惠卷返回给前端展示使用
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public Result<Integer> addIntegralCouponOrder(IntegralCouponDto integralCouponDto) {
		
		String shopId = ISecurity.userMust().getShopId();
		List<Integer> longs = new ArrayList<>(CommonConstants.NUMBER_THREE);
		longs.add(CommonConstants.NUMBER_ONE);
		longs.add(CommonConstants.NUMBER_TWO);
		long id;
		AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(integralCouponDto.getUserId(), longs);
		//获取用户状态
		Integer isBlacklist = accountInfoDto.getMiniAccountExtends().getIsBlacklist();
		//得到用户总积分值
		BigDecimal integral = accountInfoDto.getMiniAccountExtends().getIntegral();
		//用户在不在商家黑名单中
		if (isBlacklist != 1) {
			//可进行积分兑换
			if (integral.compareTo(integralCouponDto.getIntegralValue()) > -1) {
				do {
					//生成一个14bit的积分订单号
					id = RandomUtil.randomLong(IntegralOrderConstent.MIN_ID, IntegralOrderConstent.MAX_ID);
					IntegralCouponOrder integralCouponOrder = this.baseMapper
							.selectOne(new LambdaQueryWrapper<IntegralCouponOrder>().eq(IntegralCouponOrder::getId, id));
					if (BeanUtil.isEmpty(integralCouponOrder)) {
						break;
					}
				} while (true);
				IntegralCouponOrder integralCouponOrder = new IntegralCouponOrder();
				BeanUtil.copyProperties(integralCouponDto, integralCouponOrder);
				integralCouponOrder.setId(id);
				integralCouponOrder.setWxuserName(accountInfoDto.getMiniAccountunt().getNikeName());
				integralCouponOrder.setSendTime(LocalDateTime.now());
				integralCouponOrder.setRealPayIntegral(integralCouponDto.getIntegralValue());
				integralCouponOrder.setEffectiveType(integralCouponDto.getEffectiveType());
				integralCouponOrder.setEffectiveDays(integralCouponDto.getEffectiveDays());
				int insert = integralCouponOrderMapper.insert(integralCouponOrder);
				if (insert < 1) {
					throw new ServiceException("积分优惠券订单生成失败", SystemCode.DATA_ADD_FAILED.getCode());
				}
				//积分明细 封装
				IntegralDetail integralDetail = new IntegralDetail();
				integralDetail.setIntegral(integralCouponDto.getIntegralValue().negate());
				integralDetail.setGainIntegralType(IntegralGainTypeEnum.EXCHANGE_INTEGRAL_GOODS.getStatus());
				integralDetail.setUserId(integralCouponDto.getUserId());
				BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, integralCouponDto.getUserId());
				if (null == bigDecimal) {
					bigDecimal = new BigDecimal(0);
				}
				integralDetail.setNowTheIntegral(bigDecimal.add(integralCouponDto.getIntegralValue().negate()));
				insert = integralDetailMapper.insert(integralDetail);
				//判断是否插入成功
				if (insert < 1) {
					throw new ServiceException("积分明細生成失败", SystemCode.DATA_ADD_FAILED.getCode());
				}
				//通知用户扣除积分
				IntegraChangelDto integraChangelDto = new IntegraChangelDto();
				integraChangelDto.setValue(integralCouponDto.getIntegralValue());
				integraChangelDto.setUserId(integralCouponDto.getUserId());
				integraChangelDto.setOption(IntegralOperationTypeEnum.REDUCE.getStatus());
				integraChangelDto.setShopId(shopId);
				sender.sendIntegralDeductionMessage(integraChangelDto);
				return Result.ok(SystemCode.SUCCESS.getCode());
			}
			return Result.failed(SystemCode.FAILURE.getCode(), "积分不足");
		}
		return Result.failed(SystemCode.FAILURE.getCode(), "账号异常请联系客服");
	}

	/**
	 *  积分优惠券订单备注
	 *
	 * @param ids  ids
	 * @param backRemark 备注内容
	 * @param isCoverage 是否覆盖
	 */
	@Override
	public void remarkIntegralCouponOrder(List<Long> ids, String backRemark, Boolean isCoverage) {
		List<IntegralCouponOrder> integralCouponOrders = baseMapper.selectByIds(ids);
		if (CollectionUtil.isNotEmpty(integralCouponOrders)) {
			for (IntegralCouponOrder integralCouponOrder : integralCouponOrders) {
				StringBuilder sb = new StringBuilder();
				if (StrUtil.isBlank(backRemark)) {
					backRemark = "";
				}
				//是否进行覆盖 true 进行覆盖 false不覆盖
				if (!isCoverage) {
					integralCouponOrder.setBackRemark(
					        StrUtil.isNotBlank(integralCouponOrder.getBackRemark())
                                    ?sb.append(integralCouponOrder.getBackRemark()).append(StrUtil.CRLF).append(backRemark).toString()
                                    :backRemark);
				} else {
					integralCouponOrder.setBackRemark(backRemark);
				}
			}
			boolean update = this.updateBatchById(integralCouponOrders);
			if (!update) {
				throw new ServiceException("批量备注失败", SystemCode.DATA_UPDATE_FAILED.getCode());
			}
		}
	}
}