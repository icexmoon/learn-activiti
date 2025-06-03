package cn.icexmoon.oaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 提供给前端页面的接口
 * @TableName interface
 */
@TableName(value ="interface")
@Data
public class Interface {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String desc;

    /**
     * 接口地址
     */
    private String path;

    /**
     * http 方法
     */
    private String method;

    /**
     * 是否查看类型接口
     */
    private Boolean modeView;

    /**
     * 是否添加类型接口
     */
    private Boolean modeAdd;

    /**
     * 是否修改类型接口
     */
    private Boolean modeEdit;

    /**
     * 是否删除类型接口
     */
    private Boolean modeDel;
}