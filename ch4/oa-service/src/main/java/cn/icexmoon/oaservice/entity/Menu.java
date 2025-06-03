package cn.icexmoon.oaservice.entity;

import cn.icexmoon.tree.inter.Nodeable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * 菜单表
 */
@Data
public class Menu implements Nodeable<Menu> {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单id(0表示根菜单)
     */
    private Integer parentId;

    /**
     * icn图标
     */
    private String icon;
    /**
     * 路由
     */
    private String path;
    /**
     * 权重
     */
    private Integer weight;

    /**
     * 父菜单
     */
    @TableField(exist = false)
    private Menu parent;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<Menu> children;
}