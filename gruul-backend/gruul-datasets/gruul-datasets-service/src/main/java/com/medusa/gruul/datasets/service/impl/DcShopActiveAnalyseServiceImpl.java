package com.medusa.gruul.datasets.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.datasets.api.constant.DataSetsMethod;
import com.medusa.gruul.datasets.api.entity.DcShopActiveAnalyse;
import com.medusa.gruul.datasets.api.model.CallerDto;
import com.medusa.gruul.datasets.mapper.DcShopActiveAnalyseMapper;
import com.medusa.gruul.datasets.service.IDcShopActiveAnalyseService;
import com.medusa.gruul.datasets.web.model.CallerInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 访客数据统计表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@Service
public class DcShopActiveAnalyseServiceImpl extends ServiceImpl<DcShopActiveAnalyseMapper, DcShopActiveAnalyse>
        implements IDcShopActiveAnalyseService {

    @Autowired
    private DcShopActiveAnalyseMapper dcShopActiveAnalyseMapper;

    /**
     * 更新访客访客数据公共方法
     *
     * @param callerInfo 访客数据
     */
    private void updateCallerInfo(DcShopActiveAnalyse callerInfo) {
        callerInfo.setPv(callerInfo.getPv() + CommonConstants.NUMBER_ONE);
        callerInfo.setYyyyMM(DataSetsMethod.communalYearAndMonth());
        callerInfo.setYyyyMMdd(DataSetsMethod.communalDate());
        int update = dcShopActiveAnalyseMapper.updateById(callerInfo);
        if (update < 1) {
            throw new ServiceException("修改访客数据失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 新增访客访客数据公共方法
     *
     * @param callerInfo 访客数据
     */
    private void insertCallerInfo(DcShopActiveAnalyse callerInfo) {
        callerInfo.setPv(CommonConstants.NUMBER_ONE);
        callerInfo.setUv(CommonConstants.NUMBER_ONE);
        callerInfo.setYyyyMM(DataSetsMethod.communalYearAndMonth());
        callerInfo.setYyyyMMdd(DataSetsMethod.communalDate());
        int insert = dcShopActiveAnalyseMapper.insert(callerInfo);
        if (insert < 1) {
            throw new ServiceException("新增访客数据失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 新增访客数据
     */
    @Override
    public void addCallerInfo() {
        DcShopActiveAnalyse callerInfo = dcShopActiveAnalyseMapper.getCallerInfoByToday();
        if (callerInfo == null) {
            callerInfo = new DcShopActiveAnalyse();
            insertCallerInfo(callerInfo);
        } else {
            updateCallerInfo(callerInfo);
        }
    }

    /**
     * 新增访客数据
     *
     * @param shopId 商铺id
     */
    @Override
    public void saveCallerInfo(String shopId) {
        DcShopActiveAnalyse callerInfo = dcShopActiveAnalyseMapper.getCallerInfoByShopId(shopId);
        if (callerInfo == null) {
            callerInfo = new DcShopActiveAnalyse();
            callerInfo.setShopId(shopId);
            insertCallerInfo(callerInfo);
        } else {
            callerInfo.setUv(callerInfo.getUv() + CommonConstants.NUMBER_ONE);
            updateCallerInfo(callerInfo);
        }
    }

    /**
     * 根据时间来筛选浏览量 当结束时间为null是 提供当日浏览量及昨日浏览量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return CallerDto
     */
    @Override
    public CallerDto getCallerInfo(Integer startTime, Integer endTime) {
        CallerDto callerDto = new CallerDto();
        if (endTime == null) {
            DcShopActiveAnalyse callerInfoToday = dcShopActiveAnalyseMapper.getCallerInfoByToday();
            DcShopActiveAnalyse callerInfoYesterday = dcShopActiveAnalyseMapper.getCallerInfoByYesterday();
            callerDto.setYesterdayPv(callerInfoYesterday != null ? callerInfoYesterday.getPv() : CommonConstants.NUMBER_ZERO);
            callerDto.setPv(callerInfoToday != null ? callerInfoToday.getPv() : CommonConstants.NUMBER_ZERO);
        } else {
            Integer pv = dcShopActiveAnalyseMapper.getCallerInfoByTime(startTime, endTime);
            callerDto.setPv(pv != null ? pv : CommonConstants.NUMBER_ZERO);
            callerDto.setYesterdayPv(CommonConstants.NUMBER_NEGATIVE_ONE);
        }
        return callerDto;
    }

    /**
     * 访客数据信息获取
     *
     * @return 访客数据信息
     */
    @Override
    public CallerInfoVo getShopCallerInfo() {
        CallerInfoVo callerInfoVo = new CallerInfoVo();
        //当日浏览量 访客数
        DcShopActiveAnalyse callerInfo = dcShopActiveAnalyseMapper.getCallerInfoByToday();
        //总访客数
        Integer totalUv = dcShopActiveAnalyseMapper.getAllCallerInfoByShopId();
        //总浏览量
        callerInfoVo.setPv(callerInfo != null ? callerInfo.getPv() : CommonConstants.NUMBER_ZERO);
        callerInfoVo.setUv(callerInfo != null ? callerInfo.getUv() : CommonConstants.NUMBER_ZERO);
        callerInfoVo.setTotalUv(totalUv != null ? totalUv : CommonConstants.NUMBER_ZERO);
        return callerInfoVo;
    }
}
