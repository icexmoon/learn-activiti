package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.ApplyInstance;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MyApproval
 * @Description 审批待办相关接口
 * @Author icexmoon@qq.com
 * @Date 2025/6/13 上午10:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/my_todo")
public class MyTodoController {
    @Autowired
    private ApplyInstanceService applyInstanceService;

    @GetMapping("page")
    public Result<IPage<ApplyInstance>> page(@RequestParam Long pageNum,
                                             @RequestParam Long pageSize,
                                             @RequestParam(required = false) Long applyProcessId,
                                             @RequestParam(required = false) ApplyInstance.ApprovalStatus status) {
        IPage<ApplyInstance> pageData = applyInstanceService.queryPreapprovalPage(pageNum, pageSize,
                applyProcessId, UserHolder.getUser().getId(), status);
        return Result.success(pageData);
    }

}
