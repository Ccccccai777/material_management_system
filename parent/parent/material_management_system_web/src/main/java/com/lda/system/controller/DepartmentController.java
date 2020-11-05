package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.handler.BusinessException;
import com.lda.response.Result;
import com.lda.response.ResultCode;
import com.lda.system.entity.Department;
import com.lda.system.entity.User;
import com.lda.system.entity.vo.DepartmentVO;
import com.lda.system.entity.vo.UserVO;
import com.lda.system.service.DepartmentService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
@RestController
@Api("部门接口")
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @ApiOperation(value = "查询部门人数",notes = "从部门表中分组查出用户人数")
    @GetMapping("/findDeptAndCount")
    public Result findDeptAndCount(){
        List<Department> departments = departmentService.findDeptAndCount();
        if (departments.size()==0) {
            throw  new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST.getCode(),
                                 ResultCode.DEPARTMENT_NOT_EXIST.getMessage());
        }
        return Result.ok().data("departments",departments);
    }

    @PostMapping("/findDepartmentByPage")
    @ApiOperation(value = "查询部门列表包括分页", notes = "查询部门列表包括分页")
    public Result findDepartmentByPage(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestBody DepartmentVO departmentVO){
        Page<User> page=new Page<>(current,size);
        QueryWrapper<Department> wrapper = getWrapper(departmentVO);
        IPage<Department> departmentIPage = departmentService.findDepartmentByPage(page,wrapper);
        long total = departmentIPage.getTotal();
        List<Department> departmentList = departmentIPage.getRecords();
        return Result.ok().data("total", total).data("departmentList", departmentList);

    }

    /**
     * 添加用户信息
     *
     * @param department
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "添加部门失败", operation = "添加部门")
    @ApiOperation(value = "添加部门", notes = "添加部门信息")
    @RequiresPermissions({"department:add"})
    @PostMapping("/addDepartment")
    public Result addDepartment(@RequestBody Department department){
        try {
            departmentService.add(department);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 修改部门信息
     *
     * @param department
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "更新部门失败", operation = "更新部门")
    @ApiOperation(value = "修改部门", notes = "修改部门信息")
    @RequiresPermissions({"department:update"})
    @PutMapping("/updateDepartment")
    public Result updateDepartment(@RequestBody Department department){
        try {
            departmentService.updateDepartment(department);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 删除部门信息
     *
     * @param department
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "删除部门失败", operation = "删除部门")
    @ApiOperation(value = "删除部门", notes = "删除部门信息根据Id")
    @RequiresPermissions({"department:delete"})
    @DeleteMapping("/delete/{id}")
    public Result deleteDepartment(@PathVariable Long id){
        try {
            departmentService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 编辑用户信息
     *
     * @param department
     * @return
     */
    @ApiOperation(value = "编辑部门", notes = "获取部门的详情，编辑部门信息")
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id){

        Department department=departmentService.edit(id);
        return Result.ok().data("department",department);



    }
    /**
     * 导出excel
     * @param response
     */
    @ApiOperation(value = "导出excel", notes = "导出所有部门的excel表格")
    @PostMapping("/excel")
    @RequiresPermissions("department:export")
    @ControllerEndpoint(exceptionMessage = "导出Excel失败",operation = "导出部门excel")
    public void export(HttpServletResponse response) {
        List<Department> departments = this.departmentService.findAll();
        System.out.println(departments);
        ExcelKit.$Export(Department.class, response).downXlsx(departments, false);
    }
    private QueryWrapper<Department> getWrapper(DepartmentVO departmentVO){
        QueryWrapper<Department> qw = new QueryWrapper<>();
        if (departmentVO != null) {

            if (!StringUtils.isEmpty(departmentVO.getName())) {
                qw.like("name",departmentVO.getName());
            }

        }

        System.out.println(departmentVO.getName());
        qw.groupBy("d.id ","d.phone","d.`name`");
        return qw;
    }

}

