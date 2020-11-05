package com.lda.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_role")
@Excel("role")
@ApiModel(value="Role对象", description="角色表")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelField(value = "编号", width = 50)
    private Long id;

    @ApiModelProperty(value = "角色名称")
    @ExcelField(value = "角色名称", width = 100)
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty(value = "角色描述")
    @ExcelField(value = "备注信息", width = 180)
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @ExcelField(value = "创建时间", dateFormat = "yyyy年MM月dd日 HH:mm:ss", width = 180)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @ExcelField(value = "修改时间", dateFormat = "yyyy年MM月dd日 HH:mm:ss", width = 180)
    private Date modifiedTime;

    @ApiModelProperty(value = "是否可用,0:不可用，1：可用")
    @ExcelField(value = "禁用状态", width = 50)
    private Integer status;


}
