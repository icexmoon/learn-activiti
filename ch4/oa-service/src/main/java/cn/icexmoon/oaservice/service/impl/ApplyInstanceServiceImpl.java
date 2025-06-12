package cn.icexmoon.oaservice.service.impl;

import cn.icexmoon.oaservice.dto.ApplyAddDTO;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Override
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
        boolean saved = this.save(applyInstance);
        if (saved) {
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
        fillApplyProcessInfo(pageData.getRecords());
        return pageData;
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




