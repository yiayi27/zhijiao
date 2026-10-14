package com.medusa.gruul.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.constant.AccountStatusController;
import com.medusa.gruul.account.api.entity.MiniAccountFootMark;
import com.medusa.gruul.account.mapper.MiniAccountFootMarkMapper;
import com.medusa.gruul.account.model.dto.AccountFootMarkDto;
import com.medusa.gruul.account.model.vo.AccountFootMarkParam;
import com.medusa.gruul.account.model.vo.AccountFootMarkVo;
import com.medusa.gruul.account.service.IApiMiniAccountFootMarkService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartProductVo;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoq
 * @date 2024/2/28 10:39
 */
@Service
public class ApiMiniAccountFootMarkServiceImpl extends ServiceImpl<MiniAccountFootMarkMapper, MiniAccountFootMark>
        implements IApiMiniAccountFootMarkService {

    @Resource
    private MiniAccountFootMarkMapper accountFootMarkMapper;

    /**
     * 用户足迹信息
     *
     * @param accountFootMarkParam 足迹查询信息
     * @return List<String>
     */
    @Override
    public List<String> getAccountFootMark(AccountFootMarkParam accountFootMarkParam) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        return getAccountFootMarkInfo(accountFootMarkParam, userId, shopId);
    }

    private List<String> getAccountFootMarkInfo(AccountFootMarkParam accountFootMarkParam, String userId,
                                                String shopId) {
        // 获取商品缓存数据 key
        IPage<AccountFootMarkVo> accountFootMarkVo = new Page<>(accountFootMarkParam.getCurrent(),
                accountFootMarkParam.getSize());
        accountFootMarkParam.setUserId(userId);
        // 获取分页数据后的 用户足迹信息
        List<AccountFootMarkVo> accountFootMarks = accountFootMarkMapper
                .selectAccountFootMarkList(accountFootMarkVo, accountFootMarkParam);
        List<LocalDateTime> collect = accountFootMarks.stream().map(AccountFootMarkVo::getCreateTime).distinct()
                .toList();
        String getTime = "";
        List<String> list = new ArrayList<>(CommonConstants.NUMBER_ELEVEN);
        for (LocalDateTime localDateTime : collect) {
            List<AccountFootMarkVo> temp = new ArrayList<>();
            for (AccountFootMarkVo footMarkVo : accountFootMarks) {
                LocalDateTime createTime = footMarkVo.getCreateTime();
                getTime = createTime.format(DateTimeFormatter.ofPattern("MM-dd"));
                if (footMarkVo.getCreateTime().equals(localDateTime)) {
                    String productId = String.valueOf(footMarkVo.getProductId());
                    String cacheObject = RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, productId));
                    if (StringUtil.isBlank(cacheObject)) {
                        continue;
                    }
                    ApiShoppingCartProductVo apiShoppingCartProductVo = Jackson.readValue(cacheObject, ApiShoppingCartProductVo.class);
                    BeanUtil.copyProperties(apiShoppingCartProductVo, footMarkVo);
                    footMarkVo.setProductPrice(apiShoppingCartProductVo.getSkuStocks().get(CommonConstants.NUMBER_ZERO).getPrice());
                    footMarkVo.setOriginalPrice(apiShoppingCartProductVo.getSkuStocks().get(CommonConstants.NUMBER_ZERO).getOriginalPrice());
                    footMarkVo.setTime(getTime);
                    footMarkVo.setStatus(AccountStatusController.STATUS);
                    //遍历商品规格信息 得到该商品的 规格剩余数量
                    List<SkuStockDto> skuStocks = apiShoppingCartProductVo.getSkuStocks();
                    for (SkuStockDto skuStock : skuStocks) {
                        Integer stock = skuStock.getStock();
                        if (stock != 0) {
                            footMarkVo.setStatus(apiShoppingCartProductVo.getStatus());
                            break;
                        }
                    }
                    if (apiShoppingCartProductVo.getDeleted() == 1) {
                        footMarkVo.setStatus(CommonConstants.NUMBER_ONE);
                    }
                    temp.add(footMarkVo);
                }
            }
            list.add(Jackson.writeValueAsString(temp));


        }
        return list;
    }

    /**
     * 添加用户足迹
     *
     * @param accountFootMarkDto 商品信息
     */
    @Override
    public void addAccountFootMark(AccountFootMarkDto accountFootMarkDto) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String productId = accountFootMarkDto.getProductId();
        MiniAccountFootMark miniAccountFootMark = accountFootMarkMapper.selectByProductId(productId, userId);
        // 查看用户今日是否浏览过该商品
        accountFootMarkDto.setUserId(userId);
        if (miniAccountFootMark == null) {
            // 用户今日未添加过该足迹
            MiniAccountFootMark accountFootMark = accountFootMarkDto.coverMiniAccountFootMark();
            int insert = accountFootMarkMapper.insert(accountFootMark);
            if (insert < 1) {
                throw new ServiceException("添加用户足迹失败", SystemCode.DATA_ADD_FAILED_CODE);
            }
            int i = accountFootMarkMapper.selectAccountFootMarkCount(userId);
            if (i > AccountStatusController.FOOT_NUM) {
                //保留该用户最新的100条足迹数据
                int delCount = i - AccountStatusController.FOOT_NUM;
                int updateBeyondFootMarkCount = accountFootMarkMapper.updateBeyondFootMark(userId, delCount);
                if (updateBeyondFootMarkCount < 1) {
                    throw new ServiceException("更新用户足迹状态失败", SystemCode.DATA_ADD_FAILED_CODE);
                }
            }
        } else {
            // 用户今日添加了该足迹
            miniAccountFootMark.setCreateTime(LocalDateTime.now());
            int update = accountFootMarkMapper.updateById(miniAccountFootMark);
            if (update < 1) {
                throw new ServiceException("更新用户足迹失败", SystemCode.DATA_UPDATE_FAILED_CODE);
            }
        }
    }

    /**
     * 删除用户足迹数据
     *
     * @param longs 用户足迹id
     * @param type  :0-删除, 1-清空
     */
    @Override
    public void delAccountFootMark(List<Long> longs, Integer type) {
        // 根据选择的的用户足迹ids进行删除
        if (type.equals(0)) {
            List<MiniAccountFootMark> miniAccountFootMarks = baseMapper.selectByIds(longs);
            for (MiniAccountFootMark miniAccountFootMark : miniAccountFootMarks) {
                //
                int i = accountFootMarkMapper.deleteById(miniAccountFootMark.getFootmarkId());
                if (i < 1) {
                    throw new ServiceException("删除用户足迹失败", SystemCode.DATA_UPDATE_FAILED_CODE);
                }
            }
        }
        // 根据用户id 进行删除
        if (type.equals(1)) {
            SecureUser secureUser = ISecurity.userMust();
            String userId = secureUser.getId();
            int i = accountFootMarkMapper.deleteByUserId(userId);
            if (i < 1) {
                throw new ServiceException("清空用户足迹失败", SystemCode.DATA_UPDATE_FAILED_CODE);
            }
        }
    }

    /**
     * 查看用户收藏数量
     *
     * @return 收藏数量
     */
    @Override
    public int getAccountFootMarkCount() {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        int i = accountFootMarkMapper.selectAccountFootMarkCount(userId);
        return i;
    }
}
