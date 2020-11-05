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
 * @since 2020-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_out_stock_info")
@ApiModel(value="OutStockInfo对象", description="")
public class OutStockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String outNum;

    private String pNum;

    private Integer productNumber;

    private Date createTime;

    private Date modifiedTime;


}
