package cn.icexmoon.oaservice.entity;

import cn.icexmoon.tree.inter.Nodeable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @TableName department
 */
@TableName(value = "department")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department implements Nodeable<Department> {
    /**
     *
     */
    @EqualsAndHashCode.Include // 只使用 id 属性生成 equals 和 hashCode 方法
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门id，0表示根部门
     */
    private Long parentId;

    /**
     * 部门负责人
     */
    private Long userId;
    /**
     * 父部门
     */
    @TableField(exist = false)
    private Department parent;
    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<Department> children = new ArrayList<>();

    @TableField(exist = false)
    private String fullName;
}