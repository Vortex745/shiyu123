package com.li.lostbackend.common.api;

import lombok.Data;

/**
 * 统一API响应结果封装
 */
@Data
public class Result<T> {
    // 1. 统一使用 code (Integer 类型更通用)
    private Integer code;

    // 2. 关键修改：字段名改为 msg，因为前端 request.js 和组件里用的是 res.msg
    private String msg;

    private T data;

    // === 成功响应 ===
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    // === 失败响应 (核心修复) ===

    // 修复点 1: 增加 error 方法，解决 PostItemController 里的报错
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    // 修复点 2: 保留 failed 方法，防止其他地方用到报错 (兼容旧代码)
    public static <T> Result<T> failed(String msg) {
        return error(msg);
    }

    // === 权限相关 ===
    public static <T> Result<T> unauthorized(String msg) {
        Result<T> result = new Result<>();
        result.setCode(401);
        result.setMsg(msg);
        return result;
    }
}