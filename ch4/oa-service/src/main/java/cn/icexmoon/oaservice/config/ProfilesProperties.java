package cn.icexmoon.oaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.config
 * @ClassName : .java
 * @createTime : 2025/5/20 14:38
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@ConfigurationProperties(prefix = "spring.profiles")
@Data
public class ProfilesProperties {
    private String active;
}
