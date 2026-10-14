package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.InformationInfo;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationInfoParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 资讯信息 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
@Component
public interface InformationInfoMapper extends BaseMapper<InformationInfo> {

    /**
     * 获取资讯信息list
     *
     * @param page         分页对象
     * @param informationInfoParam 资讯信息查询参数
     * @return 资讯list信息
     */
    List<InformationInfoVo> queryList(IPage page, @Param("informationInfoParam") InformationInfoParam informationInfoParam);

    /**
     * 获取资讯详细信息
     *
     * @param id
     * @return 单个资讯详细信息
     */
    InformationInfoVo queryInfoById(@Param("id") Long id);
}
