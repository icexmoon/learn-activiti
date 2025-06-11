package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.entity.ApplyForm;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * @author 70748
 * @description 针对表【apply_form(申请表单)】的数据库操作Service
 * @createDate 2025-06-08 16:43:11
 */
public interface ApplyFormService extends IService<ApplyForm> {
    /**
     * 根据表单 key 获取一个表单
     *
     * @param formKey 表单key
     * @return 表单
     */
    ApplyForm getApplyFormByFormKey(String formKey);

    /**
     * 添加申请表单
     *
     * @param applyForm 申请单
     * @return 表单id
     */
    Result<Long> add(ApplyForm applyForm);

    /**
     * 获取表单信息
     *
     * @param key 表单 key
     * @return
     */
    Result<ApplyForm> get(String key);

    /**
     * 获取表单标识集合
     *
     * @return 表单标识集合
     */
    Result<List<String>> getFormkeys();

    /**
     * 获取申请单分页信息
     *
     * @param pageNum   页码
     * @param pageSize  页宽
     * @param key       申请单标识
     * @param name      申请单名称
     * @param beginDate 查询开始时间
     * @param endDate   查询结束时间
     * @return 申请单分页信息
     */
    Result<IPage<ApplyForm>> getPage(Long pageNum, Long pageSize, String key, String name, Date beginDate, Date endDate);

    /**
     * 删除表单
     *
     * @param id 表单id
     * @return 成功/失败
     */
    Result<Void> delete(Long id);
}
