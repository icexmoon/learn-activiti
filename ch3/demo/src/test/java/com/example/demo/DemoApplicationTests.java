package com.example.demo;

import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private DataSource dataSource;

    /**
     * 测试基本的数据库连接
     *
     * @throws SQLException
     */
    @Test
    void contextLoads() throws SQLException {
        @Cleanup Connection connection = dataSource.getConnection();
        @Cleanup Statement statement = connection.createStatement();
        String sql = "select * from users";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            System.out.println(String.format("%d[name:%s,age:%d]", id, name, age));
        }
    }

}
