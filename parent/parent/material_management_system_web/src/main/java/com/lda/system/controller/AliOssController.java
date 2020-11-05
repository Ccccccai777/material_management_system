package com.lda.system.controller;

import com.lda.response.Result;
import com.lda.system.service.AliOssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lda
 * @since 2020-10-26
 */
@Api("阿里云oss接口")
@RestController
public class AliOssController {
    @Autowired
    private AliOssService aliOssService;

    @ApiOperation(value = "上传图片文件")
    @PostMapping("/uploadImgFile")
    public Result uploadImgFile(MultipartFile file) {
        String s = aliOssService.upload(file);
        return Result.ok().data("url", s);
    }

    @ApiOperation(value = "删除上传替换之后的头像")
    @DeleteMapping("/deleteImgFile")
    public Result deleteImgFile(String file) {
        //https://xinguan-parent.oss-cn- hangzhou.aliyuncs.com/2020/09/25/1575345b2cd14c13872f9b83a0aac919.png
        try {
            String[] splitFile = file.split(".com/");
            aliOssService.deleteFile(splitFile[1]);
            return Result.ok();
        } catch (Exception e) {
            return Result.error();
        }
    }
}

