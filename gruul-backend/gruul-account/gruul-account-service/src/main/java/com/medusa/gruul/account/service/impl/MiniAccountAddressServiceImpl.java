package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MiniAccountAddress;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.mapper.MiniAccountAddressMapper;
import com.medusa.gruul.account.model.dto.AddressCraeteDto;
import com.medusa.gruul.account.model.dto.AddressUpdateDto;
import com.medusa.gruul.account.model.vo.AccountAddressListVo;
import com.medusa.gruul.account.service.IMiniAccountAddressService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 地址表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
public class MiniAccountAddressServiceImpl extends ServiceImpl<MiniAccountAddressMapper, MiniAccountAddress> implements IMiniAccountAddressService {

    /**
     * 添加用户地址
     *
     * @param addersVO com.medusa.gruul.account.model.dto.AddressCraeteDto
     */
    @Override
    public void addressCreate(AddressCraeteDto addersVO) {
        String shopId = ISystem.shopId();
        String userId = ISecurity.userMust().getId();
        MiniAccountAddress miniAccountAddress = new MiniAccountAddress();
        BeanUtils.copyProperties(addersVO, miniAccountAddress);
        miniAccountAddress.setUserId(userId);
        //校验是否存在相同
        checkRepetitionAddress(shopId, miniAccountAddress);
        miniAccountAddress.setIsDefault(CommonConstants.NUMBER_ZERO);
        //获取用户列表判断是否添加第一条地址,是则添加为默认地址
        List<MiniAccountAddress> userAddress = getUserAddress(shopId, miniAccountAddress.getUserId());
        if (CollectionUtil.isEmpty(userAddress)) {
            miniAccountAddress.setIsDefault(CommonConstants.NUMBER_ONE);
        }
        //缓存双删
        updateUserAddressCache(shopId, userId, () -> this.baseMapper.insert(miniAccountAddress));
    }

    /**
     * 获取用户地址
     *
     * @param type type 1-获取所有 2-获取默认地址,默认type=1
     * @return com.medusa.gruul.account.model.vo.AccountAddressListVo []
     */
    @Override
    public List<AccountAddressListVo> addersGet(Integer type) {
        String shopId = ISystem.shopId();
        String userId = ISecurity.userMust().getId();
        List<MiniAccountAddress> userAddress = getUserAddress(shopId, userId);
        List<AccountAddressListVo> vos = new ArrayList<>(userAddress.size());
        if (CollectionUtil.isNotEmpty(userAddress)) {
            vos = new ArrayList<>(userAddress.size());
            //封装返回数据
            for (MiniAccountAddress miniAccountAddress : userAddress) {
                AccountAddressListVo vo = new AccountAddressListVo();
                BeanUtils.copyProperties(miniAccountAddress, vo);
                vos.add(vo);
            }
            //获取默认地址过滤
            if (type.equals(CommonConstants.NUMBER_TWO)) {
                vos = vos.stream().filter(obj -> obj.getIsDefault().equals(1)).toList();
            }
        }
        return vos;
    }

    /**
     * 删除指定地址
     *
     * @param adderssId 地址id
     */
    @Override
    public void addressDelete(Integer adderssId) {
        String userId = ISecurity.userMust().getId();
        updateUserAddressCache(
                ISystem.shopId(), userId,
                () -> this.lambdaUpdate()
                        .eq(MiniAccountAddress::getId, adderssId)
                        .eq(MiniAccountAddress::getUserId, userId)
                        .remove()
        );
    }

    /**
     * 更新用户地址
     *
     * @param updateDto com.medusa.gruul.account.model.dto.AddressUpdateDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addersUpdate(AddressUpdateDto updateDto) {
        String rex = ReUtil.get("^[\\u4E00-\\u9FA5]+$", updateDto.getUserName(), 0);
        if (StrUtil.isEmpty(rex) || rex.length() > CommonConstants.NUMBER_TEN) {
            throw new ServiceException("收货人姓名必须是10个汉字以内");
        }
        String shopId = ISystem.shopId();
        String userId = ISecurity.userMust().getId();
        List<MiniAccountAddress> userAddress = getUserAddress(shopId, userId);
        Optional.ofNullable(updateDto.getDefaultStatus()).orElseThrow(() -> new ServiceException("操作失败", SystemCode.DATA_UPDATE_FAILED.getCode()));
        MiniAccountAddress miniAccountAddress = new MiniAccountAddress();
        BeanUtils.copyProperties(updateDto, miniAccountAddress);
        miniAccountAddress.setUserId(userId);
        //缓存双删
        updateUserAddressCache(
                shopId,
                userId,
                () -> {
                    if (updateDto.getDefaultStatus().equals(CommonConstants.NUMBER_ONE)) {
                        userAddress.forEach(obj -> {
                            boolean flag = false;
                            if (obj.getIsDefault().equals(1)) {
                                obj.setIsDefault(0);
                                flag = true;
                            }
                            if (obj.getId().equals(updateDto.getId())) {
                                BeanUtils.copyProperties(miniAccountAddress, obj);
                                obj.setIsDefault(1);
                                flag = true;
                            }
                            if (flag) {
                                this.baseMapper.updateById(obj);
                            }
                        });
                    }
                    if (updateDto.getDefaultStatus().equals(CommonConstants.NUMBER_ZERO)) {
                        //校验相同地址
                        checkRepetitionAddress(shopId, miniAccountAddress);
                        Optional<MiniAccountAddress> first = userAddress.stream().filter(obj -> obj.getId().equals(updateDto.getId())).findFirst();
                        first.ifPresent(obj -> {
                            BeanUtils.copyProperties(miniAccountAddress, obj);
                            this.baseMapper.updateById(obj);
                        });
                    }
                }
        );
    }


    /**
     * 更新用户地址缓存
     * 缓存双删
     */
    private void updateUserAddressCache(String shopId, String userId, Runnable tak) {
        if (userId == null || shopId == null) {
            throw new ServiceException("用户数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        RedisUtil.doubleDeletion(tak, addressCacheKey(shopId, userId));
    }

    /**
     * 校验是否存在相同的用户地址
     *
     * @param address com.medusa.gruul.account.api.entity.MiniAccountAddress
     */
    private void checkRepetitionAddress(String shopId, MiniAccountAddress address) {
        List<MiniAccountAddress> addressList = getUserAddress(shopId, address.getUserId());
        if (CollectionUtil.isEmpty(addressList)) {
            return;
        }
        Map<String, MiniAccountAddress> addressMap = addressList.stream().collect(Collectors.toMap(k ->
                        StrUtil.concat(false, k.getUserId(), k.getProvince(), k.getCity(), k.getCounty(), k.getDetailInfo(), k.getUserName(), k.getPhone()),
                v -> v));
        String key = StrUtil.concat(false, address.getUserId(), address.getProvince(), address.getCity(), address.getCounty(), address.getDetailInfo(),
                address.getUserName(), address.getPhone());
        if (addressMap.get(key) != null) {
            throw new ServiceException("请勿添加相同地址", SystemCode.DATA_EXISTED.getCode());
        }
    }

    /**
     * 获取指定用户地址
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccountAddress
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MiniAccountAddress> getUserAddress(String shopId, String userId) {
        String key = addressCacheKey(shopId, userId);
        return CollUtil.emptyIfNull(
                RedisUtil.getCache(
                        () -> {
                            List<MiniAccountAddress> caches = RedisUtil.getCacheList(key, MiniAccountAddress.class);
                            return CollUtil.isEmpty(caches) ? null : caches;
                        },
                        () -> {
                            List<MiniAccountAddress> address = this.lambdaQuery()
                                    .eq(MiniAccountAddress::getUserId, userId)
                                    .orderByDesc(MiniAccountAddress::getIsDefault)
                                    .orderByDesc(MiniAccountAddress::getUpdateTime)
                                    .list();
                            return CollUtil.isEmpty(address) ? null : address;
                        },
                        (addresses)-> RedisUtil.executePipelined(
                                redistemplate ->{
                                    RedisUtil.setCacheList(key, addresses);
                                    redistemplate.expire(key, Duration.ofDays(7).plusMinutes(RandomUtil.randomLong(60)));
                                }
                        ),
                        STR."\{key}_lock"
                )
        );
    }

    /**
     * 地址缓存key
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 缓存key
     */
    private String addressCacheKey(String shopId, String userId) {
        return RedisUtil.key(AccountConstant.ADDRESS_KEY, shopId, userId);
    }

}
