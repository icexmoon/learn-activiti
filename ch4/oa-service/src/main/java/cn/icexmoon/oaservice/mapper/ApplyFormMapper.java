package cn.icexmoon.oaservice.mapper;

import cn.icexmoon.oaservice.entity.ApplyForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 70748
* @description 针对表【apply_form(申请表单)】的数据库操作Mapper
* @createDate 2025-06-08 16:43:11
* @Entity cn.icexmoon.oaservice.entity.ApplyForm
*/
public interface ApplyFormMapper extends BaseMapper<ApplyForm> {
    void addApplyForm(ApplyForm applyForm);

    List<String> getKeys();

}




