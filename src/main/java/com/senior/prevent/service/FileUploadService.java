package com.senior.prevent.service;

import com.senior.prevent.data.FileUploadData;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface FileUploadService<T> {

    void persist(FileUploadData fileUploadData);

    void saveFile(MultipartFile... multipartFile);

    void removeFileById(long id);

    Collection<? extends T> loadAllFiles();

    FileUploadData findLogById(long id);

    void edit(FileUploadData fileUploadData);
}
