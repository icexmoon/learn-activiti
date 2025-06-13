package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.ApplyAddDTO;
import cn.icexmoon.oaservice.dto.ApprovalResultDTO;
import cn.icexmoon.oaservice.entity.ApplyInstance;
import cn.icexmoon.oaservice.entity.ApplyProcess;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.service.ApplyProcessService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName ApplyController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/8 下午12:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/apply")
public class ApplyController {
    @Autowired
    private ApplyProcessService applyProcessService;
    @Autowired
    private ApplyInstanceService applyInstanceService;

    /**
     * 返回当前用户可以提交的申请流
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ApplyProcess>> list() {
        User user = UserHolder.getUser();
        return applyProcessService.listCanApply(user);
    }

    /**
     * 提交申请
     *
     * @param dto 申请内容
     * @return 申请实例id
     */
    @PostMapping("/add")
    public Result<Long> apply(@RequestBody ApplyAddDTO dto) {
        return applyInstanceService.add(dto, UserHolder.getUser());
    }

    @GetMapping("/{applyInstanceId}")
    public Result<ApplyInstance> get(@PathVariable("applyInstanceId") Long applyInstanceId) {
        ApplyInstance applyInstance = applyInstanceService.getApplyInstance(applyInstanceId);
        return Result.success(applyInstance);
    }

    @PostMapping("/approval")
    public Result<Void> approval(@RequestBody ApprovalResultDTO dto) {
        dto.setUserId(UserHolder.getUser().getId());
        boolean result = applyInstanceService.approval(dto);
        if (result){
            return Result.success();
        }
        return Result.fail("审批失败");
    }
}
