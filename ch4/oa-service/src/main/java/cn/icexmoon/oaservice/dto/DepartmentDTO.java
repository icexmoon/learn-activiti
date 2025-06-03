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
 * @createTime : 2025/5/23 下午12:43
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Data
public class DepartmentDTO {
    /**
     * 父部门id
     */
    private Long parentId;
    /**
     * 部门名称
     */
    private String name;
}
