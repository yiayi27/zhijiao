package com.medusa.gruul.shops.controller.remote;


import com.medusa.gruul.shops.api.model.AccountCenterVo;
import com.medusa.gruul.shops.service.IAccountCenterService;
import com.medusa.gruul.shops.service.ShopsPartnerService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageAssService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * @author whh
 */
@RestController(value = "remoteController")
@Tag(name = "远程调用接口,仅限后端fegin调用-->RemoteShopsService")
public class RemoteController {


    @Autowired
    private IAccountCenterService accountCenterService;

    @Autowired
    private ShopsRenovationTemPageService shopsRenovationTemPageService;
    @Autowired
    private ShopsRenovationTemPageAssService shopsRenovationTemPageAssService;



    @GetMapping("/fegin/account-center")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取用户中心配置")
    public AccountCenterVo accountCenterSetting() {
        return accountCenterService.accountCenterSetting();
    }


    /**
     * 专区删除时匹配删除 t_shop_renovation_page info
     *
     * @param modelId 专区id
     * @return 删除结果
     */
    @DeleteMapping(value = "/del/shopRenovationPage")
    @Operation(summary = "专区删除匹配")
    @PreAuthorize("permitAll()")
    public Boolean delShopRenovationPage(@RequestParam(value = "modelId", required = true) @NotNull(message = "modelId不能为null") String modelId) {

        return shopsRenovationTemPageService.delShopRenovationPageByModelId(modelId);
    }

    /**
     * 修改专区名称
     *
     * @param shopId      店铺id
     * @param linkName    专区名称
     * @param newLinkName 专区现名称
     * @return
     */
    @GetMapping("/fegin/updateSpecialArea")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取用户中心配置")
    public boolean updateSpecialArea(@RequestParam(value = "shopId", required = true) String shopId,
                                     @RequestParam(value = "linkName", required = true) String linkName,
                                     @RequestParam(value = "newLinkName", required = true) String newLinkName) {
        return shopsRenovationTemPageAssService.updateSpecialArea(shopId, linkName, newLinkName);
    }

    /**
     * 生成店铺默认值  创建店铺时
     * @param shopId 店铺id
     */
    @RequestMapping(value = "/fegin/shopDefault", method = RequestMethod.GET)
    @PreAuthorize("permitAll()")
    @Operation(summary = "生成店铺默认值(个人中心)")
    void shopDefault(@RequestParam(value = "shopId", required = true) String shopId){
        accountCenterService.shopDefault(shopId);
    }

}
