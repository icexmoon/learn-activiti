package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.ApplyInstance;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.UserHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ClassName ApplyHistoryController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/14 下午12:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/apply_history")
public class ApplyHistoryController {
    @Autowired
    private ApplyInstanceService applyInstanceService;

    @GetMapping("/page")
    public Result<IPage<ApplyInstance>> page(@RequestParam Long pageNum, @RequestParam Long pageSize,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                             @RequestParam(required = false) Date beginDate,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                             @RequestParam(required = false) Date endDate,
                                             @RequestParam(required = false) Long applyProcessId) {
        Long userId = UserHolder.getUser().getId();
        IPage<ApplyInstance> pageData = applyInstanceService.queryApprovedPage(userId, pageNum, pageSize,
                beginDate, endDate, applyProcessId);
        return Result.success(pageData);
    }
}
