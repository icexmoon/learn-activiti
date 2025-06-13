package cn.icexmoon.oaservice.config;

import cn.icexmoon.activitiutil.ActivitiUtils;
import org.activiti.engine.ProcessEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ActivitiConfig
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/13 下午12:11
 * @Version 1.0
 */
@Configuration
public class ActivitiConfig {
    @Bean
    public ActivitiUtils activitiUtils(ProcessEngine processEngine) {
        return new ActivitiUtils(processEngine);
    }
}
