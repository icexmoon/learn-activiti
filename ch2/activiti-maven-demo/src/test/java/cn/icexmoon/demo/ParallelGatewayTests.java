package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.ProjectForm;
import cn.icexmoon.demo.util.ActivitiUtils;
import lombok.extern.log4j.Log4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
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
 * @createTime : 2025/5/16 12:03
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 并行网关测试用例
 */
@Log4j
public class ParallelGatewayTests {
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private ActivitiUtils activitiUtils = new ActivitiUtils();
    private final String PROCESS_DEFINITION_KEY = "parallel";

    /**
     * 部署测试流程
     */
    @Test
    public void deploy() {
        String bpmn = "bpmn/parallel.bpmn20.xml";
        String png = "bpmn/parallel.png";
        activitiUtils.deploy(bpmn, png, "并行网关测试流程");
    }

    /**
     * 测试并行网关
     */
    @Test
    public void testParallelGateway() {
        // 启动一个测试流程
        ProjectForm form = new ProjectForm("ZhangSan");
        form.setName("OA系统");
        form.setAmount(10000);
        String processInstanceId = activitiUtils.startAndNext(PROCESS_DEFINITION_KEY, form);
        // 打印当前可以执行的任务
        activitiUtils.printCurrentTasks(processInstanceId);
        // 模拟项目经理审批
        activitiUtils.completeTask(processInstanceId, "项目经理审批");
        log.info("项目经理已审批");
        // 打印当前可以执行的任务
        activitiUtils.printCurrentTasks(processInstanceId);
        // 单一审批节点通过后，因为并行网关的原因，下一个审批节点（总经理审批）依然不会生效
        Assert.assertFalse(activitiUtils.isCurrentTask(processInstanceId, "总经理审批"));
        // 模拟技术经理审批
        activitiUtils.completeTask(processInstanceId, "技术经理审批");
        log.info("技术经理已审批");
        // 并行网关的两个分支都已完成，应当流转到总经理审批
        activitiUtils.printCurrentTasks(processInstanceId);
        Assert.assertTrue(activitiUtils.isCurrentTask(processInstanceId, "总经理审批"));
        // 模拟总经理审批
        activitiUtils.completeTask(processInstanceId, "总经理审批");
        log.info("总经理已审批");
    }
}
