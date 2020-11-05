package com.lda.biz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("biz_product_stock")
@ApiModel(value="ProductStock对象", description="")
public class ProductStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String pNum;

    @ApiModelProperty(value = "商品库存结余")
    private Long stock;


}
