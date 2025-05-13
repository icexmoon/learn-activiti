package cn.icexmoon.demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

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

    /**
     * 获取指定用户某个流程定义的待处理任务列表
     */
    @Test
    public void testUserTaskList() {
        final String PROCESS_DEFINE_KEY = "test";
        final String USER_ID = "Jack";
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY)
                .taskAssignee(USER_ID)
                .list();
        for (Task task : tasks) {
            System.out.println("流程实例id" + task.getProcessInstanceId());
            System.out.println("任务id" + task.getId());
            System.out.println("任务负责人" + task.getAssignee());
            System.out.println("任务名称" + task.getName());
        }
    }

    @Test
    public void testCompleteTask() {
        final String TASK_ID = "2505";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(TASK_ID);
    }

    @Test
    public void testProcessInstanceFinish() {
        TaskService taskService = processEngine.getTaskService();
        // 剩余的待审批人
        final List<String> userIds = List.of("Tom", "Brus", "Jerry");
        // 遍历审批人，完成审批动作
        for (String userId : userIds) {
            Task task = taskService.createTaskQuery()
                    .processDefinitionKey("test") // 查询 test 审批流实例
                    .taskAssignee(userId) // 当前审批人
                    .singleResult();// 示例中每个审批人只有1个待审批流程
            taskService.complete(task.getId()); // 完成审批
        }
    }

    /**
     * 用 zip 包完成多个流程的部署
     */
    @Test
    public void testDeployProcessByZip() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        InputStream inputStream = this.getClass().getResourceAsStream("/bpmn/processes.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 获取指定流程定义
     */
    @Test
    public void testQueryProcessDefinitionList() {
        final String PROCESS_DEFINITION_KEY = "test"; // 流程定义的 key
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY) // 只查询指定 key 的流程定义
                .orderByProcessDefinitionVersion().desc() // 结果按照版本降序排列
                .list();
        for (ProcessDefinition processDefinition : definitionList) {
            // 打印单条流程定义信息
            System.out.printf("ID：%s，名称：%s，版本：%d%n",
                    processDefinition.getId(),
                    processDefinition.getName(),
                    processDefinition.getVersion());
        }
    }
}
