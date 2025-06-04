package cn.icexmoon.oaservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ProcessDefinitionDTO
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午8:51
 * @Version 1.0
 */
@Data
public class ProcessDefinitionDTO {
    private String id;
    private String deploymentId;
    private String deploymentName;
    private String name;
    private String key;
    private int version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;
    private String resourceName;
    private String diagramResourceName;
}
