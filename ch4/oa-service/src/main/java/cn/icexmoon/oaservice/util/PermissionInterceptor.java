package cn.icexmoon.oaservice.util;

import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.UserService;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/30 上午8:38
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断用户是否已经登录
        User user = UserHolder.getUser();
        if (user == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
            return false;
        }
        // 判断用户是否有访问接口的权限
        boolean hasPermission = userService.hasPermission(request.getRequestURI(), request.getMethod());
        if (!hasPermission){
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Result<Void> result = Result.fail("没有访问该接口的权限");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
