package cn.icexmoon.oaservice.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static cn.icexmoon.oaservice.util.RedisConstants.KEY_TOKEN;
import static cn.icexmoon.oaservice.util.RedisConstants.TTL_TOKEN;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/20 17:47
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 在 Redis 中维护的用户 token
 */
@Component
public class RedisToken {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisToken(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置用户的 token
     *
     * @param token token
     * @param uid   用户id
     */
    public void setToken(String token, long uid) {
        stringRedisTemplate.opsForValue().set(getKey(token), Long.toString(uid), TTL_TOKEN, TimeUnit.SECONDS);
    }

    /**
     * 刷新 token 的有效期
     *
     * @param token token
     */
    public void refreshToken(String token) {
        stringRedisTemplate.expire(getKey(token), TTL_TOKEN, TimeUnit.SECONDS);
    }

    /**
     * 获取对应 token 的用户 id
     *
     * @param token 用户 token
     * @return 用户 id
     */
    public Long getUid(String token) {
        String uidStr = stringRedisTemplate.opsForValue().get(getKey(token));
        if (uidStr == null) {
            return null;
        }
        return Long.valueOf(uidStr);
    }

    /**
     * 获取 token 的 Redis key
     *
     * @param token 用户 token
     * @return redis key
     */
    private String getKey(String token) {
        return KEY_TOKEN + token;
    }

    /**
     * 删除token
     *
     * @param token token
     */
    public void delete(String token) {
        stringRedisTemplate.delete(getKey(token));
    }
}
