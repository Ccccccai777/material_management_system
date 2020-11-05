package com.lda.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.converter.MenuConverter;
import com.lda.system.entity.Menu;
import com.lda.system.entity.Role;
import com.lda.system.entity.vo.MenuNodeVO;
import com.lda.system.entity.vo.MenuVO;
import com.lda.system.mapper.MenuMapper;
import com.lda.system.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.system.utils.MenuTreeBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Transactional
    @Override
    public void updateMenu(MenuVO menuVO) {
        String menuName = menuVO.getMenuName();
        Long menuVOId = menuVO.getId();

        QueryWrapper<Menu> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",menuVOId);
        Menu dbMenu = this.baseMapper.selectById(menuVOId);
        //查角色名重复没
        QueryWrapper<Menu> qw1 = new QueryWrapper<>();
        qw1.eq("menu_name",menuName);
        List<Menu> menuList = this.baseMapper.selectList(qw1);
        if( dbMenu!=null && menuVO.getMenuName().equals(dbMenu.getMenuName())|| CollectionUtils.isEmpty(menuList)){
            qw.eq("id",menuVOId);
            Menu menu = new Menu();
            BeanUtils.copyProperties(menuVO,menu);
            menu.setModifiedTime(new Date());
            menu.setAvailable(menuVO.isDisabled()?0:1);
            this.baseMapper.update(menu,qw);
        }
        else if (!CollectionUtils.isEmpty(menuList)) {
            throw  new BusinessException(ResultCode.MENU_ALREADY_EXIST.getCode(),ResultCode.MENU_ALREADY_EXIST.getMessage());
        }
    }

    /**
     * 加载菜单树（按钮和菜单）
     *
     * @return
     */
    @Override
    public List<MenuNodeVO> findMenuTree() {

         List<Menu> menuList= menuMapper.selectALL();
        List<MenuNodeVO> menuNodeVOList = MenuConverter.converterToALLMenuNodeVO(menuList);
        return MenuTreeBuilder.build(menuNodeVOList);
    }

    /**
     * 获取展开项
     *
     * @return
     */
    @Override
    public List<Long> findOpenIds() {
        List<Menu> menuList= menuMapper.selectALL();
        List<Long> ids = new ArrayList<>();
        for (Menu menu : menuList) {
            if (menu.getOpen()==1) {
                ids.add(menu.getId());
            }
        }
        return ids;
    }
    @Transactional
    @Override
    public void addMenu(MenuVO menuVo) {
        String menuName = menuVo.getMenuName();
        Long menuVoId = menuVo.getId();

        QueryWrapper<Menu> qw = new QueryWrapper<>();
        qw.eq("menu_name",menuName);
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.MENU_ALREADY_EXIST.getCode(),ResultCode.MENU_ALREADY_EXIST.getMessage());
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuVo,menu);
        menu.setCreateTime(new Date());
        menu.setModifiedTime(new Date());
        menu.setAvailable(menuVo.isDisabled()?0:1);
        menuMapper.insert(menu);
    }

    @Override
    public Menu edit(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu==null) {
            throw new BusinessException(ResultCode.MENU_NOT_EXIST.getCode(),ResultCode.MENU_NOT_EXIST.getMessage());
        }
        return menu;
    }
}
