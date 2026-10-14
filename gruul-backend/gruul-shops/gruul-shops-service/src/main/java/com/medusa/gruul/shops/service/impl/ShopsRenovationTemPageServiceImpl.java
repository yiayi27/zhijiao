package com.medusa.gruul.shops.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.shops.api.entity.ShopsRenovationAssembly;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPage;
import com.medusa.gruul.shops.mapper.ShopsRenovationTemPageMapper;
import com.medusa.gruul.shops.model.ShopConstant;
import com.medusa.gruul.shops.model.ShopUtil;
import com.medusa.gruul.shops.model.param.ShopsRenovationPageParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationAssemblyVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPageVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationTemplateVo;
import com.medusa.gruul.shops.properties.GlobalConstant;
import com.medusa.gruul.shops.service.ShopsRenovationPluginService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageAssService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 店铺装修模板页面
 * @author create by zq
 * @date created in 2024/01/14
 */
@Service
public class ShopsRenovationTemPageServiceImpl extends ServiceImpl<ShopsRenovationTemPageMapper, ShopsRenovationPage> implements ShopsRenovationTemPageService {

    @Autowired
    @Lazy
    private ShopsRenovationTemPageAssService shopsRenovationTemPageAssService;

    @Autowired
    private ShopsRenovationPluginService shopsRenovationPluginService;

    /**
     * 保存模板页面
     *
     * @param param
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplatePage(ShopsRenovationPageParam param) {

        String shopId = ISystem.shopId();
        ShopUtil.deleteRenovationCache(
                shopId,
                () -> {
                    String isDef = StrUtil.nullToDefault(param.getIsDef(), GlobalConstant.STRING_ZERO);
                    //如果是默认页面则把其他页面设置为非默认
                    Long pageId = param.getId();
                    //是否是更新操作
                    boolean update = pageId != null;
                    if (GlobalConstant.STRING_ONE.equals(isDef)) {
                        this.lambdaUpdate()
                                .set(ShopsRenovationPage::getIsDef, GlobalConstant.STRING_ZERO)
                                .ne(update, ShopsRenovationPage::getId, pageId)
                                .update();
                    }

                    //编辑
                    if (update) {
                        this.lambdaUpdate()
                                .set(ShopsRenovationPage::getType, param.getType())
                                .set(ShopsRenovationPage::getPageName, param.getPageName())
                                .set(ShopsRenovationPage::getIsDef, isDef)
                                .set(ShopsRenovationPage::getModelId, param.getModelId())
                                .eq(ShopsRenovationPage::getId, pageId)
                                .update();
                        return;
                    }

                    ShopsRenovationPage renovationPage = new ShopsRenovationPage()
                            .setShopId(shopId)
                            .setType(param.getType())
                            .setPageName(param.getPageName())
                            .setIsDef(isDef)
                            .setModelId(param.getModelId());
                    this.save(renovationPage);
                    // 店铺装修模版页面id
                    pageId = renovationPage.getId();

                    ShopsRenovationAssembly shopsRenovationAssembly = new ShopsRenovationAssembly();
                    shopsRenovationAssembly.setPageId(pageId);

                    shopsRenovationAssembly.setProperties("{\"icon\":\"sousuo\",\"value\":\"Search\",\"label\":\"搜索\",\"id\":1648092121094,\"formData\":{\"showStyle\":\"is-style-one\",\"keyWord\":\"请输入关键词搜索\",\"hotWord\":[\"预设搜索热词\"]}}");
                    if (GlobalConstant.STRING_TOW.equals(param.getType())) {
                        // Type为2时 设置分类页
                        shopsRenovationAssembly.setProperties("{\"icon\":\"\",\"value\":\"BusinessSuper\",\"label\":\"商超分类页\",\"id\":1747721987285,\"formData\":{\"selectMode\":1,\"selectType\":1,\"classChart\":[],\"groupList\":[],\"firstCatList\":[],\"currentFirstCategory\":{\"isSelected\":false,\"navigationStyle\":1},\"swiperList\":[],\"curruntNav\":{\"id\":\"\",\"name\":\"\",\"parentId\":\"\",\"productNumber\":0,\"showCategoryVos\":[]},\"navigation\":[],\"listStyle\":1,\"pagePadding\":12,\"fontColor\":\"#FF0000\",\"fontBg\":\"#FFFFFF\",\"fontNotColor\":\"#000000\",\"fontNotBg\":\"#F5F5F5\",\"goodsPadding\":10,\"goodsStyle\":6,\"doodsAngle\":1,\"textStyle\":1,\"cartButton\":3,\"goods\":{\"id\":1,\"name\":\"商品名称商品名称商品名称商品名称商品名称商品名称\",\"img\":\"https://qiniu-app.qtshe.com/u391.png\",\"endTime\":\"20:15:14\",\"price\":99,\"guide\":219,\"soldCount\":10,\"inventory\":120,\"deliveryTime\":\"06月24日 14:00\"},\"gList\":[{\"id\":1,\"name\":\"商品名称商品名称商品名称商品名称商品名称商品名称\",\"img\":\"https://qiniu-app.qtshe.com/64345435.jpg\",\"endTime\":\"20:15:14\",\"price\":99,\"guide\":219,\"soldCount\":10,\"inventory\":120,\"deliveryTime\":\"06月24日 14:00\"},{\"id\":2,\"name\":\"商品名称商品名称商品名称商品名称商品名称商品名称\",\"img\":\"https://qiniu-app.qtshe.com/64345435.jpg\",\"endTime\":\"20:15:14\",\"price\":99,\"guide\":219,\"soldCount\":10,\"inventory\":120,\"deliveryTime\":\"06月24日 14:00\"}],\"isSearch\":1,\"secondCategoryId\":\"\",\"secondCatGoodsStyle\":1}}");
                    }
                    // 保存店铺装修页面组件属性
                    shopsRenovationTemPageAssService.save(shopsRenovationAssembly);

                }
        );

    }


    /**
     * 删除商铺装修模板页面 by ids or templateId
     *
     * @param pageIds 页面id
     */
    @Override
    public void delTemplatePage(Set<String> pageIds) {
        if (CollUtil.isEmpty(pageIds)) {
            return;
        }
        String shopId = ISystem.shopId();
        ShopUtil.deleteRenovationCache(
                shopId,
                () -> {
                    //删除装修页面
                    this.lambdaUpdate()
                            .in(ShopsRenovationPage::getId, pageIds)
                            .remove();
                    //删除装修页面组件
                    shopsRenovationTemPageAssService.lambdaUpdate()
                            .in(ShopsRenovationAssembly::getPageId, pageIds)
                            .remove();
                }
        );
    }


    /**
     * 获取商铺装修模板页面list
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    @Override
    public PageUtils<ShopsRenovationPageVo> listTemplatePage(ShopsRenovationPageParam param) {
        Page<ShopsRenovationPage> page = this.lambdaQuery()
                .select(ShopsRenovationPage::getId, ShopsRenovationPage::getPageName, ShopsRenovationPage::getIsDef, ShopsRenovationPage::getType, ShopsRenovationPage::getModelId)
                .page(new Page<>(param.getCurrent(), param.getSize()));
        return new PageUtils<>(
                page.convert(
                        curPage -> new ShopsRenovationPageVo()
                                .setId(curPage.getId())
                                .setPageName(curPage.getPageName())
                                .setIsDef(curPage.getIsDef())
                                .setType(curPage.getType())
                                .setModelId(curPage.getModelId())
                )
        );
    }

    /**
     * 获取已经完成装修的商品组件
     *
     * @param param 店铺装修模板页面信息
     * @return PageUtils<ShopsRenovationPageVo>
     */
    @Override
    public PageUtils<ShopsRenovationPageVo> fitmentPrefectureEndPage(ShopsRenovationPageParam param) {
        Page<ShopsRenovationPage> page = this.lambdaQuery()
                .select(ShopsRenovationPage::getId, ShopsRenovationPage::getPageName, ShopsRenovationPage::getIsDef, ShopsRenovationPage::getType, ShopsRenovationPage::getModelId)
                .exists("SELECT 1 FROM t_shops_renovation_assembly AS assembly WHERE assembly.page_id = t_shops_renovation_page.id")
                .isNotNull(ShopsRenovationPage::getType)
                .page(new Page<>(param.getCurrent(), param.getSize()));
        return new PageUtils<>(
                page.convert(
                        curPage -> new ShopsRenovationPageVo()
                                .setId(curPage.getId())
                                .setPageName(curPage.getPageName())
                                .setIsDef(curPage.getIsDef())
                                .setType(curPage.getType())
                                .setModelId(curPage.getModelId())
                )
        );
    }

    /**
     * 删除专区时 匹配删除装修数据
     *
     * @param modelId modelId
     * @return 删除结果
     */
    @Override
    public Boolean delShopRenovationPageByModelId(String modelId) {
        return this.lambdaUpdate()
                .eq(ShopsRenovationPage::getModelId, modelId)
                .remove();
    }

    /**
     * 获取模版信息
     *
     * @param modelId 模版id
     * @return Result
     */
    @Override
    public List<ShopsRenovationPage> getByModelId(String modelId) {
        return this.lambdaQuery()
                .eq(ShopsRenovationPage::getModelId, modelId)
                .list();
    }


    /**
     * 小程序装修默认聚合接口
     *
     * @return Result
     */
    @Override
    public ShopsRenovationTemplateVo defOne() {
        ShopsRenovationTemplateVo shopsRenovationTemplateVo = RedisUtil.getCacheMap(
                ShopsRenovationTemplateVo.class,
                () -> {
                    ShopsRenovationTemplateVo vo = new ShopsRenovationTemplateVo();

                    //装修页面
                    List<ShopsRenovationPageVo> pages = this.lambdaQuery()
                            .select(ShopsRenovationPage::getId, ShopsRenovationPage::getPageName, ShopsRenovationPage::getIsDef, ShopsRenovationPage::getType)
                            .list()
                            .stream()
                            .map(
                                    page -> new ShopsRenovationPageVo()
                                            .setId(page.getId())
                                            .setPageName(page.getPageName())
                                            .setIsDef(page.getIsDef())
                                            .setType(page.getType())
                            ).toList();
                    vo.setPages(pages);
                    if (pages.isEmpty()) {
                        return vo;
                    }
                    Set<Long> pageIds = pages.stream()
                            .map(ShopsRenovationPageVo::getId)
                            .collect(Collectors.toSet());
                    //页面配置
                    Map<Long, List<ShopsRenovationAssembly>> assemblyMap = shopsRenovationTemPageAssService.lambdaQuery()
                            .select(ShopsRenovationAssembly::getId, ShopsRenovationAssembly::getPageId, ShopsRenovationAssembly::getProperties, ShopsRenovationAssembly::getSort)
                            .in(ShopsRenovationAssembly::getPageId, pageIds)
                            .list()
                            .stream()
                            .collect(
                                    Collectors.groupingBy(ShopsRenovationAssembly::getPageId)
                            );
                    for (ShopsRenovationPageVo page : pages) {
                        page.setAssemblyVos(
                                CollUtil.emptyIfNull(assemblyMap.get(page.getId()))
                                        .stream()
                                        .sorted(Comparator.comparingInt(ShopsRenovationAssembly::getSort))
                                        .map(
                                                assembly -> new ShopsRenovationAssemblyVo()
                                                        .setId(assembly.getId())
                                                        .setPageId(assembly.getPageId())
                                                        .setProperties(assembly.getProperties())
                                        ).toList()
                        );
                    }
                    return vo;
                },
                Duration.ofDays(3).plusHours(RandomUtil.randomInt(CommonConstants.NUMBER_FOUR)),
                ShopConstant.RENOVATION_CACHE, ISystem.shopId()
        );
        //底部导航
        shopsRenovationTemplateVo.setPlugins(shopsRenovationPluginService.listPlugin());
        return  shopsRenovationTemplateVo;
    }

}
