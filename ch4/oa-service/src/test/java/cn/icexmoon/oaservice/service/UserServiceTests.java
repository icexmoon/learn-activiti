package cn.icexmoon.oaservice.service;

import cn.hutool.core.util.RandomUtil;
import cn.icexmoon.oaservice.entity.Department;
import cn.icexmoon.oaservice.entity.Position;
import cn.icexmoon.oaservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice
 * @ClassName : .java
 * @createTime : 2025/5/22 下午3:48
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 生成测试用的用户数据
     */
    @Test
    public void testInitUsers() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            String phone = RandomUtil.randomNumbers(11);
            user.setPhone(phone);
            user.setDeptId(2L);
            userService.save(user);
        }
    }

    @Test
    public void testMatchApprovalUsers() {
        List<User> users = this.userService.matchApprovalUsers(2L, Position.POSITION_KEY_JINGLI);
        System.out.println(users);
    }

    @Test
    public void testGetUsersByDeptId() {
        Department department = departmentService.getFinanceDepartment();
        List<User> users = userService.getUsersByDeptId(department.getId());
        System.out.println(users);
    }
}
