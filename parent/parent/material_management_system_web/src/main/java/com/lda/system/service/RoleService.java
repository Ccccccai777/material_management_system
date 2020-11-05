package com.lda.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Menu;
import com.lda.system.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.RoleVO;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
public interface RoleService extends IService<Role> {

    IPage<Role> findRoleByPage(Page<Role> page, QueryWrapper<Role> wrapper);

    Role edit(Long id);

    void updateRole(RoleVO roleVO);

    void add(RoleVO roleVO);

    void updateStatus(Long id, Boolean status);

    List<Role> findAll();

    List<Long> findMenuIdsByRoleId(Long id);

    void authorization(Long id, Long[] mIds);

    Menu findMenuById(Long id);

    void delete(Long id);
}
