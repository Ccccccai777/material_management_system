package com.lda.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
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
 * @since 2020-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_image")
@ApiModel(value="Image对象", description="")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "图片路径")
    private String path;

    @ApiModelProperty(value = "图片大小")
    private Long size;

    @ApiModelProperty(value = "图片类型")
    private String mediaType;

    @ApiModelProperty(value = "图片后缀")
    private String suffix;

    @ApiModelProperty(value = "图片高度")
    private Integer height;

    @ApiModelProperty(value = "图片宽度")
    private Integer width;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
