package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.DepartmentDTO;
import cn.icexmoon.oaservice.dto.DepartmentUsersDTO;
import cn.icexmoon.oaservice.dto.UserPositionDTO;
import cn.icexmoon.oaservice.entity.Department;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.DepartmentService;
import cn.icexmoon.oaservice.service.DeptVirtualUserService;
import cn.icexmoon.oaservice.util.DeptTree;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
 * @createTime : 2025/5/23 上午11:27
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 部门接口
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DeptTree deptTree;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DeptVirtualUserService deptVirtualUserService;

    /**
     * 获取部门树
     *
     * @return
     */
    @GetMapping("/tree")
    @ResponseBody
    public Result<Department> getDepartmentTree() {
        Department simpleTree = deptTree.getSimpleTree();
        return Result.success(simpleTree);
    }

    @GetMapping("/pageList")
    public Result<IPage<Department>> pageList(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        return departmentService.getPageResult(pageNum, pageSize);
    }

    /**
     * 新增部门
     *
     * @param deptDTO 部门信息
     * @return 新部门的id
     */
    @PostMapping("/add")
    public Result<Long> add(@RequestBody DepartmentDTO deptDTO) {
        return departmentService.addDepartment(deptDTO);
    }


    /**
     * 删除部门
     *
     * @param id 部门id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 级联删除部门
        boolean result = departmentService.cascadeDelete(id);
        if (result) {
            return Result.success();
        }
        return Result.fail("删除部门失败");
    }

    /**
     * 添加部门的虚拟员工
     * @param dto dto
     * @return 成功/失败
     */
    @PostMapping("/user/add")
    public Result<Void> addUser(@RequestBody DepartmentUsersDTO dto) {
        return deptVirtualUserService.add(dto);
    }

    /**
     * 获取部门的所有虚拟员工
     * @param deptId 部门id
     * @return 虚拟员工集合
     */
    @GetMapping("/virtual_user/list/{deptId}")
    public Result<List<User>> users(@PathVariable Long deptId){
        return deptVirtualUserService.listByDeptId(deptId);
    }

    @PostMapping("/virtual_user/edit/{deptId}")
    public Result<Void> editUser(@PathVariable Long deptId, @RequestBody List<UserPositionDTO> userPositionDTOS) {
        return deptVirtualUserService.edit(deptId, userPositionDTOS);
    }
}
