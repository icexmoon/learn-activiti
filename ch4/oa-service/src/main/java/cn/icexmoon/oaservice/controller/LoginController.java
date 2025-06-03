package cn.icexmoon.oaservice.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.icexmoon.oaservice.config.ProfilesProperties;
import cn.icexmoon.oaservice.dto.LoginDTO;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.UserService;
import cn.icexmoon.oaservice.util.RedisToken;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static cn.icexmoon.oaservice.util.RedisConstants.KEY_LOGIN_CODE;
import static cn.icexmoon.oaservice.util.RedisConstants.TTL_LOGIN_CODE;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.controller
 * @ClassName : .java
 * @createTime : 2025/5/19 18:50
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProfilesProperties profilesProperties;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisToken redisToken;

    @PostMapping
    public Result<?> login(@RequestBody LoginDTO loginDTO) {
        // 检查验证码是否正确
        String phone = loginDTO.getPhone();
        if (null == phone || "".equals(phone.trim())) {
            return Result.fail("手机号不能为空");
        }

        String targetCode = stringRedisTemplate.opsForValue().get(KEY_LOGIN_CODE + phone);
        if (null == targetCode) {
            return Result.fail("验证码已经失效，请重新获取验证码！");
        }
        if (!targetCode.equals(loginDTO.getCaptcha())){
            return Result.fail("验证码不正确！");
        }
        // 生成一个 token
        String token = UUID.randomUUID().toString(true);
        // 用手机号查找用户
        User user = userService.getByPhone(phone);
        // 将登录信息写入 Redis
        redisToken.setToken(token, user.getId());
        // 写入 userHolder
        UserHolder.setUser(user);
        // 返回 token
        return Result.success(token, "登录成功");
    }

    /**
     * 发送手机验证码
     *
     * @return
     */
    @PutMapping("/sendCode/{phone}")
    public Result<?> sendCode(@PathVariable("phone") String phone) {
        // 生成随机验证码
        String code = RandomUtil.randomNumbers(4);
        // 将验证码存储在 Redis
        stringRedisTemplate.opsForValue().set(KEY_LOGIN_CODE + phone, code, TTL_LOGIN_CODE, TimeUnit.SECONDS);
        // 开发环境返回验证码内容
        if (profilesProperties.getActive().equals("dev")) {
            return Result.success(code);
        }
        return Result.success();
    }
}
