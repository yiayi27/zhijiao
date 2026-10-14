package com.medusa.gruul.shops.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.data.annotation.EscapeShop;
import com.medusa.gruul.shops.api.entity.AccountCenter;
import com.medusa.gruul.shops.api.entity.AccountCenterMenu;
import com.medusa.gruul.shops.api.model.AccountCenterMenuDto;
import com.medusa.gruul.shops.api.model.AccountCenterSettingDto;
import com.medusa.gruul.shops.api.model.AccountCenterVo;
import com.medusa.gruul.shops.api.model.MenuVo;
import com.medusa.gruul.shops.mapper.AccountCenterMapper;
import com.medusa.gruul.shops.service.IAccountCenterMenuService;
import com.medusa.gruul.shops.service.IAccountCenterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户中心配置 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
public class AccountCenterServiceImpl extends ServiceImpl<AccountCenterMapper, AccountCenter> implements IAccountCenterService {

    @Autowired
    private IAccountCenterMenuService accountCenterMenuService;

    /**
     * 创建菜单
     *
     * @param accountCenterSettingDto com.medusa.gruul.account.api.model.AccountCenterSettingDto
     */
    @EscapeShop
    private void createMenu(AccountCenterSettingDto accountCenterSettingDto) {
        //清空原本菜单
        accountCenterMenuService.clear();
        List<AccountCenterMenuDto> menu = accountCenterSettingDto.getMenuVos();
        //拼团用户信息菜单全部插入
        for (AccountCenterMenuDto accountCenterMenuDto : menu) {
            AccountCenterMenu accountCenterMenu = getAccountCenterMenu(accountCenterMenuDto, 0L);
            accountCenterMenuService.save(accountCenterMenu);
            List<AccountCenterMenuDto> subMenu = accountCenterMenuDto.getSubMenu();
            if (CollectionUtil.isNotEmpty(subMenu)) {
                //子菜单最多1层
                for (AccountCenterMenuDto centerMenuDto : subMenu) {
                    AccountCenterMenu sub = getAccountCenterMenu(centerMenuDto, accountCenterMenu.getId());
                    accountCenterMenuService.save(sub);
                }
            }
        }
    }

    /**
     * 获取用户中心配置
     * @return com.medusa.gruul.account.model.vo.AccountCenterVo
     */
    @Override
    public AccountCenterVo accountCenterSetting() {
        AccountCenter accountCenter = this.getByShopId();
        AccountCenterVo accountCenterVo = new AccountCenterVo();
        if (accountCenter != null){
            BeanUtils.copyProperties(accountCenter, accountCenterVo);
            List<MenuVo> menuVos = accountCenterMenuService.getInfo();
            accountCenterVo.setMenuVos(menuVos);
        }
        return accountCenterVo;
    }

    /**
     * 修改用户中心
     * @param dto  com.medusa.gruul.account.api.model.AccountCenterSettingDto
     */
    @Override
    public synchronized void accountCenterSettingMotify(AccountCenterSettingDto dto) {
        if (dto.getId() == null) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        AccountCenter accountCenter = this.baseMapper.selectOne(new LambdaQueryWrapper<AccountCenter>().eq(AccountCenter::getId, dto.getId()));
        if (accountCenter == null) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(dto, accountCenter);
        this.baseMapper.updateById(accountCenter);
        createMenu(dto);
    }

    @Override
    public void shopDefault(String shopId) {
        AccountCenter accountCenter = new AccountCenter();
        accountCenter.setCustomStyle("{\"backgroundImage\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/53684d812d8e446fa688976d079f1fc0.jpg\",\"cardColor\":\"#89bf04\",\"textColor\":\"#E7C898\"}");
        accountCenter.setShopId(shopId);
        accountCenter.setMenuStyle(2);
        accountCenter.setGetCartText("领卡文案");
        accountCenter.setHeadStyle(2);
        accountCenter.setOrderInfo("{\"afterSaleIcon\":{\"name\":\"售后\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/ca9c9c65daeb4b3eb8afbfba8686998d.png\"},\"waitIcon\":{\"name\":\"待付款\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/c3dfb900d9b14730939a7b5e206644bc.png\"},\"waitDelivered\":{\"name\":\"待发货\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/file-read-12014.png\"},\"waitPickingIcon\":{\"name\":\"待提货\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/9939df2bfa5f46ceac080d33ba40aaba.png\"},\"deliveryIcon\":{\"name\":\"配送中\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/9435ecaa9f304b1fa5555e8be01367ad.png\"},\"evaluateIcon\":{\"name\":\"评价中心\",\"url\":\"https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/26354a7bdab144c98f46844c06e18e3f.png\"}}");
        accountCenter.setHideCartInlet(Boolean.FALSE);
        this.save(accountCenter);
    }

    /**
     * 创建菜单对象
     *
     * @param accountCenterMenuDto com.medusa.gruul.account.api.model.AccountCenterMenuDto
     * @param pId                  父级id
     * @return com.medusa.gruul.account.api.entity.AccountCenterMenu
     */
    private AccountCenterMenu getAccountCenterMenu(AccountCenterMenuDto accountCenterMenuDto, Long pId) {
        AccountCenterMenu accountCenterMenu = new AccountCenterMenu();
        accountCenterMenu.setMenuName(accountCenterMenuDto.getMenuName());
        accountCenterMenu.setMenuIconUrl(accountCenterMenuDto.getMenuIconUrl());
        accountCenterMenu.setDefaultIcon(accountCenterMenuDto.getDefaultIcon());
        accountCenterMenu.setStyleType(accountCenterMenuDto.getStyleType());
        accountCenterMenu.setSortIndex(accountCenterMenuDto.getSortIndex());
        accountCenterMenu.setPId(pId);
        accountCenterMenu.setHideMenu(accountCenterMenuDto.getHideMenu());
        accountCenterMenu.setSplitFlag(accountCenterMenuDto.getSplitFlag());
        accountCenterMenu.setAllowUse(accountCenterMenuDto.getAllowUse());
        accountCenterMenu.setLinkSelectItem(accountCenterMenuDto.getLinkSelectItem());
        return accountCenterMenu;
    }

    /**
     * 根据店铺id 获取用户中心配置
     *
     * @return com.medusa.gruul.account.api.entity.AccountCenter
     */
    private AccountCenter getByShopId() {
        return this.baseMapper.selectOne(null);
    }
}
