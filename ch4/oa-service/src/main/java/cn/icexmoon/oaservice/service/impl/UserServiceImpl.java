package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.icexmoon.oaservice.dto.UserDTO;
import cn.icexmoon.oaservice.dto.UserRolesDTO;
import cn.icexmoon.oaservice.entity.*;
import cn.icexmoon.oaservice.mapper.UserMapper;
import cn.icexmoon.oaservice.service.*;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 70748
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2025-05-20 16:40:36
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private InterfaceService interfaceService;
    @Autowired
    private MenuInterfaceService menuInterfaceService;

    @Override
    public User getByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            return user;
        }
        // 没有用户，创建
        User newUser = new User();
        newUser.setPhone(phone);
        this.save(newUser);
        return newUser;
    }

    /**
     * 填充部门名称信息（适用于用户不多的情况，大量查询最好联表查询）
     *
     * @param userList 用户列表
     */
    private void fillDeptName(List<User> userList) {
        // 获取涉及的部门 id 集合
        List<Long> deptIds = userList.stream()
                .map(User::getDeptId)
                .filter(Objects::nonNull) // 过滤掉缺少部门 id 为 null 的
                .toList();
        if (deptIds.isEmpty()) {
            // 没有需要填充名称的部门
            return;
        }
        // 查询相关的部门
        QueryWrapper<Department> qw = new QueryWrapper<>();
        qw.in("id", deptIds)
                .select("id", "name"); // 只需要返回id和部门名称
        List<Department> departments = departmentService.list(qw);
        // 转换为 Map 结构
        Map<Long, Department> departmentMap = departments.stream().collect(Collectors.toMap(Department::getId, dept -> dept));
        // 填充部门名称
        userList.forEach(user -> {
            if (user.getDeptId() != null) {
                Department department = departmentMap.get(user.getDeptId());
                if (department != null) {
                    user.setDeptName(department.getName());
                }
            }
        });
    }

    @Override
    public Result<IPage<User>> getPageResult(Long pageNo, Long pageSize) {
        return queryUserPage(pageNo, pageSize, null);
    }

    private Result<IPage<User>> queryUserPage(Long pageNo, Long pageSize, QueryWrapper<User> qw) {
        IPage<User> page = new Page<>(pageNo, pageSize);
        IPage<User> pageResult = this.page(page, qw);
        if (pageResult.getRecords().isEmpty()) {
            return Result.success(pageResult);
        }
        fillDeptName(pageResult.getRecords());
        fillPosition(pageResult.getRecords());
        fillRoles(pageResult.getRecords());
        return Result.success(pageResult);
    }

    private void fillRoles(List<User> records) {
        Map<Integer, Role> roleMap = roleService.getRoleMap();
        for (User user : records) {
            List<Integer> roleIds = user.getRoleIds();
            user.setRoles(new ArrayList<>());
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    user.getRoles().add(roleMap.get(roleId));
                }
            }
        }
    }

    /**
     * 填充职位信息
     *
     * @param users 用户
     */
    private void fillPosition(List<User> users) {
        // 获取所有职位信息
        Map<Integer, Position> positionMap = positionService.getPositionMap();
        for (User user : users) {
            if (user.getPositionId() != null && positionMap.containsKey(user.getPositionId())) {
                user.setPosition(positionMap.get(user.getPositionId()));
            }
        }
    }

    @Override
    public Result<Void> updateUser(UserDTO userDTO) {
        boolean result = update()
                .set("name", userDTO.getName())
                .set("dept_id", userDTO.getDeptId())
                .set("position_id", userDTO.getPositionId())
                .eq("id", userDTO.getId())
                .update();
        if (result) {
            return Result.success();
        }
        return Result.fail("编辑用户失败");
    }

    @Override
    public Result<Void> editPersission(UserRolesDTO userRolesDTO) {
        List<Integer> roleIds = userRolesDTO.getRoleIds();
        String roleIdsStr = null;
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIdsStr = JSON.toJSONString(roleIds);
        }
        boolean updated = this.update()
                .set("role_ids", roleIdsStr)
                .eq("id", userRolesDTO.getUserId())
                .update();
        if (updated) {
            return Result.success();
        }
        return Result.fail("修改用户角色失败");
    }

    @Override
    public Result<IPage<User>> specialUserPage(Long current, Long size) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.isNotNull("role_ids");
        return queryUserPage(current, size, qw);
    }

    @Override
    public Result<List<User>> searchNoRoles(String keyword) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.isNull("role_ids")
                .and(q -> q.like("name", keyword)
                        .or()
                        .like("phone", keyword));
        List<User> list = this.list(qw);
        return Result.success(list);
    }

    @Override
    public Result<List<User>> search(String keyword) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.like("name", keyword)
                .or()
                .like("phone", keyword);
        List<User> list = this.list(qw);
        return Result.success(list);
    }

    /**
     * 判断用户是否是指定角色
     *
     * @param user 用户信息
     * @param role 角色
     * @return 是否
     */
    private boolean isRole(@NonNull User user, @NonNull Role role) {
        List<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (Role r : roles) {
            if (ObjectUtil.equals(r.getKey(), role.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(String requestURI, String method) {
        // 如果当前用户是 root，放行
        User currentUser = UserHolder.getUser();
        if (isRole(currentUser, roleService.getRoleByKey(Role.ROLE_ROOT))) {
            return true;
        }
        Map<Integer, Role.MenuPermission> multiRoleMenuPermissions = getRoleMenuPermissions();
        if (multiRoleMenuPermissions == null || multiRoleMenuPermissions.isEmpty()) {
            return false;
        }
        // 匹配符合条件的接口
        List<Interface> inters = interfaceService.match(requestURI, method);
        for (Interface inter : inters) {
            if (menuInterfaceService.hasPermission(inter, multiRoleMenuPermissions)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Map<Integer, Role.MenuPermission> getRoleMenuPermissions() {
        User currentUser = UserHolder.getUser();
        // 如果用户没有任何角色，授予一个访客角色
        if (currentUser.getRoles() == null || currentUser.getRoles().isEmpty()) {
            currentUser.setRoles(List.of(roleService.getRoleByKey(Role.ROLE_GUEST)));
        }
        // 获取用户拥有的角色的菜单权限
        Map<Integer, Role.MenuPermission> multiRoleMenuPermissions = roleService.getMultiRoleMenuPermissions(currentUser.getRoles());
        return multiRoleMenuPermissions;
    }

    @Override
    public User getUserById(Long uid) {
        User user = this.getById(uid);
        // 填充角色信息
        fillRoles(List.of(user));
        return user;
    }
}




