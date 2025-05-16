package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.TravelForm;
import cn.icexmoon.demo.util.ActivitiUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : activiti-maven-demo
 * @Package : cn.icexmoon.demo
 * @ClassName : .java
 * @createTime : 2025/5/16 10:26
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 排它网关测试用例
 */
public class ExclusionTests {
    private final ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private final ActivitiUtils activitiUtils = new ActivitiUtils();
    private final String PROCESS_DEFINITION_KEY = "exclustion";

    /**
     * 部署流程
     */
    @Test
    public void testDeploy() {
        String PNG = "bpmn/exclusion.png";
        String BPMN = "bpmn/exclusion.bpmn20.xml";
        activitiUtils.deploy(BPMN, PNG, "排它网关测试");
    }

    /**
     * 启动实例
     */
    @Test
    public void testStartInstance(){
        Map<String, Object> variables = new HashMap<>();
        variables.put("form", new TravelForm("ZhangSan", 2));
        activitiUtils.startAndNext(PROCESS_DEFINITION_KEY, variables);
    }

    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask(){
        ProcessInstance lastProcessInstance = activitiUtils.getLastProcessInstance(PROCESS_DEFINITION_KEY);
        Task lastTask = activitiUtils.getLastTask(lastProcessInstance.getId());
        String executor = activitiUtils.getTaskExecutor(lastTask.getId());
        activitiUtils.completeTaskWithCheck(executor, lastTask.getId());
    }
}
