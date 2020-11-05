package com.lda.biz.utils;

import com.lda.biz.entity.vo.ProductCategoryTreeNodeVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CategoryTreeBuilder {

    public  static List<ProductCategoryTreeNodeVO> builder( List<ProductCategoryTreeNodeVO> treeNodeVOList){
        List<ProductCategoryTreeNodeVO> rootProduct = new ArrayList<>();
        //拿到所有根节点
        if (!CollectionUtils.isEmpty(treeNodeVOList)) {
            for (ProductCategoryTreeNodeVO productCategoryTreeNodeVO : treeNodeVOList) {
                if (productCategoryTreeNodeVO.getPid()==0) {
                    productCategoryTreeNodeVO.setLev(1);
                    rootProduct.add(productCategoryTreeNodeVO);
                }
            }
        }
        /* 根据ProductCategoryTreeNodeVO类的order排序 */
        Collections.sort(rootProduct,ProductCategoryTreeNodeVO.order());
        if (!CollectionUtils.isEmpty(rootProduct)) {
            List<ProductCategoryTreeNodeVO> childProduct = new ArrayList<>();
            /* 获取根节点下的所有子节点 使用getChild方法*/
            for (ProductCategoryTreeNodeVO productCategoryTreeNodeVO : rootProduct) {
                productCategoryTreeNodeVO.setChildren(getChild(productCategoryTreeNodeVO,treeNodeVOList));
            }

        }
        return rootProduct;
    }

    //    只获取二级分类
    public  static List<ProductCategoryTreeNodeVO> buildTwoTree( List<ProductCategoryTreeNodeVO> treeNodeVOList){
        List<ProductCategoryTreeNodeVO> rootProduct = new ArrayList<>();
        //拿到所有根节点
        if (!CollectionUtils.isEmpty(treeNodeVOList)) {
            for (ProductCategoryTreeNodeVO productCategoryTreeNodeVO : treeNodeVOList) {
                if (productCategoryTreeNodeVO.getPid()==0) {
                    productCategoryTreeNodeVO.setLev(1);
                    rootProduct.add(productCategoryTreeNodeVO);
                }
            }
        }
        /* 根据ProductCategoryTreeNodeVO类的order排序 */
        Collections.sort(rootProduct,ProductCategoryTreeNodeVO.order());
        if (!CollectionUtils.isEmpty(rootProduct)) {
            List<ProductCategoryTreeNodeVO> childProduct = new ArrayList<>();
            /* 获取根节点下的所有子节点 使用getChild方法*/
            for (ProductCategoryTreeNodeVO productCategoryTreeNodeVO : rootProduct) {
                productCategoryTreeNodeVO.setChildren(getParentChild(productCategoryTreeNodeVO,treeNodeVOList));
            }

        }
        return rootProduct;
    }
    /**
     * 获取子菜单
     * @param productCategoryTreeNodeVO
     * @param treeNodeVOList
     * @return
     */
    private static List<ProductCategoryTreeNodeVO> getChild(ProductCategoryTreeNodeVO productCategoryTreeNodeVO, List<ProductCategoryTreeNodeVO> treeNodeVOList) {
     //子菜单
        List<ProductCategoryTreeNodeVO> childList = new ArrayList<>();
        for (ProductCategoryTreeNodeVO categoryTreeNodeVO : treeNodeVOList) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if(productCategoryTreeNodeVO.getId().equals(categoryTreeNodeVO.getPid())){
                categoryTreeNodeVO.setLev(productCategoryTreeNodeVO.getLev()+1);
                childList.add(categoryTreeNodeVO);
            }
        }
        //递归添加子菜单
        for (ProductCategoryTreeNodeVO categoryTreeNodeVO : childList) {
             categoryTreeNodeVO.setChildren(getChild(categoryTreeNodeVO,treeNodeVOList));
        }
       Collections.sort(childList,ProductCategoryTreeNodeVO.order());
        //如果节点下没有子节点，返回一个空List（递归退出）
        if(CollectionUtils.isEmpty(childList)){
            return  null;
        }
        return  childList;
    }
    /**
     * 获取子菜单
     * @param productCategoryTreeNodeVO
     * @param treeNodeVOList
     * @return
     */
    private static List<ProductCategoryTreeNodeVO> getParentChild(ProductCategoryTreeNodeVO productCategoryTreeNodeVO, List<ProductCategoryTreeNodeVO> treeNodeVOList) {
        //子菜单
        List<ProductCategoryTreeNodeVO> childList = new ArrayList<>();
        for (ProductCategoryTreeNodeVO categoryTreeNodeVO : treeNodeVOList) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if(productCategoryTreeNodeVO.getId().equals(categoryTreeNodeVO.getPid())){
                categoryTreeNodeVO.setLev(productCategoryTreeNodeVO.getLev()+1);
                childList.add(categoryTreeNodeVO);
            }
        }

        Collections.sort(childList,ProductCategoryTreeNodeVO.order());

        return  childList;
    }
}
