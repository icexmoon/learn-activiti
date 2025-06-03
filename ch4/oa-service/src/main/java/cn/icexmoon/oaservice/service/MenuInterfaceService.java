package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.BindInterfaceDTO;
import cn.icexmoon.oaservice.entity.Interface;
import cn.icexmoon.oaservice.entity.MenuInterface;
import cn.icexmoon.oaservice.entity.Role;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 70748
 * @description 针对表【menu_interface(菜单和接口的绑定关系)】的数据库操作Service
 * @createDate 2025-05-28 17:37:05
 */
public interface MenuInterfaceService extends IService<MenuInterface> {
    /**
     * 绑定菜单和接口关系
     *
     * @param bindInterfaceDTO 绑定关系
     * @return 成功/失败
     */
    Result<Void> bind(BindInterfaceDTO bindInterfaceDTO);

    /**
     * 获取菜单的已绑定接口
     * @param menuId 菜单id
     * @return 已绑定的接口
     */
    Result<List<Interface>> binded(Integer menuId);

    boolean hasPermission(Interface inter, Map<Integer, Role.MenuPermission> multiRoleMenuPermissions);
}
