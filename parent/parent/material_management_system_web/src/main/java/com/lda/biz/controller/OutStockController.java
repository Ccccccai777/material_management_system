package com.lda.biz.controller;


import com.lda.biz.entity.Consumer;
import com.lda.biz.entity.vo.*;

import com.lda.biz.service.ConsumerService;
import com.lda.biz.service.OutStockService;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.handler.BusinessException;
import com.lda.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
@Api(tags = "物资出库接口")
@RestController
@RequestMapping("/outStock")
public class OutStockController {
    @Autowired
    private OutStockService outStockService;
    @Autowired
    private ConsumerService consumerService;


    @ApiOperation(value = "出库单列表")
    @PostMapping("/findOutStockList")
    public Result findOutStockList(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestBody OutStockVO outStockVO) {
        PageVO<OutStockVO> pageVO = outStockService.findOutStockList(currentPage, pageSize, outStockVO);
        return Result.ok().data("outStockList", pageVO.getRows()).data("total", pageVO.getTotal());
    }

    /**
     * 提交物资发放单
     *
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "发放单申请失败", operation = "发放单申请")
    @ApiOperation("提交发放单")
    @PostMapping("/addOutStock")
    @RequiresPermissions({"outStock:out"})
    public Result addOutStock(@RequestBody @Validated OutStockVO outStockVO) {
        if (outStockVO.getConsumerId() == null) {
            //说明现在添加物资来源
            ConsumerVO consumerVO = new ConsumerVO();
            BeanUtils.copyProperties(outStockVO, consumerVO);
            if (StringUtils.isEmpty(outStockVO.getName())) {
                throw new BusinessException(456, "出库库单提供方名不能为空");
            }
            if (StringUtils.isEmpty(outStockVO.getName())) {
                throw new BusinessException(456, "邮箱不能为空");
            }
            if (StringUtils.isEmpty(outStockVO.getContact())) {
                throw new BusinessException(456, "联系人不能为空");
            }
            if (StringUtils.isEmpty(outStockVO.getAddress())) {
                throw new BusinessException(456, "地址不能为空");
            }
            if (StringUtils.isEmpty(outStockVO.getSort())) {
                throw new BusinessException(456, "排序不能为空");
            }
            Consumer consumer = consumerService.addConsumer(consumerVO);
            outStockVO.setConsumerId(consumer.getId());
        }
        //提交发放单
        outStockService.addOutStock(outStockVO);
        return Result.ok();
    }

    /**
     * 物资出库单详细
     *
     * @param id
     * @return
     */
    @RequiresPermissions({"outStock:detail"})
    @ApiOperation(value = "入库单明细")
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id,
                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        OutStockDetailVO detail = outStockService.detail(id, pageNum, pageSize);
        return Result.ok().data("detail", detail);
    }

    @ControllerEndpoint(operation = "出库单删除", exceptionMessage = "出库单删除失败")
    @DeleteMapping("/delete/{id}")
    @RequiresPermissions({"outStock:delete"})
    @ApiOperation(value = "删除物资发放单")
    public Result delete(@PathVariable Long id) {
        outStockService.delete(id);
        return Result.ok();
    }

    @ControllerEndpoint(operation = "物资确认到达", exceptionMessage = "物资确认到达失败")
    @PutMapping("/confirmArrival/{id}")
    @ApiOperation(value = "物资确认到达")
    public Result confirmArrival(@PathVariable Long id ,@Validated @RequestParam(value = "address") String address) {
        outStockService.confirmArrival(id,address);
        return Result.ok();
    }

    /*入库统计
     *
     *  @Date startTime
     * @Date endTime
     * */
    @ApiOperation(value = "入库统计")
    @GetMapping("/findOutboundStatistics")
    public Result findOutboundStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")   Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    Date endTime) {
        if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
            Date date=new Date();
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -14);
            startTime =calendar.getTime();
            endTime=new Date();

        }
        StatisticsVO statisticsVO = outStockService.findInboundStatistics(startTime, endTime);
        return Result.ok().data("statisticsVO",statisticsVO);
    }


    @ControllerEndpoint(operation = "修改物资当前位置", exceptionMessage = "修改物资当前位置失败")
    @PutMapping("/updateCurrentLocation/{id}")
    @RequiresPermissions({"outStock:updateCurrentLocation"})
    @ApiOperation(value = "修改物资当前位置")
    public Result updateCurrentLocation(@PathVariable("id") Long id,@Validated @RequestParam(value = "address") String address) {
        outStockService.updateCurrentLocation(id,address);
        return Result.ok();
    }
    /**
     * 发放审核
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "发放单审核失败", operation = "发放单审核")
    @ApiOperation(value = "入库审核")
    @PutMapping("/publish/{id}")
    @RequiresPermissions({"outStock:publish"})
    public Result publish(@PathVariable Long id) {
        outStockService.publish(id);
        return Result.ok();
    }
    /**
     * 移入回收站
     *
     * @param id
     * @return
     */

    @ControllerEndpoint(exceptionMessage = "入库单回收失败", operation = "入库单回收")
    @ApiOperation(value = "移入回收站", notes = "移入回收站")
    @PutMapping("/moveToTrash/{id}")
    @RequiresPermissions({"outStock:moveToTrash"})
    public Result moveToTrash(@PathVariable Long id) {
        outStockService.moveToTrash(id);
        return Result.ok();
    }

    /**
     * 恢复数据从回收站
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "恢复入库单失败", operation = "入库单资料恢复")
    @ApiOperation(value = "恢复入库单", notes = "从回收站中恢复入库单")
    @RequiresPermissions({"outStock:restore"})
    //这里改了权限
    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable Long id) {
        outStockService.restore(id);
        return Result.ok();
    }
}

