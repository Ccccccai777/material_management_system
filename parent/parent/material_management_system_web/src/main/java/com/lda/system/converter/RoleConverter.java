package com.lda.system.converter;


import com.lda.system.entity.Role;
import com.lda.system.entity.vo.RoleTransferItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class RoleConverter {



    /**
     * 转成前端需要的角色Item
     * @param list
     * @return
     */
    public static List<RoleTransferItemVO> converterToRoleTransferItem(List<Role> list) {
        List<RoleTransferItemVO> itemVOList=new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for (Role role : list) {
                RoleTransferItemVO item = new RoleTransferItemVO();
                item.setLabel(role.getRoleName());
                item.setDisabled(role.getStatus()==0);
                item.setKey(role.getId());
                itemVOList.add(item);
            }
        }

        return itemVOList;
    }
}
