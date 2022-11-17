package com.app.chatserver.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    void createUserFolders(String userName) throws IOException;

    boolean checkDirectories(String userName);

    Path saveImage(BufferedImage image, String ImageName, Path pathToUserImg) throws IOException;

    void saveAvatar(BufferedImage img, String pathToUserImg) throws IOException;

    String imageToBase64(String path) throws IOException;
}
