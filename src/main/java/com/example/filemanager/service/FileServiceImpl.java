package com.example.filemanager.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private final MinioClient minioClient;
    @Override
    public Map<String, List<String>> getFileNames(String directory) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Iterable<Result<Item>> files;
        if (directory != null && !directory.isEmpty()) {
            files = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(auth.getName()).prefix(directory).build()
            );

        } else {
            files = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(auth.getName()).build()
            );
        }
        List<Item> fileList = new ArrayList<>();
        for (Result<Item> file : files) {
            Item item1 = file.get();
            fileList.add(item1);
        }

        List<String> fileNames = fileList.stream()
                .map(Item::objectName)
                .toList();

        List<String> folderNames = fileNames.stream()
                .filter(name -> name.endsWith("/"))
                .map(name -> name.substring(0, name.length()-1))
                .collect(Collectors.toList());

        List<String> fileNamesWithoutFolders = fileNames.stream()
                .filter(name -> !name.endsWith("/"))
                .map(name -> name.contains("/")? name.substring(name.indexOf("/")+1): name)
                .toList();


        Map<String,List<String>> filesAndFolders = new HashMap<>();

        filesAndFolders.put("files", fileNamesWithoutFolders);
        filesAndFolders.put("folders", folderNames);


        return filesAndFolders;
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(auth.getName())
                .object(file.getOriginalFilename())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
    }
}
