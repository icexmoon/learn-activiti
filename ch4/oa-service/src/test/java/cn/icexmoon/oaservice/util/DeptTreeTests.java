package cn.icexmoon.oaservice.util;

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
 * @createTime : 2025/5/23 上午11:14
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@SpringBootTest
public class DeptTreeTests {
    @Autowired
    private DeptTree deptTree;

//    @Test
//    public void testTraversalTree() {
//        deptTree.traversalTree(node -> {
//            System.out.println(deptTree.getFullDeptName(node));
//        });
//    }
}
