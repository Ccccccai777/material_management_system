package com.lda.system.entity;

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
 * 登录日志表
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_login_log")
@ApiModel(value="LoginLog对象", description="登录日志表")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    @ApiModelProperty(value = "登录地点")
    private String location;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "操作系统")
    private String userSystem;

    @ApiModelProperty(value = "浏览器")
    private String userBrowser;


}
