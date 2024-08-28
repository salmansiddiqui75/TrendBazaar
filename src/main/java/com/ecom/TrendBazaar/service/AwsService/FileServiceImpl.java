package com.ecom.TrendBazaar.service.AwsService;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService
{
    @Autowired
    public AmazonS3 amazonS3;
    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;
    @Value("${aws.s3.bucket.product}")
    private String productBucket;
    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    @Override
    public Boolean uploadFileS3(MultipartFile file, int bucketType) throws IOException {
        String bucketName=null;
        if(bucketType==1)
        {
            bucketName=categoryBucket;
        }
        else if(bucketType==2)
        {
            bucketName=productBucket;
        }else{
            bucketName=profileBucket;
        }
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,filename,inputStream,objectMetadata);
        PutObjectResult saveData = amazonS3.putObject(putObjectRequest);
        if(ObjectUtils.isEmpty(saveData))
        {
            return true;
        }
        return false;
    }
}
