package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.InStockConverter;
import com.lda.biz.entity.*;
import com.lda.biz.entity.vo.*;
import com.lda.biz.mapper.*;
import com.lda.biz.service.InStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.bean.ActiveUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
@Service
public class InStockServiceImpl extends ServiceImpl<InStockMapper, InStock> implements InStockService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductStockMapper productStockMapper;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private InStockInfoMapper inStockInfoMapper;
    @Autowired
    private InStockConverter inStockConverter;

    @Transactional
    @Override
    public void addIntoStock(InStockVO inStockVO) {
        //随机生成入库单号
        String IN_STOCK_NUM = UUID.randomUUID().toString().substring(0, 32);
        int itemNumber = 0;//记录该单的总数
        List<Object> products = inStockVO.getProducts();
        if (!CollectionUtils.isEmpty(products)) {
            for (Object product : products) {
                LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) product;
                //该商品入库数量
                Integer productNumber = (Integer) item.get("productNumber");
                //商品编号
                Integer productId = (Integer) item.get("productId");
                Product dbProduct = productMapper.selectById(productId);
                if (dbProduct == null) {
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(), ResultCode.PRODUCT_NOT_EXIST.getMessage());

                } else if (dbProduct.getStatus() == 1) {
                    throw new BusinessException(ResultCode.PRODUCT_IS_REMOVE.getCode(), ResultCode.PRODUCT_IS_REMOVE.getMessage());
                } else if (dbProduct.getStatus() == 2) {
                    throw new BusinessException(ResultCode.PRODUCT_WAIT_PASS.getCode(), ResultCode.PRODUCT_WAIT_PASS.getMessage());
                } else if (productNumber <= 0) {
                    throw new BusinessException(ResultCode.PRODUCT_IN_STOCK_NUMBER_ERROR.getCode(), ResultCode.PRODUCT_IN_STOCK_NUMBER_ERROR.getMessage());
                } else {
                    itemNumber += productNumber;
                    InStockInfo inStockInfo = new InStockInfo();
                    inStockInfo.setCreateTime(new Date());
                    inStockInfo.setModifiedTime(new Date());
                    inStockInfo.setPNum(dbProduct.getPNum());
                    inStockInfo.setProductNumber(productNumber);
                    inStockInfo.setInNum(IN_STOCK_NUM);
                    inStockInfoMapper.insert(inStockInfo);
                }
                InStock inStock = new InStock();
                BeanUtils.copyProperties(inStockVO, inStock);
                inStock.setCreateTime(new Date());
                inStock.setModified(new Date());
                inStock.setProductNumber(itemNumber);
                inStock.setStatus(2);
                //添加入库单
                inStock.setInNum(IN_STOCK_NUM);
                ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
                inStock.setOperator(activeUser.getUser().getUsername());
                this.baseMapper.insert(inStock);
            }
        } else {
            throw new BusinessException(ResultCode.PRODUCT_IN_STOCK_NUMBER_EMPTY.getCode(), ResultCode.PRODUCT_IN_STOCK_NUMBER_EMPTY.getMessage());

        }
    }

    @Override
    public PageVO<InStockVO> findInStockList(Integer pageNum, Integer pageSize, InStockVO inStockVO) {
        Page<InStock> page = new Page<>(pageNum, pageSize);
        QueryWrapper<InStock> wrapper = getWrapper(inStockVO);
        IPage<InStock> iPage = this.baseMapper.selectPage(page, wrapper);
        List<InStock> inStockList = iPage.getRecords();
        List<InStockVO> inStockVOS = inStockConverter.converterToVOList(inStockList);
        PageVO<InStockVO> inStockPageVO = new PageVO<>(iPage.getTotal(), inStockVOS);
        return inStockPageVO;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (inStock == null) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());

        }
        if (inStock.getStatus() != 1 && inStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getCode(), ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getMessage());
        }
        this.baseMapper.deleteById(id);
        inStockInfoMapper.delete(new QueryWrapper<InStockInfo>().eq("in_num", inStock.getInNum()));

    }

    @Transactional
    @Override
    public void publish(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (inStock == null) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (inStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getCode(), ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getMessage());
        }
        Long supplierId = inStock.getSupplierId();
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.MATERIAL_SOURCE_NOT_EXIST.getCode(), ResultCode.MATERIAL_SOURCE_NOT_EXIST.getMessage());
        }
        String inNum = inStock.getInNum();//单号
        List<InStockInfo> inStockInfoList = inStockInfoMapper.selectList(new QueryWrapper<InStockInfo>().eq("in_num", inNum));
        if (!CollectionUtils.isEmpty(inStockInfoList)) {
            for (InStockInfo inStockInfo : inStockInfoList) {
                String pNum1 = inStockInfo.getPNum();
                //根据物资入库信息中查找物资
                List<Product> productList = productMapper.selectList(new QueryWrapper<Product>().eq("p_num", inStockInfo.getPNum()));
                if (!CollectionUtils.isEmpty(productList)) {
                    Product product = productList.get(0);
                    String pNum = product.getPNum();
                    List<ProductStock> productStockList = productStockMapper.selectList(new QueryWrapper<ProductStock>().eq("p_num", pNum));
                    Integer productNumber = inStockInfo.getProductNumber();

                    if (productStockList.size() > 0) {
                        ProductStock productStock = productStockList.get(0);
                        productStock.setStock(productStock.getStock() + (long) productNumber);
                        productStockMapper.updateById(productStock);
                    } else {
                        //没有就添加
                        ProductStock productStock = new ProductStock();
                        productStock.setStock((long) productNumber);
                        productStock.setPNum(product.getPNum());
                        productStockMapper.insert(productStock);
                    }
                    inStock.setStatus(0);
                    inStock.setModified(new Date());
                    this.baseMapper.updateById(inStock);
                } else {
                    throw new BusinessException(00000, "物资编号" + pNum1 + "不存在");

                }
            }
        } else {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), "该入库单信息不存在");

        }


    }

    @Transactional
    @Override
    public void moveToTrash(Long id) {
        InStock dbInStock = this.baseMapper.selectById(id);
        if (dbInStock == null) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (dbInStock.getStatus() != 0) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getCode(), ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getMessage());
        }
        InStock inStock = new InStock();
        inStock.setStatus(1);
        inStock.setId(id);
        this.baseMapper.updateById(inStock);
    }

    @Transactional
    @Override
    public void restore(Long id) {
        InStock dbInStock = this.baseMapper.selectById(id);
        if (dbInStock == null) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (dbInStock.getStatus() != 1) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getCode(), ResultCode.WAREHOUSERECEIPT_STATUS_ERROR.getMessage());
        }
        InStock inStock = new InStock();
        inStock.setStatus(0);
        inStock.setId(id);
        this.baseMapper.updateById(inStock);
    }

    @Override
    public InStockDetailVO detail(Long id, Integer pageNum, Integer pageSize) {
        InStock inStock = this.baseMapper.selectById(id);
        if (inStock == null) {
            throw new BusinessException(ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());

        }
        InStockDetailVO inStockDetailVO = new InStockDetailVO();
        BeanUtils.copyProperties(inStock, inStockDetailVO);
        Supplier supplier = supplierMapper.selectById(inStock.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(ResultCode.MATERIAL_SOURCE_NOT_EXIST.getCode(), ResultCode.MATERIAL_SOURCE_NOT_EXIST.getMessage());

        }
        SupplierVO supplierVO = new SupplierVO();
        BeanUtils.copyProperties(supplier, supplierVO);
        inStockDetailVO.setSupplierVO(supplierVO);
        String inNum = inStock.getInNum();
        //分页 根据入库单编号查找
        IPage<InStockInfo> page = inStockInfoMapper.selectPage(new Page<InStockInfo>(pageNum, pageSize), new QueryWrapper<InStockInfo>().eq("in_num", inNum));
        List<InStockInfo> inStockInfoList = page.getRecords();
        inStockDetailVO.setTotal(page.getTotal());
        if (!CollectionUtils.isEmpty(inStockInfoList)) {
            for (InStockInfo inStockInfo : inStockInfoList) {
                //查找每个物资
                String pNum = inStockInfo.getPNum();
                List<Product> productList = productMapper.selectList(new QueryWrapper<Product>().eq("p_num", pNum));
                if (!CollectionUtils.isEmpty(productList)) {
                    Product product = productList.get(0);
                    InStockItemVO inStockItemVO = new InStockItemVO();
                    BeanUtils.copyProperties(product, inStockItemVO);
                    //设置该物资的数量
                    inStockItemVO.setCount(inStockInfo.getProductNumber());
                    //添加到inStockItemVO列表中
                    inStockDetailVO.getItemVOS().add(inStockItemVO);

                } else {
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(), ResultCode.PRODUCT_NOT_EXIST.getMessage());

                }
            }
        } else {
            throw new BusinessException(ResultCode.MATERIAL_SOURCE_NOT_EXIST.getCode(), ResultCode.MATERIAL_SOURCE_NOT_EXIST.getMessage());

        }
        return inStockDetailVO;
    }

    @Override
    public StatisticsVO findInboundStatistics(Date startTime, Date endTime) {
        Page<InStock> page = new Page<>(1, 36);

        QueryWrapper<InStockInfo> wrapper = getWrapper(startTime, endTime, 0);
        StatisticsVO statisticsVO = new StatisticsVO();
        //日期和数量 柱型图
        IPage<LinkedHashMap<String, Object>> inStockInfo = this.baseMapper.findStatisticsOfWarehousingInformation(page, wrapper);
        List<LinkedHashMap<String, Object>> StatisticsOfWarehousingInformationList = inStockInfo.getRecords();
        //一个月的时候转化
        int start4 = 0;
        int start3 = 0;
        int start2 = 0;
        int start1 = 0;
        LinkedHashMap<String, Object> four = new LinkedHashMap<>();
        LinkedHashMap<String, Object> three = new LinkedHashMap<>();
        LinkedHashMap<String, Object> two = new LinkedHashMap<>();
        LinkedHashMap<String, Object> one = new LinkedHashMap<>();
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Long time = endTime.getTime();
            Long time1 = startTime.getTime();
            Long l = time - time1;
            //等于30天或31天
            if (l.equals(2592000000l) || l.equals(2678400000l)) {
                for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : StatisticsOfWarehousingInformationList) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        Date inStockInfoDate = simpleDateFormat.parse((String) stringObjectLinkedHashMap.get("inStockInfoDate"));
                        //倒数第一周  小于一周 把这一周的集合成一段时间
                        if ((endTime.getTime() - inStockInfoDate.getTime()) < 604800000) {
                            start4++;
                            if (start4 == 1) {
                                //只有第一次对inStockInfoDate进行赋值
                                four.put("inStockInfoDate", simpleDateFormat.format(new Date(endTime.getTime() - 604800000 + 86400000)) + "至" + simpleDateFormat.format(endTime));
                                four.put("inStockInfoDateTotal", 0);
                            }
                            //把这一周的入库数量累加
                            Integer b = (Integer) four.get("inStockInfoDateTotal");
                            BigDecimal b2 = (BigDecimal) stringObjectLinkedHashMap.get("inStockInfoDateTotal");
                            four.put("inStockInfoDateTotal", b + b2.intValue());
                        }//倒数第二周
                        else if ((endTime.getTime() - 604800000 - inStockInfoDate.getTime()) < 604800000) {

                            start3++;
                            if (start3 == 1) {
                                //只有第一次对inStockInfoDate进行赋值
                                three.put("inStockInfoDate", simpleDateFormat.format(new Date(endTime.getTime() - 1209600000 + 86400000)) + "至" + simpleDateFormat.format(new Date(endTime.getTime() - 604800000)));
                                three.put("inStockInfoDateTotal", 0);
                            }
                            //把这一周的入库数量累加
                            Integer b = (Integer) three.get("inStockInfoDateTotal");
                            BigDecimal b2 = (BigDecimal) stringObjectLinkedHashMap.get("inStockInfoDateTotal");
                            three.put("inStockInfoDateTotal", b + b2.intValue());
                        }//倒数第三周
                        else if ((endTime.getTime() - 1209600000l - inStockInfoDate.getTime()) < 604800000) {

                            start2++;
                            if (start2 == 1) {
                                //只有第一次对inStockInfoDate进行赋值
                                two.put("inStockInfoDate", simpleDateFormat.format(new Date(endTime.getTime() - 1814400000l + 86400000)) + "至" + simpleDateFormat.format(new Date(endTime.getTime() - 1209600000l)));
                                two.put("inStockInfoDateTotal", 0);
                            }
                            //把这一周的入库数量累加
                            Integer b = (Integer) two.get("inStockInfoDateTotal");
                            BigDecimal b2 = (BigDecimal) stringObjectLinkedHashMap.get("inStockInfoDateTotal");
                            two.put("inStockInfoDateTotal", b + b2.intValue());
                        }
                        //倒数第四周
                        else /*if((endTime.getTime()-1814400000l-inStockInfoDate.getTime())<604800000)*/ {

                            start1++;
                            if (start1 == 1) {
                                //只有第一次对inStockInfoDate进行赋值
                                one.put("inStockInfoDate", simpleDateFormat.format(new Date(endTime.getTime() - 2592000000l)) + "至" + simpleDateFormat.format(new Date(endTime.getTime() - 1814400000l)));
                                one.put("inStockInfoDateTotal", 0);
                            }
                            //把这一周的入库数量累加
                            Integer b = (Integer) one.get("inStockInfoDateTotal");
                            BigDecimal b2 = (BigDecimal) stringObjectLinkedHashMap.get("inStockInfoDateTotal");
                            one.put("inStockInfoDateTotal", b + b2.intValue());

                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                StatisticsOfWarehousingInformationList.clear();
                StatisticsOfWarehousingInformationList.add(one);
                StatisticsOfWarehousingInformationList.add(two);
                StatisticsOfWarehousingInformationList.add(three);
                StatisticsOfWarehousingInformationList.add(four);
            }

        }
        System.out.println("================" + four.get("inStockInfoDate"));
        System.out.println("================" + four.get("inStockInfoDateTotal"));
        System.out.println("================" + three.get("inStockInfoDate"));
        System.out.println("================" + three.get("inStockInfoDateTotal"));
        System.out.println("================" + two.get("inStockInfoDate"));
        System.out.println("================" + two.get("inStockInfoDateTotal"));
        System.out.println("================" + one.get("inStockInfoDate"));
        System.out.println("================" + one.get("inStockInfoDateTotal"));

        statisticsVO.setInStockInfoList(StatisticsOfWarehousingInformationList);
        //物资名称和数量 饼形图
        QueryWrapper<InStockInfo> wrapper1 = getWrapper(startTime, endTime, 1);
        IPage<LinkedHashMap<String, Object>> inStockInfoProduct = this.baseMapper.findAllStoredMaterials(page, wrapper1);
        List<LinkedHashMap<String, Object>> AllStoredMaterials = inStockInfoProduct.getRecords();
        statisticsVO.setInStockProductInfoList(AllStoredMaterials);
        //地区和贡献物资数量 饼形图
        IPage<LinkedHashMap<String, Object>> inStockAddress = this.baseMapper.findRegionalContributionMaterials(page, wrapper1);
        List<LinkedHashMap<String, Object>> RegionalContributionMaterials = inStockAddress.getRecords();
        statisticsVO.setInStockInfoAddressList(RegionalContributionMaterials);


        return statisticsVO;
    }

    private QueryWrapper<InStockInfo> getWrapper(Date startTime, Date endTime, Integer type) {
        QueryWrapper<InStockInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(startTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String format = simpleDateFormat.format(startTime);
            if (type == 1) {
                wrapper.ge("bis.create_time", format);
            } else {
                wrapper.ge("create_time", format);
            }

        }
        if (!StringUtils.isEmpty(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String format = simpleDateFormat.format(endTime);
            if (type == 1) {
                wrapper.le("bis.create_time", format);
            } else {
                wrapper.le("create_time", format);
            }
        }

        return wrapper;
    }

    private QueryWrapper<InStock> getWrapper(InStockVO inStockVO) {

        QueryWrapper<InStock> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if (!StringUtils.isEmpty(inStockVO.getType())) {
            wrapper.eq("type", inStockVO.getType());
        }
        if (!StringUtils.isEmpty(inStockVO.getStatus())) {
            wrapper.eq("status", inStockVO.getStatus());
        }
        if (!StringUtils.isEmpty(inStockVO.getInNum())) {
            wrapper.like("in_num", inStockVO.getInNum());
        }
        if (!StringUtils.isEmpty(inStockVO.getStartTime())) {
            Date startTime = inStockVO.getStartTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String format = simpleDateFormat.format(startTime);
            wrapper.ge("create_time", format);
        }
        if (!StringUtils.isEmpty(inStockVO.getEndTime())) {
            Date endTime = inStockVO.getEndTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String format = simpleDateFormat.format(endTime);

            wrapper.le("create_time", format);
        }

        return wrapper;
    }
}
