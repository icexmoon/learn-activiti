package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

/**
 * 角色表
 *
 * @TableName role
 */
@TableName(value = "role", autoResultMap = true)
@Data
public class Role {
    // 特殊的硬编码角色
    public static final String ROLE_GUEST = "guest";
    public static final String ROLE_ROOT = "root";
    public static final String ROLE_ADMIN = "admin";

    /**
     * 菜单权限
     */
    @Data
    public static class MenuPermission {
        private Integer menuId;
        private Boolean view;
        private Boolean add;
        private Boolean edit;
        private Boolean delete;
    }

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String key;

    /**
     *
     */
    private String name;
    /**
     * 菜单权限
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<MenuPermission> menuPermissions;

    public boolean isRole(String roleKey) {
        if (roleKey == null) {
            return false;
        }
        return roleKey.equals(this.getKey());
    }
}