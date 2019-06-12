package com.csye6225.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class ImageService  {
    private static final String PNG = "image/png";
    private static final String JPG = "image/jpg";
    private static final String JPEG = "image/jpeg";
    public String generateFileName(MultipartFile multiPart) {


        String uploadDir = "/home/avitiwari/Desktop/cloud/csye6225/dev/";

        return uploadDir + multiPart.getOriginalFilename().replace(" ", "_");

    }

    public boolean fileCheck(String filetype) {

      return( filetype.equals(JPEG) || filetype.equals(JPG) || filetype.equals(PNG));


    }


    public String uploadFile(MultipartFile multipartFile,String filePath) throws Exception {

        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (Exception e) {
            throw e;
        }

        return filePath;


    }


}
