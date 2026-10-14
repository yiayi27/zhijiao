package com.medusa.gruul.logistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;

import com.medusa.gruul.logistics.api.entity.LogisticsCompany;
import com.medusa.gruul.logistics.api.entity.LogisticsExpress;
import com.medusa.gruul.logistics.mapper.LogisticsCompanyMapper;
import com.medusa.gruul.logistics.mapper.LogisticsExpressMapper;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsExpressDto;
import com.medusa.gruul.logistics.model.param.LogisticsExpressParam;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressVo;
import com.medusa.gruul.logistics.service.ILogisticsExpressService;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class LogisticsExpressServiceImpl implements ILogisticsExpressService {

    @Autowired
    private LogisticsExpressMapper logisticsExpressMapper;

    @Autowired
    private LogisticsCompanyMapper logisticsCompanyMapper;

    /**
     * 查询快递公司信息列表
     *
     * @param logisticsExpressParam 快递公司信息查询参数
     * @return IPage<LogisticsAddressVo>
     */
    @Override
    public List<LogisticsExpressVo> getExpressList(LogisticsExpressParam logisticsExpressParam) {
        return this.logisticsExpressMapper.queryLogisticsExpressList(logisticsExpressParam);
    }

    /**
     * 查询单条物流公司信息
     *
     * @param id
     * @return LogisticsAddressVo
     */
    @Override
    public LogisticsExpressVo getExpressInfo(Long id) {
        return this.logisticsExpressMapper.queryLogisticsExpressInfo(id);
    }

    /**
     * 更新/新增 快递公司信息
     *
     * @param logisticsExpressDto 快递公司信息
     */
    @Override
    public void setExpressInfo(LogisticsExpressDto logisticsExpressDto) {
        //物流公司基础信息表判断是否存在 不存在插入
        List<LogisticsCompany> logisticsCompanies = this.logisticsCompanyMapper.selectList(new QueryWrapper<LogisticsCompany>().eq("code", logisticsExpressDto.getCode()));
        if (CollectionUtil.isEmpty(logisticsCompanies)) {
            LogisticsCompany logisticsCompany = new LogisticsCompany();
            logisticsCompany.setCode(logisticsExpressDto.getCode());
            logisticsCompany.setName(logisticsExpressDto.getName());
            int insert = this.logisticsCompanyMapper.insert(logisticsCompany);
            if (insert == 0) {
                throw new ServiceException("快递公司信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
        //快递公司信息新增或修改
        if (logisticsExpressDto.getId() == null) {
            //新增
            LogisticsExpress logisticsExpress = logisticsExpressDto.coverBean();
            int insert = this.logisticsExpressMapper.insert(logisticsExpress);
            if (insert == 0) {
                throw new ServiceException("快递公司信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        } else {
            //修改
            LogisticsExpress logisticsExpressSearch = this.logisticsExpressMapper.selectById(logisticsExpressDto.getId());
            if (BeanUtil.isEmpty(logisticsExpressSearch)) {
                throw new ServiceException("快递公司信息已删除或不存在！", SystemCode.DATA_EXISTED.getCode());
            }
            LogisticsExpress logisticsExpress = logisticsExpressDto.coverBean();
            int update = this.logisticsExpressMapper.updateById(logisticsExpress);
            if (update == 0) {
                throw new ServiceException("快递公司信息修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }

    /**
     * 删除快递公司信息
     *
     * @param id 快递公司信息id
     */
    @Override
    public void delExpressInfo(Long id) {
        LogisticsExpress logisticsExpress = this.logisticsExpressMapper.selectById(id);
        if (BeanUtil.isEmpty(logisticsExpress)) {
            throw new ServiceException("该信息不存在或已删除！", SystemCode.DATA_EXISTED.getCode());
        } else {
            int delete = this.logisticsExpressMapper.deleteById(id);
            if (delete == 0) {
                throw new ServiceException("快递公司信息删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
        }
    }
}
