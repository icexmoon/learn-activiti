package cn.icexmoon.oaservice.util;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception ex, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        log.error("服务器内部错误", ex);
        Result<Void> result = Result.fail("服务器内部错误");
        String s = JSON.toJSONString(result);
        return s;
    }

    @ExceptionHandler(NoHandlerFoundException.class) // 处理 404
    @ResponseBody
    public String handle404(NoHandlerFoundException ex, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        log.error("请求的资源不存在: {}", ex.getRequestURL());
        Result<Void> result = Result.fail("请求的资源不存在");
        return JSON.toJSONString(result);
    }
}