package cn.icexmoon.oaservice.util;

import cn.icexmoon.oaservice.entity.Department;
import cn.icexmoon.oaservice.service.DepartmentService;
import cn.icexmoon.tree.Node;
import cn.icexmoon.tree.Tree;
import cn.icexmoon.tree.TreeUtil;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/23 上午10:13
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 部门树
 */
@Component
public class DeptTree {
    private Tree<Department> tree;
    private final DepartmentService departmentService;

    public DeptTree(DepartmentService departmentService) {
        this.departmentService = departmentService;
        this.tree = getDepartmentTree();
    }

    private Tree<Department> getDepartmentTree() {
        if (this.tree != null) {
            return this.tree;
        }
        final Tree<Department> tree;
        List<Department> departments = departmentService.list();
        tree = new Tree<>(dept -> {
            return departments.stream().filter(d -> d.getParentId() != null && d.getParentId().equals(dept.getId())).toList();
        }, () -> {

            for (Department department : departments) {
                if (department.getParentId().equals(0L)) {
                    return department;
                }
            }
            return null;
        });
        return tree;
    }

    public Set<Long> getAllSubDeptIds(@NonNull Long id) {
        Node<Department> currentNode = this.getDepartmentTree().findNode(node -> id.equals(node.getValue().getId()));
        List<Node<Department>> allChildren = this.getDepartmentTree().getAllChildren(currentNode);
        return allChildren.stream().map(n -> n.getValue().getId()).collect(Collectors.toSet());
    }

    public void destroy() {
        this.tree.destroy();
        this.tree = null;
    }

    public String getFullDeptName(@NonNull Long id) {
        final Tree<Department> tree = getDepartmentTree();
        Node<Department> currentNode = tree.findNode(node -> id.equals(node.getValue().getId()));
        List<Node<Department>> allParents = tree.getAllParents(currentNode);
        if (allParents.isEmpty()) {
            return currentNode.getValue().getName();
        }
        List<String> names = allParents.stream().map(n -> n.getValue().getName()).collect(Collectors.toList());
        names.add(currentNode.getValue().getName());
        return String.join("/", names);
    }

    public Department getSimpleTree() {
        return TreeUtil.getSimpleTreeCopy(getDepartmentTree(), Department.class, false);
    }

    /**
     * 根据匿名函数查找符合条件的第一个部门
     *
     * @param predicate 条件
     * @return 符合条件的部门
     */
    public Department findDepartment(Predicate<Department> predicate) {
        Node<Department> findNode = this.tree.findNode(node -> predicate.test(node.getValue()));
        if (findNode == null) {
            return null;
        }
        return findNode.getValue();
    }
}
