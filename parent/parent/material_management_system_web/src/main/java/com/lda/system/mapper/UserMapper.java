package com.lda.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
public interface UserMapper extends BaseMapper<User> {

    IPage<User> findUserByPage(Page<User> page,@Param(Constants.WRAPPER) QueryWrapper<User> wrapper);

    List<Long> roles(Long id);

    List<User> findAll();
}
