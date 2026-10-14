package com.medusa.gruul.discount.service.api.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.medusa.gruul.discount.api.model.vo.api.ApiFullScaleVo;
import com.medusa.gruul.discount.mapper.api.ApiFullScaleMapper;
import com.medusa.gruul.discount.service.api.IApiFullScaleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 满减活动 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class ApiFullScaleServiceImpl extends ServiceImpl<ApiFullScaleMapper, FullScale> implements IApiFullScaleService {

    /**
     * 获取满减活动分页信息
     *
     * @return 满减活动list对象
     */
    @Override
    public List<ApiFullScaleVo> getApiFullScaleList() {
        return this.baseMapper.queryApiFullScaleList();
    }
}
