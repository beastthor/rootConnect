package com.rootconnect.rootconnect.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserMatchedResponse{

    private Long id;
    private String username;
    private String location;
    private List<String> sharedInterests;
}
