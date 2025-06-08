package cn.icexmoon.oaservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.icexmoon.oaservice.entity.ApplyProcess;
import cn.icexmoon.oaservice.mapper.ApplyProcessMapper;
import cn.icexmoon.oaservice.service.ApplyProcessService;
import cn.icexmoon.oaservice.util.Result;
import cn.icexmoon.oaservice.util.TimeUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 70748
 * @description 针对表【apply_process(申请流)】的数据库操作Service实现
 * @createDate 2025-06-07 19:37:15
 */
@Service
public class ApplyProcessServiceImpl extends ServiceImpl<ApplyProcessMapper, ApplyProcess>
        implements ApplyProcessService {

    @Override
    public Result<Long> add(ApplyProcess applyProcess) {
        Date now = new Date();
        applyProcess.setCreateTime(now);
        boolean saved = this.save(applyProcess);
        if (saved) {
            return Result.success(applyProcess.getId());
        }
        return Result.fail(-1L, "添加申请流失败");
    }

    @Override
    public Result<IPage<ApplyProcess>> queryPage(Long pageNum, Long pageSize, String name, String processKey, Date startDate, Date endDate, Boolean enable) {
        IPage<ApplyProcess> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ApplyProcess> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StrUtil.isEmpty(processKey)) {
            queryWrapper.like("process_key", processKey);
        }
        if (startDate != null) {
            queryWrapper.gt("create_time", TimeUtils.toStartTime(startDate));
        }
        if (endDate != null) {
            queryWrapper.lt("create_time", TimeUtils.toEndTime(endDate));
        }
        if (enable != null) {
            queryWrapper.eq("enable", enable);
        }
        this.page(page, queryWrapper);
        return Result.success(page);
    }

    @Override
    public Result<Void> enable(Long processId, boolean enable) {
        if (processId == null) {
            return Result.fail("申请流 id 不能为 null");
        }
        ApplyProcess applyProcess = new ApplyProcess();
        applyProcess.setId(processId);
        applyProcess.setEnable(enable);
        boolean updated = this.updateById(applyProcess);
        if (updated) {
            return Result.success();
        }
        return Result.fail((enable ? "启用" : "停用") + "申请流失败");
    }

    @Override
    public Result<Void> edit(ApplyProcess applyProcess) {
        String positionIdsStr = getPositionIdsStr(applyProcess);
        boolean updated = this.update()
                .set("enable", applyProcess.getEnable())
                .set("form_key", applyProcess.getFormKey())
                .set("name", applyProcess.getName())
                .set("position_ids", positionIdsStr)
                .eq("id", applyProcess.getId())
                .update();
        if (updated){
            return Result.success();
        }
        return Result.fail("更新申请流失败");
    }

    private static String getPositionIdsStr(ApplyProcess applyProcess) {
        Integer[] positionIds = applyProcess.getPositionIds();
        String positionIdsStr = null;
        if (positionIds != null && positionIds.length > 0) {
            positionIdsStr = JSON.toJSONString(positionIds);
        }
        return positionIdsStr;
    }
}




