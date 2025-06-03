package cn.icexmoon.oaservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/30 上午10:57
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@SpringBootTest
public class MenuTreeTests {
    @Autowired
    private MenuTree menuTree;

    @Test
    public void testIsParent(){
        Assertions.assertTrue(menuTree.isParent(1,2));
        Assertions.assertFalse(menuTree.isParent(2,3));
        Assertions.assertTrue(menuTree.isParent(1,10));
    }
}
