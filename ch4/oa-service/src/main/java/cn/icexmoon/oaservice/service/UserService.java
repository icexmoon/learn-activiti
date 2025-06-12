package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.UserDTO;
import cn.icexmoon.oaservice.dto.UserInfoDTO;
import cn.icexmoon.oaservice.dto.UserRolesDTO;
import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 70748
 * @description 针对表【users】的数据库操作Service
 * @createDate 2025-05-20 16:40:36
 */
public interface UserService extends IService<User> {
    /**
     * 匹配审批负责人
     *
     * @param applier            申请人id
     * @param approvalPositionId 审批人职位id
     * @return 审批负责人集合
     */
    List<User> matchApprovalUsers(Long applier, Integer approvalPositionId);

    /**
     * 匹配审批负责人
     *
     * @param applier             申请人id
     * @param approvalPositionKey 审批人职位key
     * @return 审批负责人集合
     */
    List<User> matchApprovalUsers(Long applier, String approvalPositionKey);

    /**
     * 获取指定部门的员工（不包含虚拟员工）
     * @param deptId 部门id
     * @return 员工列表
     */
    List<User> getUsersByDeptId(Long deptId);

    /**
     * 根据手机号获取用户（如果不存在，创建）
     *
     * @param phone 手机号
     * @return 用户
     */
    User getByPhone(String phone);

    /**
     * 获取员工的分页信息
     *
     * @param pageNo   当前页码
     * @param pageSize 页宽
     * @return 分页信息
     */
    Result<IPage<User>> getPageResult(Long pageNo, Long pageSize);

    Result<Void> updateUser(UserDTO userDTO);

    /**
     * 编辑用户的角色
     *
     * @param userRolesDTO 用户角色
     * @return 成功/失败
     */
    Result<Void> editPersission(UserRolesDTO userRolesDTO);

    /**
     * 返回特殊用户（设置了角色的）的分页信息
     *
     * @param current 当前页码
     * @param size    页宽
     * @return 分页信息
     */
    Result<IPage<User>> specialUserPage(Long current, Long size);

    /**
     * 按名称或手机号查询特殊用户（没有设置过角色的）
     *
     * @param keyword 用户名或手机号
     * @return 符合结果的用户集合
     */
    Result<List<User>> searchNoRoles(String keyword);

    /**
     * 按名称或手机号查询特殊用户
     *
     * @param keyword 用户名或手机号
     * @return 符合结果的用户集合
     */
    Result<List<User>> search(String keyword);

    /**
     * 判断当前用户是否有权访问接口
     *
     * @param requestURI 接口的url地址
     * @param method     http method
     * @return 是否
     */
    boolean hasPermission(String requestURI, String method);

    /**
     * 获取当前用户拥有的菜单权限
     *
     * @return 菜单权限集合
     */
    Map<Integer, Role.MenuPermission> getRoleMenuPermissions();

    /**
     * 返回用户信息（带角色信息）
     *
     * @param uid 用户id
     * @return 用户
     */
    User getUserById(Long uid);

    /**
     * 获取指定用户的基本信息
     *
     * @param user 用户
     * @return 基本信息
     */
    Result<UserInfoDTO> getInfo(User user);

    /**
     * 获取财务审批人员
     * @return 人员列表
     */
    List<User> getFinanceApprovalUsers();
}
