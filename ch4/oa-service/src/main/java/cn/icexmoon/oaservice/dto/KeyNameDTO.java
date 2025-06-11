package cn.icexmoon.oaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName KeyNameDTO
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/11 下午5:45
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KeyNameDTO {
    @EqualsAndHashCode.Include
    private String key;
    private String name;
}
