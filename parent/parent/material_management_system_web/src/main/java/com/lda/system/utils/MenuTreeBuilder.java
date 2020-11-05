package com.lda.system.utils;

import com.lda.system.entity.vo.MenuNodeVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 该类用于递归构建树形菜单
 */
public class MenuTreeBuilder {

    public static List<MenuNodeVO> build(List<MenuNodeVO> menuNodeVOList) {
        //获得所有根节点
        List<MenuNodeVO> parentMenu = new ArrayList<>();
        for (MenuNodeVO menuNodeVo : menuNodeVOList) {
            if (menuNodeVo.getParentId() == 0) {
                parentMenu.add(menuNodeVo);
            }

        }
        /* 根据Menu类的order排序 */
        Collections.sort(parentMenu, MenuNodeVO.order());
        /*为根根节点设置子菜单，getChild是递归调用的*/
        for (MenuNodeVO parent : parentMenu) {
            List<MenuNodeVO> childMenu = getChild(parent.getId(), menuNodeVOList);
            parent.setChildren(childMenu);
        }


        return parentMenu;
    }

    private static List<MenuNodeVO> getChild(Long id, List<MenuNodeVO> menuNodeVOList) {

        /*获得父节点下的子菜单*/
        List<MenuNodeVO> childMenu = new ArrayList<>();
        for (MenuNodeVO menuNodeVo : menuNodeVOList) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if (menuNodeVo.getParentId().equals(id)) {
                childMenu.add(menuNodeVo);
            }
        }

        //递归
        for (MenuNodeVO nav : childMenu) {
            nav.setChildren(getChild(nav.getId(), menuNodeVOList));
        }
        Collections.sort(childMenu, MenuNodeVO.order());
        if (childMenu.size()==0) {
            return new ArrayList<MenuNodeVO>();
        }
        return childMenu;




    }
}
