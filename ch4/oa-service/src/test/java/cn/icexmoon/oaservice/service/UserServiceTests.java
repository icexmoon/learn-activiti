package cn.icexmoon.oaservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    /**
     * 生成测试用的用户数据
     */
//    @Test
//    public void testInitUsers() {
//        for(int i=0;i<100;i++) {
//            User user = new User();
//            String phone = RandomUtil.randomNumbers(11);
//            user.setPhone(phone);
//            user.setDeptId(2L);
//            userService.save(user);
//        }
//    }
}
