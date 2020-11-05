package com.lda.biz.controller;


import com.lda.biz.entity.Supplier;
import com.lda.biz.entity.vo.*;
import com.lda.biz.mapper.SupplierMapper;
import com.lda.biz.service.InStockService;
import com.lda.biz.service.SupplierService;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
@Api(tags = "入库单入库接口")
@RestController
@RequestMapping("/inStock")
public class InStockController {
    @Autowired
    private InStockService inStockService;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private SupplierService supplierService;

    /**
     * 入库单列表
     *
     * @param pageNum
     * @param pageSize
     * @param inStockVO
     * @return
     */
    @ApiOperation(value = "入库单列表")
    @PostMapping("/findInStockList")
    public Result findInStockList(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestBody InStockVO inStockVO) {
        PageVO<InStockVO> pageVO = inStockService.findInStockList(currentPage, pageSize, inStockVO);
        return Result.ok().data("inStockList", pageVO.getRows()).data("total", pageVO.getTotal());
    }

    /*入库统计
     *
     *  @Date startTime
     * @Date endTime
     * */
    @ApiOperation(value = "入库统计")
    @GetMapping("/findInboundStatistics")
    public Result findInboundStatistics(
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
        StatisticsVO statisticsVO = inStockService.findInboundStatistics(startTime, endTime);
        return Result.ok().data("statisticsVO",statisticsVO);
    }

    /**
     * 删除入库单入库单
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "入库单删除失败", operation = "入库单删除")
    @RequiresPermissions({"inStock:delete"})
    @ApiOperation(value = "删除入库单")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        inStockService.delete(id);
        return Result.ok();
    }

    /**
     * 入库审核
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "入库单审核失败", operation = "入库单审核")
    @ApiOperation(value = "入库审核")
    @PutMapping("/publish/{id}")
    @RequiresPermissions({"inStock:publish"})
    public Result publish(@PathVariable Long id) {
        inStockService.publish(id);
        return Result.ok();
    }

    /**
     * 物资入库单详细
     *
     * @param id
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "查看入库单明细失败", operation = "查看入库单明细")
    @RequiresPermissions({"inStock:detail"})
    @ApiOperation(value = "入库单明细")
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id,
                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        InStockDetailVO detail = inStockService.detail(id, pageNum, pageSize);
        return Result.ok().data("detail", detail);
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
    @RequiresPermissions({"inStock:moveToTrash"})
    public Result moveToTrash(@PathVariable Long id) {
        inStockService.moveToTrash(id);
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
    @RequiresPermissions({"inStock:restore"})
    //这里改了权限
    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable Long id) {
        inStockService.restore(id);
        return Result.ok();
    }

    /**
     * 入库单入库
     *
     * @param inStockVO
     * @return
     */
    @ControllerEndpoint(exceptionMessage = "入库单申请失败", operation = "入库单申请")
    @PostMapping("/addIntoStock")
    @ApiOperation(value = "入库单入库")
    @RequiresPermissions("inStock:in")
    public Result addIntoStock(@RequestBody @Validated InStockVO inStockVO) {
        if (inStockVO.getSupplierId() == null) {
            if (StringUtils.isEmpty(inStockVO.getName())) {
                throw new BusinessException(456, "入库单提供方名不能为空");
            }
            if (StringUtils.isEmpty(inStockVO.getEmail())) {
                throw new BusinessException(456, "入库单提供方名不能为空");
            }
            if (StringUtils.isEmpty(inStockVO.getName())) {
                throw new BusinessException(456, "邮箱不能为空");
            }
            if (StringUtils.isEmpty(inStockVO.getContact())) {
                throw new BusinessException(456, "联系人不能为空");
            }
            if (StringUtils.isEmpty(inStockVO.getAddress())) {
                throw new BusinessException(456, "地址不能为空");
            }
            if (StringUtils.isEmpty(inStockVO.getSort())) {
                throw new BusinessException(456, "排序不能为空");
            }
            SupplierVO supplierVO = new SupplierVO();
            BeanUtils.copyProperties(inStockVO, supplierVO);
            Supplier supplier = supplierService.addSupplier(supplierVO);
            inStockVO.setSupplierId(supplier.getId());
        }
        inStockService.addIntoStock(inStockVO);
        return Result.ok();


    }


}

