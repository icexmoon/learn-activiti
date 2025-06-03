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
 * @createTime : 2025/5/28 下午3:39
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Data
public class MenuDTO {
    private Integer id;
    private String name;
    private String icon;
    private String path;
}
