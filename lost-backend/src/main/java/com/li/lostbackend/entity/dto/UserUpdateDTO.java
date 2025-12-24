package com.li.lostbackend.entity.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度在 1-20 个字符之间")
    private String nickname;

    private String avatarUrl; // 头像链接 (可以是默认的，也可以是上传的)
}