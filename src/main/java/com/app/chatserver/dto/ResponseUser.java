package com.app.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
@AllArgsConstructor
public class ResponseUser {
    private ResponseMedia avatar;
    private String userName;
    private String firstName;
    private String middleName;
}
