package com.lda.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author lda
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_department")
@Excel("department")
@ApiModel(value="Department对象", description="")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelField(value = "编号", width = 50)
    private Long id;

    @ApiModelProperty(value = "系名")
    @ExcelField(value = "部门名称", width = 100)
    private String name;

    @ApiModelProperty(value = "系办公电话")
    @ExcelField(value = "联系电话", width = 120)
    private String phone;

    @ApiModelProperty(value = "办公室地点")
    @ExcelField(value = "部门地址", width = 150)
    private String address;

    @ApiModelProperty(value = "创建时间")
    @ExcelField(value = "创建时间", dateFormat = "yyyy年MM月dd日 HH:mm:ss", width = 180)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @ExcelField(value = "修改时间", dateFormat = "yyyy年MM月dd日 HH:mm:ss", width = 180)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date modifiedTime;

    @ApiModelProperty(value = "系主任id，关联用户表")
    @ExcelField(value = "系主任id", width = 30)
    private Long mgrId;

    @ApiModelProperty(value = "分组查询的部门人数")
    @TableField(exist = false)
    private Integer total;

}
