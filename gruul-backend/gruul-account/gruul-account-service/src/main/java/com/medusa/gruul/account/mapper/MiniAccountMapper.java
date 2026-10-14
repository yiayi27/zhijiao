package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.account.api.entity.MiniAccount;
import com.medusa.gruul.account.api.model.MiniAccountExtDto;
import com.medusa.gruul.account.model.dto.BlacklistUserDto;
import com.medusa.gruul.account.model.dto.UserListDto;
import com.medusa.gruul.account.model.param.ApiUserDistributionParam;
import com.medusa.gruul.account.model.vo.FirstDistributionVo;
import com.medusa.gruul.account.model.vo.UserDistributionDetailVo;
import com.medusa.gruul.account.model.vo.UserDistributionVo;
import com.medusa.gruul.common.data.annotation.EscapeShop;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 小程序用户表 Mapper 接口
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
public interface MiniAccountMapper extends BaseMapper<MiniAccount> {

    /**
     * PC端获取用户列表
     *
     * @param userListDtoPage 分页数据
     * @param paramMap        查询条件参数
     * @return com.medusa.gruul.account.model.dto.UserListDto
     */
    IPage<UserListDto> selectByUserList(Page<UserListDto> userListDtoPage, @Param("paramMap") Map<String, Object> paramMap);

    /**
     * 分頁查詢會員信息
     * @param page 分頁對象
     * @param param 查詢條件
     * @return 會員信息列表
     */
    IPage<UserListDto> selectMemberInfo(IPage<UserListDto> page, @Param("param") Map<String, Object> param);

    /**
     * 获取黑名单列表
     *
     * @param blacklistUserDtoPage 分页数据
     * @param paramMap             查询条件参数
     * @return com.medusa.gruul.account.model.dto.BlacklistUserDto
     */
    IPage<BlacklistUserDto> selectByBlackListUser(Page<BlacklistUserDto> blacklistUserDtoPage, @Param("paramMap") Map<String, Object> paramMap);

    /**
     * 根据用户店铺id查询用户基本信息
     *
     * @param shopUserIds 用户店铺id
     * @return
     */
    List<MiniAccountExtDto> selectByShopUserIds(@Param("shopUserIds") List<String> shopUserIds);


    /**
     * 根据店铺用户获取会员信息
     * @param userId 店铺用户id
     * @return 账户信息
     *
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    MiniAccount getMemberInfo(@Param("userId") String userId);

    /**
     * 获取指定用户的积分值
     *
     * @param shopUserId 店铺用户id
     * @param shopId 店铺id
     * @return 积分值
     */
    BigDecimal getIntegralByShopUserId(@Param("shopUserId") String shopUserId, @Param("shopId") String shopId);

    /**
     * 根据用户更新用户余额
     *
     * @param userId 店铺用户id
     * @param amount 更新余额
     * @return 0 or 1
     */
    int updateBonus(@Param("userId") String userId, @Param("amount") BigDecimal amount);

    /**
     * 根据用户更新用户返利余额
     *
     * @param userId 店铺用户id
     * @param amount 余额
     * @return 0 or 1
     */
    int updateRebateBonus(@Param("userId") String userId, @Param("amount") BigDecimal amount);

    /**
     * 根据店铺用户id查询用户所在店铺唯一id
     *
     * @param shopUserId 店铺用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    MiniAccount selectByShopUserId(@Param("shopUserId") String shopUserId);

    /**
     * 根据店铺用户id查询用户分销首页信息
     *
     * @param shopUserId 店铺用户id
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    @EscapeShop
    UserDistributionVo selectUserDistributionIndex(@Param("shopUserId") String shopUserId,@Param("shopId")String shopId);

    /**
     * 获取分销明细信息list
     *
     * @param page
     * @param apiUserDistributionParam 分销信息查询参数
     * @return 分销list信息
     */
    List<UserDistributionDetailVo> selectUserDistributionList(IPage page, @Param("apiUserDistributionParam") ApiUserDistributionParam apiUserDistributionParam);

    /**
     * 更新用户信息
     *
     * @param miniAccount 用户信息列表
     * @return 0 or 1
     */
    int updateMulitClientAccountById(@Param("miniAccount") MiniAccount miniAccount);

    /**
     * 根据用户id查询用户信息
     *
     * @param firstUserId 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccount
     */
    FirstDistributionVo selectFirstNickNameByFirstUserId(@Param("firstUserId") String firstUserId);
}
