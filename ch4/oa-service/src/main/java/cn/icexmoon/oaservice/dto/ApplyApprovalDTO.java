package cn.icexmoon.oaservice.dto;

import cn.icexmoon.oaservice.annotation.DateTimeJsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ApplyConfirmDTO
 * @Description 申请审批DTO
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午4:03
 * @Version 1.0
 */
@Data
public class ApplyApprovalDTO {
    private String taskId; // activiti 工作流 task id
    private String userName; //审批人姓名
    private String positionName; //审批人职位
    @DateTimeJsonFormat
    private Date time; //审批时间
    private String opinion;// 审批意见
    private Boolean approved; // 是否已审批
    private String title; // 审批项标题
    private String statusText; // 审批状态文字说明
    private boolean canApproval; // 当前用户是否能审批该环节
}
