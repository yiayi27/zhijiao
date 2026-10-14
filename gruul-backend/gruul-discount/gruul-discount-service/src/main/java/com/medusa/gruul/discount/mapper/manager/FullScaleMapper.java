package com.medusa.gruul.discount.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.medusa.gruul.discount.api.model.param.manager.FullScaleParam;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 满减活动 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface FullScaleMapper extends BaseMapper<FullScale> {

    /**
     * 获取满减活动信息list
     *
     * @param page           分页对象
     * @param fullScaleParam 满减活动信息查询参数
     * @return 满减活动list信息
     */
    List<FullScaleVo> queryFullScaleList(IPage page, @Param("fullScaleParam") FullScaleParam fullScaleParam);

    /**
     * 获取满减活动详细信息
     *
     * @param id
     * @return 单条满减活动详细信息
     */
    FullScaleVo queryFullScaleById(@Param("id") Long id);

    /**
     * 查询可使用满减活动
     *
     * @return List<FullScaleVo>
     */
    List<FullScaleVo> queryGoingFullScaleList();

    /**
     * 定时任务查询未开始与进行中的满减活动
     *
     * @return List<FullScaleVo>
     */
    List<FullScaleVo> queryFullScaleListForTask();

    /**
     * 定时任务更新满减活动
     *
     * @param fullScaleVo
     */
    void updateFullScaleForTask(FullScaleVo fullScaleVo);

}
