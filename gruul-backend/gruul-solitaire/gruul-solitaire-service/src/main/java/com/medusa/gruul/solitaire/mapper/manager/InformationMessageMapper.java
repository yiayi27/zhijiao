package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationMessageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 资讯留言 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/05/26
 */
@Component
public interface InformationMessageMapper extends BaseMapper<InformationMessage> {

    /**
     * 获取资讯留言信息list
     *
     * @param page         分页对象
     * @param informationMessageParam 资讯留言信息查询参数
     * @return 资讯留言list信息
     */
    List<InformationMessageVo> queryList(IPage page, @Param("informationMessageParam") InformationMessageParam informationMessageParam);

}
