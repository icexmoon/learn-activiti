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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

}




