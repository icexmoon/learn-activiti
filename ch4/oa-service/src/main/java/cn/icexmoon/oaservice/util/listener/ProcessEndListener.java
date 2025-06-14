package cn.icexmoon.oaservice.util.listener;

import cn.icexmoon.oaservice.service.ApplyInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName ProcessEndListener
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/14 上午10:44
 * @Version 1.0
 */
@Component("processEndListener")
@Slf4j
public class ProcessEndListener implements ExecutionListener {
    @Autowired
    private ApplyInstanceService applyInstanceService;

    @Override
    public void notify(DelegateExecution execution) {
        // 获取流程实例ID和业务键
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();
        // 执行业务逻辑（如数据清理、通知等）
        log.info("流程结束，实例ID：" + processInstanceId + "，业务键：" + businessKey);
        applyInstanceService.endProcess(processInstanceId);
    }
}
