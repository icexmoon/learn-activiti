package cn.icexmoon.demo;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final String TRAVEL_APPLY_DEFINITION_KEY = "travel_apply_listen";

    public ApplicationTests() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void testBpmnDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请")
                .addClasspathResource("bpmn/travel-listen.bpmn20.xml")
                .addClasspathResource("bpmn/travel-listen.png")
                .deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public void testStartProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final String processDefinitionKey = TRAVEL_APPLY_DEFINITION_KEY;
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例id:" + instance.getId());
        System.out.println("流程定义id:" + instance.getProcessDefinitionId());
        System.out.println("当前活动id:" + instance.getActivityId());
    }

    @Test
    public void testStartProcessInstanceWithData() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final String processDefinitionKey = TRAVEL_APPLY_DEFINITION_KEY;
        Map<String, Object> data = new HashMap<>();
        data.put("self", "Brus");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, data);
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

    /**
     * 删除流程部署
     */
    @Test
    public void testDelProcessDefinition() {
        final String DEPLOY_ID = "12501";
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        repositoryService.deleteDeployment(DEPLOY_ID); // 普通删除
        repositoryService.deleteDeployment(DEPLOY_ID, true); // 级联删除
    }

    /**
     * 获取流程定义部署时使用的资源文件
     */
    @Test
    public void testSaveResourceFile() throws IOException {
        final String PROCESS_DEFINITION_ID = "travel_apply:4:25004"; // 流程定义 id
        // 获取流程定义
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(PROCESS_DEFINITION_ID);
        // 获取资源名称
        String bpmnName = processDefinition.getResourceName(); // bpmn资源名称
        String pngName = processDefinition.getDiagramResourceName(); // PNG资源名称
        // 获取资源的输入流
        String deploymentId = processDefinition.getDeploymentId(); // 流程定义所属的部署 id
        InputStream bpmnInputStream = repositoryService.getResourceAsStream(
                deploymentId, //  部署id
                bpmnName); // 资源名
        InputStream pngInputStream = repositoryService.getResourceAsStream(
                deploymentId, //  部署id
                pngName); // 资源名
        // 保存到 classpath 目录
        String classpathDir = this.getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath(); // 当前运行的 classpath 目录
        saveInputStream(bpmnInputStream, classpathDir + "travel.bpmn20.xml");
        saveInputStream(pngInputStream, classpathDir + "travel.png");
    }

    /**
     * 将输入流保存到指定位置的文件
     *
     * @param inputStream 输入流
     * @param savePath    保存的文件位置
     * @throws IOException
     */
    private void saveInputStream(InputStream inputStream, String savePath) throws IOException {
        try (inputStream) {
            OutputStream outputStream = new FileOutputStream(savePath);
            inputStream.transferTo(outputStream);
            outputStream.close();
        }
    }

    /**
     * 查询流程实例的历史活动
     */
    @Test
    public void testQueryHistoryActivities() {
        final String PROCESS_INSTANCE_ID = "27501"; // 流程实例id
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(PROCESS_INSTANCE_ID)
                .orderByHistoricActivityInstanceStartTime().asc() // 按活动开始时间升序
                .list();
        for (HistoricActivityInstance activityInstance : activityInstances) {
            System.out.println("活动实例ID：" + activityInstance.getId());
            System.out.println("活动ID：" + activityInstance.getActivityId());
            System.out.println("活动名称：" + activityInstance.getActivityName());
            System.out.println("活动类型：" + activityInstance.getActivityType());
            System.out.println("活动负责人：" + activityInstance.getAssignee());
            System.out.println("活动开始时间：" + activityInstance.getStartTime());
            System.out.println("活动结束时间：" + activityInstance.getEndTime());
            System.out.println("========================================");
        }
    }

    /**
     * 创建进程实例时附加 businessID
     */
    @Test
    public void testAppendBusinessID() {
        final String PROCESS_DEF_KEY = "travel_apply";
        // 启动进程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String businessId = "1001";
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_DEF_KEY, businessId);
        System.out.println(instance.getBusinessKey());
        TaskService taskService = processEngine.getTaskService();
        completeTask(instance.getId(), taskService);
    }

    /**
     * 完成指定进程实例的最近一个待处理任务
     *
     * @param processInstanceId 进程实例id
     * @param taskService       任务服务
     */
    private void completeTask(String processInstanceId, TaskService taskService) {
        // 获取进程实例的当前需要处理的任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().desc() // 按照任务创建事件降序
                .singleResult();
        // 完成任务
        if (task != null) {
            taskService.complete(task.getId());
            System.out.printf("进程实例（%s）的任务（%s）已完成%n", processInstanceId, task.getId());
        }
    }

    /**
     * 挂起进程实例
     */
    @Test
    public void testSuspendProcessInstance() {
        // 获取一个没有被挂起的出差申请实例
        ProcessInstance instance = getProcessInstance(TRAVEL_APPLY_DEFINITION_KEY, false);
        System.out.println("进程实例ID：" + instance.getId());
        // 挂起进程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.suspendProcessInstanceById(instance.getId());
        // 检查实例状态
        checkInstanceSuspened(instance, runtimeService);
    }

    @Test
    public void testActiveProcessInstance() {
        // 获取一个已挂起的出差申请实例
        ProcessInstance instance = getProcessInstance(TRAVEL_APPLY_DEFINITION_KEY, true);
        System.out.println("进程实例ID：" + instance.getId());
        // 激活进程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.activateProcessInstanceById(instance.getId());
        // 检查实例状态
        checkInstanceSuspened(instance, runtimeService);
    }

    private void checkInstanceSuspened(ProcessInstance instance, RuntimeService runtimeService) {
        instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(instance.getId())
                .singleResult();
        if (instance.isSuspended()) {
            System.out.println("进程实例已挂起");
        } else {
            System.out.println("进程实例已激活");
        }
    }

    /**
     * 获取一个进程实例
     *
     * @param processDefinitionKey 进程定义key
     * @param isSuspended          是否已经被挂起
     * @return 进程实例
     */
    private ProcessInstance getProcessInstance(String processDefinitionKey, boolean isSuspended) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (isSuspended) {
            processInstanceQuery.suspended();
        } else {
            processInstanceQuery.active();
        }
        List<ProcessInstance> processInstances = processInstanceQuery
                .processDefinitionKey(processDefinitionKey)
                .list();
        if (processInstances == null || processInstances.isEmpty()) {
            String statStr = isSuspended ? "未挂起" : "已挂起";
            String errorMsg = "没有找到" + statStr + "的进程实例";
            throw new RuntimeException(errorMsg);
        }
        return processInstances.get(0);
    }

    /**
     * 执行已经被挂起的进程实例的任务
     */
    @Test
    public void testCompleteSuspendedProcessInstanceTask() {
        // 获取一个已经被挂起的进程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .suspended()
                .processDefinitionKey(TRAVEL_APPLY_DEFINITION_KEY)
                .list();
        if (processInstances == null || processInstances.isEmpty()) {
            System.out.println("没有已挂起的进程实例");
            return;
        }
        ProcessInstance instance = processInstances.get(0);
        // 执行进程实例的当前任务
        completeTask(instance.getId(), processEngine.getTaskService());
    }

    /**
     * 挂起进程定义下的所有进程实例
     */
    @Test
    public void testSuspendProcessDefinition() {
        // 挂起进程定义
        processEngine.getRepositoryService().suspendProcessDefinitionByKey(TRAVEL_APPLY_DEFINITION_KEY);
        // 检查进程定义的相关进程实例是否都已经挂起
        checkProcessInstanceSuspended(TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 检查进程定义的相关进程实例是否都已经挂起
     */
    private void checkProcessInstanceSuspended(String processDefinitionKey) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
        for (ProcessInstance instance : list) {
            if (!instance.isSuspended()) {
                System.out.println("进程实例" + instance.getId() + "没有被挂起！");
                return;
            }
        }
        System.out.printf("进程定义（%s）下的所有进程实例都已被挂起%n", TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 检查进程定义的相关进程实例是否都已经恢复
     */
    private void checkProcessInstanceActived(String processDefinitionKey) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
        for (ProcessInstance instance : list) {
            if (instance.isSuspended()) {
                System.out.println("进程实例" + instance.getId() + "依然处于挂起状态！");
                return;
            }
        }
        System.out.printf("进程定义（%s）下的所有进程实例都已被恢复%n", TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 恢复进程定义
     */
    @Test
    public void testActiveProcessDefinition() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.activateProcessDefinitionByKey(TRAVEL_APPLY_DEFINITION_KEY);
        // 查询进程定义挂起状态
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(TRAVEL_APPLY_DEFINITION_KEY)
                .list();
        for (ProcessDefinition processDefinition : list) {
            if (processDefinition.isSuspended()) {
                System.out.printf("进程定义（%s）仍然处于挂起状态！%n", processDefinition.getId());
                return;
            }
        }
        System.out.printf("进程定义（%s）都已恢复%n", TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 挂起进程定义和实例
     */
    @Test
    public void testSuspendProcessDefinitionAndInstance() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.suspendProcessDefinitionByKey(
                TRAVEL_APPLY_DEFINITION_KEY,
                true, // 同时挂起进程实例
                null); // 立即挂起
        checkProcessInstanceSuspended(TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 恢复进程定义和实例
     */
    @Test
    public void testActiveProcessDefinitionAndInstance() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.activateProcessDefinitionByKey(
                TRAVEL_APPLY_DEFINITION_KEY,
                true, // 同时恢复进程实例
                null // 立即执行
        );
        checkProcessInstanceActived(TRAVEL_APPLY_DEFINITION_KEY);
    }

    /**
     * 模拟审批人依次审批
     */
    @Test
    public void testCompleteOneByOne() {
        final String PROCESS_INSTANCE_ID = "90001";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        Task task = completeLastTask(PROCESS_INSTANCE_ID);
    }

    private Task completeLastTask(String processInstanceId) {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().desc()
                .list();
        if (list != null && !list.isEmpty()) {
            Task task = list.get(0);
            taskService.complete(task.getId());
            return task;
        }
        return null;
    }
}
