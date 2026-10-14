package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivityProduct;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 接龙活动关联商品 Mapper接口
 * </p>
 *
 * @author lcysike
 * @since 2024/6/5
 */
@Component
public interface SolitaireActivityProductMapper extends BaseMapper<SolitaireActivityProduct> {

}
