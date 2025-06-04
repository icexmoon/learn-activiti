package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.MenuDTO;
import cn.icexmoon.oaservice.entity.Menu;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 70748
 * @description 针对表【menu(菜单表)】的数据库操作Service
 * @createDate 2025-05-26 17:27:05
 */
public interface MenuService extends IService<Menu> {

    /**
     * 返回菜单树（只显示当前用户可以访问的）
     * @return
     */
    Menu getTree(Boolean checkPermission);

    Result<?> add(Menu menu);

    /**
     * 级联删除菜单
     *
     * @return 成功/失败
     */
    Result<Void> delMenu(Integer id);

    /**
     * 获取菜单分页信息
     *
     * @param pageNum  当前页码
     * @param pageSize 页宽
     * @return 菜单分页信息
     */
    IPage<Menu> getPagedMenues(Integer pageNum, Integer pageSize);

    /**
     * 修改菜单
     * @param menu 菜单基本信息
     * @return 成功/失败
     */
    Result<Void> edit(MenuDTO menu);

    /**
     * 判断一个菜单是否为另一个菜单的父菜单
     * @param parentMenuId 父菜单 id
     * @param childMenuId 子菜单 id
     * @return 是否
     */
    boolean isParent(Integer parentMenuId, Integer childMenuId);
}
