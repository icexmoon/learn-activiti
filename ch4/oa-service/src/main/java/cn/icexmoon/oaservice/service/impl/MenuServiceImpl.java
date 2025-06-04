package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.icexmoon.oaservice.dto.MenuDTO;
import cn.icexmoon.oaservice.entity.Menu;
import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.mapper.MenuMapper;
import cn.icexmoon.oaservice.service.MenuService;
import cn.icexmoon.oaservice.service.UserService;
import cn.icexmoon.oaservice.util.MenuTree;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.tree.TreeUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 70748
 * @description 针对表【menu(菜单表)】的数据库操作Service实现
 * @createDate 2025-05-26 17:27:05
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
        implements MenuService {
    @Autowired
    private MenuTree menuTree;
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public Menu getTree(Boolean checkPermission) {
        Menu simpleTreeCopy = TreeUtil.getSimpleTreeCopy(menuTree.getTree(), Menu.class, false);
        if (checkPermission == null || !checkPermission){
            return simpleTreeCopy;
        }
        // 删除掉没有权限的菜单
        // 遍历树
        Menu permissionMenu = BeanUtil.copyProperties(simpleTreeCopy, Menu.class, "parent", "children");
        Map<Integer, Role.MenuPermission> roleMenuPermissions = userService.getRoleMenuPermissions();
        Set<Integer> permissionMenuIds = new HashSet<>(roleMenuPermissions.keySet());
        for (Menu child : simpleTreeCopy.getChildren()) {
            traversalTree(child, permissionMenu, permissionMenuIds);
        }
        return permissionMenu;
    }

    private void traversalTree(Menu menu, Menu newMenu, Set<Integer> permissionMenuIds) {
        for (Integer permissionMenuId : permissionMenuIds) {
            if (this.isParent(menu.getId(), permissionMenuId) || menu.getId().equals(permissionMenuId)) {
                // 如果当前菜单节点是有权限的菜单的父节点，保留
                Menu menuCopy = BeanUtil.copyProperties(menu, Menu.class, "parent", "children");
                if (newMenu.getChildren() == null) {
                    newMenu.setChildren(new ArrayList<>());
                }
                newMenu.getChildren().add(menuCopy);
                List<Menu> children = menu.getChildren();
                if (children != null) {
                    for (Menu child : children) {
                        traversalTree(child, menuCopy, permissionMenuIds);
                    }
                }
                break;
            }
        }
    }

    @Override
    public Result<?> add(Menu menu) {
        boolean save = this.save(menu);
        if (!save) {
            return Result.fail("新增菜单失败");
        }
        // 重建菜单树
        menuTree.destroy();
        return Result.success(menu.getId());
    }

    @Override
    public Result<Void> delMenu(Integer id) {
        List<Integer> ids = menuTree.getAllChildrenIds(id);
        ids.add(id);
        boolean removed = this.removeByIds(ids);
        if (removed) {
            // 重新加载菜单树
            menuTree.destroy();
            return Result.success(null, "删除菜单成功");
        }
        return Result.fail("删除菜单失败");
    }

    @Override
    public IPage<Menu> getPagedMenues(Integer pageNum, Integer pageSize) {
        return this.page(new Page<>(pageNum, pageSize));
    }

    @Override
    public Result<Void> edit(MenuDTO dto) {
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        boolean saved = this.updateById(menu);
        if (!saved) {
            return Result.fail("更新菜单失败");
        }
        menuTree.destroy();
        return Result.success(null, "更新菜单成功");
    }

    @Override
    public boolean isParent(Integer parentMenuId, Integer childMenuId) {
        return menuTree.isParent(parentMenuId, childMenuId);
    }
}




