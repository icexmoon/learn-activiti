package cn.icexmoon.oaservice.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static cn.icexmoon.oaservice.util.RedisConstants.KEY_CACHE_METHOD;

/**
 * @ClassName RedisMethodCache
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/11 下午4:53
 * @Version 1.0
 */
public class RedisMethodCache<T> {
    private final String key;
    private final StringRedisTemplate stringRedisTemplate;
    private final Supplier<T> supplier;
//    private final Class<T> cls;
    private final TypeReference<T> typeReference;
    // 默认缓存时长为5秒
    private long expireSeconds = 5;

    public RedisMethodCache(StringRedisTemplate stringRedisTemplate,
                            String methodRedisKey,
                            Supplier<T> supplier,
                            TypeReference<T> typeReference
    ) {
        this.key = KEY_CACHE_METHOD + methodRedisKey;
        this.stringRedisTemplate = stringRedisTemplate;
        this.supplier = supplier;
        this.typeReference = typeReference;
    }

    public RedisMethodCache(StringRedisTemplate stringRedisTemplate,
                            String methodRedisKey,
                            Supplier<T> supplier,
                            TypeReference<T> typeReference,
                            long expireSeconds
    ) {
        this.key = KEY_CACHE_METHOD + methodRedisKey;
        this.stringRedisTemplate = stringRedisTemplate;
        this.supplier = supplier;
        this.typeReference = typeReference;
        this.expireSeconds = expireSeconds;
    }

    public T get() {
        String strValue = stringRedisTemplate.opsForValue().get(key);
        T value = JSON.parseObject(strValue, typeReference);
        if (value != null) {
            // 存在缓存，返回缓存数据
            return value;
        }
        // 没有缓存，通过匿名函数获取数据
        T data = supplier.get();
        if (data == null) {
            return null;
        }
        // 缓存数据
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(data), expireSeconds, TimeUnit.SECONDS);
        // 返回数据
        return data;
    }
}
