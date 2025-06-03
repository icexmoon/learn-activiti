package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.controller
 * @ClassName : .java
 * @createTime : 2025/5/20 17:06
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@RestController
@RequestMapping("/logout")
public class LogoutController {

    @PostMapping
    public Result<?> logout() {
        User user = UserHolder.getUser();
        if (user == null) {
            return Result.success();
        }
        // 清理 UserHolder
        UserHolder.setUser(null);
        return Result.success();
    }
}
