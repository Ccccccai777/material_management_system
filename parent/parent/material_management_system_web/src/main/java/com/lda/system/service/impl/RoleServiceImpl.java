package com.lda.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.*;
import com.lda.system.entity.bean.ActiveUser;
import com.lda.system.entity.vo.RoleVO;
import com.lda.system.enums.RoleStatusEnum;
import com.lda.system.enums.UserStatusEnum;
import com.lda.system.mapper.MenuMapper;
import com.lda.system.mapper.RoleMapper;
import com.lda.system.mapper.RoleMenuMapper;
import com.lda.system.service.MenuService;
import com.lda.system.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private  RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Override
    public IPage<Role> findRoleByPage(Page<Role> page, QueryWrapper<Role> wrapper) {
        return this.baseMapper.findRoleByPage(page,wrapper);
    }

    @Override
    public Role edit(Long id) {
        Role role = this.baseMapper.selectById(id);

        if (role == null) {
            throw new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());
        }
        return  role;

    }
    @Transactional
    @Override
    public void updateRole(RoleVO roleVO) {
        String roleName = roleVO.getRoleName();
        Long roleVOId = roleVO.getId();

        QueryWrapper<Role> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",roleVOId);
        Role dbRole = this.baseMapper.selectById(roleVOId);
        System.out.println(dbRole);
        //查角色名重复没
        QueryWrapper<Role> qw1 = new QueryWrapper<>();
        qw1.eq("role_name",roleName);
        List<Role> roleList = this.baseMapper.selectList(qw1);
        System.out.println(roleList);
        if( dbRole!=null && roleVO.getRoleName().equals(dbRole.getRoleName())|| CollectionUtils.isEmpty(roleList)){
            qw.eq("id",roleVOId);
            Role role = new Role();
            BeanUtils.copyProperties(roleVO,role);
            role.setModifiedTime(new Date());
            this.baseMapper.update(role,qw);
        }
        else if (!CollectionUtils.isEmpty(roleList)) {
            throw  new BusinessException(ResultCode.Role_ALREADY_EXIST.getCode(),ResultCode.Role_ALREADY_EXIST.getMessage());
        }
    }

    @Transactional
    @Override
    public void add(RoleVO roleVO) {
        String roleName = roleVO.getRoleName();
        Long roleVOId = roleVO.getId();

        QueryWrapper<Role> qw = new QueryWrapper<>();
        qw.eq("role_name",roleName);
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.Role_ALREADY_EXIST.getCode(),ResultCode.Role_ALREADY_EXIST.getMessage());
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleVO,role);
        role.setModifiedTime(new Date());
        role.setCreateTime(new Date());
        role.setStatus(RoleStatusEnum.AVAILABLE.getStatusCode());
        this.baseMapper.insert(role);
    }
    @Transactional
    @Override
    public void updateStatus(Long id, Boolean status) {
        Role role = this.baseMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());

        }
        Role t = new Role();
        t.setId(id);
        t.setStatus(status? RoleStatusEnum.DISABLE.getStatusCode():RoleStatusEnum.AVAILABLE.getStatusCode());
        this.baseMapper.update(t,new QueryWrapper<Role>().eq("id",id));
    }

    @Override
    public List<Role> findAll() {


        return this.baseMapper.selectList(new QueryWrapper<Role>().ne("status",0));
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long id) {
        Role role = this.baseMapper.selectById(id);
        if (role==null) {
            throw  new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());
        }
        return roleMapper.findMenuIdsByRoleId(id) ;
    }
    @Transactional
    @Override
    public void authorization(Long id, Long[] mIds) {

        Role role = roleMapper.selectById(id);
        if (role==null) {
            throw new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());
        }
        if (mIds.length>0) {
            roleMenuMapper.delete(new QueryWrapper<RoleMenu>().eq("role_id",id));
            for (Long mid : mIds) {
                Menu menu = menuMapper.selectById(mid);
                if (menu==null) {
                    throw new BusinessException(ResultCode.MENU_NOT_EXIST.getCode(),ResultCode.MENU_NOT_EXIST.getMessage());
                }
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setMenuId(mid);
                roleMenu.setRoleId(id);
                roleMenuMapper.insert(roleMenu);
            }
        }else {
            roleMenuMapper.delete(new QueryWrapper<RoleMenu>().eq("role_id",id));
        }
    }

    @Override
    public Menu findMenuById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu==null) {
            throw  new BusinessException(ResultCode.MENU_NOT_EXIST.getCode(),ResultCode.MENU_NOT_EXIST.getMessage());
        }
        return menu;
    }

    @Override
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role==null) {
            throw  new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());
        }
        roleMapper.deleteById(id);
    }


}
