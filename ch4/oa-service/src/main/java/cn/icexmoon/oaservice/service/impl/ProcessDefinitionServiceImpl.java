package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.mapper.ActivitiCustomMapper;
import cn.icexmoon.oaservice.service.ProcessDefinitionService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.TimeUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ProcessDefitnitionServiceImpl
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午7:07
 * @Version 1.0
 */
@Service
@Log4j2
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {
    // BPMN2 文件后缀名
    private static final String BPMN2_SUFFIX = ".bpmn20.xml";
    // 流程定义图片文件后缀名
    private static final String PNG_SUFFIX = ".png";
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ManagementService managementService;

    @Override
    public Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize, String key, String processDefinitionName) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (!StrUtil.isEmpty(key)) {
            processDefinitionQuery.processDefinitionKeyLike(key);
        }
        if (!StrUtil.isEmpty(processDefinitionName)) {
            processDefinitionQuery.processDefinitionNameLike(processDefinitionName);
        }
        int offset = (int)((pageNum - 1) * pageSize);
        List<ProcessDefinition> processDefinitions = processDefinitionQuery
                .orderByProcessDefinitionVersion().desc()
                .listPage(offset, pageSize.intValue());
        long count = repositoryService.createProcessDefinitionQuery().count();
        Page<ProcessDefinitionDTO> page = new Page<>(pageNum, pageSize);
        List<ProcessDefinitionDTO> dtos = getProcessDefinitionDTOS(processDefinitions);
        page.setRecords(dtos);
        page.setTotal(count);
        return page;
    }

    @Override
    public Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize, String key, String processDefinitionName, String deploymentName, Date start, Date end) {
        // 如果没有流程部署相关查询条件，通过其它API查询
        if (StrUtil.isEmpty(deploymentName) && start == null && end == null) {
            return page(pageNum, pageSize, key, processDefinitionName);
        }
        // 查询流程部署
        // 执行自定义 Mapper 查询
        int offset = (int) ((pageNum - 1) * pageSize);
        int Limit = pageSize.intValue();
        List<ProcessDefinition> processDefinitions = getProcessDefinitions(start, end, offset, Limit, key, processDefinitionName, deploymentName);
        long total = getTotal(start, end, key, processDefinitionName, deploymentName);
        Page<ProcessDefinitionDTO> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        List<ProcessDefinitionDTO> dtos = getProcessDefinitionDTOS(processDefinitions);
        page.setRecords(dtos);
        return page;
    }

    private long getTotal(Date start, Date end, String key, String processDefinitionName, String deploymentName) {
        long total = managementService.executeCustomSql(new AbstractCustomSqlExecution<ActivitiCustomMapper, Long>(ActivitiCustomMapper.class) {
            @Override
            public Long execute(ActivitiCustomMapper activitiCustomMapper) {
                return activitiCustomMapper.customCountProcessDefinitions(TimeUtils.toStartTime(start), TimeUtils.toEndTime(end), key, processDefinitionName, deploymentName);
            }
        });
        return total;
    }

    private List<ProcessDefinition> getProcessDefinitions(Date start, Date end, int offset, int Limit, String key, String processDefinitionName, String deploymentName) {
        List<ProcessDefinition> processDefinitions = managementService.executeCustomSql(new AbstractCustomSqlExecution<ActivitiCustomMapper, List<ProcessDefinition>>(ActivitiCustomMapper.class) {
            @Override
            public List<ProcessDefinition> execute(ActivitiCustomMapper activitiCustomMapper) {
                return activitiCustomMapper.customSelectProcessDefinitions(TimeUtils.toStartTime(start), TimeUtils.toEndTime(end), offset, Limit, key, processDefinitionName, deploymentName);
            }
        });
        return processDefinitions;
    }

    private List<ProcessDefinitionDTO> getProcessDefinitionDTOS(List<ProcessDefinition> processDefinitions) {
        List<ProcessDefinitionDTO> dtos = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            ProcessDefinitionDTO processDefinitionDTO = BeanUtil.copyProperties(processDefinition, ProcessDefinitionDTO.class);
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(processDefinition.getDeploymentId())
                    .singleResult();
            if (deployment != null) {
                processDefinitionDTO.setDeploymentTime(deployment.getDeploymentTime());
                processDefinitionDTO.setDeploymentName(deployment.getName());
            }
            dtos.add(processDefinitionDTO);
        }
        return dtos;
    }

    @Override
    public Result<Void> add(MultipartFile bpmnFile, MultipartFile pngFile, String name) throws IOException {
        if (bpmnFile == null || pngFile == null) {
            return Result.fail("必须包含 BPMN2 文件以及对应的 PNG 文件");
        }
        String bpmn2FileOriginalFilename = bpmnFile.getOriginalFilename();
        String pngFileOriginalFilename = pngFile.getOriginalFilename();
        if (StrUtil.isEmpty(bpmn2FileOriginalFilename)
                || StrUtil.isEmpty(pngFileOriginalFilename)) {
            return Result.fail("文件名不能为空");
        }
        if (!bpmn2FileOriginalFilename.endsWith(BPMN2_SUFFIX)) {
            return Result.fail("bpmn2 文件必须以 " + BPMN2_SUFFIX + " 为后缀名");
        }
        if (!pngFileOriginalFilename.endsWith(PNG_SUFFIX)) {
            return Result.fail("图片文件必须以 " + PNG_SUFFIX + " 为后缀名");
        }
        // PNG 文件名必须与 BPMN2 文件名一致
        String bpmn2SubName = bpmn2FileOriginalFilename.substring(0, bpmn2FileOriginalFilename.length() - BPMN2_SUFFIX.length());
        String pngSubName = pngFileOriginalFilename.substring(0, pngFileOriginalFilename.length() - PNG_SUFFIX.length());
        if (StrUtil.isEmpty(bpmn2SubName) || !bpmn2SubName.equals(pngSubName)) {
            return Result.fail("BPMN2 文件名必须与 PNG 文件名一致");
        }
        // 添加流程定义
        Deployment deploy = repositoryService.createDeployment()
                .name(name)
                .addInputStream(bpmn2FileOriginalFilename, bpmnFile.getInputStream())
                .addInputStream(pngFileOriginalFilename, pngFile.getInputStream())
                .deploy();
        log.info("流程[%s]已部署".formatted(deploy.getId()));
        return Result.success();
    }

    @Override
    public InputStream getResource(String deploymentId, String resourceName) {
        return repositoryService.getResourceAsStream(deploymentId, resourceName);
    }

    @Override
    public Result<Void> delete(String deploymentId, Boolean force) {
        if (BooleanUtil.isTrue(force)) {
            repositoryService.deleteDeployment(deploymentId, true);
        } else {
            try {
                repositoryService.deleteDeployment(deploymentId);
            } catch (PersistenceException e) {
                // 存在没有完成审批的流程实例，不能删除
                return Result.fail("该审批流有申请未完成审批");
            }
        }
        return Result.success();
    }
}
