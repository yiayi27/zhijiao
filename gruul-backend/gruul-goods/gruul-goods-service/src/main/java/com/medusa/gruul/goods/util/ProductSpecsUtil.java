package com.medusa.gruul.goods.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;

import java.util.*;

/**
 * 商品规格工具类
 *
 * @author 张治保
 * date 2024/11/15
 */

public class ProductSpecsUtil {
    private ProductSpecsUtil() {
    }

    /**
     * 获取所属的商品规格Id列表
     *
     * @author 张治保
     */
    public static List<Long> getSpecIdsBySpecNames(List<String> productSpecNames, List<ProductSpec> specs) {
        if (CollUtil.isEmpty(productSpecNames) || CollUtil.isEmpty(specs)) {
            return Collections.emptyList();
        }
        List<Long> productSpecIds = new ArrayList<>(specs.size());
        int bound = productSpecNames.size();
        for (int index = 0; index < bound; index++) {
            ProductSpec currentSpecGroup = specs.get(index);
            String currentName = productSpecNames.get(index);
            Optional<ProductSpec> any = currentSpecGroup.getChildren()
                    .stream()
                    .filter(spec -> spec.getName().equals(currentName)).findAny();
            if (!any.isPresent()) {
                throw new ServiceException("商品sku信息新增失败！", SystemCode.DATA_ADD_FAILED_CODE);
            }
            productSpecIds.add(any.get().getId());
        }
        return productSpecIds;
    }

    /**
     * 规格列表与sku列表 入参参数校验
     *
     * @author 张治保
     */
    public static void checkSpecsAndSkusParam(List<ProductSpec> specs, List<SkuStockDto> skus) {

        if (CollUtil.isEmpty(skus)) {
            throw new ServiceException("参数错误,SKU列表为空", SystemCode.PARAM_MISS_CODE);
        }
        /*
         * 检查是否没有设置多规格
         */
        boolean emptySpecs = CollUtil.isEmpty(specs);
        if ( emptySpecs && skus.size() == 1) {
            return;
        }
        specs = emptySpecs?Collections.emptyList():specs;

        /*
         * 检查specs的数据
         */
        checkSpecs(specs);
        /*
         * 检查skus列表中数据
         */
        checkSkus(specs.size(), skus);
        /*
         * 检查是skus长度否超过最大可能的长度
         */
        checkMaximumPossibleLength(specs, skus.size());
        /*
         * 检查 sku规格名称 与 规格列表匹配是否匹配
         */
        checkTheParameterLimit(specs, skus);
    }


    /**
     * 检查specs的数据
     */
    public static void checkSpecs(List<ProductSpec> specs) {
        specs.forEach(
                spec -> {
                    List<ProductSpec> children = spec.getChildren();
                    if (children.stream().map(ProductSpec::getName).distinct().count() == children.size()) {
                        return;
                    }
                    throw new ServiceException("规格组设置错误,规格列表中存在重复项或者空项", SystemCode.PARAM_VALID_ERROR_CODE);
                }
        );
        if (specs.stream().map(ProductSpec::getName).distinct().filter(StrUtil::isNotBlank).count() < specs.size()) {
            throw new ServiceException("规格组名称有重复项或空项", SystemCode.PARAM_VALID_ERROR_CODE);
        }
    }

    /**
     * 检查skus列表中数据
     */
    public static void checkSkus(int specsSize, List<SkuStockDto> skus) {
        Map<String, List<List<String>>> map = new HashMap<>(skus.size());
        for (SkuStockDto sku : skus) {
            List<String> specNames = sku.getProductSpecNames();
            if (specsSize != 0 && CollUtil.isEmpty(specNames)) {
                throw new ServiceException("SKU参数设置错误,空的规格名称列表", SystemCode.PARAM_VALID_ERROR_CODE);
            }
            if (specsSize != specNames.size()) {
                throw new ServiceException("SKU参数设置错误,规格名称列表设置有误", SystemCode.PARAM_VALID_ERROR_CODE);
            }

            String firstName = specNames.get(0);
            List<List<String>> lists = map.get(firstName);
            if (lists == null) {
                map.put(firstName, new ArrayList<List<String>>() {{
                    add(specNames);
                }});
                return;
            }
            for (List<String> list : lists) {
                if (list.containsAll(specNames)) {
                    throw new ServiceException("列表存在重复项", SystemCode.PARAM_VALID_ERROR_CODE);
                }
            }
        }
    }

    /**
     * 检查是否超过最大可能长度
     *
     * @author 张治保
     */
    public static void checkMaximumPossibleLength(List<ProductSpec> specs, int skusSize) {
        /*
         *
         * 可能的最大长度 maxSkuSize 超过这个长度设置有误
         */
        Integer maxSkuSize = specs.stream().map(spec -> {
            if (spec.getChildren() == null) {
                throw new ServiceException("规格组的商品规格列表不能为空", SystemCode.PARAM_VALID_ERROR_CODE);
            }
            return spec.getChildren().size();
        }).reduce((a, b) -> a * b).orElse(0);

        if (skusSize > maxSkuSize) {
            throw new ServiceException("SKU参数设置错误,超过了可能的最大长度", SystemCode.PARAM_VALID_ERROR_CODE);
        }
    }

    /**
     * 检查是sku中的字段是否符合规格specs参数的限制
     *
     * @author 张治保
     */
    public static void checkTheParameterLimit(List<ProductSpec> specs, List<SkuStockDto> skus) {
        skus.forEach(
                sku -> {
                    List<String> specNames = sku.getProductSpecNames();
                    for (int i = 0; i < specNames.size(); i++) {
                        final String currName = specNames.get(i);
                        if (
                                specs.get(i).getChildren().stream()
                                        .map(ProductSpec::getName)
                                        .anyMatch(name -> name.equals(currName))
                        ) {
                            continue;
                        }
                        throw new ServiceException("SKU参数设置错误,不存在的规格名称", SystemCode.PARAM_VALID_ERROR_CODE);
                    }
                }
        );
    }

    /**
     * 获取二维矩阵 规格
     *
     * @author 张治保
     */
    public static String[][] getMatrix(List<ProductSpec> specs) {
        return specs.stream().map(
                spec -> spec.getChildren().stream().map(ProductSpec::getName).toArray(String[]::new)
        ).toArray(String[][]::new);
    }
}
