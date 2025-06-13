package cn.icexmoon.oaservice.entity;

import cn.icexmoon.oaservice.annotation.DateTimeJsonFormat;
import cn.icexmoon.oaservice.dto.ApplyApprovalDTO;
import cn.icexmoon.oaservice.util.IDescEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.util.List;
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
     * 审批状态
     */
    public enum ApprovalStatus implements IDescEnum<Integer> {
        PENDING_APPROVAL(0,"待审批"),
        UNDER_APPROVAL(1,"审批中"),
        PASSED(2,"已通过"),
        FAILED(3,"未通过");

        ApprovalStatus(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }
        private Integer value;
        private String desc;

        public static ApprovalStatus valueOf(@NonNull Integer statusVal) {
            for (ApprovalStatus value : ApprovalStatus.values()) {
                if (statusVal.equals(value.getValue())) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }

        @Override
        public Integer getValue() {
            return this.value;
        }
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
    @TableField(exist = false)
    private ApplyForm applyForm;

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

    /**
     * 申请实例的审批记录
     */
    @TableField(exist = false)
    private List<ApplyApprovalDTO> approvalDTOS;

    /**
     * Activiti 工作流实例id
     */
    private String processInstanceId;

    /**
     * 审批状态
     */
    private ApprovalStatus status;
}