package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.entity.Position;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 70748
 * @description 针对表【position(职位表)】的数据库操作Service
 * @createDate 2025-05-23 14:45:10
 */
public interface PositionService extends IService<Position> {

    /**
     * 获取职位 Map
     *
     * @return
     */
    Map<Integer, Position> getPositionMap();

    Result<List<Position>> getPositions();

    /**
     * 获取职位名称
     *
     * @param positionId 职位id
     * @return 职位名称
     */
    String getPositionName(Integer positionId);

    /**
     * 根据职位key获取职位信息
     *
     * @param approvalPositionKey 职位key
     * @return 职位信息
     */
    Position getPositionByKey(String approvalPositionKey);
}
