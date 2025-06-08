package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.ApplyProcess;
import cn.icexmoon.oaservice.service.ApplyProcessService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @ClassName ApplyProcessController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/7 下午7:38
 * @Version 1.0
 */
@RestController
@RequestMapping("/apply_process")
public class ApplyProcessController {
    @Autowired
    private ApplyProcessService applyProcessService;

    @PostMapping("/add")
    public Result<Long> add(@RequestBody ApplyProcess applyProcess) {
        return applyProcessService.add(applyProcess);
    }

    @GetMapping("/page")
    public Result<IPage<ApplyProcess>> page(@RequestParam Long pageNum,
                                            @RequestParam Long pageSize,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String processKey,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(required = false) Date startDate,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(required = false) Date endDate,
                                            @RequestParam(required = false) Boolean enable) {
        return applyProcessService.queryPage(pageNum, pageSize, name, processKey, startDate, endDate, enable);
    }

    @PutMapping("/enable/{processId}")
    public Result<Void> enable(@PathVariable Long processId){
        return applyProcessService.enable(processId, true);
    }

    @PutMapping("/disable/{processId}")
    public Result<Void> disable(@PathVariable Long processId){
        return applyProcessService.enable(processId, false);
    }

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody ApplyProcess applyProcess){
        return applyProcessService.edit(applyProcess);
    }
}
