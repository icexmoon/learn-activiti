package cn.icexmoon.oaservice.annotation;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * @ClassName DateTimeFormatIntrospector
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午12:53
 * @Version 1.0
 */
public class DateTimeFormatIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public Object findSerializer(Annotated am) {
        // 优先检查自定义注解
        DateTimeJsonFormat anno = am.getAnnotation(DateTimeJsonFormat.class);
        if (anno != null) {
            return DateTimeSerializer.class; // 返回序列化器
        }
        return super.findSerializer(am);
    }
}
