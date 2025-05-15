package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.TravelForm;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : activiti-maven-demo
 * @Package : cn.icexmoon.demo
 * @ClassName : .java
 * @createTime : 2025/5/15 11:02
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 条件分支测试用例
 */
public class ConditionTests {
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private final String PROCESS_DEFINITION_KEY = "travel-condition";

    /**
     * 部署出差申请
     */
    @Test
    public void deploy() {
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("bpmn/travel-condition.bpmn20.xml")
                .addClasspathResource("bpmn/travel-condition.png")
                .name("出差申请v3")
                .deploy();
    }

    /**
     * 启动一个出差申请
     */
    @Test
    public void startTravelApply() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> data = new HashMap<>();
        data.put("form", new TravelForm("ZhangSan", 5));
        runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                data);
    }

    /**
     * 完成最近的一条出差申请审批
     */
    @Test
    public void completeLastTask() {
        // 获取最近一条出差申请的用户任务
        Task lastTask = getLastTask(PROCESS_DEFINITION_KEY);
        // 完成任务
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(lastTask.getId());
    }

    /**
     * 获取指定进程定义的最近一条待审批任务
     *
     * @param processDefinitionKey 进程定义key
     * @return 待完成任务
     */
    private Task getLastTask(String processDefinitionKey) {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByTaskCreateTime()
                .desc()
                .list();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
