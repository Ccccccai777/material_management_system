package com.lda.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.Product;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ProductStockVO;
import com.lda.biz.entity.vo.ProductVO;
import com.lda.biz.service.ProductService;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;
import com.lda.system.entity.User;
import com.lda.system.entity.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
@Api(tags = "物资接口")
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 全部物资列表
     * @return
     */
    @ApiOperation(value = "物资列表", notes = "物资列表,根据物资名模糊查询")
    @PostMapping("/findProductList")
    public Result findProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize") Integer pageSize,
                                  @RequestParam(value = "categorys", required = false) String categorys,
                                 @RequestBody ProductVO productVO) {
        Page<ProductVO> page=new Page<>(pageNum,pageSize);
        IPage<ProductVO> productByPage = productService.findProductByPage(page,categorys,productVO);
        long total = productByPage.getTotal();
        List<ProductVO> productList = productByPage.getRecords();
        System.out.println((productList));
        return Result.ok().data("productList",productList).data("total",total);
    }

    /**
     * 可入库物资(入库页面使用)
     * @return
     */
    @ApiOperation(value = "可入库物资列表", notes = "物资列表,根据物资名模糊查询")
    @PostMapping("/findProducts")
    public Result findProducts(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,
                                  @RequestParam(value = "categorys", required = false) String categorys,
                               @RequestBody  ProductVO productVO) {
        Page<ProductVO> page=new Page<>(pageNum,pageSize);
        productVO.setStatus(0);
        IPage<ProductVO> productByPage = productService.findProductByPage(page,categorys,productVO);
        long total = productByPage.getTotal();
        List<ProductVO> productList = productByPage.getRecords();
        return Result.ok().data("productList",productList).data("total",total);
    }
    /**
     * 可出库物资(出库页面使用)
     * @return
     */
    @ApiOperation(value = "可出库物资列表", notes = "物资列表,根据物资名模糊查询")
    @PostMapping("/findOutboundMaterials")
    public Result findOutboundMaterials(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,
                               @RequestParam(value = "categorys", required = false) String categorys,
                               @RequestBody  ProductVO productVO) {
        Page<ProductVO> page=new Page<>(pageNum,pageSize);
        productVO.setStatus(0);
        IPage<ProductStockVO> productByPage = productService.findOutboundMaterials(page,categorys,productVO);
        long total = productByPage.getTotal();
        List<ProductStockVO> productList = productByPage.getRecords();
        return Result.ok().data("productList",productList).data("total",total);
    }

    /**
     * 所有库存(饼图使用)
     * @return
     */
    @ApiOperation(value = "所有库存", notes = "物资列表,所有库存，饼图使用")
    @PostMapping("/findAllStocks")
    public Result findAllStocks(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,
                                        @RequestParam(value = "categorys", required = false) String categorys,
                                        @RequestBody  ProductVO productVO) {
        Page<ProductVO> page=new Page<>(pageNum,pageSize);
        productVO.setStatus(0);
        IPage<ProductStockVO> productByPage = productService.findAllStocks(page,categorys,productVO);
        long total = productByPage.getTotal();
        List<ProductStockVO> productList = productByPage.getRecords();
        return Result.ok().data("productList",productList).data("total",total);
    }
    /**
     * 添加物资
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "添加物资失败", operation = "物资资料添加")
    @ApiOperation(value = "添加物资")
    @RequiresPermissions({"product:add"})
    @PostMapping("/saveProduct")
    public Result saveProduct(@RequestBody @Validated ProductVO productVO) {
        if (productVO.getCategoryKeys().length != 3) {
            return Result.error(9999,"物资需要3级分类");
        }
        productService.add(productVO);
        return Result.ok();
    }

    /**
     * 编辑物资
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑物资", notes = "编辑物资信息")
    @RequiresPermissions({"product:edit"})
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id) {
        ProductVO productVO = productService.edit(id);
        return Result.ok().data("product",productVO);
    }
    /**
     * 更新物资
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "更新物资失败", operation = "物资资料更新")
    @ApiOperation(value = "更新物资", notes = "更新物资信息")
    @RequiresPermissions({"product:update"})
    @PutMapping("/updateProduct")
    public Result updateProduct( @RequestBody ProductVO productVO) {
        System.out.println(productVO);
        if (productVO.getCategoryKeys().length != 3) {
            return Result.error(9999,"物资需要3级分类");
        }
        productService.updateProduct(productVO);
        return Result.ok();
    }


    /**
     * 移入回收站
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "回收物资失败", operation = "物资资料回收")
    @ApiOperation(value = "移入回收站", notes = "移入回收站")
    //这里改了权限
    @RequiresPermissions({"product:moveToTrash"})
    @PutMapping("/moveToTrash/{id}")
    public Result moveToTrash(@PathVariable Long id) {
        productService.moveToTrash(id);
        return Result.ok();
    }
    /**
     * 删除物资
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "删除物资失败", operation = "物资资料删除")
    @ApiOperation(value = "删除物资", notes = "删除物资信息")
    @RequiresPermissions({"product:delete"})
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }
    /**
     * 物资添加审核
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资添加审核失败", operation = "物资资料审核")
    @ApiOperation(value = "物资添加审核", notes = "物资添加审核")
    @RequiresPermissions({"product:publish"})
    @PutMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        productService.publish(id);
        return Result.ok();
    }
    /**
     * 恢复数据从回收站
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "恢复物资失败", operation = "物资资料恢复")
    @ApiOperation(value = "恢复物资", notes = "从回收站中恢复物资")
    @RequiresPermissions({"product:restore"})
    //这里改了权限
    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable Long id) {
        productService.restore(id);
        return Result.ok();
    }

}

