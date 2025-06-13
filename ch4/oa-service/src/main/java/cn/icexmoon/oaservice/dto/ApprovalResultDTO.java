package cn.icexmoon.oaservice.dto;

import lombok.Data;

/**
 * @ClassName ApprovalResultDTO
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/13 下午4:26
 * @Version 1.0
 */
@Data
public class ApprovalResultDTO {
    private String taskId; // 要审批的任务id
    private String opinion; // 审批意见
    private Boolean agree; // 是否同意
    private Long userId; // 审批人
    private Long applyInstanceId; // 申请实例id
}
