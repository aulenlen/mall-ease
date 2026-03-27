package com.mallease.user.service.menu;

import com.mallease.common.exception.ApiException;
import com.mallease.user.dal.mapper.MenuDao;
import com.mallease.user.dal.entity.Menu;
import com.mallease.user.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 菜单服务实现
 *
 * @author: Aulen
 * @create: 2026-01-24
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;

    @Override
    public List<Menu> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return menuDao.selectByIds(ids);
    }

    @Override
    public List<Menu> listAll() {
        return menuDao.selectAll();
    }

    @Override
    public List<Menu> listByParentId(Long parentId) {
        return menuDao.selectByParentId(parentId);
    }

    @Override
    public Integer create(Menu menu) {
        return menuDao.insert(menu);
    }

    @Override
    public Integer update(Menu menu) {
        return menuDao.updateByPrimaryKeySelective(menu);
    }

    @Override
    public Integer delete(Long id) {
        List<Menu> menus = menuDao.selectByParentId(id);
        if (menus != null && !menus.isEmpty()) {
            throw new ApiException("存在下属菜单，无法删除！");
        }
        return menuDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (ids.isEmpty()) {
            return 0;
        }

        List<Menu> menus = menuDao.selectByParentIds(ids);
        if (menus != null && !menus.isEmpty()) {
            throw new ApiException("存在下属菜单，无法删除！");
        }

        return menuDao.deleteBatch(ids);
    }

    @Override
    public Menu getById(Long id) {
        return menuDao.selectByPrimaryKey(id);
    }


}