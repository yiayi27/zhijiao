package com.medusa.gruul.goods.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.SaleMode;
import com.medusa.gruul.goods.api.model.param.manager.SaleModeParam;
import com.medusa.gruul.goods.api.model.vo.manager.SaleModeVo;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 商品自定义专区 Mapper 接口
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Repository
public interface ApiSaleModeMapper extends BaseMapper<SaleMode> {


    /**
     * 获取所有专区信息
     *
     * @param page 分页对象
     * @param saleModeParam 销售专区查询参数
     * @return 所有专区list信息
     */
    List<SaleModeVo> querySaleModeList(IPage page, @Param("saleModeParam") SaleModeParam saleModeParam);

    /**
     * 获取所有专区信息
     *
     * @return 所有专区list信息
     */
    List<SaleModeVo> queryAllSaleModeList();

    /**
     * 获取默认商品专区信息
     *
     * @return 所有默认商品专区list信息
     */
    List<SaleModeVo> queryDefaultSaleMode();

}
