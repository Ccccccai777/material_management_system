package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.ConsumerConverter;
import com.lda.biz.converter.OutStockConverter;
import com.lda.biz.entity.*;
import com.lda.biz.entity.vo.*;
import com.lda.biz.mapper.*;
import com.lda.biz.service.OutStockService;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
@Service
public class OutStockServiceImpl extends ServiceImpl<OutStockMapper, OutStock> implements OutStockService {

    @Autowired
    private OutStockConverter outStockConverter;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OutStockInfoMapper outStockInfoMapper;
    @Autowired
    private ConsumerMapper consumerMapper;
    @Autowired
    private ProductStockMapper productStockMapper;

    @Override
    public PageVO<OutStockVO> findOutStockList(Integer currentPage, Integer pageSize, OutStockVO outStockVO) {
        Page<OutStock> outStockPage = new Page<>(currentPage, pageSize);
        QueryWrapper<OutStock> wrapper = getWrapper(outStockVO);
        IPage<OutStock> page = this.baseMapper.selectPage(outStockPage, wrapper);
        List<OutStock> outStockList = page.getRecords();
        List<OutStockVO> outStockVOList = outStockConverter.converterToVOList(outStockList);
        return new PageVO<OutStockVO>(page.getTotal(), outStockVOList);
    }
    @Transactional
    @Override
    public void addOutStock(OutStockVO outStockVO) {
        String outNum = UUID.randomUUID().toString().substring(0, 32);
        Integer itemNum = 0;//记录该单的总数
        List<Object> products = outStockVO.getProducts();
        if (!CollectionUtils.isEmpty(products)) {
            for (Object product : products) {
                LinkedHashMap p = (LinkedHashMap) product;
                //发放数量
                Integer productNumber = (Integer) p.get("productNumber");
                //物资编号
                Integer productId = (Integer) p.get("productId");
                Product dbProduct = productMapper.selectById(productId);
                if (dbProduct == null) {
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(), ResultCode.PRODUCT_NOT_EXIST.getMessage());
                } else if (dbProduct.getStatus() == 1) {
                    throw new BusinessException(ResultCode.PRODUCT_IS_REMOVE.getCode(), ResultCode.PRODUCT_IS_REMOVE.getMessage());
                } else if (dbProduct.getStatus() == 2) {
                    throw new BusinessException(ResultCode.PRODUCT_WAIT_PASS.getCode(), ResultCode.PRODUCT_WAIT_PASS.getMessage());
                } else if (productNumber <= 0) {
                    throw new BusinessException(ResultCode.PRODUCT_OUT_STOCK_NUMBER_ERROR.getCode(), ResultCode.PRODUCT_OUT_STOCK_NUMBER_ERROR.getMessage());
                } else {
                    OutStockInfo outStockInfo = new OutStockInfo();
                    outStockInfo.setCreateTime(new Date());
                    outStockInfo.setModifiedTime(new Date());
                    outStockInfo.setOutNum(outNum);
                    outStockInfo.setPNum(dbProduct.getPNum());
                    outStockInfo.setProductNumber(productNumber);
                    outStockInfoMapper.insert(outStockInfo);
                }
            }
            OutStock outStock = new OutStock();
            BeanUtils.copyProperties(outStockVO, outStock);
            outStock.setCreateTime(new Date());
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            outStock.setOperator(activeUser.getUser().getUsername());
            outStock.setOutNum(outNum);
            outStock.setProductNumber(itemNum);
            //设置为待审核
            outStock.setStatus(2);
            this.baseMapper.insert(outStock);
        } else {
            throw new BusinessException(ResultCode.PRODUCT_IN_STOCK_NUMBER_EMPTY.getCode(), ResultCode.PRODUCT_IN_STOCK_NUMBER_EMPTY.getMessage());

        }

    }
    @Transactional
    @Override
    public void restore(Long id) {
        OutStock dbOutStock = this.baseMapper.selectById(id);
        if (dbOutStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (dbOutStock.getStatus() != 1) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getCode(), ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getMessage());
        }
        OutStock outStock = new OutStock();
        outStock.setStatus(0);
        outStock.setId(id);
        this.baseMapper.updateById(outStock);
    }
    @Transactional
    @Override
    public void moveToTrash(Long id) {
        OutStock dbOutStock = this.baseMapper.selectById(id);
        if (dbOutStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (dbOutStock.getStatus() != 0) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getCode(), ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getMessage());
        }
        OutStock outStock = new OutStock();
        outStock.setStatus(1);
        outStock.setId(id);
        this.baseMapper.updateById(outStock);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        OutStock dbOutStock = this.baseMapper.selectById(id);
        if (dbOutStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.WAREHOUSERECEIPT_NOT_EXIST.getMessage());
        }
        if (dbOutStock.getStatus() != 1 && dbOutStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getCode(), ResultCode.OUTBOUND_ORDER_STATUS_ERROR.getMessage());
        }
        this.baseMapper.deleteById(id);
        String outNum = dbOutStock.getOutNum();
        QueryWrapper<OutStockInfo> wrapper = new QueryWrapper<OutStockInfo>().eq("out_num", outNum);
        OutStockInfo outStockInfo = outStockInfoMapper.selectOne(wrapper);
        if (outStockInfo == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_INFORMATION_NOT_EXIST.getCode(), ResultCode.OUTBOUND_ORDER_INFORMATION_NOT_EXIST.getMessage());
        }
        outStockInfoMapper.delete(wrapper);
    }

    @Override
    public OutStockDetailVO detail(Long id, Integer pageNum, Integer pageSize) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (outStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.OUTBOUND_ORDER_NOT_EXIST.getMessage());
        }
        OutStockDetailVO outStockDetailVO = new OutStockDetailVO();
        BeanUtils.copyProperties(outStock, outStockDetailVO);
        Long consumerId = outStock.getConsumerId();
        Consumer consumer = consumerMapper.selectById(consumerId);
        if (consumer == null) {
            throw new BusinessException(ResultCode.WHERE_TO_GO_NOT_EXIST.getCode(), ResultCode.WHERE_TO_GO_NOT_EXIST.getMessage());
        }
        outStockDetailVO.setConsumerVO(ConsumerConverter.converterToConsumerVO(consumer));
        //查询该单所有的物资
        String outNum = outStock.getOutNum();
        Page<OutStockInfo> page = new Page<>(pageNum, pageSize);
        IPage<OutStockInfo> iPage = outStockInfoMapper.selectPage(page, new QueryWrapper<OutStockInfo>().eq("out_num", outNum));
        List<OutStockInfo> outStockInfoList = iPage.getRecords();
        if (!CollectionUtils.isEmpty(outStockInfoList)) {
            //设置列表数量
            outStockDetailVO.setTotal(iPage.getTotal());
            for (OutStockInfo outStockInfo : outStockInfoList) {
                String pNum = outStockInfo.getPNum();
                Product product = productMapper.selectOne(new QueryWrapper<Product>().eq("p_num", pNum));
                if (product == null) {
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(), ResultCode.PRODUCT_NOT_EXIST.getMessage());
                }
                //把物资和物资数量存入itemVOS集合中
                OutStockItemVO outStockItemVO = new OutStockItemVO();
                BeanUtils.copyProperties(product, outStockItemVO);
                outStockItemVO.setCount(outStockInfo.getProductNumber());
                outStockDetailVO.getItemVOS().add(outStockItemVO);
            }
        } else {
            throw new BusinessException(66666, "出库记录不存在！！");

        }
        return outStockDetailVO;
    }
    @Transactional
    @Override
    public void publish(Long id) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (outStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.OUTBOUND_ORDER_NOT_EXIST.getMessage());
        }
        if (outStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.WHERE_TO_GO_STATUS_ERROR.getCode(), ResultCode.WHERE_TO_GO_STATUS_ERROR.getMessage());

        }
        Consumer consumer = consumerMapper.selectById(outStock.getConsumerId());
        if (consumer == null) {
            throw new BusinessException(ResultCode.WHERE_TO_GO_NOT_EXIST.getCode(), ResultCode.WHERE_TO_GO_NOT_EXIST.getMessage());
        }
        String outNum = outStock.getOutNum();
        List<OutStockInfo> outStockInfoList = outStockInfoMapper.selectList(new QueryWrapper<OutStockInfo>().eq("out_num", outNum));
        if (!CollectionUtils.isEmpty(outStockInfoList)) {
            for (OutStockInfo outStockInfo : outStockInfoList) {
                String pNum = outStockInfo.getPNum();
                List<Product> productList = productMapper.selectList(new QueryWrapper<Product>().eq("p_num", pNum));
                if (CollectionUtils.isEmpty(productList)) {
                    throw new BusinessException(00000,"物资编号"+pNum+"不存在");
                }
                ProductStock productStock = productStockMapper.selectOne(new QueryWrapper<ProductStock>().eq("p_num", pNum));
                if (productStock == null) {
                    throw new BusinessException(ResultCode.Material_inventory_NOT_EXIST.getCode(), ResultCode.Material_inventory_NOT_EXIST.getMessage());
                }
                Integer productNumber = outStockInfo.getProductNumber();
                Long stock = productStock.getStock();
                if(stock-productNumber<0){
                    throw new BusinessException(ResultCode.INSUFFICIENT_INVENTORY_OF_MATERIALS.getCode(), ResultCode.INSUFFICIENT_INVENTORY_OF_MATERIALS.getMessage());
                }
                productStock.setStock(stock-productNumber);
                productStockMapper.updateById(productStock);
            }
            outStock.setStatus(0);
            outStock.setModifiedTime(new Date());
            this.baseMapper.updateById(outStock);

        } else {
            throw new BusinessException(66666, "出库记录不存在！！");
        }
    }

    @Transactional
    @Override
    public void updateCurrentLocation(Long id, String address) {
        if(StringUtils.isEmpty(address)){
            throw new BusinessException(66666, "当前地址不能为空");
        }
        OutStock outStock = this.baseMapper.selectById(id);
        if (outStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.OUTBOUND_ORDER_NOT_EXIST.getMessage());
        }
        outStock.setCurrentLocation(address);
        this.baseMapper.updateById(outStock);

    }

    @Override
    public void confirmArrival(Long id, String address) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (outStock == null) {
            throw new BusinessException(ResultCode.OUTBOUND_ORDER_NOT_EXIST.getCode(), ResultCode.OUTBOUND_ORDER_NOT_EXIST.getMessage());
        }
        if (outStock.getStatus() != 0) {
            throw new BusinessException(ResultCode.WHERE_TO_GO_STATUS_ERROR.getCode(), ResultCode.WHERE_TO_GO_STATUS_ERROR.getMessage());
        }
        outStock.setCurrentLocation(address);
        outStock.setStatus(3);
        this.baseMapper.updateById(outStock);
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
                wrapper.ge("bos.create_time", format);
            } else {
                wrapper.ge("create_time", format);
            }

        }
        if (!StringUtils.isEmpty(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String format = simpleDateFormat.format(endTime);
            if (type == 1) {
                wrapper.le("bos.create_time", format);
            } else {
                wrapper.le("create_time", format);
            }
        }

        return wrapper;
    }
    private QueryWrapper<OutStock> getWrapper(OutStockVO outStockVO) {
        QueryWrapper<OutStock> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("create_time");
        if (!StringUtils.isEmpty(outStockVO.getOutNum())) {
            wrapper.like("out_num", outStockVO.getOutNum().trim());
        }
        if (!StringUtils.isEmpty(outStockVO.getType())) {
            wrapper.eq("type", outStockVO.getType());
        }
        if (!StringUtils.isEmpty(outStockVO.getStatus())) {
            wrapper.eq("status", outStockVO.getStatus());
        }
        return wrapper;
    }
}
