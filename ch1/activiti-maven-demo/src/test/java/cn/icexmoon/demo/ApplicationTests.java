package cn.icexmoon.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : activiti-maven-demo
 * @Package : cn.icexmoon.demo
 * @ClassName : .java
 * @createTime : 2025/5/13 11:47
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
public class ApplicationTests {

    private final ProcessEngine processEngine;

    public ApplicationTests() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void testBpmnDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请")
                .addClasspathResource("bpmn/test.bpmn20.xml")
                .addClasspathResource("bpmn/test.bpmn20.png")
                .deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public void testStartProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("test");
        System.out.println("流程实例id:" + instance.getId());
        System.out.println("流程定义id:" + instance.getProcessDefinitionId());
        System.out.println("当前活动id:" + instance.getActivityId());
    }
}
