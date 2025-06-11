package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.ApplyForm;
import cn.icexmoon.oaservice.service.ApplyFormService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ApplyFormController
 * @Description 申请表单管理
 * @Author icexmoon@qq.com
 * @Date 2025/6/8 下午4:55
 * @Version 1.0
 */
@RestController
@RequestMapping("/apply_form")
public class ApplyFormController {
    @Autowired
    private ApplyFormService applyFormService;

    @PostMapping("/add")
    public Result<Long> add(@RequestBody ApplyForm applyForm) {
        return applyFormService.add(applyForm);
    }

    @GetMapping("/{key}")
    public Result<ApplyForm> get(@PathVariable String key) {
        return applyFormService.get(key);
    }

    @GetMapping("/keys")
    public Result<List<String>> getKeys() {
        return applyFormService.getFormkeys();
    }

    @GetMapping("/page")
    public Result<IPage<ApplyForm>> getPage(@RequestParam Long pageNum,
                                            @RequestParam Long pageSize,
                                            @RequestParam(required = false) String key,
                                            @RequestParam(required = false) String name,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(required = false) Date beginDate,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(required = false) Date endDate){
        return applyFormService.getPage(pageNum, pageSize, key, name, beginDate, endDate);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return applyFormService.delete(id);
    }
}
