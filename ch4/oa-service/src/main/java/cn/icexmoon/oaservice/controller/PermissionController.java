package cn.icexmoon.oaservice.controller;

import cn.icexmoon.oaservice.dto.UserRolesDTO;
import cn.icexmoon.oaservice.entity.User;
import cn.icexmoon.oaservice.service.UserService;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.controller
 * @ClassName : .java
 * @createTime : 2025/5/29 下午8:51
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 权限管理
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private UserService userService;

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody UserRolesDTO userRolesDTO) {
        return userService.editPersission(userRolesDTO);
    }

    @GetMapping("/user/page")
    public Result<IPage<User>> page(@RequestParam Long current, @RequestParam Long size){
        return userService.specialUserPage(current, size);
    }
}
