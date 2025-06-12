package cn.icexmoon.oaservice.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName DateTimeFormat
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午12:45
 * @Version 1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside // 关键：声明为 Jackson 注解组合
@JsonSerialize(using = DateTimeSerializer.class) // 绑定自定义序列化器
public @interface DateTimeJsonFormat {
    String pattern() default "yyyy-MM-dd HH:mm:ss"; // 默认格式
    String timezone() default "GMT+8"; // 默认时区
}
