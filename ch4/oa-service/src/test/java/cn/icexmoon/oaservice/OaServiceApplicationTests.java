package cn.icexmoon.oaservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ConfigurationPropertiesScan("cn.icexmoon.oaservice.config")
class OaServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
