package com.medusa.gruul.account.api.model;

import com.medusa.gruul.account.api.entity.MiniAccount;
import com.medusa.gruul.account.api.entity.MiniAccountAddress;
import com.medusa.gruul.account.api.entity.MiniAccountExtends;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/29
 */
@Data
public class AccountInfoDto {

    @Schema(title = "用户地址数组,自行判空")
    private List<MiniAccountAddress> miniAccountAddress;

    @Schema(title = "用户扩展数据")
    private MiniAccountExtends miniAccountExtends;

    @Schema(title = "用户基础数据")
    private MiniAccount miniAccountunt;

    @Schema(title = "黑名单限制类型数组,查询扩展数据时返回,可能为null,自行判断")
    private List<Integer> restrictTypes;


}
