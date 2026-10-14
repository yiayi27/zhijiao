package com.medusa.gruul.discount.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.medusa.gruul.discount.api.model.vo.api.ApiFullScaleVo;

import java.util.List;

/**
 * <p>
 * 小程序满减活动 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface IApiFullScaleService extends IService<FullScale> {

    /**
     * 查询列表
     *
     * @return List<ApiFullScaleVo>
     */
    List<ApiFullScaleVo> getApiFullScaleList();

}
