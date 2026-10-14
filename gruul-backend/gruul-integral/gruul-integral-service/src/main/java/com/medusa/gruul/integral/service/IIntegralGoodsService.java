package com.medusa.gruul.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.entity.IntegralGoods;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralGoodsDto;
import com.medusa.gruul.integral.web.model.param.api.ApiIntegralGoodsParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodVo;

/**
 * <p>
 * 积分商品表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralGoodsService extends IService<IntegralGoods> {


	/**
	 * 积分商品基础信息获取
	 *
	 * @param id 积分商品id
	 * @return 积分商品 基础信息
	 */
	IntegralGoodVo getIntegralGoodsInfo(Long id);


	/**
	 * 积分商品新增
	 *
	 * @param integralGoodsDto 积分商品信息
	 * @param shopId 商户id
	 */
	void addIntegralGoods(IntegralGoodsDto integralGoodsDto, String shopId);

	/**
	 * 积分商品删除
	 *
	 * @param id 积分商品id
	 */
	void deleteIntegralGoods(Long id);

	/**
	 * 积分商品信息修改
	 *
	 * @param integralGoodsDto 积分商品信息
	 */
	void updateIntegralGoods(IntegralGoodsDto integralGoodsDto);


	/**
	 * 积分商品上下架
	 *
	 * @param id 积分商品id
	 * @param status    积分商品状态
	 */
	void updateIntegralGoodsStatus(Long id, Integer status);

	/**
	 * 批量积分商品上下架
	 *
	 * @param ids   积分商品ids
	 * @param status    积分商品状态
	 */
	void updateBatchStatus(Long[] ids, Integer status);

	/**
	 * 批量删除积分商品
	 *
	 * @param ids 积分商品 ids
	 */
	void deleteBatch(Long[] ids);


	/**
	 * 积分商品查询列表
	 *
	 * @param integralGoodsParam 分页信息
	 * @return 进行分页的信息
	 */
	IPage<IntegralGoodVo> selectIntegralGoodsList(IntegralGoodsParam integralGoodsParam);

//------------------------------------------------小程序端实现----------------------------------------------------------------------//

	/**
	 * 获取小程序端 积分商品信息
	 *
	 * @param apiIntegralGoodsParam 分页信息
	 * @return  分页对象
	 */
	IPage<IntegralGoodVo> getPageList(ApiIntegralGoodsParam apiIntegralGoodsParam);

}
