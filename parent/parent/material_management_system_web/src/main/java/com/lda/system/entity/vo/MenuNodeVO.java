package com.lda.system.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Data

public class MenuNodeVO implements Serializable {


    private Long id;


    private Long parentId;

    private String menuName;


    private String url;


    private String perms;


    private String icon;


    private String type;

    private Long orderNum;


    private boolean disabled;

    private Integer open;

    private List<MenuNodeVO> children = new ArrayList<>();

    public static Comparator<MenuNodeVO> order() {
        Comparator<MenuNodeVO> comparator = (o1, o2) -> {

            if (o1.getOrderNum() != o2.getOrderNum()) {
                return (int) (o1.getOrderNum() - o2.getOrderNum());
            }
            return 0;
        };
        return comparator;
    }

}


