package cn.icexmoon.oaservice.dto;

import lombok.Data;

/**
 * @ClassName DepartmentUsersDTO
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午2:22
 * @Version 1.0
 */
@Data
public class DepartmentUsersDTO {
    private Long deptId;
    private Long userId;
    private Integer positionId;
}
