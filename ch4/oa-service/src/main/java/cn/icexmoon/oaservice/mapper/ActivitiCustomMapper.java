package cn.icexmoon.oaservice.mapper;

import org.activiti.engine.repository.ProcessDefinition;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ActivitiCustomMapper
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/4 下午4:10
 * @Version 1.0
 */
public interface ActivitiCustomMapper {
    List<ProcessDefinition> customSelectProcessDefinitions(LocalDateTime start, LocalDateTime end, int offset, int limit, String key, String processDefinitionName, String deploymentName);

    Long customCountProcessDefinitions(LocalDateTime start, LocalDateTime end, String key, String processDefinitionName, String deploymentName);
}
