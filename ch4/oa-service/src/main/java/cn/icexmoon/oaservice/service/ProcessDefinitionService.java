package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.dto.KeyNameDTO;
import cn.icexmoon.oaservice.dto.ProcessDefinitionDTO;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ProcessDefinitionService
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午7:06
 * @Version 1.0
 */
public interface ProcessDefinitionService {

    /**
     * 获取流程定义分页信息
     *
     * @param pageNum               当前页码
     * @param pageSize              页宽
     * @param key                   流程定义的key
     * @param processDefinitionName 部署流程时的名称
     * @return 流程定义
     */
    Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize, String key, String processDefinitionName);

    /**
     * 获取流程定义分页信息
     *
     * @param pageNum               当前页码
     * @param pageSize              页宽
     * @param key                   流程定义的key
     * @param processDefinitionName 部署流程时的名称
     * @param deploymentName        部署名称
     * @param start
     * @param end
     * @return 流程定义
     */
    Page<ProcessDefinitionDTO> page(Long pageNum, Long pageSize, String key, String processDefinitionName, String deploymentName, Date start, Date end);

    /**
     * 添加流程定义
     *
     * @param bpmnFile
     * @param pngFile
     * @param name     流程部署名称
     * @return 成功/失败
     */
    Result<Void> add(MultipartFile bpmnFile, MultipartFile pngFile, String name) throws IOException;

    /**
     * 获取流程定义的资源文件
     *
     * @param deploymentId 流程定义id
     * @param resourceName 资源文件名称
     * @return 资源文件的输入流
     */
    InputStream getResource(String deploymentId, String resourceName);

    /**
     * 删除流程部署
     *
     * @param deploymentId 流程定义id
     * @param force        是否强制删除（将级联删除相关的进程实例）
     * @return 成功/失败
     */
    Result<Void> delete(String deploymentId, Boolean force);

    /**
     * 获取流程定义的 key 列表
     *
     * @return key 列表
     */
    Result<List<KeyNameDTO>> getKeys();
}
