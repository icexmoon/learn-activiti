package cn.icexmoon.oaservice;

import lombok.extern.log4j.Log4j2;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

//    @Test
//    public void test() {
//        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
//                .list();
//        for (ProcessDefinition processDefinition : list) {
//            log.info(String.format("流程定义ID：%s，名称：%s, Key：%s",
//                    processDefinition.getId(),
//                    processDefinition.getName(),
//                    processDefinition.getKey()));
//        }
//    }
}
