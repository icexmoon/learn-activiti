package cn.icexmoon.oaservice;

import cn.icexmoon.activitiutil.ActivitiUtils;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @ClassName ActivitiTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午6:55
 * @Version 1.0
 */
@SpringBootTest
@Log4j2
public class ActivitiTests {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void test() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .list();
        for (ProcessDefinition processDefinition : list) {
            log.info(String.format("流程定义ID：%s，名称：%s, Key：%s",
                    processDefinition.getId(),
                    processDefinition.getName(),
                    processDefinition.getKey()));
        }
    }

    /**
     * 启动一个流程实例
     */
    @Test
    public void testStartProcessInstance() {
        ActivitiUtils activitiUtils = new ActivitiUtils(processEngine);
        activitiUtils.startAndNext("test");
    }
}
