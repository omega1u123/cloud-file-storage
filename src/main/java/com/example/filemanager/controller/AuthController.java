package com.example.filemanager.controller;

import com.example.filemanager.domain.RegForm;
import com.example.filemanager.domain.UserDao;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserDao userDao;
    private final MinioClient minioClient;

    @GetMapping("/loginPage")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String getRegPage(){
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(RegForm regForm) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userDao.save(regForm.toUser());
        minioClient.makeBucket(
                MakeBucketArgs
                        .builder()
                        .bucket(regForm.getUsername())
                        .build());
        return "redirect:/loginPage";
    }

}
