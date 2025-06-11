package cn.icexmoon.oaservice.util;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/20 14:27
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : Redis 相关的常量
 */
public class RedisConstants {
    // 请求登录时短信验证码的 KEY 前缀
    public static final String KEY_LOGIN_CODE = "login:code:";
    public static final String KEY_TOKEN = "login:token:";
    // 方法缓存前缀
    public static final String KEY_CACHE_METHOD = "cache:method:";
    // 登录时短信验证码的有效时长（秒）
    public static final long TTL_LOGIN_CODE = 5 * 60;
    public static final long TTL_TOKEN = 30 * 60;
}
