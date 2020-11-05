package com.lda.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
public interface RoleMapper extends BaseMapper<Role> {

    IPage<Role> findRoleByPage(Page<Role> page, @Param(Constants.WRAPPER) QueryWrapper<Role> wrapper);

    List<Long> findMenuIdsByRoleId(Long id);
}
