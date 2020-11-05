package com.lda.biz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2020-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_product_category")
@ApiModel(value="ProductCategory对象", description="")
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "类别名称")
    private String name;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    private Date createTime;

    private Date modifiedTime;

    @ApiModelProperty(value = "父级分类id")
    private Long pid;


}
