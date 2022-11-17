package com.app.chatserver.services;

import com.app.chatserver.dto.ResponseMedia;
import com.app.chatserver.model.Media;

import java.io.IOException;

public interface MediaService {
    ResponseMedia serializeMediaToSend(Media media) throws IOException;
}
