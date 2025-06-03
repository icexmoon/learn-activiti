package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.entity.Interface;
import cn.icexmoon.oaservice.service.InterfaceService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.controller
 * @ClassName : .java
 * @createTime : 2025/5/28 下午4:12
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@RestController
@RequestMapping("/interface")
public class InterfaceController {
    @Autowired
    private InterfaceService interfaceService;
    @GetMapping("/page")
    public Result<IPage<Interface>> page(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return interfaceService.pagedInterfaces(pageNum, pageSize);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Interface interfaces){
        return interfaceService.add(interfaces);
    }

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody Interface interfaces){
        return interfaceService.edit(interfaces);
    }

    @GetMapping("/search")
    public Result<List<Interface>> search(@RequestParam String keyWord){
        return interfaceService.search(keyWord);
    }
}
