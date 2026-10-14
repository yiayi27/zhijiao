package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.SaleMode;
import com.medusa.gruul.goods.api.model.dto.manager.SaleModeDto;
import com.medusa.gruul.goods.api.model.param.manager.SaleModeParam;
import com.medusa.gruul.goods.api.model.vo.manager.SaleModeVo;

import java.util.List;

/**
 * <p>
 * 商品自定义专区 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024-10-26
 */
public interface ISaleModeService extends IService<SaleMode> {

    /**
     * 查询专区列表
     *
     * @param saleModeParam 专区查询参数
     * @return 所有SaleModeVo对象
     */
    IPage<SaleModeVo> getSaleModeList(SaleModeParam saleModeParam);

    /**
     * 查询所有专区列表
     *
     * @return 所有SaleModeVo对象
     */
    List<SaleModeVo> getAllSaleModeList();

    /**
     * 新增专区
     *
     * @param saleModeDtos 专区信息
     */
    void addSaleMode(List<SaleModeDto> saleModeDtos);

    /**
     * 修改专区
     *
     * @param saleModeDto 专区信息
     */
    void updateSaleMode(SaleModeDto saleModeDto);

    /**
     * 删除专区
     *
     * @param id 专区id
     */
    void deleteSaleMode(Long id);

    /**
     * 修改专区排序
     *
     * @param saleModeDtos 专区信息
     */
    void updateSaleModeSort(List<SaleModeDto> saleModeDtos);
}
