package com.app.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserList {
    private List<ResponseUser> responseUserList = new ArrayList<>();
}
