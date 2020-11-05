package com.lda.biz.controller;


import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.SupplierVO;
import com.lda.biz.service.SupplierService;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
@Api(tags = "物资来源接口")
@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 来源列表
     *
     * @return
     */
    @ApiOperation(value = "来源列表", notes = "来源列表,根据来源名模糊查询")
    @PostMapping("/findSupplierList")
    public Result findSupplierList(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,
                                   @RequestBody SupplierVO supplierVO) {
        PageVO<SupplierVO> supplierVOPageVO = supplierService.findSupplierList(currentPage, pageSize, supplierVO);
        return Result.ok().data("supplierList",supplierVOPageVO.getRows()).data("total",supplierVOPageVO.getTotal());
    }
    /**
     * 添加来源
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资来源添加失败", operation = "物资来源添加")
    @RequiresPermissions({"supplier:add"})
    @ApiOperation(value = "添加来源")
    @PostMapping("/addSupplier")
    public Result addSupplier(@RequestBody @Validated SupplierVO supplierVO) {
        supplierService.addSupplier(supplierVO);
        return Result.ok();
    }

    /**
     * 编辑来源
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑来源", notes = "编辑来源信息")
    @RequiresPermissions({"supplier:edit"})
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id) {
        SupplierVO supplierVO = supplierService.edit(id);
        return Result.ok().data("supplier",supplierVO);
    }

    /**
     * 更新来源
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资来源更新失败", operation = "物资来源更新")
    @ApiOperation(value = "更新来源", notes = "更新来源信息")
    @RequiresPermissions({"supplier:update"})
    @PutMapping("/updateSupplier")
    public Result updateSupplier( @RequestBody @Validated SupplierVO supplierVO) {
        supplierService.updateSupplier(supplierVO);
        return Result.ok();
    }

    /**
     * 删除来源
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资来源删除失败", operation = "物资来源删除")
    @ApiOperation(value = "删除来源", notes = "删除来源信息")
    @RequiresPermissions({"supplier:delete"})
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        supplierService.delete(id);
        return Result.ok();
    }

    /**
     * 所有来源
     *
     * @return
     */
    @ApiOperation(value = "所有来源", notes = "所有来源列表")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SupplierVO> supplierVOS = supplierService.findAll();
        return Result.ok().data("supplier",supplierVOS);
    }
}

