package cn.icexmoon.oaservice.dto;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.dto
 * @ClassName : .java
 * @createTime : 2025/5/28 下午5:32
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Data
public class BindInterfaceDTO {
    private Integer menuId;
    private List<Integer> interfaceIds;
}

