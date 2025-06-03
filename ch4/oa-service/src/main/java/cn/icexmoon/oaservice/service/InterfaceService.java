package cn.icexmoon.oaservice.service;

import cn.icexmoon.oaservice.entity.Interface;
import cn.icexmoon.oaservice.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 70748
 * @description 针对表【interface(提供给前端页面的接口)】的数据库操作Service
 * @createDate 2025-05-28 16:11:16
 */
public interface InterfaceService extends IService<Interface> {

    /**
     * 根据 HTTP 请求匹配符合条件的接口
     *
     * @param requestURI 请求url
     * @param method     请求方法
     * @return 符合条件的接口
     */

    List<Interface> match(String requestURI, String method);

    /**
     * 获取接口列表分页
     *
     * @param pageNum  当前页码
     * @param pageSize 页宽
     * @return 接口列表分页
     */
    Result<IPage<Interface>> pagedInterfaces(Integer pageNum, Integer pageSize);

    /**
     * 添加接口
     *
     * @param interfaces 接口信息
     * @return 接口id
     */
    Result<?> add(Interface interfaces);

    /**
     * 修改接口信息
     *
     * @param interfaces 接口信息
     * @return 成功/失败
     */
    Result<Void> edit(Interface interfaces);

    /**
     * 查询接口
     *
     * @param keyWord 查询关键字
     * @return 符合条件的接口列表
     */
    Result<List<Interface>> search(String keyWord);

    /**
     * 获取所有的接口映射
     *
     * @return 接口映射
     */
    Map<Integer, Interface> getInterfaceMap();
}
