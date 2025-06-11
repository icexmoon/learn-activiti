package cn.icexmoon.oaservice.service.impl;

import cn.icexmoon.oaservice.entity.ApplyForm;
import cn.icexmoon.oaservice.mapper.ApplyFormMapper;
import cn.icexmoon.oaservice.service.ApplyFormService;
import cn.icexmoon.oaservice.service.ApplyInstanceService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.TimeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 70748
 * @description 针对表【apply_form(申请表单)】的数据库操作Service实现
 * @createDate 2025-06-08 16:43:11
 */
@Service
public class ApplyFormServiceImpl extends ServiceImpl<ApplyFormMapper, ApplyForm>
        implements ApplyFormService {
    @Autowired
    @Lazy
    private ApplyInstanceService applyInstanceService;
    @Override
    public ApplyForm getApplyFormByFormKey(@NonNull String formKey) {
        // 获取版本号最大的一个表单
        return this.query()
                .eq("`key`", formKey)
                .orderByDesc("version")
                .last("limit 1")
                .one();
    }

    @Override
    public Result<Long> add(ApplyForm applyForm) {
        // 保存
        ApplyForm entity = new ApplyForm();
        entity.setKey(applyForm.getKey());
        entity.setName(applyForm.getName());
        entity.setPath(applyForm.getPath());
        this.baseMapper.addApplyForm(entity);
        return Result.success(entity.getId());
    }

    @Override
    public Result<ApplyForm> get(String key) {
        ApplyForm applyForm = this.getApplyFormByFormKey(key);
        return Result.success(applyForm);
    }

    @Override
    public Result<List<String>> getFormkeys() {
        List<String> keys = this.baseMapper.getKeys();
        return Result.success(keys);
    }

    @Override
    public Result<IPage<ApplyForm>> getPage(@NonNull Long pageNum, @NonNull Long pageSize, String key, String name, Date beginDate, Date endDate) {
        QueryWrapper<ApplyForm> queryWrapper = new QueryWrapper<>();
        if (key != null && !key.isEmpty()) {
            queryWrapper.like("`key`", key);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("`name`", name);
        }
        if (beginDate != null) {
            queryWrapper.ge("`create_time`", TimeUtils.toStartTime(beginDate));
        }
        if (endDate != null) {
            queryWrapper.le("create_time", TimeUtils.toEndTime(endDate));
        }
        queryWrapper.orderByDesc(List.of("`key`", "`version`"));
        IPage<ApplyForm> page = new Page<>(pageNum, pageSize);
        IPage<ApplyForm> applyFormPage = this.page(page, queryWrapper);
        return Result.success(applyFormPage);
    }

    @Override
    public Result<Void> delete(Long id) {
        // 检查是否已经存在使用该表单的申请流实例
        boolean used = applyInstanceService.alreadyUseForm(id);
        if (used) {
            return Result.fail("已经存在使用了该申请但的申请流，不能删除");
        }
        // 删除
        boolean updated = this.removeById(id);
        if (updated){
            return Result.success();
        }
        return Result.fail("删除失败");
    }
}




