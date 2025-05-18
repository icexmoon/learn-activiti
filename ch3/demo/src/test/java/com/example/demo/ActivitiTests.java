package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : demo
 * @Package : com.example.demo
 * @ClassName : .java
 * @createTime : 2025/5/16 21:20
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@SpringBootTest
@Log4j2
public class ActivitiTests {
    @Autowired
    private RepositoryService repositoryService;

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
}
