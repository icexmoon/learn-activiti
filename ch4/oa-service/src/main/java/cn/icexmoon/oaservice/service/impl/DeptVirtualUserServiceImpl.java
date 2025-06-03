package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.icexmoon.oaservice.dto.DepartmentUsersDTO;
import cn.icexmoon.oaservice.dto.UserPositionDTO;
import cn.icexmoon.oaservice.entity.DeptVirtualUser;
import cn.icexmoon.oaservice.entity.Position;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.mapper.DeptVirtualUserMapper;
import cn.icexmoon.oaservice.service.DeptVirtualUserService;
import cn.icexmoon.oaservice.service.PositionService;
import cn.icexmoon.oaservice.service.UserService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 70748
 * @description 针对表【dept_virtual_user(部门虚拟员工表)】的数据库操作Service实现
 * @createDate 2025-06-03 14:15:16
 */
@Service
public class DeptVirtualUserServiceImpl extends ServiceImpl<DeptVirtualUserMapper, DeptVirtualUser>
        implements DeptVirtualUserService {
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService positionService;

    @Override
    public Result<Void> add(DepartmentUsersDTO dto) {
        DeptVirtualUser deptVirtualUser = BeanUtil.copyProperties(dto, DeptVirtualUser.class);
        boolean saved = this.save(deptVirtualUser);
        if (saved) {
            return Result.success();
        }
        return Result.fail("添加虚拟员工失败");
    }

    @Override
    public Result<List<User>> listByDeptId(Long deptId) {
        List<DeptVirtualUser> deptVirtualUsers = this.list(new QueryWrapper<DeptVirtualUser>().eq("dept_id", deptId));
        if (deptVirtualUsers == null || deptVirtualUsers.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        Map<Long, DeptVirtualUser> deptVirtualUserMap = deptVirtualUsers.stream().collect(Collectors.toMap(DeptVirtualUser::getUserId, dvu -> dvu));
        // 获取相关员工信息
        List<Long> userIds = deptVirtualUsers.stream().map(DeptVirtualUser::getUserId).toList();
        List<User> users = userService.listByIds(userIds);
        if (users == null || users.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        // 获取职位信息
        Map<Integer, Position> positionMap = positionService.getPositionMap();
        // 为结果添加虚拟职位信息
        for (User user : users) {
            user.setPositionId(deptVirtualUserMap.get(user.getId()).getPositionId());
            user.setPosition(positionMap.get(user.getPositionId()));
        }
        return Result.success(users);
    }

    @Override
    @Transactional
    public Result<Void> edit(Long deptId, List<UserPositionDTO> userPositionDTOS) {
        // 删除部门的虚拟员工
        this.remove(new QueryWrapper<DeptVirtualUser>().eq("dept_id", deptId));
        if (userPositionDTOS == null || userPositionDTOS.isEmpty()) {
            return Result.success();
        }
        // 添加新的部门虚拟员工
        List<DeptVirtualUser> deptVirtualUsers = new ArrayList<>();
        for (UserPositionDTO userPositionDTO : userPositionDTOS) {
            DeptVirtualUser dvu = new DeptVirtualUser();
            dvu.setDeptId(deptId);
            dvu.setUserId(userPositionDTO.getUserId());
            dvu.setPositionId(userPositionDTO.getPositionId());
            deptVirtualUsers.add(dvu);
        }
        boolean savedBatch = this.saveBatch(deptVirtualUsers);
        if (savedBatch) {
            return Result.success();
        }
        return Result.fail("更新部门虚拟员工失败");
    }

    @Override
    public boolean removeByDeptIds(Set<Long> allSubDeptIds) {
        return this.remove(new QueryWrapper<DeptVirtualUser>().in("dept_id", allSubDeptIds));
    }
}




