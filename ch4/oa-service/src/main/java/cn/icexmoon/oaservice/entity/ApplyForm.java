package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 申请表单
 *
 * @TableName apply_form
 */
@TableName(value = "apply_form")
@Data
public class ApplyForm {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表单key
     */
    private String key;

    /**
     * 表单版本，只有最大版本的表单生效
     */
    private Integer version;

    /**
     * 表单名称
     */
    private String name;

    /**
     * 表单 vue 源码路径
     */
    private String path;

    /**
     * 创建表单的时间
     */
    private Date createTime;
}