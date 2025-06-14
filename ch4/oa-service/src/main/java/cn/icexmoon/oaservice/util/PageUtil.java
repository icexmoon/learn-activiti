package cn.icexmoon.oaservice.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @ClassName PageUtil
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/14 下午3:56
 * @Version 1.0
 */
public class PageUtil {
    /**
     * 逻辑分页
     *
     * @param pageNum  页码
     * @param pageSize 页宽
     * @param data     实际数据
     * @param <T>      模版参数
     * @return 分页结果
     */
    public static <T> Page<T> logicPage(long pageNum, long pageSize, List<T> data) {
        Page<T> page = new Page<>(pageNum, pageSize);
        if (data == null || data.isEmpty()) {
            page.setTotal(0L);
            return page;
        }
        int total = data.size();
        page.setTotal(total);
        int startIndex = (int) ((pageNum - 1) * pageSize);
        int endIndex = (int) (startIndex + pageSize);
        if (startIndex >= total) {
            return page;
        }
        if (endIndex > total) {
            endIndex = total;
        }
        List<T> records = data.subList(startIndex, endIndex);
        page.setRecords(records);
        return page;
    }
}
