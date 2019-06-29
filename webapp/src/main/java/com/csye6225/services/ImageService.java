package com.csye6225.services;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Profile("default")
public class ImageService implements FileHandler{


    private final static Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Override
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

    @Override
    public String deleteFile(String fileLocation, String filePath) throws Exception {
        File file = new File(fileLocation);
        if (file.delete()) {
            return "Successfully deleted";
        }
        return null;
    }
}
