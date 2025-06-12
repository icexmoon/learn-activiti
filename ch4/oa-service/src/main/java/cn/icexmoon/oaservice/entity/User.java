package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @TableName user
 */
@TableName(value = "user", autoResultMap = true)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    /**
     *
     */
    @EqualsAndHashCode.Include
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 所属部门
     */
    private Long deptId;

    /**
     * 兼职部门虚拟
     */
//    private String exDepts;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String DeptName;

    /**
     * 职位
     */
    private Integer positionId;

    @TableField(exist = false)
    private Position position;

    /**
     * 角色 id 集合
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> roleIds;

    @TableField(exist = false)
    private List<Role> roles;
}