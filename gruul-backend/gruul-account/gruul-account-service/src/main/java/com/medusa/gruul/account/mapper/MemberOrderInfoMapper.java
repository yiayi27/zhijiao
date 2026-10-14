package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.account.api.entity.MemberOrderInfo;
import com.medusa.gruul.account.model.vo.MemberOrderInfoResVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 会员储值信息表 Mapper 接口
 *
 * @author zhaokw
 * @since 2024-06-13
 */
public interface MemberOrderInfoMapper extends BaseMapper<MemberOrderInfo> {

    /**
     * PC端获取储值列表
     *
     * @param memberOrderInfoResVoPage 分页数据
     * @param paramMap                 查询条件参数
     * @return com.medusa.gruul.account.model.entity.MemberOrderInfo
     */
    IPage<MemberOrderInfoResVo> selectMemberOrderList(Page<MemberOrderInfoResVo> memberOrderInfoResVoPage, @Param("paramMap") Map<String, Object> paramMap);

}
