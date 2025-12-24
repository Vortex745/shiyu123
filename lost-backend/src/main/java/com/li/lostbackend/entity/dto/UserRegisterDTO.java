package com.li.lostbackend.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名/学号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称/姓名不能为空")
    private String nickname;

    private String phone; // 选填
}