package com.lda.system.mapper;

import com.lda.system.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-09-29
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectALL();

    Menu edit(Long id);
}
