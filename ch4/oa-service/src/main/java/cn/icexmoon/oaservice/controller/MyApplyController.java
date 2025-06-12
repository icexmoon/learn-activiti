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
 * @ClassName MyController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 上午11:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/my_apply")
public class MyApplyController {
    @Autowired
    private ApplyInstanceService applyInstanceService;
    @GetMapping("/page")
    public Result<IPage<ApplyInstance>> page(@RequestParam Long pageNum,
                                             @RequestParam Long pageSize,
                                             @RequestParam(required = false) Long applyProcessId,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                             @RequestParam(required = false) Date beginDate,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                             @RequestParam(required = false) Date endDate
                                               ) {
        IPage<ApplyInstance> pageData =  applyInstanceService.queryPage(pageNum, pageSize, applyProcessId, beginDate, endDate, UserHolder.getUser().getId());
        return Result.success(pageData);
    }
}
