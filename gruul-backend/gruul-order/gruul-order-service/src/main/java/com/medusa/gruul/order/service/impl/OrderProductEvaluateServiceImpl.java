package com.medusa.gruul.order.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.order.api.entity.OrderProductEvaluate;
import com.medusa.gruul.order.mapper.OrderProductEvaluateMapper;
import com.medusa.gruul.order.model.ApiSearchProductEvaluateDto;
import com.medusa.gruul.order.model.ProductEvaluateVo;
import com.medusa.gruul.order.model.UserEvaluateVo;
import com.medusa.gruul.order.service.IOrderProductEvaluateService;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The type Order product evaluate service.
 * @author alan
 */
@Service
public class OrderProductEvaluateServiceImpl extends ServiceImpl<OrderProductEvaluateMapper, OrderProductEvaluate> implements IOrderProductEvaluateService {

    /**
     * 商品详情页评价概况
     *
     * @param productId 商品id
     * @return com.medusa.gruul.order.model.UserEvaluateVo user evaluate vo
     */
    @Override
    public UserEvaluateVo userEvaluateOverview(String productId) {
        UserEvaluateVo vo = new UserEvaluateVo();
        String curUserId = ISecurity.userOpt().map(SecureUser::getId).orElse("");
        int total = baseMapper.countTotal(curUserId, productId);
        vo.setTotal(total);
        int praiseNum = baseMapper.countPraiseNum(curUserId, productId);
        int praiseRate = 0;
        if (total > 0) {
            praiseRate = (int) NumberUtil.mul(NumberUtil.div(praiseNum, total, 2), 100);
        }
        vo.setPraiseRate(praiseRate);
        vo.setAll(total);
        int hasContent = baseMapper.countHasContent(curUserId, productId);
        vo.setHasContent(hasContent);
        int hasPicture = baseMapper.countHasPicture(curUserId, productId);
        vo.setHasPicture(hasPicture);
        return vo;
    }

    /**
     * 商品详情页评价
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     */
    @Override
    public PageUtils<ProductEvaluateVo> productEvaluate(ApiSearchProductEvaluateDto dto) {
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        String curUserId = userOpt.map(SecureUser::getId).orElse("");
        Page<ProductEvaluateVo> page = baseMapper.selectProductEvaluatePage(new Page(dto.getCurrent(), dto.getSize())
                , dto.getType(), dto.getProductId(), curUserId);

        for (ProductEvaluateVo record : page.getRecords()) {
            if (StrUtil.isNotBlank(record.getUserName())) {
                String name = record.getUserName();
                if (name.length() > 2) {
                    name = RegExUtils.replaceAll(name, RegexConstants.REGEX_EMOJI, "");
                    name = StrUtil.trim(name);
                    name = name.substring(0, 1).concat("**").concat(name.substring(name.length() - 1));
                }
                record.setUserName(name);
            }
        }
        return new PageUtils<>(page);
    }
}
