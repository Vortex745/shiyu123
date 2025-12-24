package com.li.lostbackend.common.exception;

import com.li.lostbackend.common.api.Result; // ✅ 改为 li.lostbackend
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 作用：拦截所有 Controller 抛出的异常，统一返回 Result 格式
 */
@Slf4j // 使用 Lombok 的日志注解，方便打印日志
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截所有未知的 Exception 异常
     * 比如：空指针、数组越界、数据库连接失败等
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 1. 在后端控制台打印详细错误堆栈，方便你排查问题（快速定位的关键！）
        log.error("系统运行出现异常: ", e);

        // 2. 告诉前端出错了，并附带简短的错误信息
        // 注意：在实际生产环境中通常只返回 "系统繁忙"，但毕设为了方便调试，我们直接返回 e.getMessage()
        return Result.failed("系统错误: " + e.getMessage());
    }

    /**
     * 你可以在这里继续添加特定的异常处理
     * 例如：处理参数校验失败的异常 (MethodArgumentNotValidException)
     */
}