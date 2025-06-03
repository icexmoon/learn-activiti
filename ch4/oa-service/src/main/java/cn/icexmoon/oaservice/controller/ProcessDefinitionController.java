package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.service.ProcessDefinitionService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName ProcessDefinitionController
 * @Description Activiti 流程定义相关接口
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午7:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/process_definition")
public class ProcessDefinitionController {
    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        if (pageNum <= 0) {
            return Result.fail("页码必须大于0");
        }
        Page<ProcessDefinitionDTO> page = processDefinitionService.page(pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestParam("files") MultipartFile[] files, @RequestParam String name) {
        try {
            return processDefinitionService.add(files, name);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("系统出错，请稍后再试");
        }
    }

}
