package com.lda.system.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.lda.handler.BusinessException;
import com.lda.system.entity.OssEntity;
import com.lda.system.service.AliOssService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.net.www.content.image.png;

import java.io.*;
import java.util.Date;
import java.util.UUID;

/*
 *在创建时就应该把以下属性装载好
 * */
@Service
public class AliOssServiceImpl implements AliOssService, InitializingBean {

    @Autowired
    private OssEntity ossEntity;
    // Endpoint以杭州为例，其它Region请按实际情况填写。
    private String endpoint;
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;


    /*初始化bean后进行的操作*/
    @Override
    public void afterPropertiesSet() throws Exception {
        //// Endpoint以杭州为例，其它Region请按实际情况填写。
        endpoint = ossEntity.getEndpoint();
        //// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行 API访问或日常运维，请登录RAM控制台创建RAM账号。
        accessKeyId = ossEntity.getAccessKeyId();
        accessKeySecret = ossEntity.getAccessKeySecret();
        bucketName = ossEntity.getBucketName();
    }

    /*** 创建存储空间 */
    @Override
    public void createBucket() {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //判断bucketName是否存在
        if (ossClient.doesBucketExist(bucketName)) {
            throw new RuntimeException(bucketName + "在对象存储的Bucket列表中已经存在");
        }
        // 创建存储空间。
        ossClient.createBucket(bucketName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /*** 上传文件 * @param file 文件对象 * @return
     * @param file*/
    @Override
    public String upload(MultipartFile file) {
        // <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String uploadUrl = null;


        // 上传文件到指定的存储空间（bucketName）并将其保存为指定的文件名称（objectName）。
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //判断bucketName是否存在
            if (!ossClient.doesBucketExist(bucketName)) {
                //创建bucket
                ossClient.createBucket(bucketName);
                //设置bucket属性未公共读
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            }
            //获取上传文件流
            InputStream inputStream = file.getInputStream();
            //构建日期路径：avatar/2020/07/31/文件名
            String datePath = new DateTime().toString("yyyy/MM/dd");
            //文件名：uuid.扩展名
            // 获取上传文件的全名称
            String original = file.getOriginalFilename();
            // 去掉uuid中生成的-
            String fileName = UUID.randomUUID().toString().replaceAll("-", "");
            // 截取掉文件获得扩展
            String fileType = original.substring(original.lastIndexOf("."));
            String newName = fileName + fileType;
            // yyyy/MM/dd+/uuid+.jpg/.png
            fileName = datePath + "/" + newName;
            //如果想要实现图片的预览效果,一定要设置一下几个点 //1.设置文件 ACL为反正不能为私有 要么是公共读,要么是公共读写 //2.一定要设置文本类型为(image/jpg)
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 设置公共读权限
            objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
            // 设置类型
            objectMetadata.setContentType(getcontentType(fileType));
            ossClient.putObject(bucketName, fileName, inputStream, objectMetadata);
            // 关闭OSSClient。
            ossClient.shutdown();
            //默认十年不过期
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            // bucket名称 文件名 过期时间
            uploadUrl = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString();
            return uploadUrl.substring(0, uploadUrl.indexOf("?"));

        } catch (Exception e) {
            throw new BusinessException(123456, e.getMessage());
        }


    }

    /*** 下载文件 * @throws IOException
     * @param fileName*/
    @Override
    public void download(String fileName) throws IOException {
        // <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String objectName = "<yourObjectName>";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        InputStream content = ossObject.getObjectContent();
        if (content != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("\n" + line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            content.close();
        }

// 关闭OSSClient。
        ossClient.shutdown();
    }

    /*** 列举文件 */
    @Override
    public void listFile() {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // ossClient.listObjects返回ObjectListing实例，包含此次listObject请求的返回结果。
        ObjectListing objectListing = ossClient.listObjects(bucketName); // objectListing.getObjectSummaries获取所有文件的描述信息。
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + " " + "(size = " + objectSummary.getSize() + ")");
        }// 关闭OSSClient。
        ossClient.shutdown();
    }

    /*** 删除文件
     * @param fileName*/
    @Override
    public void deleteFile(String fileName) {
    // <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如 abc/efg/123.jpg。
        String objectName = fileName;
    // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    // 删除文件
       ossClient.deleteObject(bucketName, objectName);
    // 关闭OSSClient。
        ossClient.shutdown();
    }

    /*** Description: 判断OSS服务文件上传时文件的contentType ** @param FilenameExtension 文件后缀 * @return String */
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") || FilenameExtension.equalsIgnoreCase(".jpg") || FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") || FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") || FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }


}
