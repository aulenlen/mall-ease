package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreatePmsCategoryCmd;
import com.mallease.pms.dto.cmd.UpdatePmsCategoryCmd;
import com.mallease.pms.dto.vo.PmsCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsCategoryListVO;
import com.mallease.pms.dto.vo.PmsCategoryTreeVO;
import com.mallease.pms.dto.vo.PmsCategoryVO;
import com.mallease.pms.pojo.PmsCategory;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类转换器
 * <p>
 * 负责分类实体与DTO之间的转换，包括状态码到中文名称的映射、
 * 扁平列表到树形结构的转换等
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Mapper(componentModel = "spring")
public interface PmsCategoryConverter {

    /**
     * Entity → VO（基础视图）
     */
    PmsCategoryVO entityToVo(PmsCategory entity);

    /**
     * Entity → ListVO（列表视图）
     */
    @Mapping(target = "childCount", ignore = true) // 由 Service 层填充
    PmsCategoryListVO entityToListVo(PmsCategory entity);

    /**
     * Entity → DetailVO（详情视图）
     */
    @Mapping(target = "parentName", ignore = true)     // 由 Service 层填充
    @Mapping(target = "breadcrumb", ignore = true)     // 由 Service 层填充
    @Mapping(target = "specGroupIds", ignore = true)   // 由 Service 层填充
    @Mapping(target = "paramGroupIds", ignore = true)  // 由 Service 层填充
    PmsCategoryDetailVO entityToDetailVo(PmsCategory entity);

    /**
     * Entity → TreeVO（树形视图）
     */
    @Mapping(target = "children", ignore = true) // 由 buildTree 方法填充
    PmsCategoryTreeVO entityToTreeVo(PmsCategory entity);

    List<PmsCategoryVO> entityListToVoList(List<PmsCategory> entities);

    List<PmsCategoryListVO> entityListToListVoList(List<PmsCategory> entities);

    List<PmsCategoryDetailVO> entityListToDetailVoList(List<PmsCategory> entities);

    List<PmsCategoryTreeVO> entityListToTreeVoList(List<PmsCategory> entities);

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", ignore = true)       // 由 Service 层计算
    @Mapping(target = "level", ignore = true)      // 由 Service 层计算
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsCategory createCmdToEntity(CreatePmsCategoryCmd cmd);

    /**
     * UpdateCmd → Entity（部分更新）
     * <p>
     * 注意：parentId 的修改需要在 Service 层特殊处理（触发分类移动）
     */
    @Mapping(target = "id", ignore = true)         // ID 不允许修改
    @Mapping(target = "path", ignore = true)       // 由 Service 层计算
    @Mapping(target = "level", ignore = true)      // 由 Service 层计算
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsCategory entity, UpdatePmsCategoryCmd cmd);

    /**
     * 将扁平列表构建为树形结构
     * <p>
     * 算法：使用 Map 缓存实现 O(n) 时间复杂度
     *
     * @param entities 扁平分类列表
     * @return 树形结构（只返回顶级节点）
     */
    default List<PmsCategoryTreeVO> buildTree(List<PmsCategory> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 转换为 TreeVO 并建立 ID -> VO 的映射
        List<PmsCategoryTreeVO> treeVoList = entityListToTreeVoList(entities);
        Map<Long, PmsCategoryTreeVO> idMap = treeVoList.stream()
                .collect(Collectors.toMap(PmsCategoryTreeVO::getId, vo -> vo));

        // 2. 构建父子关系
        List<PmsCategoryTreeVO> roots = new ArrayList<>();
        for (PmsCategoryTreeVO vo : treeVoList) {
            Long parentId = vo.getParentId();
            if (parentId == null || parentId == 0L) {
                // 顶级节点
                roots.add(vo);
            } else {
                // 找到父节点并添加到其 children
                PmsCategoryTreeVO parent = idMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 父节点不在列表中，作为顶级节点处理
                    roots.add(vo);
                }
            }
        }

        return roots;
    }
}