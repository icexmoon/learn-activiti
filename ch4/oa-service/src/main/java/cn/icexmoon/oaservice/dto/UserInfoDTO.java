package cn.icexmoon.oaservice.dto;

import lombok.Data;

/**
 * @ClassName UserInfoDTO
 * @Description 用户基本信息
 * @Author icexmoon@qq.com
 * @Date 2025/6/8 下午3:26
 * @Version 1.0
 */
@Data
public class UserInfoDTO {
    private String name;
    private String phone;
    private String deptName;
    private String fullDeptName;
    private String positionName;
}
