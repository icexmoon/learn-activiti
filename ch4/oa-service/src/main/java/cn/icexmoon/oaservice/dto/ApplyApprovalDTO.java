package cn.icexmoon.oaservice.dto;

import java.util.Date;

/**
 * @ClassName ApplyConfirmDTO
 * @Description 申请审批DTO
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午4:03
 * @Version 1.0
 */
public class ApplyApprovalDTO {
    private String userName; //审批人姓名
    private String positionName; //审批人职位
    private Date time; //审批时间
    private String opinion;// 审批意见
    private Boolean approved; // 是否已审批
}
