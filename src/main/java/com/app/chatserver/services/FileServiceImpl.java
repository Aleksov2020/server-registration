package com.app.chatserver.services;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class FileServiceImpl implements FileService {
    @Value("${hay.pathToSave}")
    private String pathToSaveAllFiles;


    @Override
    public void createUserFolders(String userName) throws IOException {
        Files.createDirectory(Paths.get(pathToSaveAllFiles + userName));
        Files.createDirectory(Paths.get(pathToSaveAllFiles + userName + "/audio"));
        Files.createDirectory(Paths.get(pathToSaveAllFiles + userName + "/img"));
    }

    @Override
    public boolean checkDirectories(String userName) {
        return Files.exists(Paths.get(pathToSaveAllFiles+userName));
    }

    @Override
    public Path saveImage(BufferedImage image, String ImageName, Path pathToUserImg) throws IOException {
        ImageIO.write(
                image,
                "jpg",
                new File(pathToUserImg +"/"+ ImageName + ".jpg"));
        return Paths.get(pathToUserImg +"/"+ ImageName + ".jpg");
    }

    @Override
    public void saveAvatar(BufferedImage img, String pathToUserImg) throws IOException {
        ImageIO.write(
                img,
                "jpg",
                new File(pathToUserImg));
    }

    @Override
    public String imageToBase64(String path) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(path));
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
