package com.lda.biz.controller;


import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ConsumerVO;
import com.lda.biz.service.ConsumerService;
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
 * @since 2020-10-17
 */
@Api(tags = "物资去向接口")
@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private ConsumerService consumerService;

    /**
     * 去向列表
     *
     * @return
     */
    @ApiOperation(value = "去向列表", notes = "去向列表,根据去向名模糊查询")
    @PostMapping("/findConsumerList")
    public Result findConsumerList(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,
                                   @RequestBody ConsumerVO ConsumerVO) {
        PageVO<ConsumerVO> ConsumerVOPageVO = consumerService.findConsumerList(currentPage, pageSize, ConsumerVO);
        return Result.ok().data("consumerList",ConsumerVOPageVO.getRows()).data("total",ConsumerVOPageVO.getTotal());
    }
    /**
     * 添加去向
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资去向添加失败", operation = "物资去向添加")
    @RequiresPermissions({"Consumer:add"})
    @ApiOperation(value = "添加去向")
    @PostMapping("/addConsumer")
    public Result addConsumer(@RequestBody @Validated ConsumerVO ConsumerVO) {
        consumerService.addConsumer(ConsumerVO);
        return Result.ok();
    }

    /**
     * 编辑去向
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "编辑去向", notes = "编辑去向信息")
    @RequiresPermissions({"Consumer:edit"})
    @GetMapping("/edit/{id}")
    public Result edit(@PathVariable Long id) {
        ConsumerVO consumerVO = consumerService.edit(id);
        return Result.ok().data("consumer",consumerVO);
    }

    /**
     * 更新去向
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资去向更新失败", operation = "物资去向更新")
    @ApiOperation(value = "更新去向", notes = "更新去向信息")
    @RequiresPermissions({"Consumer:update"})
    @PutMapping("/updateConsumer")
    public Result updateConsumer( @RequestBody @Validated ConsumerVO ConsumerVO) {
        consumerService.updateConsumer(ConsumerVO);
        return Result.ok();
    }

    /**
     * 删除去向
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "物资去向删除失败", operation = "物资去向删除")
    @ApiOperation(value = "删除去向", notes = "删除去向信息")
    @RequiresPermissions({"Consumer:delete"})
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        consumerService.delete(id);
        return Result.ok();
    }

    /**
     * 所有去向
     *
     * @return
     */
    @ApiOperation(value = "所有去向", notes = "所有去向列表")
    @GetMapping("/findAll")
    public Result findAll() {
        List<ConsumerVO> ConsumerVOS = consumerService.findAll();
        return Result.ok().data("consumer",ConsumerVOS);
    }
}

