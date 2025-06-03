package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.icexmoon.oaservice.dto.RoleMenuPermitDTO;
import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.mapper.RoleMapper;
import cn.icexmoon.oaservice.service.RoleService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.RoleCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 70748
 * @description 针对表【role(角色表)】的数据库操作Service实现
 * @createDate 2025-05-29 11:37:36
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {
    @Autowired
    @Lazy
    private RoleCache roleCache;

    @Override
    public Result<Integer> addRole(Role role) {
        try {
            boolean saved = save(role);
            if (saved) {
                roleCache.destroy();
                return Result.success(role.getId());
            }
            return Result.fail(-1, "新增角色失败");
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突
            return Result.fail(-1, "角色的 key 不能重复");
        }
    }

    @Override
    public Result<Void> editRole(Role role) {
        try {
            boolean updated = updateById(role);
            if (updated) {
                roleCache.destroy();
                return Result.success(null, "修改角色成功");
            }
            return Result.fail("修改角色失败");
        } catch (DuplicateKeyException e) {
            return Result.fail("角色的 key 不能重复");
        }
    }

    @Override
    public Result<Void> editMenuPermission(RoleMenuPermitDTO roleMenuPermitDTO) {
        Role role = new Role();
        List<Role.MenuPermission> menuPermissions = roleMenuPermitDTO.getMenuPermissions();
        // 不保存增删改查权限都没有的菜单权限设置
        menuPermissions = menuPermissions.stream().filter(mp -> BooleanUtil.isTrue(mp.getView())
                || BooleanUtil.isTrue(mp.getEdit())
                || BooleanUtil.isTrue(mp.getDelete())
                || BooleanUtil.isTrue(mp.getAdd())).toList();
        role.setMenuPermissions(menuPermissions);
        role.setId(roleMenuPermitDTO.getRoleId());
        boolean updated = this.updateById(role);
        if (updated) {
            roleCache.destroy();
            return Result.success();
        }
        return Result.fail(null, "更新角色菜单权限失败");
    }

    @Override
    public Role getRoleByKey(@NonNull String key) {
        List<Role> roles = roleCache.getRoles();
        for (Role role : roles) {
            if (key.equals(role.getKey())) {
                return role;
            }
        }
        return null;
    }

    @Override
    public Map<Integer, Role> getRoleMap() {
        return roleCache.getRoleMap();
    }

    @Override
    public Map<Integer, Role.MenuPermission> getMultiRoleMenuPermissions(@NonNull Collection<Role> roles) {
        if (roles.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Integer, Role.MenuPermission> mergedMenuPermissions = new HashMap<>();
        for (Role role : roles) {
            List<Role.MenuPermission> menuPermissions = role.getMenuPermissions();
            if (menuPermissions == null || menuPermissions.isEmpty()) {
                continue;
            }
            for (Role.MenuPermission menuPermission : menuPermissions) {
                if (!mergedMenuPermissions.containsKey(menuPermission.getMenuId())){
                    mergedMenuPermissions.put(menuPermission.getMenuId(), menuPermission);
                }
                else{
                    Role.MenuPermission currentMenuPermission = mergedMenuPermissions.get(menuPermission.getMenuId());
                    if (BooleanUtil.isTrue(menuPermission.getView())){
                        currentMenuPermission.setView(true);
                    }
                    if (BooleanUtil.isTrue(menuPermission.getEdit())){
                        currentMenuPermission.setEdit(true);
                    }
                    if (BooleanUtil.isTrue(menuPermission.getDelete())){
                        currentMenuPermission.setDelete(true);
                    }
                    if (BooleanUtil.isTrue(menuPermission.getAdd())){
                        currentMenuPermission.setAdd(true);
                    }
                }
            }
        }
        return mergedMenuPermissions;
    }
}




