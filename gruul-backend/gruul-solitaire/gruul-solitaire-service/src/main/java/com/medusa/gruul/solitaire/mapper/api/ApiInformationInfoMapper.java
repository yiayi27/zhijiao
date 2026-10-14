package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.InformationInfo;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationInfoParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 小程序资讯信息 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
@Component
public interface ApiInformationInfoMapper extends BaseMapper<InformationInfo> {

    /**
     * 获取资讯信息list
     *
     * @param page         分页对象
     * @param apiInformationInfoParam 资讯信息查询参数
     * @return 资讯list信息
     */
    List<ApiInformationInfoVo> queryList(IPage page, @Param("apiInformationInfoParam") ApiInformationInfoParam apiInformationInfoParam);

    /**
     * 获取资讯详细信息
     *
     * @param id
     * @return 单个资讯详细信息
     */
    ApiInformationInfoVo queryInfoById(@Param("id") Long id);
}
