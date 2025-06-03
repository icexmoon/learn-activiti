package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.RoleMenuPermitDTO;
import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.service.RoleService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.RoleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.controller
 * @ClassName : .java
 * @createTime : 2025/5/29 上午11:37
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@RestController
@RequestMapping("role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleCache roleCache;

    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> roles = roleCache.getRoles();
        return Result.success(roles);
    }

    @PostMapping("/add")
    public Result<Integer> add(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody Role role) {
        return roleService.editRole(role);
    }

    @DeleteMapping("/del/{roleId}")
    public Result<Void> del(@PathVariable Integer roleId) {
        boolean removed = roleService.removeById(roleId);
        if (!removed){
            return Result.fail("删除角色失败");
        }
        return Result.success();
    }

    @PostMapping("/menu/permit")
    public Result<Void> permit(@RequestBody RoleMenuPermitDTO roleMenuPermitDTO) {
        return roleService.editMenuPermission(roleMenuPermitDTO);
    }
}
