package com.csye6225.services;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Profile("dev")
public class AwsFileHandler implements FileHandler {

    private final static Logger logger = LoggerFactory.getLogger(AwsFileHandler.class);

    private AmazonS3 s3client;

    private String dir = "Images/";

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${spring.bucket.name}")
    private String bucketName;

    @PostConstruct
    private void initializeAmazon() {
        this.s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName) throws Exception {
//        String fileName = (new Date().toString() + "-" + multipartFile.getOriginalFilename()).replace(" ", "_");

        logger.info(multipartFile.getName());

        String name = this.dir + "/" + fileName;

        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        File newFile = convFile;

//        s3client.putObject(bucketName, name, multipartFile.getInputStream(), new ObjectMetadata());



        logger.info(bucketName);
        logger.info(endpointUrl);

        return this.endpointUrl + "/" + this.bucketName + "/" + name;
    }

    @Override
    public String deleteFile(String fileLocation, String fileName) throws Exception {
//        String fileName = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
        fileName = dir + "/" + fileName;
        s3client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(fileName));
        return "Successfully deleted";
    }
}