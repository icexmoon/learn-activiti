package cn.icexmoon.oaservice.util;

import cn.icexmoon.oaservice.entity.Menu;
import cn.icexmoon.oaservice.service.MenuService;
import cn.icexmoon.tree.Node;
import cn.icexmoon.tree.Tree;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/26 下午5:31
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Component
public class MenuTree {
    private Tree<Menu> tree;
    private final MenuService menuService;

    public MenuTree(@Lazy MenuService menuService) {
        this.menuService = menuService;
    }

    public Tree<Menu> getTree() {
        if (tree != null) {
            return tree;
        }
        // 初始化菜单树
        tree = initTree();
        return tree;
    }

    /**
     * 初始化树
     *
     * @return 初始化后的树
     */
    private Tree<Menu> initTree() {
        // 加载所有的菜单信息
        List<Menu> menus = menuService.list();
        Tree<Menu> menuTree = new Tree<>(m -> {
            List<Menu> children = new ArrayList<>();
            for (Menu menu : menus) {
                if (menu.getParentId() != null && menu.getParentId().equals(m.getId())) {
                    children.add(menu);
                }
            }
            // 按照菜单权重由高到低排序菜单
            children.sort((o1, o2) -> o1.getWeight().compareTo(o2.getWeight()) * -1);
            return children;
        }, () -> {
            for (Menu menu : menus) {
                if (menu.getParentId() != null && menu.getParentId().equals(0)) {
                    return menu;
                }
            }
            return null;
        });
        return menuTree;
    }

    public void destroy() {
        if (tree != null) {
            tree.destroy();
        }
        tree = null;
    }

    /**
     * 获取所有指定菜单的子菜单id集合
     *
     * @return 子菜单id集合
     */
    public List<Integer> getAllChildrenIds(@NonNull Integer id) {
        List<Integer> ids = new ArrayList<>();
        Node<Menu> node = tree.findNode(n -> id.equals(n.getValue().getId()));
        List<Node<Menu>> allChildren = tree.getAllChildren(node);
        if (allChildren.isEmpty()){
            return ids;
        }
        for (Node<Menu> child : allChildren) {
            ids.add(child.getValue().getId());
        }
        return ids;
    }

    public boolean isParent(Integer parentMenuId, Integer childMenuId) {
        Node<Menu> parentNode = this.findNode(parentMenuId);
        Node<Menu> childNode = this.findNode(childMenuId);
        List<Node<Menu>> allParents = tree.getAllParents(childNode);
        return allParents.contains(parentNode);
    }

    private Node<Menu> findNode(@NonNull Integer menuId) {
        return this.getTree().findNode(n -> menuId.equals(n.getValue().getId()));
    }
}
