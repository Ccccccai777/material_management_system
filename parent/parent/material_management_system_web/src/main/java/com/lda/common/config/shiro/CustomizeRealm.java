package com.lda.common.config.shiro;

import com.lda.handler.BusinessException;
import com.lda.jwt.JWTToken;
import com.lda.response.ResultCode;
import com.lda.system.entity.Menu;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.lda.system.entity.bean.ActiveUser;
import com.lda.system.mapper.MenuMapper;
import com.lda.system.mapper.RoleMapper;
import com.lda.system.service.UserService;
import com.lda.utils.JWTUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomizeRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        ActiveUser activeUser = (ActiveUser) principalCollection.getPrimaryPrincipal();
        if (activeUser.getUser().getType()==0) {
            //超级管理员有所有权限
            simpleAuthorizationInfo.addStringPermission("*:*");

        }else {
            List<Role> roles = activeUser.getRoles();
            List<String> permissions = new ArrayList<>(activeUser.getPermissions());
            //授权角色
            if (!CollectionUtils.isEmpty(roles)) {
                for (Role role : roles) {
                    simpleAuthorizationInfo.addRole(role.getRoleName());
                }
            }
            //授权权限
            if (!CollectionUtils.isEmpty(permissions)) {
                for (String permission : permissions) {
                    simpleAuthorizationInfo.addStringPermission(permission);
                }

            }
            System.out.println(permissions);
        }

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        JWTToken jwtToken = (JWTToken) authenticationToken;
        String token = (String) jwtToken.getCredentials();
        String username = JWTUtils.getUsername(token);
        if (username==null) {
            throw new AuthenticationException(" token错误，请重新登入！");
        }
        User user= userService.findUserByName(username);
        if (user==null) {
            throw new AuthenticationException("账户不存在！！");
        }
        if (JWTUtils.isExpire(token)) {
            throw new AuthenticationException("token过期，请重新登入！！！");
        }
        if (!JWTUtils.verify(token,username,user.getPassword())) {
            throw new CredentialsException("密码错误!");
        }
        if (user.getStatus()==0) {
            throw new LockedAccountException("账号已被锁定!");
        }
        List<Menu> menus=new ArrayList<>();
        List<Role> roles=new ArrayList<>();
        //查出用户下的所有角色和权限
        List<Long> roleIds = userService.roles(user.getId());
        for (Long rid : roleIds) {
            Role role = roleMapper.selectById(rid);
            roles.add(role);
        }

        if (!CollectionUtils.isEmpty(roles)) {
            for (Long roleId : roleIds) {
                List<Long> menuIds = roleMapper.findMenuIdsByRoleId(roleId);
                if (!CollectionUtils.isEmpty(menuIds)) {
                    for (Long menuId : menuIds) {
                        Menu menu = menuMapper.selectById(menuId);
                        if (menu==null) {
                            throw  new BusinessException(ResultCode.MENU_NOT_EXIST.getCode(),ResultCode.MENU_NOT_EXIST.getMessage());
                        }
                        menus.add(menu);
                    }
                }
            }
        }
        Set<String> urls=new HashSet<>();
        Set<String> perms=new HashSet<>();
        if (!CollectionUtils.isEmpty(menus)) {
            for (Menu menu : menus) {
                String url=menu.getUrl();
                String perm=menu.getPerms();
                if(Integer.parseInt(menu.getType())==0&& !StringUtils.isEmpty(url)){
                    urls.add(menu.getUrl());
                }
                if(Integer.parseInt(menu.getType())==1&&!StringUtils.isEmpty(perm)){
                    perms.add(menu.getPerms());
                }
            }
        }

        //把值赋给 当前用户 角色 菜单 url 权限  赋予ActiveUser
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUser(user);
        activeUser.setRoles(roles);
        activeUser.setMenus(menus);
        activeUser.setUrls(urls);
        activeUser.setPermissions(perms);

        return new SimpleAuthenticationInfo(activeUser,token,getName());
    }

}
