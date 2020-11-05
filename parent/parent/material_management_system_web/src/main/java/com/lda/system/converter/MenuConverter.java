package com.lda.system.converter;

import com.lda.system.entity.Menu;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.MenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuConverter {
    /**
     * MenuNodeVO(菜单和按钮）
     * @param menuList
     * @return
     */
    public static List<MenuNodeVO> converterToALLMenuNodeVO(List<Menu> menuList) {
        List<MenuNodeVO> menuNodeVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menuList)) {
            for (Menu menu : menuList) {
                MenuNodeVO menuNodeVo = new MenuNodeVO();
                BeanUtils.copyProperties(menu, menuNodeVo);
                menuNodeVo.setDisabled(menu.getAvailable() == 0);
                menuNodeVOList.add(menuNodeVo);
            }
        }

        return menuNodeVOList;
    }
    /**
     * 转成menuVO(菜单和按钮）
     * @param menu
     * @return
     */

    public static MenuVO converterToMenuVO(Menu menu){
        MenuVO menuVO = new MenuVO();
        if(menu!=null){
            BeanUtils.copyProperties(menu,menuVO);
            menuVO.setDisabled(menu.getAvailable()==0);
        }
        return menuVO;
    }


    /**
     * 转成menuVO(只包含菜单)List
     * @param menus
     * @return
     */
    public static List<MenuNodeVO> converterToMenuNodeVO(List<Menu> menuList) {
        List<MenuNodeVO> menuNodeVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menuList)) {
            for (Menu menu : menuList) {
                if(menu.getType().equals("0")){
                    MenuNodeVO menuNodeVo = new MenuNodeVO();
                    BeanUtils.copyProperties(menu, menuNodeVo);
                    menuNodeVo.setDisabled(menu.getAvailable() == 0);
                    menuNodeVOList.add(menuNodeVo);
                }

            }
        }

        return menuNodeVOList;
    }
}
