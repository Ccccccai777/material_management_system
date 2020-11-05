package com.lda.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.protobuf.ServiceException;
import com.lda.handler.BusinessException;
import com.lda.jwt.JWTToken;
import com.lda.response.ResultCode;
import com.lda.system.converter.MenuConverter;
import com.lda.system.entity.*;
import com.lda.system.entity.bean.ActiveUser;
import com.lda.system.entity.vo.DepartmentVO;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.UserInfoVO;
import com.lda.system.enums.UserStatusEnum;
import com.lda.system.enums.UserTypeEnum;
import com.lda.system.mapper.*;
import com.lda.system.service.UserRoleService;
import com.lda.system.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.system.utils.MenuTreeBuilder;
import com.lda.utils.JWTUtils;
import com.lda.utils.MD5Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public IPage<User> findUserByPage(Page<User> page, QueryWrapper<User> wrapper) {
        return this.baseMapper.findUserByPage(page, wrapper);
    }

    @Transactional
    @Override
    public void add(User user) {
        String username = user.getUsername();
        Long departmentId = user.getDepartmentId();

        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        Integer count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_ALREADY_EXIST.getCode(), ResultCode.USER_ACCOUNT_ALREADY_EXIST.getMessage());
        }
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST.getCode(), ResultCode.DEPARTMENT_NOT_EXIST.getMessage());

        }
        String salt = UUID.randomUUID().toString().substring(0, 32);
        user.setPassword(MD5Utils.md5Encryption(user.getPassword(), salt));
        /*user.setModifiedTime(new Date());
        user.setCreateTime(new Date());*/
        user.setSalt(salt);
        //添加的用户默认是普通用户
        user.setType(UserTypeEnum.SYSTEM_USER.getTypeCode());
        //添加的用户默认启用
        user.setStatus(UserStatusEnum.AVAILABLE.getStatusCode());
        this.baseMapper.insert(user);


    }

    @Override
    public User edit(Long id) {

        User user = this.baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(), ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());
        }

        return user;
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        String username = user.getUsername();
        Long departmentId = user.getDepartmentId();
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST.getCode(), ResultCode.DEPARTMENT_NOT_EXIST.getMessage());

        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id", user.getId());
        User user1 = this.baseMapper.selectById(user.getId());
        System.out.println(user1);
        //查用户名重复没 如果是原来的用过户名则通过 是其它的用户名抛异常
        QueryWrapper<User> qw1 = new QueryWrapper<>();
        qw1.eq("username", username);
        Integer count = this.baseMapper.selectCount(qw1);
        if (user1 != null && user.getUsername().equals(user1.getUsername()) || count <= 0) {
            qw.eq("id", user.getId());
            String salt = UUID.randomUUID().toString().substring(0, 32);
            user.setPassword(MD5Utils.md5Encryption(user.getPassword(), salt));
            user.setSalt(salt);
        /* user.setModifiedTime(new Date());*/
            this.baseMapper.update(user, qw);
        } else if (count > 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_ALREADY_EXIST.getCode(), ResultCode.USER_ACCOUNT_ALREADY_EXIST.getMessage());
        }


    }
    /**
     * 更新用户状态
     * @param id
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long id, Boolean status) {
        User user = this.baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(), ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());

        }
        //需要补上是否为当前活动用户的判断
        User t = new User();
        t.setId(id);
        t.setStatus(status ? UserStatusEnum.DISABLE.getStatusCode() : UserStatusEnum.AVAILABLE.getStatusCode());
        this.baseMapper.update(t, new QueryWrapper<User>().eq("id", id));


    }
    /*
    * 查询所有用户
    * @return
    * */

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 用户拥有的角色ID
     * @param id 用户id
     * @return
     */
    @Override
    public List<Long> roles(Long id) {
        User user = this.baseMapper.selectById(id);
        if (user==null) {
            throw  new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(),ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());
        }
        return userMapper.roles(id);
    }

    /**
     * 分配角色
     * @param id 用户id
     * @param rids 角色数组
     */
    @Transactional
    @Override
    public void assignRoles(Long id, Long[] rids) {
        User user = userMapper.selectById(id);
        if (user==null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(),ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());
        }
        if (rids.length>0) {
            userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id",id));
            for (Long rid : rids) {
                Role role = roleMapper.selectById(rid);
                if (role==null) {
                    throw new BusinessException(ResultCode.Role_NOT_EXIST.getCode(),ResultCode.Role_NOT_EXIST.getMessage());
                }
                UserRole userRole = new UserRole();
                userRole.setUserId(id);
                userRole.setRoleId(rid);
                userRoleMapper.insert(userRole);
            }
        }else {
            userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id",id));
        }

    }

    /*
    *  根据用户名密码登入
     * @param username 用户名  password密码
     * @return
    * */

    @Override
    public String login(String username, String password) {
        User user = findUserByName(username);
        if (user!=null) {
            String salt=user.getSalt();
            String secret = MD5Utils.md5Encryption(password, salt);
            String token = JWTUtils.sign(username, secret);
            JWTToken jwtToken = new JWTToken(token);
            try {
                SecurityUtils.getSubject().login(jwtToken);
                return token;
            } catch (AuthenticationException e) {
                System.out.println("aaaaaa");
                 throw new BusinessException(ResultCode.USER_TOKEN_ERROR.getCode(),e.getMessage());
            }
        }else {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(),ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());
        }

    }
    /*
    * 根据用户名查询用户
    * @param name 用户名
    * @return User
    * */

    @Override
    public User findUserByName(String username) {

        try {
            User user = this.baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));
            return user;
        } catch (Exception e) {
            throw  new BusinessException(ResultCode.USER_ACCOUNT_IS_NOT_UNIQUE.getCode(),ResultCode.USER_ACCOUNT_IS_NOT_UNIQUE.getMessage());
        }

    }

    @Override
    public UserInfoVO userInfo() {
        UserInfoVO userInfoVO = new UserInfoVO();
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        if (activeUser != null) {
            userInfoVO.setAvatar(activeUser.getUser().getAvatar());
            userInfoVO.setUsername(activeUser.getUser().getUsername());
            userInfoVO.setUrl(activeUser.getUrls());
            userInfoVO.setNickname(activeUser.getUser().getNickname());
            List<String> roleNames = activeUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
            System.out.println(activeUser.getRoles());
            System.out.println(activeUser.getPermissions());
            userInfoVO.setRoles(roleNames);
            userInfoVO.setPerms(activeUser.getPermissions());
            userInfoVO.setIsAdmin(activeUser.getUser().getType()== UserTypeEnum.SYSTEM_ADMIN.getTypeCode());
            Department dept = departmentMapper.selectById(activeUser.getUser().getDepartmentId());
            dept.setTotal(departmentMapper.selectCount(null));
            if(dept!=null){
                userInfoVO.setDepartment(dept.getName());
            }
        }
        return userInfoVO;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user==null) {
            throw  new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST.getCode(),ResultCode.USER_ACCOUNT_NOT_EXIST.getMessage());
        }
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        if (user.getId().equals(activeUser.getUser().getId())) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_THE_CURRENT_ACTIVE_ACCOUNT.getCode(),ResultCode.CANNOT_DELETE_THE_CURRENT_ACTIVE_ACCOUNT.getMessage());
        }
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id",id));
          userMapper.deleteById(id);


    }

    @Override
    public List<MenuNodeVO> findMenu() {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Menu> menuList=null;
        List<MenuNodeVO> menuNodeVOS = null;
        if (activeUser.getUser().getType()== UserTypeEnum.SYSTEM_ADMIN.getTypeCode()) {
            menuList = menuMapper.selectList(null);

        }else {
            menuList=activeUser.getMenus();
        }
        if (!CollectionUtils.isEmpty(menuList)) {
            menuNodeVOS= MenuConverter.converterToMenuNodeVO(menuList);

        }
        System.out.println(menuNodeVOS);
        return MenuTreeBuilder.build(menuNodeVOS);
    }
}


