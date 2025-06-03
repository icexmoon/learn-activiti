package cn.icexmoon.oaservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("cn.icexmoon.oaservice.config")
@MapperScan("cn.icexmoon.oaservice.mapper")
public class OaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaServiceApplication.class, args);
    }

}
