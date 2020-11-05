package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.handler.BusinessException;
import com.lda.response.Result;
import com.lda.response.ResultCode;
import com.lda.system.converter.MenuConverter;
import com.lda.system.entity.Menu;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.MenuVO;
import com.lda.system.entity.vo.MenuVO;
import com.lda.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Api(tags = "菜单权限接口")
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    /**
     * 加载菜单树
     *
     * @return
     */
    @ApiOperation(value = "加载菜单树", notes = "获取所有菜单树，以及展开项")
      @GetMapping("/findTree")
       public Result findTree(){
        List<MenuNodeVO>  menuTree=menuService.findMenuTree();
        List<Long>  ids=menuService.findOpenIds();
       return   Result.ok().data("menuTree",menuTree).data("open",ids);
     }
    /**
     * 添加
     *
     * @return
     */
    @ApiOperation(value = "添加菜单")
    @ControllerEndpoint(exceptionMessage = "添加菜单失败", operation = "添加菜单")
    @RequiresPermissions({"menu:add"})
    @PostMapping("/addMenu")
    public Result addMenu(@RequestBody @Validated MenuVO menuVo){

            menuService.addMenu(menuVo);
            return   Result.ok();


    }
    /**
     * 修改菜单信息
     *
     * @param MenuVO
     * @return
     */
    @ApiOperation(value = "修改菜单", notes = "修改菜单信息")
    @ControllerEndpoint(exceptionMessage = "修改菜单失败", operation = "修改菜单")
    @RequiresPermissions({"menu:update"})
    @PutMapping("/updateMenu")
    public Result updateMenu(@RequestBody @Validated MenuVO menuVO){
        try {
            menuService.updateMenu(menuVO);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    /**
     * 编辑角菜单信息
     *
     * @param department
     * @return
     */
    @ApiOperation(value = "编辑菜单", notes = "获取菜单的详情，编辑菜单信息")
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id){
        Menu menu=menuService.edit(id);
        MenuVO menuVO = MenuConverter.converterToMenuVO(menu);
        return Result.ok().data("menu",menuVO);

    }
    /**
     * 删除节点信息
     *
     * @param department
     * @return
     */
    @ApiOperation(value = "删除节点", notes = "删除节点信息根据Id")
    @ControllerEndpoint(exceptionMessage = "删除节点失败", operation = "删除节点")
    @RequiresPermissions({"menu:delete"})
    @DeleteMapping("/delete/{id}")
    public Result deleteMenu(@PathVariable Long id){
        try {
            List<Menu> list = menuService.list(new QueryWrapper<Menu>().eq("parent_id", id));
            if (!CollectionUtils.isEmpty(list)) {
                throw new BusinessException(ResultCode.MENU_CANNOT_DELETE.getCode(),ResultCode.MENU_CANNOT_DELETE.getMessage());
            }
            menuService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }


}

