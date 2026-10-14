package com.medusa.gruul.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.platform.api.entity.PlatformShopInfo;
import com.medusa.gruul.platform.api.model.dto.ShopConfigDto;
import com.medusa.gruul.platform.api.model.dto.ShopInfoDto;
import com.medusa.gruul.platform.model.dto.ConsoleUpdateDto;
import com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto;
import com.medusa.gruul.platform.model.vo.ShopInfoVo;

/**
 * <p>
 * 店铺信息表 服务类
 * </p>
 *
 * @author whh
 * @since 2024-03-06
 */
public interface IPlatformShopInfoService extends IService<PlatformShopInfo> {


    /**
     * 商家控制台创建店铺
     *
     * @param dto com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto
     * @return 平台店铺id
     */
    String consoleCreate(ShopInfoConsoleCreateDto dto);

    /**
     * 控制台查询所属用户的所有店铺
     *
     * @param page 分页对象
     * @return com.medusa.gruul.platform.model.vo.ShopViewListVo
     */
    PageUtils<PlatformShopInfo> consoleList(Page<PlatformShopInfo> page);

    /**
     * 商家自己删除店铺
     *
     * @param shopId 店铺id
     */
    void consoleDel(String shopId);

    /**
     * 店铺设置
     *
     * @param dto    com.medusa.gruul.platform.model.dto.ConsoleUpdateDto
     * @param shopId 店铺id
     */
    void consoleUpdate(ConsoleUpdateDto dto, String shopId);

    /**
     * 校验指定店铺是否存在
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.entity.PlatformShopInfo
     */
    PlatformShopInfo checkCurrentShopId(String shopId);

    /**
     * 获取店铺基本信息
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.model.vo.ShopInfoVo
     */
    ShopInfoVo shopInfo(String shopId);


    /**
     * 获取店铺配置信息
     * 远程调用接口使用
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    ShopConfigDto getShopConfig(String shopId);

    /**
     * 重新生成店所有默认值
     *
     * @param shopId 店铺id
     */
    void defaultRepair(String shopId);

    /**
     * 获取指定店铺店铺信息
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.model.dto.ShopInfoDto
     */
    Result<ShopInfoDto> getShopInfo(String shopId);

    /**
     * 套餐过期
     *
     * @param platformInfoId 平台id
     */
    void packageDueReceive(Integer platformInfoId);

    /**
     * 获取店铺配置信息
     * 远程调用接口使用
     *
     * @param appId 小程序appId
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    ShopConfigDto getShopConfigAndAppId(String appId);
}
