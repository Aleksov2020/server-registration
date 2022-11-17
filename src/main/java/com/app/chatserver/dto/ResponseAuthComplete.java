package com.app.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseAuthComplete {
    private String accessToken;
    private String refreshToken;
}
