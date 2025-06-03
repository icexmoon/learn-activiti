package cn.icexmoon.oaservice.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.dto
 * @ClassName : .java
 * @createTime : 2025/5/19 18:54
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Data
public class LoginDTO {
    // 手机号
    private String phone;
    // 验证码
    private String captcha;
}
