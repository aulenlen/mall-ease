package com.mallease.user.service.role;

import com.mallease.common.exception.ApiException;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.dal.mapper.AdminRoleRelationDao;
import com.mallease.user.dal.mapper.RoleDao;
import com.mallease.user.dal.mapper.RoleMenuRelationDao;
import com.mallease.user.dal.mapper.RoleResourceRelationDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleResourceRelationDao roleResourceRelationDao;

    @Mock
    private RoleMenuRelationDao roleMenuRelationDao;

    @Mock
    private AdminRoleRelationDao adminRoleRelationDao;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void createShouldValidateRoleCodeAndInitAdminCount() {
        Role role = new Role();
        role.setName("商品管理员");
        role.setRoleCode("PRODUCT_ADMIN");

        roleService.create(role);

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleDao).insertSelective(roleCaptor.capture());
        assertEquals(0, roleCaptor.getValue().getAdminCount());
        assertEquals("PRODUCT_ADMIN", roleCaptor.getValue().getRoleCode());
    }

    @Test
    void createShouldThrowWhenRoleCodeAlreadyExists() {
        Role existedRole = new Role();
        existedRole.setId(1L);
        existedRole.setRoleCode("PRODUCT_ADMIN");
        when(roleDao.selectByRoleCode("PRODUCT_ADMIN")).thenReturn(existedRole);

        Role role = new Role();
        role.setRoleCode("PRODUCT_ADMIN");

        assertThrows(ApiException.class, () -> roleService.create(role));
        verify(roleDao, never()).insertSelective(role);
    }

    @Test
    void deleteShouldRejectMissingRole() {
        when(roleDao.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(ApiException.class, () -> roleService.delete(1L));
        verify(roleDao, never()).deleteByPrimaryKey(1L);
    }

    @Test
    void updateStatusShouldRejectMissingRole() {
        Role role = new Role();
        when(roleDao.selectByPrimaryKey(1L)).thenReturn(null);

        assertThrows(ApiException.class, () -> roleService.updateStatus(1L, 0));
        verify(roleDao, never()).updateByPrimaryKeySelective(org.mockito.ArgumentMatchers.any(Role.class));
    }
}
