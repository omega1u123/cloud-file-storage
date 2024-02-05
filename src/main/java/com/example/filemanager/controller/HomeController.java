package com.example.filemanager.controller;

import com.example.filemanager.service.FileService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

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

        if(filesAndFolders.get("currentDirectory").get(0) == null ){
            model.addAttribute("currentDirectory", "");
            return "home";
        }
        model.addAttribute("currentDirectory", filesAndFolders.get("currentDirectory"));
        return "home";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("fileToUpload")MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.uploadFile(file);
        return "redirect:/home";
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(@RequestParam(required = false) String directory, @RequestParam("filesToUpload")MultipartFile[] files) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.uploadFolder(directory, files);
        return "redirect:/home";
    }

    @GetMapping("/search")
    public String findFile(@RequestParam String query, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Map<String, List<String>> filesAndFolders = fileService.getFileNames(query);

        model.addAttribute("folders", filesAndFolders.get("folders"));
        model.addAttribute("files", filesAndFolders.get("files"));
        return "home";
    }

    @GetMapping("/deleteFile")
    public String deleteFile(@RequestParam String fileName, @RequestParam(required = false) String directory) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.deleteFile(fileName, directory);
        return "redirect:/home";
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName, @RequestParam(required = false) String directory) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(fileService.downloadFile(fileName, directory)));
    }

}
