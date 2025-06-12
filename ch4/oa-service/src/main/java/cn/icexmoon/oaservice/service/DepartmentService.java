package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.DepartmentDTO;
import cn.icexmoon.oaservice.entity.Department;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 70748
 * @description 针对表【department】的数据库操作Service
 * @createDate 2025-05-22 11:06:18
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取根节点部门（不属于部门树）
     *
     * @return 根节点部门
     */
    Department getRootDept();

    /**
     * 分页查询部门信息
     *
     * @param pageNum  当前页码
     * @param pageSize 页宽
     * @return
     */
    Result<IPage<Department>> getPageResult(Long pageNum, Long pageSize);

    /**
     * 级联删除部门
     *
     * @param id 部门id
     */
    boolean cascadeDelete(Long id);

    Result<Long> addDepartment(DepartmentDTO deptDTO);

    /**
     * 根据部门 id 获取部门名称
     *
     * @param deptId 部门id
     * @return 部门名称
     */
    String getDeptName(Long deptId);

    /**
     * 获取完整的部门名称
     *
     * @param deptId 部门id
     * @return 完整部门名称
     */
    String getFullDeptName(Long deptId);

    /**
     * 获取父部门
     *
     * @param deptId 子部门id
     * @return 父部门
     */
    Department getParent(Long deptId);

    /**
     * 获取财务部
     *
     * @return 财务部
     */
    Department getFinanceDepartment();
}
