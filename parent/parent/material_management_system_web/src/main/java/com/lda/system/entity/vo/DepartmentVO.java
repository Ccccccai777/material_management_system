package com.lda.system.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class DepartmentVO {
    private Long id;

    @NotBlank(message = "院系名称不能为空")
    private String name;


}
