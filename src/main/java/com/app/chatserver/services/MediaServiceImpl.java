package com.app.chatserver.services;

import com.app.chatserver.dto.ResponseMedia;
import com.app.chatserver.model.Media;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MediaServiceImpl implements MediaService {
    private final FileService fileService;

    public MediaServiceImpl(FileService fileService){
        this.fileService = fileService;
    }

    @Override
    public ResponseMedia serializeMediaToSend(Media media) throws IOException {
        return new ResponseMedia(
                fileService.imageToBase64(media.getPath())
        );
    }
}
