package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 部门虚拟员工表
 * @TableName dept_virtual_user
 */
@TableName(value ="dept_virtual_user")
@Data
public class DeptVirtualUser {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 员工id
     */
    private Long userId;

    /**
     * 职位id
     */
    private Integer positionId;
}