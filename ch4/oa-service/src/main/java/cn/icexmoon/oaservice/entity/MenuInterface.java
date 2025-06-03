package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 菜单和接口的绑定关系
 *
 * @TableName menu_interface
 */
@TableName(value = "menu_interface")
@Data
public class MenuInterface {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 菜单id
     */
    private Integer menuId;

    /**
     * 接口id
     */
    private Integer interfaceId;
}