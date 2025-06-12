package cn.icexmoon.oaservice.config;

import cn.icexmoon.oaservice.annotation.DateTimeFormatIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName JacksonConfig
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午12:56
 * @Version 1.0
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 组合 Jackson 原生注解与自定义注解
        mapper.setAnnotationIntrospector(
                AnnotationIntrospector.pair(
                        new JacksonAnnotationIntrospector(),
                        new DateTimeFormatIntrospector() // 自定义注解处理器
                )
        );
        return mapper;
    }
}
