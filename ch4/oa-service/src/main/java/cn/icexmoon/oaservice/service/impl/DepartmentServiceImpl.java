package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.icexmoon.oaservice.dto.DepartmentDTO;
import cn.icexmoon.oaservice.entity.Department;
import cn.icexmoon.oaservice.mapper.DepartmentMapper;
import cn.icexmoon.oaservice.service.DepartmentService;
import cn.icexmoon.oaservice.service.DeptVirtualUserService;
import cn.icexmoon.oaservice.util.DeptTree;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author 70748
 * @description 针对表【department】的数据库操作Service实现
 * @createDate 2025-05-22 11:06:18
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
        implements DepartmentService {
    @Lazy
    @Autowired
    private DeptTree deptTree;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private DeptVirtualUserService deptVirtualUserService;

    @Override
    public Department getRootDept() {
        Department department = this.getOne(new QueryWrapper<Department>().eq("parent_id", 0L));
        if (department == null) {
            throw new RuntimeException("系统中没有根部门");
        }
        return department;
    }

    @Override
    public Result<IPage<Department>> getPageResult(Long pageNum, Long pageSize) {
        IPage<Department> page = new PageDTO<>(pageNum, pageSize);
        IPage<Department> pageResult = page(page);
        // 填充完成部门名称
        List<Department> departments = pageResult.getRecords();
        fillFullName(departments);
        return Result.success(pageResult);
    }

    @Override
    @Transactional
    public boolean cascadeDelete(Long id) {
        // 获取部门的所有子部门
        Set<Long> allSubDeptIds = deptTree.getAllSubDeptIds(id);
        allSubDeptIds.add(id);
        // 删除部门和子部门
        DepartmentService departmentService = beanFactory.getBean(DepartmentService.class);
        boolean result = departmentService.removeBatchByIds(allSubDeptIds);
        if (result){
            // 级联删除部门虚拟员工表
            deptVirtualUserService.removeByDeptIds(allSubDeptIds);
        }
        // 销毁部门树
        deptTree.destroy();
        return result;
    }

    private void fillFullName(List<Department> departments) {
        if (departments != null && !departments.isEmpty()) {
            for (Department department : departments) {
                String fullDeptName = deptTree.getFullDeptName(department.getId());
                department.setFullName(fullDeptName);
            }
        }
    }

    @Override
    public Result<Long> addDepartment(DepartmentDTO deptDTO) {
        Department department = BeanUtil.copyProperties(deptDTO, Department.class);
        save(department);
        // 销毁部门树
        deptTree.destroy();
        return Result.success(department.getId(), "添加部门成功");
    }

}




