package cn.icexmoon.oaservice.entity;

import cn.icexmoon.oaservice.annotation.DateTimeJsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 申请流实例
 *
 * @TableName apply_instance
 */
@TableName(value = "apply_instance", autoResultMap = true)
@Data
public class ApplyInstance {
    /**
     * 申请单数据
     */
    @Data
    public static class FormData {
        private String userName; //申请人
        private String phone; //手机号
        private String fullDeptName; //完整部门名称
        private String positionName; //职位名称
        private Map<String, Object> extraData; // 其它数据
    }

    /**
     * 申请实例id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 申请流id
     */
    private Long applyProcessId;
    @TableField(exist = false)
    private ApplyProcess applyProcess;

    /**
     * Activiti流程key
     */
    private String processKey;

    /**
     * 表单id
     */
    private Long formId;

    /**
     * 申请单内数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private FormData formData;

    /**
     * 申请时间
     */
    @DateTimeJsonFormat
    private Date createTime;
}