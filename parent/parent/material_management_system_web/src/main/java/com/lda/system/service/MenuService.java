package com.lda.system.service;

import com.lda.system.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.MenuVO;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
public interface MenuService extends IService<Menu> {


    void updateMenu(MenuVO menuVO);

    List<MenuNodeVO> findMenuTree();

    List<Long> findOpenIds();

    void addMenu(MenuVO menuVo);

    Menu edit(Long id);
}
