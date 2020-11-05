package com.lda.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lda.system.entity.User;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> findDeptAndCount();

    IPage<Department> findDepartmentByPage(Page<User> page,@Param(Constants.WRAPPER) QueryWrapper<Department> wrapper);
}
