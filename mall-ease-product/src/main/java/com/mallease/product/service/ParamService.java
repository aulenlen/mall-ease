package com.mallease.product.service;

import com.mallease.product.model.client.cmd.ParamCmd;
import com.mallease.product.model.client.vo.ParamVO;

import java.util.List;

/**
 * 参数服务接口
 * <p>
 * 提供参数定义的 CRUD 功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface ParamService {

    /**
     * 创建参数
     *
     * @param cmd 保存命令（创建场景）
     * @return 新参数ID
     */
    Long create(ParamCmd cmd);

    /**
     * 更新参数
     *
     * @param cmd 保存命令（更新场景）
     * @return 影响行数
     */
    int update(ParamCmd cmd);

    /**
     * 删除参数
     *
     * @param id 参数ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除参数
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询参数详情
     *
     * @param id 参数ID
     * @return 参数详情
     */
    ParamVO getById(Long id);

    /**
     * 根据参数组ID查询参数列表
     *
     * @param groupId 参数组ID
     * @return 参数列表
     */
    List<ParamVO> listByGroupId(Long groupId);

    /**
     * 查询可搜索的参数
     *
     * @return 参数列表
     */
    List<ParamVO> listSearchable();

    /**
     * 查询亮点参数（商品列表展示）
     *
     * @return 参数列表
     */
    List<ParamVO> listHighlight();

    /**
     * 查询可对比的参数
     *
     * @return 参数列表
     */
    List<ParamVO> listComparable();
}
