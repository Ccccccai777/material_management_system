package com.lda.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Data

public class MenuVO implements Serializable {





    private Long id;

    @NotNull(message = "父级ID必须")
    private Long parentId;


    @NotBlank(message = "菜单名称不能为空")
    private String menuName;


    private String url;


    private String perms;


    private String icon;

    @NotNull(message = "菜单类型不为空")
    private String type;

    @NotNull(message = "排序数不能为空")
    private Long orderNum;


    private Date createTime;

    private Date modifiedTime;

    @NotNull(message = "菜单状态不能为空")
    private boolean disabled;

    @NotNull(message = "是否展开不能为空")
    private Integer open;


}
