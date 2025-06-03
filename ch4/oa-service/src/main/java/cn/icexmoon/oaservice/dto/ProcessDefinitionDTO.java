package cn.icexmoon.oaservice.dto;

import lombok.Data;

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
    private String name;
    private String key;
    private int version;
}
