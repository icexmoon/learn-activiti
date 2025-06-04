package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.BindInterfaceDTO;
import cn.icexmoon.oaservice.dto.MenuDTO;
import cn.icexmoon.oaservice.entity.Interface;
import cn.icexmoon.oaservice.entity.Menu;
import cn.icexmoon.oaservice.service.MenuInterfaceService;
import cn.icexmoon.oaservice.service.MenuService;
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
 * @createTime : 2025/5/26 下午5:27
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@RestController
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuInterfaceService menuInterfaceService;

    /**
     * 获取菜单树的接口
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    public Result<Menu> tree(@RequestParam(required = false) Boolean checkPermission) {
        Menu rootMenu = menuService.getTree(checkPermission);
        return Result.success(rootMenu);
    }

    /**
     * 添加菜单
     *
     * @param menu 新菜单
     * @return 成功/失败
     */
    @PostMapping("/add")
    public Result<?> insert(@RequestBody Menu menu) {
        return menuService.add(menu);
    }

    /**
     * 级联删除菜单
     * @param id 菜单id
     * @return 成功/失败
     */
    @DeleteMapping("/del/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        return menuService.delMenu(id);
    }

    /**
     * 获取菜单列表
     * @return 菜单列表
     */
    @GetMapping("/page")
    public Result<IPage<Menu>> page(@RequestParam Integer pageNum, @RequestParam Integer pageSize ) {
        IPage<Menu> pagedMenus = menuService.getPagedMenues(pageNum, pageSize);
        return Result.success(pagedMenus);
    }

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody MenuDTO menu) {
        return menuService.edit(menu);
    }

    @PostMapping("/bind/interface")
    public Result<Void> bindInterface(@RequestBody BindInterfaceDTO bindInterfaceDTO) {
        return menuInterfaceService.bind(bindInterfaceDTO);
    }

    @GetMapping("/binded/{menuId}")
    public Result<List<Interface>> binded(@PathVariable Integer menuId) {
        return menuInterfaceService.binded(menuId);
    }
}
