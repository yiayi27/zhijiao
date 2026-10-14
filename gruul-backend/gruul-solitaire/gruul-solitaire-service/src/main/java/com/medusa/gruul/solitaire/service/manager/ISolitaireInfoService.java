package com.medusa.gruul.solitaire.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.solitaire.api.entity.SolitaireInfo;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireInfoDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireInfoVo;

/**
 * <p>
 * 接龙信息设置 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
public interface ISolitaireInfoService extends IService<SolitaireInfo> {

    /**
     * 查询接龙设置信息
     *
     * @return SolitaireInfoVo
     */
    SolitaireInfoVo getInfo();

    /**
     * 接龙信息新增/修改
     *
     * @param solitaireInfoDto
     */
    void saveInfo(SolitaireInfoDto solitaireInfoDto);

}
