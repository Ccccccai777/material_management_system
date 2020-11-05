package com.lda.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.UserInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
public interface UserService extends IService<User> {

    IPage<User> findUserByPage(Page<User> page, QueryWrapper<User> wrapper);

    void add(User user);

    User edit(Long id);

    void updateUser(User user);

    void updateStatus(Long id, Boolean status);

    List<User> findAll();

    List<Long> roles(Long id);

    void assignRoles(Long id, Long[] rids);

    String login(String username, String password);

    User findUserByName(String username);

    UserInfoVO userInfo();

    void delete(Long id);

    List<MenuNodeVO> findMenu();
}
