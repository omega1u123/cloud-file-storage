package com.example.filemanager.domain;

import lombok.Data;

@Data
public class RegForm {

    private String username;
    private String password;

    public UserEntity toUser(){
        return new UserEntity(username, password);
    }

}
