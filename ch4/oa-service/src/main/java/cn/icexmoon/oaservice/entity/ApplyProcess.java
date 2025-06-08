package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 申请流
 *
 * @TableName apply_process
 */
@TableName(value = "apply_process", autoResultMap = true)
@Data
public class ApplyProcess {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请名称
     */
    private String name;

    /**
     * 可以使用申请的职位
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Integer[] positionIds;

    /**
     * 申请使用的activiti流程的key
     */
    private String processKey;

    /**
     * 关联的表单key
     */
    private String formKey;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 是否启用
     */
    private Boolean enable;
}