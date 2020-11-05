package com.lda.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.Department;
import com.lda.system.entity.User;
import com.lda.system.enums.UserStatusEnum;
import com.lda.system.enums.UserTypeEnum;
import com.lda.system.mapper.DepartmentMapper;
import com.lda.system.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> findDeptAndCount() {
        return this.baseMapper.findDeptAndCount();
    }
    @Transactional
    @Override
    public void updateDepartment(Department department) {
        String name = department.getName();
        Long departmentId = department.getId();

        QueryWrapper<Department> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",departmentId);
        Department department1 = this.baseMapper.selectById(department.getId());
        System.out.println(department1);
        //查用户名重复没
        QueryWrapper<Department> qw1 = new QueryWrapper<>();
        qw1.eq("name",name);
        Integer count = this.baseMapper.selectCount(qw1);
        System.out.println(count);
        if( department1!=null && department.getName().equals(department1.getName())|| count<=0){
            qw.eq("id",department.getId());
            department.setModifiedTime(new Date());
            this.baseMapper.update(department,qw);
        }
        else if ( count>0) {
            throw  new BusinessException(ResultCode.DEPARTMENT_ALREADY_EXIST.getCode(),ResultCode.DEPARTMENT_ALREADY_EXIST.getMessage());
        }

    }

    @Transactional
    @Override
    public void add(Department department) {
        String name = department.getName();
        Long departmentId = department.getId();

        QueryWrapper<Department> qw = new QueryWrapper<>();
        qw.eq("name",name);
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.DEPARTMENT_ALREADY_EXIST.getCode(),ResultCode.DEPARTMENT_ALREADY_EXIST.getMessage());
        }
        department.setModifiedTime(new Date());
        department.setCreateTime(new Date());
        this.baseMapper.insert(department);

    }

    @Override
    public Department edit(Long id)
    {
        Department department = this.baseMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST.getCode(),ResultCode.DEPARTMENT_NOT_EXIST.getMessage());
        }
        return department;
    }

    @Override
    public IPage<Department> findDepartmentByPage(Page<User> page, QueryWrapper<Department> wrapper) {
        return departmentMapper.findDepartmentByPage(page,wrapper);
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.selectList(new QueryWrapper<>());
    }


}
