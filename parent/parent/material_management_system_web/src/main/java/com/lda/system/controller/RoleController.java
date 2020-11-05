package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;

import com.lda.system.entity.Department;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.lda.system.entity.vo.DepartmentVO;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.RoleVO;
import com.lda.system.service.DepartmentService;
import com.lda.system.service.MenuService;
import com.lda.system.service.RoleService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@RestController
@Api(tags = "角色接口")
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "查询所有角色包括模糊查询", notes = "查询所有角色包括模糊查询")
    @PostMapping("/findRoleByPage")
    public Result findRoleByPage(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestBody RoleVO roleVO){
        Page<Role> page=new Page<>(current,size);
        QueryWrapper<Role> wrapper = getWrapper(roleVO);
        IPage<Role> roleIPage = roleService.findRoleByPage(page,wrapper);
        long total = roleIPage.getTotal();
        List<Role> roleList = roleIPage.getRecords();
        return Result.ok().data("total", total).data("roleList", roleList);

    }

    @ApiOperation(value = "角色菜单")
    @GetMapping("/findRoleMenu/{id}")
    public Result findRoleMenu(@PathVariable Long id){
        List<MenuNodeVO> menuTree = menuService.findMenuTree();
        List<Long> openIds = menuService.findOpenIds();
        List<Long> mIds=roleService.findMenuIdsByRoleId(id);
        return Result.ok().data("menuTree",menuTree).data("openIds",openIds).data("mIds",mIds);

    }
    /**
     * 为角色添加权限
     *
     * @param roleVO
     * @return
     */
    @ApiOperation(value = "分配权限", notes = "分配权限信息")
    @ControllerEndpoint(exceptionMessage = "分配权限失败", operation = "分配权限")
    @RequiresPermissions({"role:authority"})
    @PostMapping("/authorization/{id}")
    public Result authorization(@PathVariable Long id,@RequestBody Long[] mIds){
        try {
            roleService.authorization(id,mIds);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 添加角色信息
     *
     * @param roleVO
     * @return
     */
    @ApiOperation(value = "添加角色", notes = "添加角色信息")
    @ControllerEndpoint(exceptionMessage = "添加角色失败", operation = "添加角色")
    @RequiresPermissions({"role:add"})
    @PostMapping("/addRole")
    public Result addRole(@RequestBody @Validated RoleVO roleVO){
        try {
            roleService.add(roleVO);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "角色状态", notes = "禁用和启用这两种状态")
    @ControllerEndpoint(exceptionMessage = "更新角色状态失败", operation = "更新角色状态")
    @RequiresPermissions({"role:assign"})
    @PutMapping("/updateRoleStatus/{id}/{status}")
    public Result updateRoleStatus(@PathVariable Long id, @PathVariable Boolean status) {
        roleService.updateStatus(id, status);
        return Result.ok();
    }
    /**
     * 修改角色信息
     *
     * @param roleVO
     * @return
     */
    @ApiOperation(value = "修改角色", notes = "修改角色信息")
    @PutMapping("/updateRole")
    @ControllerEndpoint(exceptionMessage = "修改角色失败", operation = "修改角色")
    @RequiresPermissions({"role:update"})
    public Result updateRole(@RequestBody @Validated RoleVO roleVO){
        try {
            roleService.updateRole(roleVO);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 导出excel
     * @param response
     */
    @ApiOperation(value = "导出excel", notes = "导出所有角色的excel表格")
    @ControllerEndpoint(exceptionMessage = "导出部门excel失败", operation = "导出部门excel")
    @RequiresPermissions({"role:export"})
    @PostMapping("/excel")
    public void export(HttpServletResponse response) {
        List<Role> roleList = this.roleService.findAll();
        ExcelKit.$Export(Role.class, response).downXlsx(roleList, false);
    }

    /**
     * 删除角色信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除角色", notes = "删除角色信息根据Id")
    @DeleteMapping("/delete/{id}")
    public Result deleteRole(@PathVariable  Long id){
        try {
            roleService.delete(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 编辑角色信息
     *
     * @param department
     * @return
     */
    @ApiOperation(value = "编辑角色", notes = "获取角色的详情，编辑角色信息")
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id){

        Role role=roleService.edit(id);
        return Result.ok().data("role",role);



    }
    private QueryWrapper<Role> getWrapper(RoleVO roleVO){
        QueryWrapper<Role> qw = new QueryWrapper<>();
        if (roleVO != null) {

            if (!StringUtils.isEmpty(roleVO.getRoleName())) {
                qw.like("role_name",roleVO.getRoleName());
            }

        }
        return qw;
    }
}

