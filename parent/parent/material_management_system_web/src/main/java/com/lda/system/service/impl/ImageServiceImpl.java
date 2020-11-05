package com.lda.system.service.impl;

import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.SupplierConverter;
import com.lda.biz.entity.Supplier;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.SupplierVO;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.Image;
import com.lda.system.entity.vo.ImageAttachmentVO;
import com.lda.system.mapper.ImageMapper;
import com.lda.system.service.AliOssService;
import com.lda.system.service.ImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-26
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {
    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private AliOssService aliOssService;

    @Override
    public PageVO<Image> findImageList(Integer pageNum, Integer pageSize, ImageAttachmentVO imageAttachmentVO) {
        Page<Image> page=new Page<>(pageNum,pageSize);
        QueryWrapper<Image> wrapper=getWrapper(imageAttachmentVO);
        IPage<Image> page1 = this.baseMapper.selectPage(page, wrapper);
        List<Image> images = page1.getRecords();
        return new PageVO<Image>(page1.getTotal(),images);
    }
    private QueryWrapper<Image> getWrapper(ImageAttachmentVO imageAttachmentVO){
        System.out.println(imageAttachmentVO);
        QueryWrapper<Image> qw = new QueryWrapper<>();
        if (imageAttachmentVO != null) {
            if (!StringUtils.isEmpty(imageAttachmentVO.getMediaType())) {
                qw.eq("media_type",imageAttachmentVO.getMediaType());
            }
            if (!StringUtils.isEmpty(imageAttachmentVO.getPath())) {
                qw.like("path",imageAttachmentVO.getPath());
            }


        }
        return qw;
    }
    @Override
    public void delete(Long id) {
        Image image = this.baseMapper.selectById(id);
        if (image==null) {
            throw  new BusinessException(99999,"图片不存在");
        }
        this.baseMapper.deleteById(id);
        aliOssService.deleteFile(image.getPath());
    }

    @Override
    public String uploadImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException("上传的文件不能为空");
        }
        //上传到oss中
        String path = aliOssService.upload(file);
        // 获取上传文件的全名称
        String original = file.getOriginalFilename();
        // 截取掉文件获得扩展
        String fileType = original.substring(original.lastIndexOf("."));
        long size = file.getSize();
        //保存图片信息到数据库
        try {
            BufferedImage bufferedImageimage = ImageIO.read(file.getInputStream());
            if (bufferedImageimage != null) {//如果image=null 表示上传的不是图片格式
                Image image = new Image();
                image.setSize(size);
                image.setHeight(bufferedImageimage.getHeight());
                image.setWidth(bufferedImageimage.getWidth());
                image.setMediaType(fileType);
                image.setPath(path);
                imageMapper.insert(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return path;
    }
}
