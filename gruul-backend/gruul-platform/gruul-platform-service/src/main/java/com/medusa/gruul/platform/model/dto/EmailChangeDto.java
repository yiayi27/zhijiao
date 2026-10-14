package com.medusa.gruul.platform.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/8/2
 */
@Data
public class EmailChangeDto {

    @Email(message = "邮箱格式错误")
    private String email;
}
