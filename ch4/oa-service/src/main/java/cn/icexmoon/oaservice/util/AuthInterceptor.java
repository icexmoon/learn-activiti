package cn.icexmoon.oaservice.util;

import cn.hutool.core.util.StrUtil;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.UserService;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/20 17:36
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final RedisToken redisToken;
    private final UserService userService;

    public AuthInterceptor(RedisToken redisToken, UserService userService) {
        this.redisToken = redisToken;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头中获取 Authorization 标头
        String authHeader = request.getHeader("Authorization");

        // 2. 检查是否包含 token（假设 token 格式为 "Bearer <token>"）
        if (authHeader == null || StrUtil.isEmpty(authHeader)) {
            // 如果没有 token，返回 401 未授权
            setLoginErrorResponse(response, "当前用户没有登录，请先登录");
            return false; // 拦截请求，不继续执行后续逻辑
        }

        // 3. 提取 token（去掉 "Bearer " 前缀）
        String token = authHeader;

        // 4. 验证 token（这里简单示例，实际应调用 JWT 或数据库验证）
        Long uid = redisToken.getUid(token);
        if (uid == null) {
            // 如果 token 无效，返回 401
            setLoginErrorResponse(response, "长时间未操作，登录状态已失效，请重新登录！");
            return false;
        }

        // 5. 如果 token 有效，继续执行后续逻辑
        // 查询用户信息
        User user = userService.getUserById(uid);
        if (user == null) {
            // 查不到对应的用户信息，视作登录失效
            setLoginErrorResponse(response, "长时间未操作，登录状态已失效，请重新登录！");
            return false;
        }
        // 刷新 token 有效期
        redisToken.refreshToken(token);
        // 写入本地用户信息
        UserHolder.setUser(user);
        return true;
    }

    private static void setLoginErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Void> fail = Result.fail(message);
        response.getWriter().write(JSON.toJSONString(fail));
    }
}
