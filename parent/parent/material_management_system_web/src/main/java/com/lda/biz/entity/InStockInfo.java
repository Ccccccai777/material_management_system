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
 * @since 2020-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_in_stock_info")
@ApiModel(value="InStockInfo对象", description="")
public class InStockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "入库单编号")
    private String inNum;

    @ApiModelProperty(value = "商品编号")
    private String pNum;

    @ApiModelProperty(value = "数量")
    private Integer productNumber;

    private Date createTime;

    private Date modifiedTime;


}
