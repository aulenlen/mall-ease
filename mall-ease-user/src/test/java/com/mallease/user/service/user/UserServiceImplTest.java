package com.mallease.user.service.user;

import com.mallease.user.dal.entity.Resource;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.dal.mapper.AdminDao;
import com.mallease.user.dal.mapper.AdminRoleRelationDao;
import com.mallease.user.dal.mapper.MemberDao;
import com.mallease.user.service.menu.MenuService;
import com.mallease.user.service.resource.ResourceService;
import com.mallease.user.service.role.RoleService;
import com.mallease.common.service.TypedRedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AdminDao adminDao;

    @Mock
    private MemberDao memberDao;

    @Mock
    private TypedRedisService typedRedisService;

    @Mock
    private AdminRoleRelationDao adminRoleRelationDao;

    @Mock
    private RoleService roleService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getCurrentRolesShouldFilterDisabledRoles() {
        when(adminRoleRelationDao.selectRoleIdsByAdminId(1L)).thenReturn(List.of(1L, 2L));
        when(roleService.listByIds(List.of(1L, 2L))).thenReturn(List.of(
                buildRole(1L, 1),
                buildRole(2L, 0)
        ));

        List<Role> roles = userService.getCurrentRoles(1L);

        assertEquals(1, roles.size());
        assertEquals(1L, roles.get(0).getId());
    }

    @Test
    void getResourceListShouldOnlyUseEnabledRoles() {
        when(adminRoleRelationDao.selectRoleIdsByAdminId(1L)).thenReturn(List.of(1L, 2L));
        when(roleService.listByIds(List.of(1L, 2L))).thenReturn(List.of(
                buildRole(1L, 1),
                buildRole(2L, 0)
        ));
        when(roleService.getResourceIdsByRoleIds(List.of(1L))).thenReturn(List.of(101L));
        when(resourceService.listByIds(List.of(101L))).thenReturn(List.of(new Resource()));

        List<Resource> resources = userService.getResourceList(1L);

        assertEquals(1, resources.size());
        verify(roleService).getResourceIdsByRoleIds(List.of(1L));
    }

    private Role buildRole(Long id, Integer status) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        return role;
    }
}
