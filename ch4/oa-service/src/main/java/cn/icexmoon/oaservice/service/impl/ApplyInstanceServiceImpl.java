package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.icexmoon.activitiutil.ActivitiUtils;
import cn.icexmoon.oaservice.dto.ApplyAddDTO;
import cn.icexmoon.oaservice.dto.ApplyApprovalDTO;
import cn.icexmoon.oaservice.dto.ApprovalResultDTO;
import cn.icexmoon.oaservice.dto.UserInfoDTO;
import cn.icexmoon.oaservice.entity.ApplyForm;
import cn.icexmoon.oaservice.entity.ApplyInstance;
import cn.icexmoon.oaservice.entity.ApplyProcess;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.mapper.ApplyInstanceMapper;
import cn.icexmoon.oaservice.service.ApplyFormService;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.service.ApplyProcessService;
import cn.icexmoon.oaservice.service.UserService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.TimeUtils;
import cn.icexmoon.oaservice.util.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 70748
 * @description 针对表【apply_instance(申请流实例)】的数据库操作Service实现
 * @createDate 2025-06-08 16:06:13
 */
@Service
public class ApplyInstanceServiceImpl extends ServiceImpl<ApplyInstanceMapper, ApplyInstance>
        implements ApplyInstanceService {
    @Autowired
    private ApplyProcessService applyProcessService;
    @Autowired
    private ApplyFormService applyFormService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivitiUtils activitiUtils;

    @Override
    @Transactional
    public Result<Long> add(@NonNull ApplyAddDTO dto, @NonNull User user) {
        ApplyProcess applyProcess = applyProcessService.getById(dto.getApplyProcessId());
        if (applyProcess == null) {
            return Result.fail(0L, "添加申请失败，不正确的申请流id");
        }
        String formKey = applyProcess.getFormKey();
        ApplyForm applyForm = applyFormService.getApplyFormByFormKey(formKey);
        if (applyForm == null) {
            return Result.fail(0L, "添加申请失败，系统中缺少表单[%s]".formatted(formKey));
        }
        ApplyInstance applyInstance = new ApplyInstance();
        applyInstance.setApplyProcessId(dto.getApplyProcessId());
        ApplyInstance.FormData formData = new ApplyInstance.FormData();
        Result<UserInfoDTO> infoRes = userService.getInfo(user);
        if (!infoRes.isSuccess()) {
            return Result.fail(0L, "系统出错，请稍后再试");
        }
        UserInfoDTO userInfoDTO = infoRes.getData();
        formData.setUserName(userInfoDTO.getName());
        formData.setPhone(userInfoDTO.getPhone());
        formData.setPositionName(userInfoDTO.getPositionName());
        formData.setFullDeptName(userInfoDTO.getFullDeptName());
        formData.setExtraData(dto.getExtraData());
        applyInstance.setFormData(formData);
        applyInstance.setUserId(user.getId());
        applyInstance.setFormId(applyForm.getId());
        applyInstance.setProcessKey(applyProcess.getProcessKey());
        applyInstance.setCreateTime(new Date());
        applyInstance.setStatus(ApplyInstance.ApprovalStatus.PENDING_APPROVAL);
        boolean saved = this.save(applyInstance);
        if (saved) {
            // 启动关联的 activiti 工作流
            Map<String, Object> vars = new HashMap<>();
            vars.put("applier", applyInstance.getUserId().toString());
            vars.put("days", applyInstance.getFormData().getExtraData().get("days"));
            vars.put("budget", applyInstance.getFormData().getExtraData().get("budget"));
            ProcessInstance processInstance = activitiUtils.startAndNext(applyInstance.getProcessKey(), applyInstance.getId(), vars);
            // 更新申请实例表，关联工作流id
            ApplyInstance instance = new ApplyInstance();
            instance.setId(applyInstance.getId());
            instance.setProcessInstanceId(processInstance.getId());
            this.updateById(instance);
            return Result.success(applyInstance.getId());
        }
        return Result.fail(0L, "添加申请流实例失败");
    }

    @Override
    public boolean alreadyUseForm(Long formId) {
        ApplyInstance applyInstance = this.query()
                .eq("form_id", formId)
                .last("limit 1")
                .one();
        if (applyInstance != null) {
            return true;
        }
        return false;
    }

    @Override
    public IPage<ApplyInstance> queryPage(Long pageNum, Long pageSize, Long applyProcessId, Date beginDate, Date endDate, Long userId) {
        Page<ApplyInstance> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ApplyInstance> queryWrapper = new QueryWrapper<>();
        if (applyProcessId != null) {
            queryWrapper.eq("apply_process_id", applyProcessId);
        }
        if (beginDate != null) {
            queryWrapper.ge("create_time", TimeUtils.toStartTime(beginDate));
        }
        if (endDate != null) {
            queryWrapper.le("create_time", TimeUtils.toEndTime(endDate));
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("create_time");
        Page<ApplyInstance> pageData = this.page(page, queryWrapper);
        // 增加状态描述
        if (page.getRecords() != null && page.getRecords().size() > 0) {
            for (ApplyInstance record : page.getRecords()) {
                if (record.getStatus() != null) {
                    record.setStatusText(record.getStatus().getDesc());
                }
            }
        }
        fillApplyProcessInfo(pageData.getRecords());
        return pageData;
    }

    @Override
    public ApplyInstance getApplyInstance(Long applyInstanceId) {
        ApplyInstance applyInstance = this.getById(applyInstanceId);
        ApplyForm applyForm = applyFormService.getById(applyInstance.getFormId());
        applyInstance.setApplyForm(applyForm);
        // 获取审批记录
        Task lastTask = activitiUtils.getLastTask(applyInstance.getProcessInstanceId());
        List<ApplyApprovalDTO> approvalDTOS = new ArrayList<>();
        List<HistoricTaskInstance> historicTaskInstances = activitiUtils.listHistoryTasks(applyInstance.getProcessInstanceId());
        // 历史审批环节
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            if (historicTaskInstance.getEndTime() == null
                    || historicTaskInstance.getName().equals("创建申请")) {
                // 不包含未完成的审批环节
                // 不包含创建申请环节
                continue;
            }
            Map<String, Object> taskVariables = activitiUtils.getTaskVariables(historicTaskInstance.getId());
            ApplyApprovalDTO approvalDTO = new ApplyApprovalDTO();
            approvalDTO.setTime(historicTaskInstance.getEndTime());
            String opinion = "";
            if (taskVariables.get("opinion") != null) {
                opinion = taskVariables.get("opinion").toString();
            }
            approvalDTO.setOpinion(opinion);
            approvalDTO.setTitle(historicTaskInstance.getName());
            if (historicTaskInstance.getAssignee() != null) {
                Long assigneeUserId = Long.valueOf(historicTaskInstance.getAssignee());
                User assigneeUser = userService.getById(assigneeUserId);
                approvalDTO.setUserName(assigneeUser.getName());
            }
            String statusText = "";
            if (taskVariables.get("status") != null) {
                Integer statusVal = Integer.valueOf(taskVariables.get("status").toString());
                ApplyInstance.ApprovalStatus status = ApplyInstance.ApprovalStatus.valueOf(statusVal);
                if (status != null) {
                    statusText = status.getDesc();
                }
            }
            approvalDTO.setStatusText(statusText);
            approvalDTO.setCanApproval(false);
            approvalDTOS.add(approvalDTO);
        }
        // 当前生效的审批环节
        if (lastTask != null) {
            List<String> candidates = activitiUtils.listCandidates(lastTask.getId());
            for (String candidate : candidates) {
                Long approvalUserId = Long.valueOf(candidate);
                User user = userService.getById(approvalUserId);
                ApplyApprovalDTO approvalDTO = new ApplyApprovalDTO();
                approvalDTO.setTime(new Date());
                approvalDTO.setOpinion("");
                approvalDTO.setUserName(user.getName());
                approvalDTO.setTitle(lastTask.getName());
                ApplyInstance.ApprovalStatus pendingApproval = ApplyInstance.ApprovalStatus.PENDING_APPROVAL;
                approvalDTO.setStatusText(pendingApproval.getDesc());
                // 当前用户是审批人且是待审批环节，表明可以审批该环节
                boolean canApproval = approvalUserId.equals(UserHolder.getUser().getId());
                approvalDTO.setCanApproval(canApproval);
                approvalDTO.setTaskId(lastTask.getId());
                approvalDTOS.add(approvalDTO);
            }
        }
        applyInstance.setApprovalDTOS(approvalDTOS);
        return applyInstance;
    }

    @Override
    public IPage<ApplyInstance> queryApprovalPage(Long pageNum, Long pageSize,
                                                  Long applyProcessId,
                                                  @NonNull Long approvalUserId,
                                                  ApplyInstance.ApprovalStatus status) {
        // 获取需要由指定用户审批的工作流
        List<ProcessInstance> processInstances = activitiUtils.listPendingApprovalProcessInstances(approvalUserId.toString());
        if (processInstances == null || processInstances.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        List<String> processInstanceIds = processInstances.stream().map(ProcessInstance::getId).toList();
        // 查找对应的申请流
        List<ApplyInstance> applyInstances = this.query().in("process_instance_id", processInstanceIds).list();
        // 根据条件进行过滤
        applyInstances = applyInstances.stream().filter(pi -> {
            if (applyProcessId != null && !applyProcessId.equals(pi.getApplyProcessId())) {
                return false;
            }
            if (status != null && !status.equals(pi.getStatus())) {
                return false;
            }
            return true;
        }).toList();
        if (applyInstances.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        long starIndex = (pageNum - 1) * pageSize;
        long endIndex = starIndex + pageSize;
        int total = applyInstances.size();
        if (starIndex >= total) {
            return new Page<>(pageNum, pageSize);
        }
        if (endIndex > total) {
            endIndex = total;
        }
        List<ApplyInstance> pagedApplyInstances = applyInstances.subList((int) starIndex, (int) endIndex);
        Page<ApplyInstance> pageData = new Page<>(pageNum, pageSize);
        pageData.setRecords(pagedApplyInstances);
        pageData.setTotal(total);
        return pageData;
    }

    @Override
    @Transactional
    public boolean approval(ApprovalResultDTO dto) {
        Map<String, Object> vars = new HashMap<>();
        if (BooleanUtil.isTrue(dto.getAgree())) {
            // 如果没有审批意见，且同意的，审批意见设置为默认的同意
            if (dto.getOpinion() == null || dto.getOpinion().isEmpty()) {
                dto.setOpinion("同意");
            }
            vars.put("opinion", dto.getOpinion());
            vars.put("status", ApplyInstance.ApprovalStatus.PASSED.getValue());
            activitiUtils.completeTaskWithCheck(dto.getUserId().toString(), dto.getTaskId(), vars);
            // 如果申请实例状态是待审批，修改状态为审批中
            ApplyInstance applyInstance = this.getById(dto.getApplyInstanceId());
            if (applyInstance != null && applyInstance.getStatus() != null
                    && applyInstance.getStatus().equals(ApplyInstance.ApprovalStatus.PENDING_APPROVAL)) {
                this.changeStatus(dto.getApplyInstanceId(), ApplyInstance.ApprovalStatus.UNDER_APPROVAL);
            }
        } else {
            if (dto.getOpinion() == null || dto.getOpinion().isEmpty()) {
                dto.setOpinion("不同意");
            }
            vars.put("opinion", dto.getOpinion());
            vars.put("status", ApplyInstance.ApprovalStatus.FAILED.getValue());
            activitiUtils.rejectTask(dto.getTaskId(), dto.getUserId().toString(), "审批未通过", vars);
            this.changeStatus(dto.getApplyInstanceId(), ApplyInstance.ApprovalStatus.FAILED);
        }
        return true;
    }

    @Override
    public void endProcess(String processInstanceId) {
        // 修改申请实例状态为已通过
        ApplyInstance applyInstance = this.getApplyInstanceByProcessInstanceId(processInstanceId);
        if (applyInstance == null) {
            return;
        }
        this.changeStatus(applyInstance.getId(), ApplyInstance.ApprovalStatus.PASSED);
    }

    private ApplyInstance getApplyInstanceByProcessInstanceId(String processInstanceId) {
        ApplyInstance applyInstance = this.getOne(new QueryWrapper<ApplyInstance>().eq("process_instance_id", processInstanceId));
        return applyInstance;
    }

    /**
     * 修改申请流实例状态
     *
     * @param applyInstanceId 申请流实例id
     * @param status          状态
     */
    private void changeStatus(Long applyInstanceId, ApplyInstance.ApprovalStatus status) {
        ApplyInstance entity = new ApplyInstance();
        entity.setId(applyInstanceId);
        entity.setStatus(status);
        this.updateById(entity);
    }

    /**
     * 填充申请流相关信息
     *
     * @param records 申请流实例集合
     */
    private void fillApplyProcessInfo(List<ApplyInstance> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> applyProcessIds = records.stream().map(r -> r.getApplyProcessId()).toList();
        Map<Long, ApplyProcess> applyProcessMap = applyProcessService.listByIds(applyProcessIds).stream().collect(Collectors.toMap(ap -> ap.getId(), ap -> ap));
        for (ApplyInstance record : records) {
            record.setApplyProcess(applyProcessMap.get(record.getApplyProcessId()));
        }
    }

}




