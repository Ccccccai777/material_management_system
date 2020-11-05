package com.lda.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户角色关联表
 * </p>
 *
 * @author lda
 * @since 2020-10-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user_role")
@ApiModel(value="UserRole对象", description="用户角色关联表")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;


}
