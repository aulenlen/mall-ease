package com.mallease.user.converter;

import com.mallease.user.model.client.cmd.SaveMenuCmd;
import com.mallease.user.model.client.vo.MenuTreeVO;
import com.mallease.user.model.client.vo.MenuVO;
import com.mallease.user.model.data.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 菜单转换器
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    /**
     * Entity → VO
     */
    MenuVO entityToVo(Menu entity);

    /**
     * Entity List → VO List
     */
    List<MenuVO> entityListToVoList(List<Menu> entities);

    /**
     * Entity → TreeVO
     */
    MenuTreeVO entityToTreeVo(Menu entity);

    /**
     * Entity List → TreeVO List
     */
    List<MenuTreeVO> entityListToTreeVoList(List<Menu> entities);

    /**
     * Cmd → Entity
     */
    Menu cmdToEntity(SaveMenuCmd cmd);

    /**
     * Cmd → Entity (更新)
     */
    void updateEntityFromCmd(@MappingTarget Menu entity, SaveMenuCmd cmd);
}