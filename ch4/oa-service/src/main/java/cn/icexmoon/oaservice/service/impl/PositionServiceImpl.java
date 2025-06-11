package cn.icexmoon.oaservice.service.impl;

import cn.icexmoon.oaservice.entity.Position;
import cn.icexmoon.oaservice.mapper.PositionMapper;
import cn.icexmoon.oaservice.service.PositionService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 70748
 * @description 针对表【position(职位表)】的数据库操作Service实现
 * @createDate 2025-05-23 14:45:10
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position>
        implements PositionService {

    @Override
    public Map<Integer, Position> getPositionMap() {
        List<Position> positions = list();
        if (positions == null || positions.isEmpty()) {
            return Collections.emptyMap();
        }
        return positions.stream().collect(Collectors.toMap(Position::getId, p -> p));
    }

    @Override
    public Result<List<Position>> getPositions() {
        List<Position> positions = list(new QueryWrapper<Position>().orderByAsc("level"));
        return Result.success(positions);
    }

    @Override
    public String getPositionName(Integer positionId) {
        if (positionId == null) {
            return "";
        }
        // 从内存获取职位映射
        Map<Integer, Position> positionMap = this.getPositionMap();
        Position position = positionMap.get(positionId);
        if (position == null) {
            return "";
        }
        return position.getName();
    }
}




