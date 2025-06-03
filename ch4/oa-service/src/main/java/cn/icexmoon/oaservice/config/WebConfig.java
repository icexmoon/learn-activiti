package cn.icexmoon.oaservice.config;

import cn.icexmoon.oaservice.util.AuthInterceptor;
import cn.icexmoon.oaservice.util.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.config
 * @ClassName : .java
 * @createTime : 2025/5/20 18:11
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定拦截路径
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/login",
                        "/login/sendCode/*"); // 排除登录和注册接口
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/login/sendCode/*","/logout");
    }
}
