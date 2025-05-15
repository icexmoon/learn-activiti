package cn.icexmoon.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
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
 * @createTime : 2025/5/14 20:19
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : uel 相关测试
 */
public class UelTests {
    private final ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private final String TRAVEL_APPLY_DEFINITION_KEY = "travel_apply_uel";

    /**
     * 使用 UEL 设置委托人
     */
    @Test
    public void testUelAssignee() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String businessKey = "1001";
        Map<String, Object> variables = new HashMap<>();
        variables.put("self", "Jack");
        variables.put("manager", "Tom");
        variables.put("highManager", "Brus");
        variables.put("finance", "Jerry");
        runtimeService.startProcessInstanceByKey(
                TRAVEL_APPLY_DEFINITION_KEY,
                businessKey,
                variables
        );
    }
}
