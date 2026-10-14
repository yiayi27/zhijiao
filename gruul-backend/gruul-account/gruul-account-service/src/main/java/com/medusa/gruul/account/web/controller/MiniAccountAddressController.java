package com.medusa.gruul.account.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.account.model.dto.AddressCraeteDto;
import com.medusa.gruul.account.model.dto.AddressUpdateDto;
import com.medusa.gruul.account.model.vo.AccountAddressListVo;
import com.medusa.gruul.account.model.vo.AnalysisLatitudeAndlongitudeVo;
import com.medusa.gruul.account.model.vo.LatitudeAndlongitudeVo;
import com.medusa.gruul.account.service.IMiniAccountAddressService;
import com.medusa.gruul.account.util.GeoCodeUtil;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.AreaUtil;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.dto.AreaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 会员地址表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Slf4j
@RestController
@RequestMapping("/mini-account-address")
@Tag(name = "用户地址管理接口")
public class MiniAccountAddressController {

    @Autowired
    private IMiniAccountAddressService miniAccountAddressService;
    @Autowired
    private GeoCodeUtil geoCodeUtil;

    /**
     * 获取用户地址
     *
     * @param type 1-获取所有 2-获取默认地址,默认type=1
     * @return Result<List<AccountAddressListVo>>
     */
    @GetMapping
    @Operation(summary = "获取用户地址")
    public Result<List<AccountAddressListVo>> addersGet(@Parameter(description = "type 1-获取所有 2-获取默认地址,默认type=1") @RequestParam(defaultValue = "1") @NotNull(message = "错误参数") Integer type) {
        List<AccountAddressListVo> vos = miniAccountAddressService.addersGet(type);
        return Result.ok(vos);
    }

    /**
     * 获取指定用户地址
     *
     * @param addersVO 用户地址信息
     * @return Result<Void>
     */
    @PostMapping
    @Operation(summary = "添加用户地址")
    public Result<Void> addressCreate(@RequestBody @Validated AddressCraeteDto addersVO) {
        miniAccountAddressService.addressCreate(addersVO);
        return Result.ok();
    }

    /**
     * 获取指定用户地址
     *
     * @param updateDto 用户地址信息
     * @return Result<Void>
     */
    @PutMapping
    @Operation(summary = "更新用户地址|设置为默认")
    public Result<Void> addersUpdate(@RequestBody @Validated AddressUpdateDto updateDto) {
        miniAccountAddressService.addersUpdate(updateDto);
        return Result.ok();
    }

    /**
     * 获取指定用户地址
     *
     * @param adderssId 地址id
     * @return Result<Void>
     */
    @DeleteMapping("{adderssId}")
    @Operation(summary = "删除用户地址")
    public Result<Void> addressDelete(@Parameter(name = "地址id") @PathVariable(value = "adderssId") @NotNull(message = "错误参数") Integer adderssId) {
        miniAccountAddressService.addressDelete(adderssId);
        return Result.ok();
    }

    /**
     * 获取指定用户地址
     *
     * @param type 获取类型 1-省 2-市 3-区
     * @param id 父级id
     * @return Result<List<AreaDto>>
     */
    @GetMapping("/area")
    @Operation(summary = "获取省/市/区")
    public Result<List<AreaDto>> getArea(@Parameter(description = "获取类型 1-省 2-市 3-区") @RequestParam @NotNull(message = "错误参数") Integer type,
                                         @Parameter(description = "父级id") @RequestParam(required = false) String id) {
        List<AreaDto> vo;
        switch (type) {
            case 1:
                vo = AreaUtil.getProvinceList(id);
                break;
            case 2:
                vo = AreaUtil.getCityList(id);
                break;
            case 3:
                vo = AreaUtil.getDistrictList(id);
                break;
            default:
                return Result.failed("类型错误");
        }

        return Result.ok(vo);
    }

    /**
     * 获取指定地理位置经纬度
     *
     * @param site 经纬度
     * @return Result<LatitudeAndlongitudeVo>
     */
    @GetMapping("/latitude-longitude")
    @Operation(summary = "获取指定地理位置经纬度")
    public Result<LatitudeAndlongitudeVo> getLatitudeAndlongitude(@Parameter(description = "详细地址") @RequestParam @NotNull(message = "地理位置为空") String site) {
        String addressEncoded = geoCodeUtil.getAddressEncoded(site, Boolean.FALSE);
        log.debug("addressEncoded:{}",addressEncoded);
        JSONObject jsonObject = new JSONObject(addressEncoded);
        JSONArray geocodes = jsonObject.getJSONArray("geocodes");
        if (CollUtil.isEmpty(geocodes)) {
            throw new ServiceException("地址错误");
        }
        String location = geocodes.getJSONObject(0).getStr("location");
        LatitudeAndlongitudeVo vo = new LatitudeAndlongitudeVo();
        vo.setLocation(location);
        vo.setAndlongitudeVo(this.analysisLatitudeAndlongitude(location).getData());
        return Result.ok(vo);
    }

    /**
     * 小程序地址管理在使用
     * 控制台用户注册时省市区待使用
     *
     * @param site 经纬度
     * @return Result<AnalysisLatitudeAndlongitudeVo>
     */
    @GetMapping("/analysis/latitude-longitude")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据经纬度返回省市区")
    public Result<AnalysisLatitudeAndlongitudeVo> analysisLatitudeAndlongitude(@Parameter(description = "经纬度") @RequestParam @NotNull(message = "经纬度为空") String site) {
        String addressRegeo = geoCodeUtil.getAddressRegeo(site, Boolean.FALSE);
        JSONObject jsonObject = new JSONObject(addressRegeo);
        Integer status = jsonObject.getInt("status");
        if (status != 1) {
            throw new ServiceException("数据异常");
        }
        JSONObject addressComponent = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent");
        AreaDto province = AreaUtil.getByLabel(0, addressComponent.getStr("province"));
        AreaDto city = AreaUtil.getByLabel(1, addressComponent.getStr("city"));
        AreaDto district = AreaUtil.getByValue(2, addressComponent.getStr("adcode"));
        String adcode = addressComponent.getStr("adcode");
        AnalysisLatitudeAndlongitudeVo vo = new AnalysisLatitudeAndlongitudeVo();
        vo.setProvince(province);
        vo.setCity(city);
        vo.setDistrict(district);
        vo.setAdcode(adcode);
        return Result.ok(vo);
    }


}
