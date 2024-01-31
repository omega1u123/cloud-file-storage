package com.example.filemanager.controller;

import com.example.filemanager.service.FileService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FileService fileService;

    @GetMapping("/home")
    public String getHome(@RequestParam(required = false) String directory, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());

        Map<String, List<String>> filesAndFolders = fileService.getFileNames(directory);

        model.addAttribute("folders", filesAndFolders.get("folders"));
        model.addAttribute("files", filesAndFolders.get("files"));
        model.addAttribute("currentDirectory", "");

        return "home";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("fileToUpload")MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.uploadFile(file);
        return "redirect:/home";
    }

}
