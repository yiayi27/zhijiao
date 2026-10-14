package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.entity.IntegralFreightTemplate;
import com.medusa.gruul.integral.api.entity.IntegralGoods;
import com.medusa.gruul.integral.api.entity.IntegralGoodsAttribute;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.mapper.IntegralFreightTemplateMapper;
import com.medusa.gruul.integral.mapper.IntegralGoodsAttributeMapper;
import com.medusa.gruul.integral.mapper.IntegralGoodsMapper;
import com.medusa.gruul.integral.mapper.IntegralShopMapper;
import com.medusa.gruul.integral.service.IIntegralGoodsService;
import com.medusa.gruul.integral.web.enums.VirtualProductStatusEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralFreightTemplateDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralGoodsAttibuteDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralGoodsDto;
import com.medusa.gruul.integral.web.model.param.api.ApiIntegralGoodsParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 积分商品表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
public class IntegralGoodsServiceImpl extends ServiceImpl<IntegralGoodsMapper, IntegralGoods>
        implements IIntegralGoodsService {
    @Autowired
    private IntegralGoodsMapper integralGoodsMapper;

    @Autowired
    private IntegralGoodsAttributeMapper integralGoodsAttributeMapper;

    @Autowired
    private IntegralFreightTemplateMapper integralFreightTemplateMapper;

    @Autowired
    private IntegralShopMapper integralShopMapper;

    /**
     * 单个积分商品基础信息获取
     *
     * @param id 积分商品id
     * @return 积分商品 基础信息
     */
    @Override
    public IntegralGoodVo getIntegralGoodsInfo(Long id) {
        return this.baseMapper.getIntegralGoodsInfo(id);
    }


    /**
     * 新增积分商品 插入积分商品属性信息,积分商品运费模板信息 清除页面缓存
     *
     * @param integralGoodsDto 积分商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addIntegralGoods(IntegralGoodsDto integralGoodsDto, String shopId) {
        long productSn;
        do {
            //生成一个12bit的积分商品货号
            productSn = RandomUtil.randomLong(IntegralConstant.MIN_ID, IntegralConstant.MAX_ID);
            //查看生成货号是否存在
            IntegralGoods integralGoodsSearch = this.baseMapper
                    .selectOne(new LambdaQueryWrapper<IntegralGoods>().eq(IntegralGoods::getProductSn, productSn));
            if (BeanUtil.isEmpty(integralGoodsSearch)) {
                break;
            }
        } while (true);

        //根据店铺id 获取到 积分商城表id set给积分商品表
        Long id = integralShopMapper.selectByShopId(shopId);
        if (id == null) {
            throw new ServiceException("商品发布失败!店铺信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        //set积分商城表id
        integralGoodsDto.setTIntegralShopId(id);
        //set积分商品货号
        integralGoodsDto.setProductSn(String.valueOf(productSn));

        //新增积分商品默认为上架状态
        if (integralGoodsDto.getStatus() == null) {
            integralGoodsDto.setStatus(VirtualProductStatusEnum.SELL_ON.getStatus());
        }
        //销量为空的时候 设置为0
        if (integralGoodsDto.getSales() == null) {
            integralGoodsDto.setSales(CommonConstants.NUMBER_ONE);
        }
        IntegralGoods integralGoods = integralGoodsDto.coverIntegralGoods();
        //积分商品 基础信息新增
        int insert = integralGoodsMapper.insert(integralGoods);
        //发布失败
        if (insert == 0) {
            throw new ServiceException("发布失败", SystemCode.DATA_ADD_FAILED.getCode());
        }

        //积分商品属性信息新增
        List<IntegralGoodsAttibuteDto> integralGoodsAttibuteDtos = integralGoodsDto.getIntegralGoodsAttibuteDtos();
        if (CollectionUtil.isNotEmpty(integralGoodsAttibuteDtos)) {
            addIntegralGoodsAttributeList(integralGoodsAttibuteDtos, integralGoods.getId());
        }

        //积分商品运费模版信息新增
        List<IntegralFreightTemplateDto> integralFreightTemplateDtos = integralGoodsDto
                .getIntegralFreightTemplateDtos();
        if (CollectionUtil.isNotEmpty(integralFreightTemplateDtos)) {
            addFreightTemplateIntegralList(integralFreightTemplateDtos, integralGoods.getId());
        }
    }


    /**
     * 积分商品属性新增
     *
     * @param integralGoodsAttibuteDtos 积分商品属性信息
     * @param id                        积分商品id
     */
    private void addIntegralGoodsAttributeList(List<IntegralGoodsAttibuteDto> integralGoodsAttibuteDtos, Long id) {
        integralGoodsAttibuteDtos.forEach(bean -> {
            IntegralGoodsAttribute integralGoodsAttribute = bean.coverIntegralGoodsAttribute();
            integralGoodsAttribute.setIntegralGoodsId(id);
            int insertProductAttribute = integralGoodsAttributeMapper.insert(integralGoodsAttribute);
            if (insertProductAttribute == 0) {
                throw new ServiceException("积分商品属性信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }


    /**
     * 运费属性模板新增
     *
     * @param integralFreightTemplateDtos 运费模板信息
     * @param id                          积分商品id
     */
    private void addFreightTemplateIntegralList(List<IntegralFreightTemplateDto> integralFreightTemplateDtos, Long id) {
        integralFreightTemplateDtos.forEach(bean -> {
            IntegralFreightTemplate integralFreightTemplate = bean.coverIntegralFreightTemplate();
            integralFreightTemplate.setIntegralGoodsId(id);
            int insert = integralFreightTemplateMapper.insert(integralFreightTemplate);
            if (insert == 0) {
                throw new ServiceException("积分商品运费模板新增失败!", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }


    /**
     * 删除积分商品
     *
     * @param id 积分商品id
     */
    @Override
    public void deleteIntegralGoods(Long id) {
        //根据积分商品id进行删除 删除方式为逻辑删除 is_deleted状态从 0 变成 1
        int del = integralGoodsMapper.deleteById(id);
        if (del < 1) {
            throw new ServiceException("删除失败 .积分商品不存在!", SystemCode.DATA_DELETE_FAILED.getCode());
        }
    }


    /**
     * 积分商品信息 修改
     *
     * @param integralGoodsDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIntegralGoods(IntegralGoodsDto integralGoodsDto) {
        //查看积分商品是否存在
        IntegralGoods integralGoodsSearch = integralGoodsMapper.selectById(integralGoodsDto.getId());
        if (BeanUtil.isEmpty(integralGoodsSearch)) {
            throw new ServiceException("商品不存在 !", SystemCode.DATA_NOT_EXIST.getCode());
        }
        //积分商品 基础信息修改
        IntegralGoods integralGoods = integralGoodsDto.coverIntegralGoods();
        int update = integralGoodsMapper.updateById(integralGoods);

        if (update < 1) {
            throw new ServiceException("修改商品信息失败 !", SystemCode.DATA_UPDATE_FAILED.getCode());
        }

        //积分商品属性信息修改  先删除原来的积分商品属性 再新增
        integralGoodsAttributeMapper
                .delete(new LambdaQueryWrapper<IntegralGoodsAttribute>().eq(IntegralGoodsAttribute::getIntegralGoodsId, integralGoodsDto.getId()));
        List<IntegralGoodsAttibuteDto> integralGoodsAttibuteDtos = integralGoodsDto.getIntegralGoodsAttibuteDtos();
        if (CollectionUtil.isNotEmpty(integralGoodsAttibuteDtos)) {
            //新增积分商品属性信息
            addIntegralGoodsAttributeList(integralGoodsAttibuteDtos, integralGoodsDto.getId());
        }
    }


    /**
     * 修改积分商品状态 进行上架或者下架
     *
     * @param id     积分商品id
     * @param status 积分商品状态
     */
    @Override
    public void updateIntegralGoodsStatus(Long id, Integer status) {
        int i = integralGoodsMapper.deleteById(id);
        if (i != 1) {
            throw new ServiceException("积分商品有误");
        }
        integralGoodsMapper.updateIntegralGoodsStatus(id, status);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchStatus(Long[] ids, Integer status) {
        //将数组转换成 List类型 并进行遍历逐一修改状态为status
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            boolean updateStatus = new LambdaUpdateChainWrapper<>(integralGoodsMapper).eq(IntegralGoods::getId, id)
                    .set(IntegralGoods::getStatus, status).update();
            if (!updateStatus) {
                throw new ServiceException("积分商品状态更新失败 !", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }


    /**
     * 积分商品批量删除
     *
     * @param ids 积分商品 ids
     */
    @Override
    public void deleteBatch(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            IntegralGoods integralGoods = integralGoodsMapper.selectById(id);
            if (BeanUtil.isEmpty(integralGoods)) {
                throw new ServiceException("积分商品不存在！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
        });
        removeByIds(list);
    }


    /**
     * 获取积分商品分页信息
     *
     * @param integralGoodsParam 积分商品查询信息
     * @return 积分商品分页对象
     */
    @Override
    public IPage<IntegralGoodVo> selectIntegralGoodsList(IntegralGoodsParam integralGoodsParam) {
        Page<IntegralGoodVo> objectPage = new Page<>(integralGoodsParam.getCurrent(), integralGoodsParam.getSize());
        List<IntegralGoodVo> integralGoodVos = this.baseMapper.selectIntegralGoodsList(objectPage, integralGoodsParam);
        return objectPage.setRecords(integralGoodVos);
    }

    /**
     * 获取小程序端 积分商品信息
     *
     * @param apiIntegralGoodsParam 分页信息
     * @return IPage<IntegralGoodVo>
     */
    @Override
    public IPage<IntegralGoodVo> getPageList(ApiIntegralGoodsParam apiIntegralGoodsParam) {
        //根据商铺id 获取商城状态
        String shopId = ISystem.shopId();
        IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
        //商城开启显示积分商品 不开启则为null
        IPage<IntegralGoodVo> integralGoodVo = new Page<>(apiIntegralGoodsParam.getCurrent(),
                apiIntegralGoodsParam.getSize());
        //商城为开启状态
        if (integralShop.getType()) {
            //sql中查询数据
            List<IntegralGoodVo> integralGoodVos = integralGoodsMapper
                    .selectApiIntegralGoodsList(integralGoodVo, apiIntegralGoodsParam);
            integralGoodVo.setRecords(integralGoodVos);
        }
        return integralGoodVo;
    }
}
