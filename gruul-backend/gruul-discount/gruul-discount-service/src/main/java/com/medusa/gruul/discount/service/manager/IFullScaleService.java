package com.medusa.gruul.discount.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.discount.api.entity.FullScale;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.discount.api.model.dto.manager.FullScaleDto;
import com.medusa.gruul.discount.api.model.param.manager.FullScaleParam;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleSimpleVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 满减活动 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface IFullScaleService extends IService<FullScale> {

    /**
     * 查询列表
     *
     * @param fullScaleParam 查询参数
     * @return 分页对象
     */
    IPage<FullScaleVo> getFullScaleList(FullScaleParam fullScaleParam);

    /**
     * 查询单张满减活动信息
     *
     * @param id 满减活动id
     * @return FullScaleSimpleVo 满减活动信息
     */
    FullScaleSimpleVo getSimpleFullScale(Long id);

    /**
     * 查询单张满减活动信息接口
     *
     * @param id 满减活动id
     * @return FullScaleVo
     */
    FullScaleVo getFullScaleById(Long id);

    /**
     * 满减活动新增
     *
     * @param fullScaleDto 满减活动信息
     */
    void addFullScale(FullScaleDto fullScaleDto);

    /**
     * 满减活动修改
     *
     * @param fullScaleDto 满减活动信息
     */
    void updateFullScale(FullScaleDto fullScaleDto);

    /**
     * 关闭/启用满减活动
     *
     * @param ids  满减活动ids
     * @param status 满减活动状态值
     */
    void changeFullScaleStatus(Long[] ids, Integer status);

    /**
     * 满减活动删除
     *
     * @param ids 满减活动ids
     */
    void deleteFullScaleList(Long[] ids);

    /**
     * 查询客户可使用满减活动
     *
     * @param productIds 商品ids
     * @return List<FullScaleVo>
     */
    List<FullScaleVo> findFullScaleListForOrder(Collection<Long> productIds);

    /**
     * 定时任务查询可未开始与进行中的满减活动
     *
     * @return List<FullScaleVo>
     */
    List<FullScaleVo> findFullScaleListForTask();

    /**
     * 定时任务查更新满减活动状态
     *
     * @param fullScaleVo 满减活动信息
     */
    void updateFullScaleForTask(FullScaleVo fullScaleVo);

    /**
     * 查询单个商品可使用满减信息
     *
     * @param productId
     * @return List<FullScaleVo>
     */
    List<FullScaleVo> findFullScaleListForProduct(Long productId);
}
