package com.medusa.gruul.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminAddDTO;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminUpdateDTO;
import com.medusa.gruul.platform.model.enums.ShopAdminAvailable;
import com.medusa.gruul.platform.model.vo.AdminAccountVO;

/**
 * @author 张治保
 * @since 2025/4/9
 */
public interface ShopAdminService {


    /**
     * 分页查询店铺管理员
     *
     * @param param 分页参数
     * @return 分页查询结果
     */
    PageUtils<AdminAccountVO> page(Page<AccountInfo> param);

    /**
     * 查询该账号是否可绑定本店管理员
     *
     * @param mobile 手机号
     * @return 该账号是否可绑定本店管理员
     */
    ShopAdminAvailable available(String mobile);

    /**
     * 添加管理员
     *
     * @param param 管理员信息
     */
    void add(ShopAdminAddDTO param);


    /**
     * 修改管理员
     *
     * @param param 管理员信息
     */
    void update(ShopAdminUpdateDTO param);


    /**
     * 删除管理员
     *
     * @param mobile 管理员手机号
     */
    void delete(String mobile);
}
