package cn.icexmoon.oaservice.util.listener;

import cn.icexmoon.oaservice.entity.ApplyInstance;
import cn.icexmoon.oaservice.entity.Position;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.service.UserService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ReAssigneeListener
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午4:44
 * @Version 1.0
 */
@Component("reAssigneeListener")
public class ReAssigneeListener implements TaskListener {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ApplyInstanceService applyInstanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("create".equals(delegateTask.getEventName())) {
            // 只在任务实例创建后，指定委托人之前生效
            // 获取申请人
            String processInstanceId = delegateTask.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            String businessKey = processInstance.getBusinessKey();
            ApplyInstance applyInstance = applyInstanceService.getById(Long.valueOf(businessKey));
            Long userId = applyInstance.getUserId();
            List<User> users;
            // 根据审批环节名称匹配审批责任人
            switch (delegateTask.getName()) {
                case "经理审批":
                    users = userService.matchApprovalUsers(userId, Position.POSITION_KEY_JINGLI);
                    for (User user : users) {
                        taskService.addCandidateUser(delegateTask.getId(), user.getId().toString());
                    }
                    break;
                case "高级经理审批":
                    users = userService.matchApprovalUsers(userId, Position.POSITION_KEY_GAOJIJINGLI);
                    for (User user : users) {
                        taskService.addCandidateUser(delegateTask.getId(), user.getId().toString());
                    }
                    break;
                case "财务审批":
                    users = userService.getFinanceApprovalUsers();
                    for (User user : users) {
                        taskService.addCandidateUser(delegateTask.getId(), user.getId().toString());
                    }
                    break;
                default:
                     //不做任何处理
            }
        }
    }
}
