package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.service.ProcessDefinitionService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    public Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize) {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().desc()
                .listPage(pageNum.intValue() - 1, pageSize.intValue());
        long count = repositoryService.createProcessDefinitionQuery().count();
        Page<ProcessDefinitionDTO> page = new Page<>(pageNum, pageSize);
        List<ProcessDefinitionDTO> dtos = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            dtos.add(BeanUtil.copyProperties(processDefinition, ProcessDefinitionDTO.class));
        }
        page.setRecords(dtos);
        page.setTotal(count);
        return page;
    }

    @Override
    public Result<Void> add(MultipartFile[] files, String name) throws IOException {
        if (files == null || files.length < 2) {
            return Result.fail("必须包含 BPMN2 文件以及对应的 PNG 文件");
        }
        MultipartFile bpmn2File = files[0];
        MultipartFile pngFile = files[1];
        if (bpmn2File == null || pngFile == null) {
            return Result.fail("必须包含 BPMN2 文件以及对应的 PNG 文件");
        }
        String bpmn2FileOriginalFilename = bpmn2File.getOriginalFilename();
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
                .addInputStream(bpmn2FileOriginalFilename, bpmn2File.getInputStream())
                .addInputStream(pngFileOriginalFilename, pngFile.getInputStream())
                .deploy();
        log.info("流程[%s]已部署".formatted(deploy.getId()));
        return Result.success();
    }
}
