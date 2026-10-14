package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.account.api.model.MemberCardInfoCenterDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;


import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.medusa.gruul.integral.api.entity.IntegralRuleParameter;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.api.model.IntegralCouponDto;
import com.medusa.gruul.integral.mapper.IntegralDetailMapper;
import com.medusa.gruul.integral.mapper.IntegralRuleMapper;
import com.medusa.gruul.integral.mapper.IntegralRuleParameterMapper;
import com.medusa.gruul.integral.mapper.IntegralShopMapper;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.IIntegralDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.model.dto.IntegralDetailDto;
import com.medusa.gruul.integral.web.model.param.api.IntegralDetailParam;
import com.medusa.gruul.integral.web.model.vo.IntegralDetailVo;
import com.medusa.gruul.integral.api.model.UserInfoVo;
import com.medusa.gruul.order.api.model.OrderFailMessage;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 积分明细表
 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
@Slf4j
public class IntegralDetailServiceImpl extends ServiceImpl<IntegralDetailMapper, IntegralDetail> implements IIntegralDetailService {
	@Autowired
	private IntegralDetailMapper integralDetailMapper;
	@Autowired
	private IntegralRuleMapper integralRuleMapper;
	@Autowired
	private IntegralRuleParameterMapper integralRuleParameterMapper;
	@Autowired
	private IntegralShopMapper integralShopMapper;
	@Autowired
	private RemoteMiniAccountService remoteMiniAccountService;
	@Autowired
	private Sender sender;

	/**
	 * 用户查询积分明细
	 *
	 * @param integralDetailParam 分页对象
	 * @return 明细记录
	 */
	@Override
	public IPage<IntegralDetailVo> selectIntegralDetail(IntegralDetailParam integralDetailParam) {
		Page<IntegralDetailVo> page = new Page<>(integralDetailParam.getCurrent(), integralDetailParam.getSize());
		return page.setRecords(this.baseMapper.getIntegralDetail(page, integralDetailParam));
	}

	/**
	 * 删除积分明细记录
	 *
	 * @param integralDetailId 积分明细Id
	 */
	@Override
	public void delDetail(Long integralDetailId) {
		int integralDetail = integralDetailMapper.deleteById(integralDetailId);
		if (integralDetail < 1) {
			throw new ServiceException("积分明细记录删除失败", SystemCode.DATA_DELETE_FAILED.getCode());
		}
	}

	/**
	 * 用户每日首次分享 得到该店铺设置的分享积分值
	 * @param shopId 商铺id
	 * @param userId 用户Id
	 * @return 用户分享获得的积分值
	 */
	@Override
	public BigDecimal addIntegralDetail(String shopId, String userId) {
		
		//1.根据店铺id 得到积分商城表信息
		BigDecimal integral = null;
		IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
		if (integralShop == null) {
			throw new ServiceException("商城信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
		}
		//积分商城是否开启
		if (integralShop.getType()) {
			//2.根据积分商城表id 得到积分规则表类型为分享的 数据
			IntegralRule integralRule = integralRuleMapper.selectIntegralRuleInfo(integralShop.getId());
			if (integralRule == null) {
				throw new ServiceException("分享数据信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
			}
			//查看商家是否开启分享功能
			if (integralRule.getOpen()) {
				//3.根据积分规则表分享信息得到 商家设置的分享得到积分
				IntegralRuleParameter shareIntegral = integralRuleParameterMapper
						.getShareIntegral(integralRule.getIntegralRuleId());
				//4.查询积分明细 用户今日是否有进行分享得到积分
				List<IntegralDetail> integralDetails = integralDetailMapper.selectDetailInfo(shopId, userId);
				//5.判断用户今日是否为首次分享
				if (CollectionUtil.isEmpty(integralDetails)) {
					return null;
				}
				//获取用户会员倍数 非会员或者无积分权益 倍数为1
				BigDecimal integralMultiples = memberCardIntegralPrivilegeInfo(userId);
				IntegralDetail integralDetail = new IntegralDetail();
				if (integralMultiples.compareTo(BigDecimal.ONE) > 0) {
					//拥有会员权益 积分加倍时 新增字段 会员权益
					integralDetail
							.setDetailName(integralMultiples.doubleValue() + IntegralConstant.INTEGRAL_PRIVILEGE_NAME);
				}
				integralDetail.setGainIntegralType(IntegralGainTypeEnum.DAY_SHARE.getStatus());
				integralDetail.setUserId(userId);
				integralDetail.setIntegral(shareIntegral.getValue().multiply(integralMultiples));
				//计算用户现在拥有的积分值(未加当前获取的积分)
				BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
				//本次获取的积分
				integral = integralDetail.getIntegral();
				bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
				//用户目前积分(现有积分+本次获得的积分)
				integralDetail.setNowTheIntegral(bigDecimal.add(integral));
				int insert = integralDetailMapper.insert(integralDetail);
				if (insert < 1) {
					throw new ServiceException("积分明细生成失败", SystemCode.DATA_ADD_FAILED.getCode());
				}
				//mq通知用户
				sendIntegralMessage(integral, userId, IntegralOperationTypeEnum.INCREASE.getStatus());
			}
		}
		return integral;
	}

	/**
	 * 生成积分归还 积分明细
	 *
	 * @param userId 用户Id
	 * @param integralValue 积分值
	 * @return 插入积分明细 归还积分是否成功
	 */
	@Override
	public Boolean addIntegralDetailRecord(String userId, BigDecimal integralValue) {
		String shopId = ISecurity.userMust().getShopId();
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setIntegral(integralValue);
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.GIVE_BACK.getStatus());
		integralDetail.setUserId(userId);
		BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
		bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
		integralDetail.setNowTheIntegral(bigDecimal.add(integralValue));
		log.info("归还积分为        " + integralValue);
		boolean flag = Boolean.TRUE;
		int insert = integralDetailMapper.insert(integralDetail);
		//判断是否插入成功
		if (insert < 1) {
			flag = Boolean.FALSE;
		}
		return flag;
	}


	/**
	 * 生成积分扣减 积分明细
	 *
	 * @param userId 用戶id
	 * @param integralValue  积分值
	 */
	@Override
	public Boolean subtractIntegralDetailRecord(String userId, BigDecimal integralValue,String shopId) {
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setIntegral(integralValue.negate());
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.USE_BUY_GOODS.getStatus());
		integralDetail.setUserId(userId);
		BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
		bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
		integralDetail.setNowTheIntegral(bigDecimal.add(integralValue.negate()));
		int insert = integralDetailMapper.insert(integralDetail);
		//判断是否插入成功
		boolean flag = Boolean.TRUE;
		if (insert < 1) {
			flag = Boolean.FALSE;
		}
		return flag;
	}


	/**
	 * 生成兑换积分优惠券信息
	 * @param integralCouponDto 积分优惠卷订单生成所需值
	 * @return Boolean
	 */
	@Override
	public Boolean addConversionIntegralDetail(IntegralCouponDto integralCouponDto) {
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setIntegral(integralCouponDto.getIntegralValue().negate());
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.EXCHANGE_INTEGRAL_GOODS.getStatus());
		integralDetail.setUserId(integralCouponDto.getUserId());
		BigDecimal bigDecimal = integralDetailMapper
				.selectNowIntegral(integralDetail.getShopId(), integralCouponDto.getUserId());
		bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
		integralDetail.setNowTheIntegral(bigDecimal.add(integralCouponDto.getIntegralValue().negate()));
		int insert = integralDetailMapper.insert(integralDetail);
		//判断是否插入成功
		boolean flag = Boolean.TRUE;
		if (insert < 1) {
			flag = Boolean.FALSE;
		}
		return flag;
	}


	/**
	 * 商家充值积分 生成明细记录 发mq给用户
	 *
	 * @param userIds 用户ids
	 * @param integral 积分值
	 */
	@Override
	public void rechargeIntegral(List<String> userIds, BigDecimal integral) {
		//遍历userIds 逐一插入明细 修改数据
		for (String userId : userIds) {
			String shopId = ISecurity.userMust().getShopId();
			List<Integer> list = new ArrayList<>(CommonConstants.NUMBER_TWO);
			list.add(CommonConstants.NUMBER_TWO);
			AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, list);
			if (accountInfoDto == null) {
				throw new ServiceException("用户不存在", SystemCode.DATA_NOT_EXIST.getCode());
			}
			IntegralDetail integralDetail = new IntegralDetail();
			integralDetail.setIntegral(integral);
			integralDetail.setGainIntegralType(IntegralGainTypeEnum.MERCHANT_RECHARGE.getStatus());
			integralDetail.setUserId(userId);
			BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
			bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
			integralDetail.setNowTheIntegral(bigDecimal.add(integral));
			int insert = integralDetailMapper.insert(integralDetail);
			if (insert < 1) {
				throw new ServiceException("积分明细生成失败", SystemCode.DATA_ADD_FAILED.getCode());
			}
			//mq通知用户
			sendIntegralMessage(integral, userId, IntegralOperationTypeEnum.INCREASE.getStatus());
		}
	}


	/**
	 * 商家扣除积分 生成明细记录 发mq给用户
	 *
	 * @param userIds 用户ids
	 * @param integral 积分值
	 */
	@Override
	public void deductIntegral(List<String> userIds, BigDecimal integral) {
		//遍历userIds 逐一插入明细 修改数据
		for (String userId : userIds) {
			String shopId = ISecurity.userMust().getShopId();
			List<Integer> list = new ArrayList<Integer>(CommonConstants.NUMBER_TWO);
			list.add(CommonConstants.NUMBER_TWO);
			AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, list);
			if (accountInfoDto.getMiniAccountExtends() == null) {
				throw new ServiceException("用户不存在", SystemCode.DATA_NOT_EXIST.getCode());
			}
			if (accountInfoDto.getMiniAccountExtends().getIntegral().compareTo(integral) == -1) {
				throw new ServiceException("用户积分不足");
			}
			IntegralDetail integralDetail = new IntegralDetail();
			integralDetail.setIntegral(integral.negate());
			integralDetail.setGainIntegralType(IntegralGainTypeEnum.MERCHANT_DEDUCT.getStatus());
			integralDetail.setUserId(userId);
			BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
			bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
			integralDetail.setNowTheIntegral(bigDecimal.add(integral.negate()));
			int insert = integralDetailMapper.insert(integralDetail);
			if (insert < 1) {
				throw new ServiceException("积分明细生成失败", SystemCode.DATA_ADD_FAILED.getCode());
			}
			//mq通知用户
			sendIntegralMessage(integral, userId, IntegralOperationTypeEnum.REDUCE.getStatus());
		}
	}

	/**
	 * mq通知用户共方法
	 */
	public void sendIntegralMessage(BigDecimal integral, String userId, Integer integralType){
		IntegraChangelDto integraChangelDto = new IntegraChangelDto();
		integraChangelDto.setValue(integral);
		integraChangelDto.setUserId(userId);
		integraChangelDto.setOption(integralType);
		sender.sendIntegralDeductionMessage(integraChangelDto);
	}


	/**
	 * 获取用户信息 积分值 头像 昵称
	 * @param userId 用户id
	 * @return 用户信息
	 */
	@Override
	public UserInfoVo getUserInfo(String userId) {
		List<Integer> list = new ArrayList<>(CommonConstants.NUMBER_THREE);
		list.add(CommonConstants.NUMBER_ONE);
		list.add(CommonConstants.NUMBER_TWO);
		UserInfoVo userInfoVo = new UserInfoVo();
		AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, list);
		if (accountInfoDto.getMiniAccountExtends() == null) {
			throw new ServiceException("用户不存在", SystemCode.DATA_NOT_EXIST.getCode());
		}
		userInfoVo.setIntegral(accountInfoDto.getMiniAccountExtends().getIntegral());
		userInfoVo.setAvatarUrl(accountInfoDto.getMiniAccountunt().getAvatarUrl());
		userInfoVo.setNickName(accountInfoDto.getMiniAccountunt().getNikeName());
		return userInfoVo;
	}


	/**
	 * 订阅已完成的订单 生成明细 通知用户
	 *
	 * @param orderVo 订单详情
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void orderFinish(OrderVo orderVo) {
		//生成积分明细记录
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setShopId(orderVo.getShopId());
		integralDetail.setUserId(orderVo.getUserId());
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.BUY_GOODS.getStatus());
		List<OrderItemVo> orderItemList = orderVo.getOrderItemList();
		if (CollectionUtil.isNotEmpty(orderItemList)) {
			//遍历订单详情
			for (OrderItemVo orderItem : orderItemList) {
				if (orderItem.getGiftIntegration().compareTo(BigDecimal.ZERO) == 0) {
					//商品赠送积分为0时 不生成积分明细
					break;
				}
				integralDetail.setQuantity(orderItem.getProductQuantity());
				//所获得积分值   =    赠送的积分   *   购买数量
                BigDecimal integralValue = orderItem.getGiftIntegration();
                log.info("打印订单数据：赠送积分"+orderItem.getGiftIntegration());
                log.info("打印订单数据：购买数量"+orderItem.getProductQuantity());
				//判断用户是否拥有会员卡
				BigDecimal integralMultiples = memberCardIntegralPrivilegeInfo(orderVo.getUserId());
				if (integralMultiples.compareTo(BigDecimal.ONE) == 1) {
					//拥有会员权益 积分加倍时 新增字段 会员权益
					integralDetail
							.setDetailName(integralMultiples.doubleValue() + IntegralConstant.INTEGRAL_PRIVILEGE_NAME);
				}
				integralDetail.setIntegral(integralValue);
				BigDecimal bigDecimal = integralDetailMapper
						.selectNowIntegral(integralDetail.getShopId(), orderVo.getUserId());
				bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
				integralDetail.setNowTheIntegral(bigDecimal.add(integralValue));
				int insert = integralDetailMapper.insert(integralDetail);
				if (insert < 1) {
					throw new ServiceException("积分明细插入失败", SystemCode.DATA_ADD_FAILED.getCode());
				}
				//通知用户
				sendIntegralMessage(integralValue, orderVo.getUserId(), IntegralOperationTypeEnum.INCREASE.getStatus());
			}
		}
	}


	/**
	 * 获取需要清空的积分 (根据积分清空时间计算 获取积分时间小于createTime的积分明细数据)
	 *
	 * @param shopId 商铺id
	 * @param createTime 积分清空时间 (对积分获取时间小于该时间的积分进行清空)
	 * @return List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> getNeedClearIntegrals(String shopId, String createTime) {
		return integralDetailMapper.selectNeedClearIntegrals(shopId, createTime);
	}

	/**
	 * 获取需要清空的用户以及积分值
	 *
	 * @param shopId 商品id
	 * @return
	 */
	@Override
	public List<Map<String, Object>> selectAllUser(String shopId) {
		return integralDetailMapper.getAllUser(shopId);
	}

	/**
	 * 生成新的积分明细 并通知用户扣积分
	 *
	 * @param userId 用户id
	 * @param integral 扣除积分值
	 * @param shopId 商品id
	 */
	@Override
	public void generateNowIntegralDetailService(String userId, BigDecimal integral, String shopId) {
		//生成积分明细
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setClear(true);
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.EMPTY.getStatus());
		integralDetail.setNowTheIntegral(BigDecimal.ZERO);
		integralDetail.setShopId(shopId);
		integralDetail.setUserId(userId);
		integralDetail.setIntegral(integral.negate());
		int insert = integralDetailMapper.insert(integralDetail);
		if (insert < 1) {
			throw new ServiceException("积分清空明细插入失败", SystemCode.DATA_ADD_FAILED.getCode());
		}
		//2.1通知用户扣积分
		sendIntegralMessage(integral, userId, IntegralOperationTypeEnum.REDUCE.getStatus());
	}

	/**
	 * 修改这个商铺中所有的积分明细信息为 已清空状态
	 *
	 * @param shopId 商铺id
	 */
	@Override
	public void updateAll(String shopId) {
		integralDetailMapper.updateAllStatus(shopId);
	}

	/**
	 * 获取积分明细 (pc端)
	 * @param integralDetailDto 查询条件
	 * @return 符合条件的数据
	 */
	@Override
	public IPage<IntegralDetailVo> getIntegralDetail(IntegralDetailDto integralDetailDto) {
		Page<IntegralDetailVo> page = new Page<>(integralDetailDto.getCurrent(), integralDetailDto.getSize());
		return page.setRecords(this.baseMapper.selectIntegralDetail(page, integralDetailDto));
	}

	/**
	 * 创建订单失败  归还用户积分
	 * @param orderFailMessage
	 */
	@Override
	public void createOrderFail(OrderFailMessage orderFailMessage) {
		//生成积分明细
		if (orderFailMessage.getIntegration().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		IntegralDetail integralDetail = new IntegralDetail();
		integralDetail.setShopId(orderFailMessage.getShopId());
		integralDetail.setUserId(orderFailMessage.getUserId());
		integralDetail.setIntegral(orderFailMessage.getIntegration());
		integralDetail.setGainIntegralType(IntegralGainTypeEnum.GIVE_BACK.getStatus());
		BigDecimal bigDecimal = integralDetailMapper
				.selectNowIntegral(integralDetail.getShopId(), orderFailMessage.getUserId());
		bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
		integralDetail.setNowTheIntegral(bigDecimal.add(orderFailMessage.getIntegration()));
		int insert = integralDetailMapper.insert(integralDetail);
		if (insert < 1) {
			throw new ServiceException("积分归还明细插入失败", SystemCode.DATA_ADD_FAILED.getCode());
		}
		//通知用户归还积分
		sendIntegralMessage(orderFailMessage.getIntegration(), orderFailMessage.getUserId(), IntegralOperationTypeEnum.INCREASE.getStatus());
	}

    /**
     * 使用积分兑换会员卡
     * @param userId 用户id
     * @param integralValue 所需积分值
     * @return 状态码
     */
	@Override
	public Result<Integer> useIntegralConversionClubCard(String userId, BigDecimal integralValue) {
		String shopId = ISecurity.userMust().getShopId();
		AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, Arrays.asList(2));
		//得到用户总积分值
		BigDecimal integral = accountInfoDto.getMiniAccountExtends().getIntegral();
			if (integral.compareTo(integralValue) > -1) {
				IntegralDetail integralDetail = new IntegralDetail();
				integralDetail.setIntegral(integralValue.negate());
				integralDetail.setGainIntegralType(IntegralGainTypeEnum.CONVERSION_CLUB_CARD.getStatus());
				integralDetail.setUserId(userId);
				BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
				bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
				integralDetail.setNowTheIntegral(bigDecimal.add(integralValue.negate()));
				int insert = integralDetailMapper.insert(integralDetail);
				if (insert < 1) {
					throw new ServiceException("积分明細生成失败", SystemCode.DATA_ADD_FAILED.getCode());
				}
				//通知用户扣除积分
				sendIntegralMessage(integralValue, userId, IntegralOperationTypeEnum.REDUCE.getStatus());
				return Result.ok(SystemCode.SUCCESS.getCode());
			}
			return Result.failed(SystemCode.FAILURE.getCode(), "积分不足");
	}

	/**
	 *  获取当前用户的会员卡积分权益信息
	 *
	 * @param userId 用户id
	 * @return 返回会员积分权益倍数
	 */
	protected BigDecimal memberCardIntegralPrivilegeInfo(String userId) {
		//查看用户会员信息
		MemberCardInfoCenterDto memberInfo = remoteMiniAccountService.getMemberInfo(userId);
		log.info(userId);
		//判断该用户是否是会员 即为是否存在会员卡
		if (ObjectUtil.isNotNull(memberInfo)) {
			List<MemberCardPrivilege> memberCardPrivileges = memberInfo.getMemberCardPrivileges();
			//是否存在会员权益
			if (CollUtil.isNotEmpty(memberInfo.getMemberCardPrivileges())) {
				for (MemberCardPrivilege memberCardPrivilege : memberCardPrivileges) {
					if (IntegralConstant.INTEGRAL_PRIVILEGE_TYPE.equals(memberCardPrivilege.getPType())
							& memberCardPrivilege.getOpen() == 1) {
						log.info("拥有会员权益");
						return memberCardPrivilege.getDefValue();
					}
				}
			}
		}
		return BigDecimal.ONE;
	}
}
