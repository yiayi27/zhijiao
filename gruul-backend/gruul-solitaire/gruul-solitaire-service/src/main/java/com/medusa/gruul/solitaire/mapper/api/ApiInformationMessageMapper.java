package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationMessageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 小程序资讯留言 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
@Component
public interface ApiInformationMessageMapper extends BaseMapper<InformationMessage> {

    /**
     * 获取资讯留言信息list
     *
     * @param page         分页对象
     * @param apiInformationMessageParam 资讯留言信息查询参数
     * @return 资讯留言list信息
     */
    List<ApiInformationMessageVo> queryMessageList(IPage page, @Param("apiInformationMessageParam") ApiInformationMessageParam apiInformationMessageParam);

    /**
     * 获取当前用户的资讯留言信息
     *
     * @param apiInformationMessageParam 资讯留言信息查询参数
     * @return ApiInformationMessageVo
     */
    ApiInformationMessageVo queryMessage(@Param("apiInformationMessageParam") ApiInformationMessageParam apiInformationMessageParam);
}
