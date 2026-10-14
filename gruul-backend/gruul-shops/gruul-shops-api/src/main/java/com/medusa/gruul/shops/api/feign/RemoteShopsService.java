package com.medusa.gruul.shops.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsPartner;
import com.medusa.gruul.shops.api.model.AccountCenterVo;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@FeignClient(value = "shops")
public interface RemoteShopsService {


    /**
     * 获取店铺 by shopId
     *
     * @return ShopsPartner
     */
    @RequestMapping(value = "/fegin/account-center", method = RequestMethod.GET)
    AccountCenterVo accountCenterSetting();

    /**
     * 专区删除时匹配删除 t_shop_renovation_page info
     *
     * @param modelId 专区id
     * @return 删除结果
     */
    @RequestMapping(value = "/del/shopRenovationPage", method = RequestMethod.DELETE)
    Boolean delShopRenovationPage(@RequestParam(value = "modelId", required = true) @NotNull(message = "modelId不能为null") String modelId);

    /**
     * 修改专修专区名称
     *
     * @param shopId      店铺id
     * @param linkName    专区原名称
     * @param newLinkName 专区现名称
     * @return
     */
    @RequestMapping(value = "/fegin/updateSpecialArea", method = RequestMethod.GET)
    boolean updateSpecialArea(@RequestParam(value = "shopId", required = true) String shopId,
                              @RequestParam(value = "linkName", required = true) String linkName,
                              @RequestParam(value = "newLinkName", required = true) String newLinkName);

    /**
     * 生成店铺默认值  创建店铺时
     * @param shopId 店铺id
     */
    @RequestMapping(value = "/fegin/shopDefault", method = RequestMethod.GET)
    void shopDefault(@RequestParam(value = "shopId", required = true) String shopId);
}
