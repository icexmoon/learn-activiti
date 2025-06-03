package cn.icexmoon.oaservice.util;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : oa-service
 * @Package : cn.icexmoon.oaservice.util
 * @ClassName : .java
 * @createTime : 2025/5/19 18:23
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description :
 */
@Getter
public class Result<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <D> Result<D> success(D data) {
        return new Result<>(true, "", data);
    }

    public static <D> Result<D> success(D data, String message) {
        return new Result<>(true, message, data);
    }


    public static Result<Void> success() {
        return success(null);
    }

    public static Result<Void> fail(String message) {
        return new Result<>(false, message, null);
    }

    public static <T> Result<T> fail(T data, String message) {
        return new Result<>(false, message, data);
    }
}
