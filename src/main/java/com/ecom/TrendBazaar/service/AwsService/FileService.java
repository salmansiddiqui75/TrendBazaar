package com.ecom.TrendBazaar.service.AwsService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService
{
    public Boolean uploadFileS3(MultipartFile file,int bucketType) throws IOException;
}
