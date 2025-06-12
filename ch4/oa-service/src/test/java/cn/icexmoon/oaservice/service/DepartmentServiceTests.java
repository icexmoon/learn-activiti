package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName DepartmentServiceTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午6:59
 * @Version 1.0
 */
@SpringBootTest
public class DepartmentServiceTests {
    @Autowired
    private DepartmentService departmentService;
    @Test
    public void testGetFinanceDepartment() {
        Department financeDepartment = this.departmentService.getFinanceDepartment();
        System.out.println(financeDepartment);
    }
}
