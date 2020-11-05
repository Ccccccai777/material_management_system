package com.lda.biz.service;

import com.lda.biz.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.SupplierVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
public interface SupplierService extends IService<Supplier> {

    PageVO<SupplierVO> findSupplierList(Integer pageNum, Integer pageSize, SupplierVO supplierVO);

    Supplier addSupplier(SupplierVO supplierVO);

    SupplierVO edit(Long id);

    void updateSupplier(SupplierVO supplierVO);

    void delete(Long id);

    List<SupplierVO> findAll();
}
