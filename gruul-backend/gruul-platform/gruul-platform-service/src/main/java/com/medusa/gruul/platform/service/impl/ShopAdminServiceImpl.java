package com.medusa.gruul.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.constant.MeConstant;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminAddDTO;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminUpdateDTO;
import com.medusa.gruul.platform.model.enums.ShopAdminAvailable;
import com.medusa.gruul.platform.model.vo.AdminAccountVO;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.ShopAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2025/4/9
 */
@Service
@RequiredArgsConstructor
public class ShopAdminServiceImpl implements ShopAdminService {

    private final PasswordEncoder passwordEncoder;
    private final IAccountInfoService accountInfoService;

    /**
     * 分页查询店铺管理员
     *
     * @param param 分页参数
     * @return 分页查询结果
     */
    @Override
    public PageUtils<AdminAccountVO> page(Page<AccountInfo> param) {
        String mobile = ISecurity.userMust().getMobile();
        return new PageUtils<>(
                accountInfoService.lambdaQuery()
                        .select(AccountInfo::getRole, AccountInfo::getAdminNickname, AccountInfo::getMobile, AccountInfo::getPerms)
                        .eq(AccountInfo::getShopId, ISystem.shopId())
                        .isNotNull(AccountInfo::getRole)
                        .page(param)
                        .convert(
                                account -> new AdminAccountVO()
                                        .setNickname(account.getAdminNickname())
                                        .setMobile(account.getMobile())
                                        .setPerms(account.getPerms())
                                        .setEditable(Role.ADMIN != account.getRole() && !mobile.equals(account.getMobile()))
                        )
        );
    }

    /**
     * 查询该账号是否可绑定本店管理员
     *
     * @param mobile 手机号
     * @return 该账号是否可绑定本店管理员
     */
    @Override
    public ShopAdminAvailable available(String mobile) {
        String shopId = ISystem.shopId();
        Map<String, Role> shopRoleMap = new HashMap<>(CommonConstants.NUMBER_TEN);
        accountInfoService.lambdaQuery()
                .select(AccountInfo::getShopId, AccountInfo::getRole)
                .eq(AccountInfo::getMobile, mobile)
                .list()
                .forEach(
                        account -> shopRoleMap.put(account.getShopId(), account.getRole())
                );
        //如果这个手机号在系统中未注册过 则需要密码
        if (CollUtil.isEmpty(shopRoleMap)) {
            return ShopAdminAvailable.PASSWORD;
        }
        //注册过
        //没绑定过当前店铺
        if (!shopRoleMap.containsKey(shopId)) {
            return ShopAdminAvailable.NO_PASSWORD;
        }
        //绑定过当前店铺
        Role role = shopRoleMap.get(shopId);
        //角色为空表示注册过C端
        //不为空表示已经是当前店铺的管理员了
        return role == null ? ShopAdminAvailable.NO_PASSWORD : ShopAdminAvailable.BOUNDED;
    }

    /**
     * 添加管理员
     *
     * @param param 管理员信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = MeConstant.ACCOUNT_LOCK_PREFIX, key = "#param.mobile")
    public void add(ShopAdminAddDTO param) {
        //当前添加管理员的店铺id
        String shopId = ISystem.shopId();
        //当前添加的管理员在系统中拥有的账号对应的店铺id与角色
        String mobile = param.getMobile();
        Map<String, Role> shopRoleMap = new HashMap<>(CommonConstants.NUMBER_TEN);
        accountInfoService.lambdaQuery()
                .select(AccountInfo::getShopId, AccountInfo::getRole)
                .eq(AccountInfo::getMobile, mobile)
                .list()
                .forEach(
                        account -> shopRoleMap.put(account.getShopId(), account.getRole())
                );
        //1. 如果店铺id包含0 则说明该管理员是超管 超管无需添加
        if (shopRoleMap.containsKey(CommonConstants.DEFAULT_SHOP_ID)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        //2. 如果包含当前的店铺id
        if (shopRoleMap.containsKey(shopId)) {
            Role role = shopRoleMap.get(shopId);
            //店铺角色不为空 说明已经是管理员 不需要再次添加
            if (role != null) {
                throw SystemCode.PARAM_VALID_ERROR.exception();
            }
            //否则 说明原来已是店铺用户 直接设置为店铺管理员角色即可
            //此时 不能设置密码
            if (StrUtil.isNotEmpty(param.getPassword())) {
                throw SystemCode.PARAM_VALID_ERROR.exception();
            }
            accountInfoService.lambdaUpdate()
                    .set(AccountInfo::getAdminNickname, param.getNickname())
                    .set(AccountInfo::getRole, Role.SUB_ADMIN)
                    .set(AccountInfo::getPerms, Jackson.writeValueAsString(param.getPerms()))
                    .eq(AccountInfo::getShopId, shopId)
                    .eq(AccountInfo::getMobile, mobile)
                    .update();
            return;
        }
        AccountInfo newAccount;
        //3. 不包含当前店铺的 但是包含其他店铺的
        if (!shopRoleMap.isEmpty()) {
            //此时 不能设置密码
            if (StrUtil.isNotEmpty(param.getPassword())) {
                throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
            }
            AccountInfo account = accountInfoService.lambdaQuery()
                    .eq(AccountInfo::getMobile, mobile)
                    .last(CommonConstants.SQL_LIMIT_1)
                    .one();
            newAccount = account.copy(shopId, Role.SUB_ADMIN);
        }
        //4. 未在系统中注册过
        else {
            newAccount = new AccountInfo()
                    .setShopId(shopId)
                    .setMobile(mobile)
                    .setPassword(passwordEncoder.encode(param.getPassword()))
                    .setRole(Role.SUB_ADMIN);
        }
        //保存账户信息
        accountInfoService.saveShopAccount(
                newAccount.setAdminNickname(param.getNickname())
                        .setPerms(param.getPerms())

        );
    }

    /**
     * 修改管理员
     *
     * @param param 管理员信息
     */
    @Override
    public void update(ShopAdminUpdateDTO param) {
        String mobile = param.getMobile();
        String shopId = ISystem.shopId();
        //查询需要更新的管理员的用户id
        AccountInfo curAdmin = accountInfoService.lambdaQuery()
                .select(AccountInfo::getId, AccountInfo::getRole)
                .eq(AccountInfo::getMobile, mobile)
                .eq(AccountInfo::getShopId, shopId)
                .one();
        //如果当前管理员不存在 或者 当前管理员是自己 则不允许修改
        if (curAdmin == null || mobile.equals(ISecurity.userMust().getMobile()) || Role.ADMIN == curAdmin.getRole()) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        accountInfoService.lambdaUpdate()
                .set(AccountInfo::getAdminNickname, param.getNickname())
                .set(AccountInfo::getPerms, Jackson.writeValueAsString(param.getPerms()))
                .eq(AccountInfo::getMobile, mobile)
                .eq(AccountInfo::getShopId, shopId)
                .eq(AccountInfo::getRole, Role.SUB_ADMIN)
                .update();
        //刷新当前更新的管理员的资料
        ISecurity.refreshUsers(ClientType.SHOP, shopId, Set.of(curAdmin.getId()));
    }

    /**
     * 删除管理员
     *
     * @param mobile 管理员手机号
     */
    @Override
    public void delete(String mobile) {
        String shopId = ISystem.shopId();
        //不能删除自己 或 管理员
        if (
            //不能删除自己
                mobile.equals(ISecurity.userMust().getMobile())
                        //不能删除管理员
                        || accountInfoService.lambdaQuery()
                        .eq(AccountInfo::getMobile, mobile)
                        .eq(AccountInfo::getShopId, shopId)
                        .eq(AccountInfo::getRole, Role.ADMIN)
                        .exists()
        ) {
            throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
        }
        accountInfoService.lambdaUpdate()
                .set(AccountInfo::getRole, null)
                .set(AccountInfo::getPerms, null)
                .set(AccountInfo::getAdminNickname, null)
                .eq(AccountInfo::getRole, Role.SUB_ADMIN)
                .eq(AccountInfo::getMobile, mobile)
                .eq(AccountInfo::getShopId, shopId)
                .update();
    }
}
