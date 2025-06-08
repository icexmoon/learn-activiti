package cn.icexmoon.oaservice.mapper;

import org.activiti.engine.repository.ProcessDefinition;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ActivitiCustomMapper
 * @Description 自定义 Activiti Mapper
 * @Author icexmoon@qq.com
 * @Date 2025/6/4 下午4:10
 * @Version 1.0
 */
public interface ActivitiCustomMapper {
    /**
     * 查询流程定义
     *
     * @param start                 流程部署开始时间
     * @param end                   流程部署结束时间
     * @param offset                游标
     * @param limit                 返回数据条数
     * @param key                   流程定义key
     * @param processDefinitionName 流程定义名称
     * @param deploymentName        部署名称
     * @return 流程定义集合
     */
    List<ProcessDefinition> customSelectProcessDefinitions(LocalDateTime start, LocalDateTime end, int offset, int limit, String key, String processDefinitionName, String deploymentName);

    /**
     * 查询流程定义数目
     *
     * @param start                 流程部署开始时间
     * @param end                   流程部署结束时间
     * @param key                   流程定义key
     * @param processDefinitionName 流程定义名称
     * @param deploymentName        流程部署名称
     * @return 流程定义集合
     */
    Long customCountProcessDefinitions(LocalDateTime start, LocalDateTime end, String key, String processDefinitionName, String deploymentName);
}
