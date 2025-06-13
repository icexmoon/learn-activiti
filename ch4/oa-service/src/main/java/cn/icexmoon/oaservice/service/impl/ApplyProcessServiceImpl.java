package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.icexmoon.oaservice.dto.KeyNameDTO;
import cn.icexmoon.oaservice.entity.*;
import cn.icexmoon.oaservice.mapper.ApplyProcessMapper;
import cn.icexmoon.oaservice.service.ApplyFormService;
import cn.icexmoon.oaservice.service.ApplyProcessService;
import cn.icexmoon.oaservice.service.RoleService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.TimeUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author 70748
 * @description 针对表【apply_process(申请流)】的数据库操作Service实现
 * @createDate 2025-06-07 19:37:15
 */
@Service
public class ApplyProcessServiceImpl extends ServiceImpl<ApplyProcessMapper, ApplyProcess>
        implements ApplyProcessService {
    @Autowired
    private RoleService roleService;
    @Lazy
    @Autowired
    private ApplyFormService applyFormService;

    @Override
    public Result<Long> add(ApplyProcess applyProcess) {
        Date now = new Date();
        applyProcess.setCreateTime(now);
        boolean saved = this.save(applyProcess);
        if (saved) {
            return Result.success(applyProcess.getId());
        }
        return Result.fail(-1L, "添加申请流失败");
    }

    @Override
    public Result<IPage<ApplyProcess>> queryPage(Long pageNum, Long pageSize, String name, String processKey, Date startDate, Date endDate, Boolean enable) {
        IPage<ApplyProcess> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ApplyProcess> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StrUtil.isEmpty(processKey)) {
            queryWrapper.like("process_key", processKey);
        }
        if (startDate != null) {
            queryWrapper.gt("create_time", TimeUtils.toStartTime(startDate));
        }
        if (endDate != null) {
            queryWrapper.lt("create_time", TimeUtils.toEndTime(endDate));
        }
        if (enable != null) {
            queryWrapper.eq("enable", enable);
        }
        this.page(page, queryWrapper);
        return Result.success(page);
    }

    @Override
    public Result<Void> enable(Long processId, boolean enable) {
        if (processId == null) {
            return Result.fail("申请流 id 不能为 null");
        }
        ApplyProcess applyProcess = new ApplyProcess();
        applyProcess.setId(processId);
        applyProcess.setEnable(enable);
        boolean updated = this.updateById(applyProcess);
        if (updated) {
            return Result.success();
        }
        return Result.fail((enable ? "启用" : "停用") + "申请流失败");
    }

    @Override
    public Result<Void> edit(ApplyProcess applyProcess) {
        String positionIdsStr = getPositionIdsStr(applyProcess);
        boolean updated = this.update()
                .set("enable", applyProcess.getEnable())
                .set("form_key", applyProcess.getFormKey())
                .set("name", applyProcess.getName())
                .set("position_ids", positionIdsStr)
                .eq("id", applyProcess.getId())
                .update();
        if (updated) {
            return Result.success();
        }
        return Result.fail("更新申请流失败");
    }

    @Override
    public Result<List<ApplyProcess>> listCanApply(User user) {
        if (user == null) {
            return Result.fail(Collections.emptyList(), "user 不能为 null");
        }
        // 读取所有申请流
        List<ApplyProcess> applyProcesses = this.list();
        // 过滤掉禁用的申请流
        applyProcesses = applyProcesses.stream().filter(ap -> BooleanUtil.isTrue(ap.getEnable())).toList();
        // 管理员可以看到所有申请入口
        if (roleService.isRole(user, Role.ROLE_ADMIN)) {
            return Result.success(applyProcesses);
        }
        // 过滤申请流，只保留有申请权限的
        applyProcesses = applyProcesses.stream().filter(ap -> ap.canApply(user.getPositionId())).toList();
        return Result.success(applyProcesses);
    }

    @Override
    public Result<Void> del(Long id) {
        boolean updated = this.removeById(id);
        if (updated) {
            return Result.success();
        }
        return Result.fail("删除申请流失败");
    }

    @Override
    public Result<ApplyProcess> getApplyProcess(Long id) {
        ApplyProcess applyProcess = this.getById(id);
        // 匹配一个最新的申请单，添加
        if (applyProcess != null) {
            ApplyForm applyForm = applyFormService.getApplyFormByFormKey(applyProcess.getFormKey());
            if (applyForm != null) {
                applyProcess.setApplyForm(applyForm);
            }
        }
        return Result.success(applyProcess);
    }

    @Override
    public List<KeyNameDTO> getApprovalStatus() {
        List<KeyNameDTO> keyNameDTOS = new ArrayList<>();
        ApplyInstance.ApprovalStatus[] statuses = ApplyInstance.ApprovalStatus.values();
        for (ApplyInstance.ApprovalStatus status : statuses) {
            KeyNameDTO keyNameDTO = new KeyNameDTO(status.name(), status.getDesc());
            keyNameDTOS.add(keyNameDTO);
        }
        return keyNameDTOS;
    }

    private static String getPositionIdsStr(ApplyProcess applyProcess) {
        List<Integer> positionIds = applyProcess.getPositionIds();
        String positionIdsStr = null;
        if (positionIds != null && !positionIds.isEmpty()) {
            positionIdsStr = JSON.toJSONString(positionIds);
        }
        return positionIdsStr;
    }
}




