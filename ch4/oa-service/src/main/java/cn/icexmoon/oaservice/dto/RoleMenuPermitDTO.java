package cn.icexmoon.oaservice.dto;

import cn.icexmoon.oaservice.entity.Role;
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
 * @createTime : 2025/5/29 下午12:24
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Data
public class RoleMenuPermitDTO {
    private Integer roleId;
    private List<Role.MenuPermission> menuPermissions;
}
