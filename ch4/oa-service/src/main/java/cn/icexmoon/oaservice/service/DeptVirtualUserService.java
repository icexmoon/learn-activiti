package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.DepartmentUsersDTO;
import cn.icexmoon.oaservice.dto.UserPositionDTO;
import cn.icexmoon.oaservice.entity.DeptVirtualUser;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
* @author 70748
* @description 针对表【dept_virtual_user(部门虚拟员工表)】的数据库操作Service
* @createDate 2025-06-03 14:15:16
*/
public interface DeptVirtualUserService extends IService<DeptVirtualUser> {

    Result<Void> add(DepartmentUsersDTO dto);

    /**
     * 获取指定部门的虚拟员工集合
     * @param deptId 部门id
     * @return 虚拟员工集合
     */
    Result<List<User>> listByDeptId(Long deptId);

    /**
     * 更新部门虚拟员工
     * @param deptId 部门id
     * @param userPositionDTOS 虚拟员工集合
     * @return 成功/失败
     */
    Result<Void> edit(Long deptId, List<UserPositionDTO> userPositionDTOS);

    /**
     * 删除部门虚拟员工
     * @param allSubDeptIds 部门id集合
     * @return 成功/失败
     */
    boolean removeByDeptIds(Set<Long> allSubDeptIds);
}
