package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;
import com.lda.system.converter.RoleConverter;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.RoleTransferItemVO;
import com.lda.system.entity.vo.UserInfoVO;
import com.lda.system.entity.vo.UserVO;
import com.lda.system.service.AliOssService;
import com.lda.system.service.LoginLogService;
import com.lda.system.service.RoleService;
import com.lda.system.service.UserService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
@RestController
@RequestMapping("/user")
@Api(value = "系统用户模块", tags = "系统用户接口")
public class UserController {
    @Autowired
     private UserService userService;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private RoleService roleService;
    @GetMapping("findByUserList")
    public Result findByUserList(@RequestParam(defaultValue = "1") Integer current,@RequestParam(defaultValue = "6") Integer size){
        Page<User> page=new Page<>(current,size);
        Page<User> userPage = userService.page(page);
        long total = userPage.getTotal();
        List<User> userList = userPage.getRecords();
        return Result.ok().data("total", total).data("userList", userList);

    }  /**
     * 分页查询用户
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "分页查询用户", notes = "分页查询用户信息")

    @PostMapping("/findUserByPage")
    public Result findUserByPage(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "6") Integer size,
                                 @RequestBody UserVO userVO){
        Page<User> page=new Page<>(current,size);
        QueryWrapper<User> wrapper = getWrapper(userVO);
        System.out.println(userVO);
        IPage<User> userPage = userService.findUserByPage(page,wrapper);
        long total = userPage.getTotal();
        List<User> userList = userPage.getRecords();
         return Result.ok().data("total", total).data("userList", userList);

    }

    /**
     * 添加用户信息
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "添加用户", notes = "添加用户信息")
    @ControllerEndpoint(exceptionMessage = "添加用户失败", operation = "添加用户")
    @RequiresPermissions({"user:add"})
    @PostMapping("/addUser")
    public Result addUser(@RequestBody User user){

            userService.add(user);
            return Result.ok();
    }
    /**
     * 加载菜单
     *
     * @return
     */
    @ApiOperation(value = "加载菜单", notes = "用户登入后,根据角色加载菜单树")
    @GetMapping("/findMenu")
    public Result findMenu() {
        List<MenuNodeVO> menuTreeVOS = userService.findMenu();
        return Result.ok().data("menuTree",menuTreeVOS);
    }
    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "修改用户", notes = "修改用户信息")
    @ControllerEndpoint(exceptionMessage = "修改用户失败", operation = "修改用户")
    @RequiresPermissions({"user:update"})
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user){
        try {
            userService.updateUser(user);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 删除用户信息
     *
     * @param user
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "删除用户失败", operation = "删除用户")
    @ApiOperation(value = "删除用户", notes = "删除用户信息根据Id")
    @RequiresPermissions({"user:delete"})
    @DeleteMapping("/delete/{id}")
    public Result addUser(@PathVariable Long id){
        try {
            userService.delete(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 编辑用户信息
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "编辑部门", notes = "获取部门的详情，编辑部门信息")
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id){

          User user=userService.edit(id);
            return Result.ok().data("user",user);



    }

    /**
     * 获取用户信息
     *
     * @param
     * @return
     */
    @ApiOperation(value = "用户信息", notes = "当前用户的信息")
    @GetMapping("/userInfo")
    public Result userInfo(){

        UserInfoVO userInfo=userService.userInfo();
        return Result.ok().data("userInfo",userInfo);

    }
    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "更新用户状态失败", operation = "用户|禁用/启用")
    @ApiOperation(value = "用户状态", notes = "禁用和启用这两种状态")
    @RequiresPermissions({"user:status"})
    @PutMapping("/updateUserStatus/{id}/{status}")
    public Result updateUserStatus(@PathVariable Long id, @PathVariable Boolean status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }
    /**
     * 分配角色
     *
     * @param id
     * @param rids
     * @return
     */
    @ApiOperation(value = "分配角色", notes = "角色分配给用户")
    @ControllerEndpoint(exceptionMessage = "分配角色失败", operation = "角色分配给用户")
    @RequiresPermissions({"user:assign"})
    @PostMapping("/assignRoles/{id}")
    public Result assignRoles(@PathVariable Long id, @RequestBody Long[] rids) {
        userService.assignRoles(id, rids);
        return Result.ok();
    }

    /**
     * 登入
     *
     * @param id
     * @param rids
     * @return
     */
    @ApiOperation(value = "登入")
    @PostMapping("/login")
    public Result login(@NotBlank(message = "用户名必填") String username,
                        @NotBlank(message = "密码必填") String password,
                        HttpServletRequest request) {

         String token= userService.login(username, password);
         //添加登入日志
         loginLogService.add(request);

        return Result.ok().data("token",token);
    }
    /**
     * 查询用户角色信息
     * @param response
     */
    @ApiOperation(value = "已有角色", notes = "根据用户id，获取用户已经拥有的角色")
    @GetMapping("/roles/{id}")
    public Result export(@PathVariable Long id) {
        List<Long> uRolesIds = this.userService.roles(id);
        List<Role> roleList = this.roleService.findAll();
        List<RoleTransferItemVO> itemVOS = RoleConverter.converterToRoleTransferItem(roleList);
        return Result.ok().data("values",uRolesIds).data("roles",itemVOS);

    }
    /**
     * 导出excel
     * @param response
     */
    @ControllerEndpoint(exceptionMessage = "导出excel失败", operation = "导出excel")
    @ApiOperation(value = "导出excel", notes = "导出所有角色的excel表格")
    @RequiresPermissions({"user:export"})
    @PostMapping("/excel")
    public void export(HttpServletResponse response) {
        List<User> users = this.userService.findAll();
        ExcelKit.$Export(User.class, response).downXlsx(users, false);
    }



    private QueryWrapper<User> getWrapper(UserVO userVO){
        System.out.println(userVO);
        QueryWrapper<User> qw = new QueryWrapper<>();
        if (userVO != null) {

            if (!StringUtils.isEmpty(userVO.getDepartmentId())) {
                qw.eq("department_id",userVO.getDepartmentId());
            }
            if (!StringUtils.isEmpty(userVO.getUsername())) {
                qw.like("username",userVO.getUsername());
            }
            if (!StringUtils.isEmpty(userVO.getEmail())) {
                qw.like("email",userVO.getEmail());
            }
            if (!StringUtils.isEmpty(userVO.getSex())) {
                qw.eq("sex",userVO.getSex());
            }
            if (!StringUtils.isEmpty(userVO.getNickname())) {
                qw.like("nickname",userVO.getNickname());
            }

        }
        return qw;
    }


}

