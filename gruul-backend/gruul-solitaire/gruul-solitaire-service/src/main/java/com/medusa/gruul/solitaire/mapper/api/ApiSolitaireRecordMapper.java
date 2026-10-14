package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.solitaire.api.entity.SolitaireRecord;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireRecordVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 接龙活动记录 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
@Component
public interface ApiSolitaireRecordMapper extends BaseMapper<SolitaireRecord> {

    /**
     * 获取接龙活动的购买记录
     *
     * @param activityId
     * @return List<ApiSolitaireRecordVo>
     */
    List<ApiSolitaireRecordVo> getSolitaireRecord(@Param("activityId") Long activityId);

}
