package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName ProcessDefinitionService
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午7:06
 * @Version 1.0
 */
public interface ProcessDefinitionService {

    Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize);

    /**
     * 添加流程定义
     * @param files 流程定义所需的 BPMN2 文件和 PNG 文件
     * @param name 流程部署名称
     * @return 成功/失败
     */
    Result<Void> add(MultipartFile[] files, String name) throws IOException;
}
