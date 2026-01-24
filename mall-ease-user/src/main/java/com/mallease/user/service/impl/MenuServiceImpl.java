package com.mallease.user.service.impl;

import com.mallease.user.dao.MenuDao;
import com.mallease.user.model.data.Menu;
import com.mallease.user.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 菜单服务实现
 *
 * @author: Aulen
 * @create: 2026-01-24
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

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
}