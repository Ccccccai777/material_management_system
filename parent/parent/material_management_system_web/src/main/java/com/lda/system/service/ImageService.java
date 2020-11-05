package com.lda.system.service;

import com.lda.biz.entity.vo.PageVO;
import com.lda.system.entity.Image;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.ImageAttachmentVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-26
 */
public interface ImageService extends IService<Image> {

    PageVO<Image> findImageList(Integer pageNum, Integer pageSize, ImageAttachmentVO imageAttachmentVO);

    void delete(Long id);

    String uploadImage(MultipartFile file);
}
