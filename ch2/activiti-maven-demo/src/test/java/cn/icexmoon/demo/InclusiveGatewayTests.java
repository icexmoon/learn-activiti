package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.ProjectForm;
import cn.icexmoon.demo.util.ActivitiUtils;
import lombok.extern.log4j.Log4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : activiti-maven-demo
 * @Package : cn.icexmoon.demo
 * @ClassName : .java
 * @createTime : 2025/5/16 14:22
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 包含网关测试用例
 */
@Log4j
public class InclusiveGatewayTests {
    private ActivitiUtils activitiUtils = new ActivitiUtils();
    private final String PROCESS_DEFINITION_KEY = "inclusive";
    private final String TASK_TECHNICAL_MANAGER_APPROVAL = "技术经理审批";
    private final String TASK_PROJECT_MANAGER_APPROVAL = "项目经理审批";
    private final String TASK_FINANCE_APPROVAL = "财务审批";
    private final String TASK_GENERAL_MANAGER_APPROVAL = "总经理审批";

    /**
     * 部署测试流程
     */
    @Test
    public void deploy() {
        String bpmn = "bpmn/inclusive.bpmn20.xml";
        String png = "bpmn/inclusive.png";
        activitiUtils.deploy(bpmn, png, "包含网关测试流程");
    }

    /**
     * 启动流程实例，并指定项目预算金额
     * @param amount 项目预算金额
     * @return 流程实例id
     */
    private String startProcessInstance(int amount) {
        ProjectForm projectForm = new ProjectForm("ZhangSan");
        projectForm.setAmount(amount);
        projectForm.setName("OA自动化办公系统");
        String processInstanceId = activitiUtils.startAndNext(PROCESS_DEFINITION_KEY, projectForm);
        log.info("项目立项申请已创建");
        activitiUtils.printCurrentTasks(processInstanceId);
        return processInstanceId;
    }

    @Test
    public void testInclusive() {
        // 测试预算小于1万时候的流程
        String processInstanceId = startProcessInstance(1000);
        // 此时应该只需要技术经理审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_TECHNICAL_MANAGER_APPROVAL));
        Assert.assertFalse(activitiUtils.isCurrentTask(processInstanceId, TASK_PROJECT_MANAGER_APPROVAL));
        Assert.assertFalse(activitiUtils.isCurrentTask(processInstanceId, TASK_FINANCE_APPROVAL));
        // 模拟技术经理审批
        simulateTaskExecution(processInstanceId, TASK_TECHNICAL_MANAGER_APPROVAL);
        // 技术经理审批后直接打到总经理审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_GENERAL_MANAGER_APPROVAL));
        // 模拟总经理审批
        simulateTaskExecution(processInstanceId, TASK_GENERAL_MANAGER_APPROVAL);
    }

    @Test
    public void testInclusive2() {
        // 测试预算大于1万小于5万的流程
        String processInstanceId = startProcessInstance(20000);
        // 审批流创建后，应当由项目经理和技术经理共同审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_PROJECT_MANAGER_APPROVAL));
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_TECHNICAL_MANAGER_APPROVAL));
        Assert.assertFalse(activitiUtils.isCurrentTask(processInstanceId, TASK_FINANCE_APPROVAL));
        // 模拟项目经理审批
        simulateTaskExecution(processInstanceId, TASK_PROJECT_MANAGER_APPROVAL);
        // 此时还需要技术经理审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_TECHNICAL_MANAGER_APPROVAL));
        Assert.assertFalse(activitiUtils.isCurrentTask(processInstanceId, TASK_GENERAL_MANAGER_APPROVAL));
        // 模拟技术经理审批
        simulateTaskExecution(processInstanceId, TASK_TECHNICAL_MANAGER_APPROVAL);
        // 技术经理和项目经理都审批完，到总经理审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, TASK_GENERAL_MANAGER_APPROVAL));
        // 模拟总经理审批
        simulateTaskExecution(processInstanceId, TASK_GENERAL_MANAGER_APPROVAL);
    }

    @Test
    public void testInclusive3(){
        // 测试预算大于5万的流程
        String processInsId = startProcessInstance(60000);
        // 提交申请后需要项目经理、技术经理和财务的共同审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_PROJECT_MANAGER_APPROVAL));
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_TECHNICAL_MANAGER_APPROVAL));
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_FINANCE_APPROVAL));
        // 模拟项目经理审批
        simulateTaskExecution(processInsId, TASK_PROJECT_MANAGER_APPROVAL);
        // 现在还需要技术经理和财务审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_FINANCE_APPROVAL));
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_TECHNICAL_MANAGER_APPROVAL));
        // 模拟财务审批
        simulateTaskExecution(processInsId, TASK_FINANCE_APPROVAL);
        // 模拟技术经理审批
        simulateTaskExecution(processInsId, TASK_TECHNICAL_MANAGER_APPROVAL);
        // 流程流转到总经理审批
        Assert.assertTrue(activitiUtils.isCurrentTask(processInsId, TASK_GENERAL_MANAGER_APPROVAL));
        // 模拟总经理审批
        simulateTaskExecution(processInsId, TASK_GENERAL_MANAGER_APPROVAL);
    }

    private void simulateTaskExecution(String processInstanceId, String taskName) {
        activitiUtils.completeTask(processInstanceId, taskName);
        log.info(taskName +"已执行");
        activitiUtils.printCurrentTasks(processInstanceId);
    }
}
