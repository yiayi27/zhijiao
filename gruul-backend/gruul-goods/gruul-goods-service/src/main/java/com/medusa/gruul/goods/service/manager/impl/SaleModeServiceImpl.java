package com.medusa.gruul.goods.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.SaleMode;
import com.medusa.gruul.goods.api.entity.ShowCategory;
import com.medusa.gruul.goods.api.model.dto.manager.SaleModeDto;
import com.medusa.gruul.goods.api.model.param.manager.SaleModeParam;
import com.medusa.gruul.goods.api.model.vo.manager.SaleModeVo;
import com.medusa.gruul.goods.mapper.manager.ProductMapper;
import com.medusa.gruul.goods.mapper.manager.SaleModeMapper;
import com.medusa.gruul.goods.mapper.manager.ShowCategoryMapper;
import com.medusa.gruul.goods.service.manager.ISaleModeService;
import com.medusa.gruul.shops.api.feign.RemoteShopsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 商品自定义专区 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-10-26
 */
@Service
public class SaleModeServiceImpl extends ServiceImpl<SaleModeMapper, SaleMode> implements ISaleModeService {

    @Autowired
    private SaleModeMapper saleModeMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShowCategoryMapper showCategoryMapper;

    @Resource
    private RemoteShopsService remoteShopsService;

    /**
     * 查询专区列表
     *
     * @return 商品专区list对象
     */
    @Override
    public IPage<SaleModeVo> getSaleModeList(SaleModeParam saleModeParam) {
        IPage<SaleModeVo> page = new Page<>(saleModeParam.getCurrent(), saleModeParam.getSize());
        return page.setRecords(this.baseMapper.querySaleModeList(page, saleModeParam));
    }

    /**
     * 查询专区列表
     *
     * @return 商品专区list对象
     */
    @Override
    public List<SaleModeVo> getAllSaleModeList() {
        return this.baseMapper.queryAllSaleModeList();
    }

    /**
     * 商品专区新增，先判断专区名称是否已存在
     * 门店版套餐店铺最多可拥有2个销售专区
     * 企业版套餐店铺最多可拥有8个销售专区
     * 其他套餐销售专区不做限制
     *
     * @param saleModeDtos
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaleMode(List<SaleModeDto> saleModeDtos) {
        //取专区最大排序值
        Integer maxSort = this.baseMapper.queryMaxSort();
        if (maxSort == null) {
            maxSort = 0;
        }
        for (SaleModeDto saleModeDto : saleModeDtos) {
            //保存专区
            SaleMode saleMode = ifExistSaleModeName(saleModeDto);
            maxSort++;
            saleMode.setSort(maxSort);
            int insert = this.baseMapper.insert(saleMode);
            if (insert == 0) {
                throw new ServiceException("新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }

    /**
     * 商品专区修改
     *
     * @param saleModeDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleMode(SaleModeDto saleModeDto) {
        //判断原分类是否已被删除
        SaleMode oldSaleMode = saleModeMapper.selectById(saleModeDto.getId());
        if (BeanUtil.isEmpty(oldSaleMode)) {
            throw new ServiceException("该专区不存在！", SystemCode.DATA_NOT_EXIST_CODE);
        }
        SaleMode saleModeSearch = saleModeMapper.selectOne(new LambdaQueryWrapper<SaleMode>()
                .eq(SaleMode::getModeName, saleModeDto.getModeName())
                .ne(SaleMode::getId, saleModeDto.getId()));
        if (!BeanUtil.isEmpty(saleModeSearch)) {
            throw new ServiceException("专区名称已存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //专区保存
        SaleMode saleMode = saleModeDto.coverSaleMode();
        int update = saleModeMapper.updateById(saleMode);
        if (update == 0) {
            throw new ServiceException("修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        String shopId = ISecurity.userMust().getShopId();
        // 同步修改装修名称
        boolean flag = remoteShopsService.updateSpecialArea(shopId, oldSaleMode.getModeName(), saleModeDto.getModeName());
        if (!flag) {
            throw new ServiceException("装修同步修改专区名称失败，请稍后再试");
        }
    }

    /**
     * 商品专区删除
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSaleMode(Long id) {
        SaleMode saleMode = saleModeMapper.selectById(id);
        if (BeanUtil.isEmpty(saleMode)) {
            throw new ServiceException("该专区不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //判断专区下是否有商品
        Long count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getSaleMode, id));
        if (count > 0) {
            throw new ServiceException("该专区下有关联商品，不能删除！", SystemCode.DATA_EXISTED.getCode());
        }
        //删除专区下的展示分类与分拣分类
        showCategoryMapper.delete(new LambdaQueryWrapper<ShowCategory>().eq(ShowCategory::getSaleMode, id));
        //删除专区
        int delete = saleModeMapper.deleteById(id);
        if (delete == 0) {
            throw new ServiceException("删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
        }
        //调用店铺接口删除装修对应的专区
        if (!remoteShopsService.delShopRenovationPage(String.valueOf(id))) {
            throw new ServiceException("装修专区删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
        }
    }

    /**
     * 修改专区排序
     *
     * @param saleModeDtos 专区排序对象集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleModeSort(List<SaleModeDto> saleModeDtos) {
        saleModeDtos.forEach(saleModeDto -> {
            boolean update = new LambdaUpdateChainWrapper<>(saleModeMapper)
                    .eq(SaleMode::getId, saleModeDto.getId())
                    .set(SaleMode::getSort, saleModeDto.getSort())
                    .update();
            if (!update) {
                throw new ServiceException("修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 判断查询专区名称是否已存在
     *
     * @param saleModeDto
     * @return saleMode
     */
    private SaleMode ifExistSaleModeName(SaleModeDto saleModeDto) {
        SaleMode saleMode = this.baseMapper.selectOne(new LambdaQueryWrapper<SaleMode>().eq(SaleMode::getModeName, saleModeDto.getModeName()));
        if (!BeanUtil.isEmpty(saleMode)) {
            throw new ServiceException("专区名称已存在！", SystemCode.DATA_EXISTED.getCode());
        }
        saleMode = saleModeDto.coverSaleMode();
        return saleMode;
    }

}
