package cn.icexmoon.oaservice.util;

import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.service.RoleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/29 下午6:04
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Component
public class RoleCache {
    private final RoleService roleService;
    private List<Role> roles = null;

    public RoleCache(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<Role> getRoles() {
        if (roles != null && !roles.isEmpty()) {
            return roles;
        }
        roles = roleService.list();
        return roles;
    }

    public Map<Integer, Role> getRoleMap(){
        List<Role> roles = getRoles();
        return roles.stream().collect(Collectors.toMap(r->r.getId(), r->r ));
    }

    public void destroy(){
        roles = null;
    }
}
