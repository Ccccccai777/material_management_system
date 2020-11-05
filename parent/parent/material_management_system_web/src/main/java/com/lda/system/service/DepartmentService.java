package com.lda.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
public interface DepartmentService extends IService<Department> {

    List<Department> findDeptAndCount();

    void updateDepartment(Department department);



    void add(Department department);

    Department edit(Long id);


    IPage<Department> findDepartmentByPage(Page<User> page, QueryWrapper<Department> wrapper);


    List<Department> findAll();
}
