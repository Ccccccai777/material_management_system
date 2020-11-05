package com.lda.biz.converter;

import com.lda.biz.entity.Consumer;
import com.lda.biz.entity.vo.ConsumerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsumerConverter {
    public static List<ConsumerVO> converterToVOList(List<Consumer> consumerList) {
        List<ConsumerVO> consumerVOList=new ArrayList<>();
        if (!CollectionUtils.isEmpty(consumerList)) {
            for (Consumer consumer : consumerList) {
                consumerVOList.add(converterToConsumerVO(consumer));
            }
        }
        return consumerVOList;
    }
    /***
     * 转VO
     * @param consumer
     * @return
     */
    public static ConsumerVO converterToConsumerVO(Consumer consumer) {
        ConsumerVO consumerVO = new ConsumerVO();
        BeanUtils.copyProperties(consumer,consumerVO);
        return consumerVO;
    }
}
