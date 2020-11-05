package com.lda.system.controller;


import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.SupplierVO;
import com.lda.response.Result;
import com.lda.system.entity.Image;
import com.lda.system.entity.vo.ImageAttachmentVO;
import com.lda.system.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-26
 */
@Api("图片附件接口")
@RestController
@RequestMapping("/upload")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * 上传图片文件
     * @param file
     * @return
     */
    @ApiOperation(value = "上传文件")
    @RequiresPermissions({"upload:image"})
    @PostMapping("/image")
    public Result uploadImage(MultipartFile file) throws IOException {
        String realPath= imageService.uploadImage(file);
        return Result.ok().data("realPath",realPath);
    }


    /**
     * 附件列表(图片)
     *
     * @return
     */
    @ApiOperation(value = "附件列表", notes = "模糊查询附件列表")
    @GetMapping("/findImageList")
    public Result findImageList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
                                      ImageAttachmentVO imageAttachmentVO) {
        PageVO<Image> imagePageVO = imageService.findImageList(pageNum, pageSize, imageAttachmentVO);
        return Result.ok().data("imageList",imagePageVO.getRows()).data("total",imagePageVO.getTotal());


    }

    /**
     * 删除图片
     * @param id
     * @return
     */
    @ApiOperation(value = "删除图片", notes = "删除数据库记录,删除图片服务器上的图片")
    @RequiresPermissions("attachment:delete")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id){
        imageService.delete(id);
        return Result.ok();
    }

}

