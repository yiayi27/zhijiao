package com.medusa.gruul.discount.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.medusa.gruul.discount.api.model.vo.api.ApiFullScaleVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 小程序满减活动 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface ApiFullScaleMapper extends BaseMapper<FullScale> {

    /**
     *  获取满减活动信息list
     *
     * @return 满减活动list信息
     */
    List<ApiFullScaleVo> queryApiFullScaleList();

}
