package cn.icexmoon.oaservice.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @ClassName DateTimeSerializer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/12 下午12:46
 * @Version 1.0
 */
public class DateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {

        // 获取字段上的注解
        DateTimeJsonFormat anno = null;
        try {
            anno = gen.getCurrentValue().getClass()
                    .getDeclaredField(gen.getOutputContext().getCurrentName())
                    .getAnnotation(DateTimeJsonFormat.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        if (anno == null) return;

        // 2. 按注解配置格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(anno.pattern());
        sdf.setTimeZone(TimeZone.getTimeZone(anno.timezone()));
        gen.writeString(sdf.format(value));
    }
}
