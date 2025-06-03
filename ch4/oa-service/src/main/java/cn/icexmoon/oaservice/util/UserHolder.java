package cn.icexmoon.oaservice.util;

import cn.icexmoon.oaservice.entity.User;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/20 17:09
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
public class UserHolder {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();
    public static User getUser() {
        return userThreadLocal.get();
    }

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }
}
