package cn.icexmoon.demo;

import cn.icexmoon.demo.dto.TravelForm;
import cn.icexmoon.demo.util.ActivitiUtils;
import lombok.extern.log4j.Log4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
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
 * @createTime : 2025/5/15 18:36
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 测试候选人
 */
@Log4j
public class CandidateTests {
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private final String PROCESS_DEFINITION_KEY = "candidate";
    private final ActivitiUtils activitiUtils = new ActivitiUtils();

    /**
     * 部署出差申请
     */
    @Test
    public void deploy() {
        final String BPMN = "bpmn/candidate.bpmn20.xml";
        final String PNG = "bpmn/candidate.png";
        String name = "出差申请v4";
        activitiUtils.deploy(BPMN, PNG, name);
    }

    /**
     * 启动一个出差申请
     */
    @Test
    public void startTravelApply() {
        TravelForm travelForm = new TravelForm("ZhangSan", 6);
        Map<String, Object> variables = new HashMap<>();
        variables.put("form", travelForm);
        activitiUtils.startAndNext(PROCESS_DEFINITION_KEY, variables);
    }

    @Test
    public void testCandidate() {
        // 获取任务
        ProcessInstance lastProcessInstance = activitiUtils.getLastProcessInstance(PROCESS_DEFINITION_KEY);
        Task lastTask = activitiUtils.getLastTask(lastProcessInstance.getId());
        log.info("流程id:" + lastTask.getProcessInstanceId());
        // 获取任务的候选人
        List<String> candidates = activitiUtils.listCandidates(lastTask.getId());
        // 打印候选人可以处理的代办任务
        for (String candidate : candidates) {
            List<Task> tasks = activitiUtils.listCompletableTask(candidate, lastProcessInstance.getProcessDefinitionKey());
            printTasks(candidate, tasks);
        }
    }

    @Test
    public void testClaimAndComplete() {
        activitiUtils.completeTaskWithCheck("Jack", "345011");
    }


    /**
     * 打印任务
     */
    private void printTasks(String userId, List<Task> tasks) {
        log.info("============UserId:" + userId + "============");
        for (Task task : tasks) {
            log.info(String.format("任务id:%s,任务名称:%s", task.getId(), task.getName()));
        }
        log.info("==============================");
    }
}
