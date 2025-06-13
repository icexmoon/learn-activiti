package cn.icexmoon.oaservice.util;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * @ClassName IDescEnum
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/13 上午10:47
 * @Version 1.0
 */
public interface IDescEnum<T extends Serializable> extends IEnum<T> {
    String getDesc();
}
