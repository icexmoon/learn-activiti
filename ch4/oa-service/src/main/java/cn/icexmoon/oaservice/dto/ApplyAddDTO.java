package cn.icexmoon.oaservice.dto;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ApplyDTO
 * @Description 提交申请时的DTO
 * @Author icexmoon@qq.com
 * @Date 2025/6/8 下午4:30
 * @Version 1.0
 */
@Data
public class ApplyAddDTO {
    private Long applyProcessId;
    private Map<String, Object> extraData;
}
