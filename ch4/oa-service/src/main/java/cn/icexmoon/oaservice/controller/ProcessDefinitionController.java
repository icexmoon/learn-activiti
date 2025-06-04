package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.service.ProcessDefinitionService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
    public Result<?> page(@RequestParam Long pageNum,
                          @RequestParam Long pageSize,
                          @RequestParam(required = false) String key,
                          @RequestParam(required = false) String name,
                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                          @RequestParam(required = false) Date start,
                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                          @RequestParam(required = false) Date end,
                          @RequestParam(required = false) String deploymentName) {
        if (pageNum <= 0) {
            return Result.fail("页码必须大于0");
        }
        Page<ProcessDefinitionDTO> page = processDefinitionService.page(pageNum, pageSize, key, name,
                deploymentName, start, end);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestParam("bpmnFile") MultipartFile bpmnFile,
                            @RequestParam("pngFile") MultipartFile pngFile,
                            @RequestParam String name) {
        try {
            return processDefinitionService.add(bpmnFile, pngFile, name);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("系统出错，请稍后再试");
        }
    }

    @GetMapping("/file")
    public ResponseEntity<InputStreamResource> file(@RequestParam String deploymentId, @RequestParam String fileName) throws IOException {
        InputStream inputStream = processDefinitionService.getResource(deploymentId, fileName);
        // 输出文件
        // 2. 包装为可下载资源
        InputStreamResource resource = new InputStreamResource(inputStream);
        // 3. 设置响应头（关键步骤）
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName); // [3,6](@ref)
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // [6,8](@re
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(inputStream.available()) // [2](@ref)
                .body(resource);
    }

}
