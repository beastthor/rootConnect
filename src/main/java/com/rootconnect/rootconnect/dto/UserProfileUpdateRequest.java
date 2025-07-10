package com.rootconnect.rootconnect.dto;


import lombok.Data;

@Data
public class UserProfileUpdateRequest{

    private String bio;
    private String location;
    private String personalityType;
}
