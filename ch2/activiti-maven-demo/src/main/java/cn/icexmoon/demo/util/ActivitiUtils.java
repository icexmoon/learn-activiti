package cn.icexmoon.demo.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : activiti-maven-demo
 * @Package : cn.icexmoon.demo.util
 * @ClassName : .java
 * @createTime : 2025/5/15 19:08
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : Activiti 工具类
 */
public class ActivitiUtils {
    private final ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    /**
     * 获取指定进程实例的最近一条待审批任务
     *
     * @param processInstanceId 进程实例id
     * @return 待完成任务
     */
    public Task getLastTask(String processInstanceId) {
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

    /**
     * 获取最新的一个激活的流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return 流程实例
     */
    public ProcessInstance getLastProcessInstance(String processDefinitionKey) {
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
     * 部署出差申请
     *
     * @param bpmn bpmn文件路径
     * @param png  png 文件路径
     * @param name 部署名称
     */
    public void deploy(String bpmn, String png, String name) {
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource(bpmn)
                .addClasspathResource(png)
                .name(name)
                .deploy();
    }

    /**
     * 启动一个流程实例，并且自动完成第一个任务
     *
     * @param PROCESS_DEFINITION_KEY 流程定义key
     * @param variables              流程变量
     */
    public void startAndNext(final String PROCESS_DEFINITION_KEY, Map<String, Object> variables) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                variables);
        Task lastTask = getLastTask(instance.getId());
        processEngine.getTaskService().complete(lastTask.getId());
    }

    /**
     * 返回指定任务实例的候选人列表
     *
     * @param taskId 任务实例id
     * @return 候选人列表
     */
    public List<String> listCandidates(String taskId) {
        TaskService taskService = processEngine.getTaskService();
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(taskId);
        List<String> candidates = new ArrayList<>();
        for (IdentityLink identityLink : identityLinksForTask) {
            if ("candidate".equals(identityLink.getType())) {
                if (identityLink.getUserId() != null) {
                    candidates.add(identityLink.getUserId());
                }
            }
        }
        return candidates;
    }

    /**
     * 列出指定用户可以完成的任务（包括个人任务和作为候选人的任务）
     *
     * @param processDefinitionKey 流程定义key
     * @return 任务列表
     */
    public List<Task> listCompletableTask(String userId, String processDefinitionKey) {
        // 获取个人任务
        TaskService taskService = processEngine.getTaskService();
        return taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .processDefinitionKey(processDefinitionKey)
                .list();
    }

    /**
     * 完成任务（会检查指定用户是否有权限完成该任务）
     *
     * @param userId 指定用户id
     * @param taskId 任务id
     */
    public void completeTaskWithCheck(String userId, String taskId) {
        if (userId == null) {
            throw new RuntimeException("必须指定一个用户id");
        }
        // 检查指定用户是否是任务的委托人
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new RuntimeException(String.format("任务（%s）不存在！", taskId));
        }
        if (!userId.equals(task.getAssignee())) {
            // 指定用户不是任务的委托人
            // 检查指定用户是否是任务的候选人
            List<String> candidates = listCandidates(taskId);
            if (candidates == null || candidates.isEmpty() || !candidates.contains(userId)) {
                // 指定用户不是任务的候选人
                throw new RuntimeException(String.format("用户（%s）不是任务（%s）的委托人或候选人，不能完成任务", userId, taskId));
            }
        }
        // 如果指定用户不是任务的委托人，先获取任务
        if (!userId.equals(task.getAssignee())) {
            taskService.claim(taskId, userId);
        }
        // 完成任务
        taskService.complete(taskId);
    }


    /**
     * 获取任务实例的一个执行人（委托人或候选人）
     *
     * @param taskId 任务实例id
     * @return 用户id
     */
    public String getTaskExecutor(String taskId) {
        // 检查任务有没有委托人，如果有，直接返回
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new RuntimeException(String.format("不存在ID为(%s)的任务实例", taskId));
        }
        if (task.getAssignee() != null) {
            return task.getAssignee();
        }
        // 获取一个候选人并返回
        List<String> candidates = listCandidates(taskId);
        if (candidates == null || candidates.isEmpty()) {
            // 既没有委托人也没有候选人
            return null;
        }
        return candidates.get(0);
    }
}
