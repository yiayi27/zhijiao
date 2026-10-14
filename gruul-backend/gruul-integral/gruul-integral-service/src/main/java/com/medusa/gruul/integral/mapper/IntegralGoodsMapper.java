package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.integral.api.entity.IntegralGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.web.model.param.api.ApiIntegralGoodsParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分商品表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralGoodsMapper extends BaseMapper<IntegralGoods> {

	/**
	 * 获取单个积分商品信息
	 *
	 * @param id 积分商品id
	 * @return
	 */
	IntegralGoodVo getIntegralGoodsInfo(@Param("id") Long id);


	/**
	 * 根据积分商品id 对这个商品进行上下架
	 * @param id        积分商品id
	 * @param status    积分商品上下架状态
	 * @return
	 */
	Boolean updateIntegralGoodsStatus(@Param("id") Long id, @Param("status") Integer status);

	/**
	 * 查询积分商品信息列表
	 * @param objectPage    分页对象
	 * @param integralGoodsParam    积分商品信息查询参数
	 * @return 积分商品List对象
	 */
	List<IntegralGoodVo> selectIntegralGoodsList(@Param("objectPage") Page<IntegralGoodVo> objectPage,
			@Param("integralGoodsParam") IntegralGoodsParam integralGoodsParam);

    //	----------------------------------小程序端功能mapper接口------------------------------------------------
	//  ----------------------------------小程序端功能mapper接口------------------------------------------------


	/**
	 * 查询积分商品信息列表  小程序端
	 * @param page
	 * @param apiIntegralGoodsParam 查询参数
	 * @return 积分商品List对象
     *
	 */
	List<IntegralGoodVo> selectApiIntegralGoodsList(
			IPage page, @Param("apiIntegralGoodsParam") ApiIntegralGoodsParam apiIntegralGoodsParam);
}
