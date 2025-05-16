package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.BaseForm;
import cn.icexmoon.demo.dto.TravelForm;
import lombok.extern.log4j.Log4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
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
@Log4j
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
        data.put("form", new TravelForm("ZhangSan", 6));
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
        ProcessInstance lastProcessInstance = getLastProcessInstance(PROCESS_DEFINITION_KEY);
        Task lastTask = getLastTask(lastProcessInstance.getId());
        if (lastTask == null) {
            System.out.println("没有要处理的任务");
            return;
        }
        if ("经理审批".equals(lastTask.getName())) {
            // 经理审批时强行修改出差天数为3天以下
            RuntimeService runtimeService = processEngine.getRuntimeService();
            TravelForm oldForm = runtimeService.getVariable(lastTask.getExecutionId(), "form", TravelForm.class);
            if (oldForm.getDays() > 3) {
                BaseForm newForm = new TravelForm(oldForm.getCreator(), 2);
                // 使用 RuntimeService 直接修改进程变量
                runtimeService.setVariable(lastTask.getExecutionId(), "form", newForm);
            }
        }
        // 完成任务
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(lastTask.getId());
    }

    @Test
    public void completeWithComment() {
        // 审批时添加审批意见
        // 获取最近一条出差申请的用户任务
        ProcessInstance lastProcessInstance = getLastProcessInstance(PROCESS_DEFINITION_KEY);
        Task lastTask = getLastTask(lastProcessInstance.getId());
        if (lastTask == null) {
            System.out.println("没有要处理的任务");
            return;
        }
        TaskService taskService = processEngine.getTaskService();
        String comment = lastTask.getAssignee() + "的审批意见"; //审批意见
        taskService.setVariableLocal(lastTask.getId(), "comment", comment);
        // 完成任务
        taskService.complete(lastTask.getId());
        log.info(lastTask.getName() + "已完成");
    }

    /**
     * 获取最新的一个激活的流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return 流程实例
     */
    private ProcessInstance getLastProcessInstance(String processDefinitionKey) {
        List<ProcessInstance> processInstances = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .active()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessInstanceId().desc()
                .list();
        if (processInstances == null || processInstances.isEmpty()) {
            return null;
        }
        return processInstances.get(0);
    }

    /**
     * 获取最新的额一条记录流程实例
     *
     * @param processDefinitionKey 流程定义 key
     * @return 历史流程实例
     */
    private HistoricProcessInstance getLastHistoricProcessInstance(String processDefinitionKey) {
        List<HistoricProcessInstance> list = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessInstanceStartTime().desc()
                .list();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Test
    public void printComments() {
        // 打印审批流的审批意见
        // 从历史记录中获取最新的一个请假申请实例
        HistoricProcessInstance lastHistoricProcessInstance = getLastHistoricProcessInstance(PROCESS_DEFINITION_KEY);
        final String processInstanceId = lastHistoricProcessInstance.getId();
        log.info("流程实例id：" + processInstanceId);
        HistoryService historyService = processEngine.getHistoryService();
        // 获取历史任务
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().asc()
                .list();
        for (HistoricTaskInstance taskInstance : taskInstances) {
            String assignee = taskInstance.getAssignee();
            Date endTime = taskInstance.getEndTime();
            // 获取任务的审批意见
            HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                    .taskId(taskInstance.getId())
                    .variableName("comment")
                    .singleResult();
            String comment = (String) variableInstance.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = sdf.format(endTime);
            log.info(String.format("审批人：%s，审批时间：%s，审批意见：%s%n", assignee, timeStr, comment));
        }
    }

    /**
     * 获取指定进程定义的最近一条待审批任务
     *
     * @param processInstanceId 进程定义key
     * @return 待完成任务
     */
    private Task getLastTask(String processInstanceId) {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .desc()
                .list();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
