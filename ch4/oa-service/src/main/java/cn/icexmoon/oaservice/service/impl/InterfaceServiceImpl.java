package cn.icexmoon.oaservice.service.impl;

import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.icexmoon.oaservice.entity.Interface;
import cn.icexmoon.oaservice.service.InterfaceService;
import cn.icexmoon.oaservice.mapper.InterfaceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 70748
 * @description 针对表【interface(提供给前端页面的接口)】的数据库操作Service实现
 * @createDate 2025-05-28 16:11:16
 */
@Service
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, Interface>
        implements InterfaceService {

    @Override
    public List<Interface> match(String requestURI, String method) {
        QueryWrapper<Interface> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("path", requestURI)
                .eq("method", method);
        List<Interface> list = this.list(queryWrapper);
        if (list.isEmpty()) {
            // 尝试匹配使用路径参数的接口，比如`/apply/apply_process/{id}`
            // 截取路径，去除路径参数
            int lastIndexOf = requestURI.lastIndexOf("/");
            requestURI = requestURI.substring(0, lastIndexOf);
            list = this.list(new QueryWrapper<Interface>()
                    .eq("path",requestURI + "/{id}")
                    .eq("method", method));
        }
        return list;
    }

    @Override
    public Result<IPage<Interface>> pagedInterfaces(Integer pageNum, Integer pageSize) {
        Page<Interface> interfacePage = this.page(new Page<Interface>(pageNum, pageSize));
        return Result.success(interfacePage);
    }

    @Override
    public Result<?> add(Interface interfaces) {
        boolean saved = this.save(interfaces);
        if (saved) {
            return Result.success(interfaces.getId());
        }
        return Result.fail("新增接口失败");
    }

    @Override
    public Result<Void> edit(Interface interfaces) {
        boolean saved = this.updateById(interfaces);
        if (saved) {
            return Result.success();
        }
        return Result.fail("新增接口失败");
    }

    @Override
    public Result<List<Interface>> search(String keyWord) {
        if (keyWord == null || keyWord.trim().isEmpty()) {
            // 查询关键字为空，返回全部接口
            return Result.success(this.list());
        }
        List<Interface> list = this.query()
                .like("name", keyWord)
                .or()
                .like("path", keyWord)
                .list();
        return Result.success(list);
    }

    @Override
    public Map<Integer, Interface> getInterfaceMap() {
        return this.list().stream().collect(Collectors.toMap(Interface::getId, i -> i));
    }
}




