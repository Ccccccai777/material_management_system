package com.lda.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.ProductCategory;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ProductCategoryTreeNodeVO;
import com.lda.biz.entity.vo.ProductCategoryVO;
import com.lda.biz.service.ProductCategoryService;
import com.lda.biz.service.ProductService;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.handler.BusinessException;
import com.lda.response.Result;
import com.lda.response.ResultCode;
import com.lda.system.entity.Menu;
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
 *  前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
@Api(tags = "物资类别接口")
@RestController
@RequestMapping("/productCategory")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 分类树形结构(分页)
     *
     * @return
     */
    @ApiOperation(value = "分类树形结构")
    @GetMapping("/productCategoryTree")
    public Result productCategoryTree(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        PageVO<ProductCategoryTreeNodeVO> pageVO=productCategoryService.productCategoryTree(pageNum,pageSize);

        return  Result.ok().data("productCategoryTree",pageVO.getRows()).data("total",pageVO.getTotal());
    }

    /**
     * 只获取二级分类树：2级树
     *
     * @return
     */
    @ApiOperation(value = "二级分类树")
    @GetMapping("/getTwoCategoryTree")
    public Result getParentCategoryTree() {
        List<ProductCategoryTreeNodeVO> twoCategoryTree = productCategoryService.getTwoCategoryTree();
        return Result.ok().data("twoCategoryTree",twoCategoryTree);
    }

    /**
     * 添加物资分类
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资分类添加失败", operation = "物资分类添加")
    @RequiresPermissions({"productCategory:add"})
    @ApiOperation(value = "添加分类")
    @PostMapping("/addProductCategory")
    public Result addProductCategory(@RequestBody @Validated ProductCategoryVO productCategoryVO) {
        productCategoryService.add(productCategoryVO);
        return Result.ok();
    }
    /**
     * 编辑物资分类
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑分类")
    @RequiresPermissions({"productCategory:edit"})
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id) {
        ProductCategoryVO productCategoryVO = productCategoryService.edit(id);
        return Result.ok().data("productCategory",productCategoryVO);
    }
    /**
     * 更新物资分类
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资分类更新失败", operation = "物资分类更新")
    @ApiOperation(value = "更新分类")
    @RequiresPermissions({"productCategory:update"})
    @PutMapping("/updateProductCategory")
    public Result updateProductCategory( @RequestBody @Validated ProductCategoryVO productCategoryVO) {
        productCategoryService.updateProductCategory(productCategoryVO);
        return Result.ok();
    }

    /**
     * 删除物资分类
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资分类删除失败", operation = "物资分类删除")
    @ApiOperation(value = "删除分类")
    @RequiresPermissions({"productCategory:delete"})
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            List<ProductCategory> list = productCategoryService.list(new QueryWrapper<ProductCategory>().eq("pid", id));
            if (!CollectionUtils.isEmpty(list)) {
                throw new BusinessException(ResultCode.CATEGORIES_CANNOT_DELETE.getCode(),ResultCode.CATEGORIES_CANNOT_DELETE.getMessage());
            }
            productCategoryService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }
}

