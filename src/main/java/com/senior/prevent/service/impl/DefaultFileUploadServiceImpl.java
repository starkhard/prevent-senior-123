package com.senior.prevent.service.impl;

import com.senior.prevent.constants.FileUploadConstants;
import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.exception.FileUploadRequestException;
import com.senior.prevent.model.FileModel;
import com.senior.prevent.populator.impl.FileUploadPopulator;
import com.senior.prevent.populator.impl.FileUploadReversePopulator;
import com.senior.prevent.repository.FileUploadRepository;
import com.senior.prevent.service.FileUploadService;
import com.senior.prevent.utils.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DefaultFileUploadServiceImpl<T> implements FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;
    @Autowired
    private FileUploadPopulator fileUploadPopulator;
    @Autowired
    private FileUploadReversePopulator fileUploadReversePopulator;
    @Value("${prevent.senior.store.new.log.file}")
    private String PATH_IMPORT_FILE;
    @Value("${prevent.senior.store.new.log.success}")
    private String PATH_IMPORT_FILE_SUCCESS;
    @Value("${prevent.senior.store.new.log.error}")
    private String PATH_IMPORT_FILE_ERROR;
    @Autowired
    private RedisTemplate<Long, FileModel> redisTemplate;


    @Override
    public void persist(FileUploadData fileUploadData) {
        if (Objects.nonNull(fileUploadData)) {
            Optional<FileModel> fileModel = fileUploadRepository.findById(fileUploadData.getId());
            if (fileModel.isPresent()) {
                fileUploadReversePopulator.populate(fileUploadData, fileModel.get());
                fileUploadRepository.save(fileModel.get());
            } else {
                FileModel file = new FileModel();
                fileUploadReversePopulator.populate(fileUploadData, file);
                fileUploadRepository.save(file);
            }
        }
    }

    @Override
    public void saveFile(MultipartFile... multipartFile) {

        if (CollectionUtils.size(multipartFile) <= 0)
            throw new FileUploadRequestException("Empty File ");

        try {

            for (MultipartFile file : multipartFile) {

                final byte[] bytes = file.getBytes();
                final Path IMPORT_FILE = Paths.get(PATH_IMPORT_FILE);
                if (!Files.exists(IMPORT_FILE)) {
                    Files.createDirectories(IMPORT_FILE);
                    Files.createDirectories(Paths.get(PATH_IMPORT_FILE_SUCCESS));
                    Files.createDirectories(Paths.get(PATH_IMPORT_FILE_ERROR));
                }

                final File newLogFileName = new File(PATH_IMPORT_FILE +
                        FileUploadConstants.File.Upload.Variables.PREVENT_FILE_NAME +
                        FileUploadConstants.File.Upload.Variables.TRACE + DateFormatUtils.format() +
                        FileUploadConstants.File.Upload.Variables.FILE_ENDS_WITH);

                final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newLogFileName.getAbsolutePath()));
                out.write(bytes);
                out.flush();
                out.close();
            }

        } catch (IOException cause) {
            log.error("Failed to store log file ! " + cause.getMessage());
        }

    }


    @Override
    public void removeFileById(long id) {
        fileUploadRepository.deleteById(id);
    }

    @Override
    public Collection<? extends T> loadAllFiles() {
        Collection<T> fileUploadDataToMethodReturn = new ArrayList<>();
        Collection<FileModel> files = fileUploadRepository.findAll();

        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(file -> {
                FileUploadData fileUploadData = new FileUploadData();
                fileUploadPopulator.populate(file, fileUploadData);
                T value = (T) fileUploadData;
                fileUploadDataToMethodReturn.add(value);
            });
        }
        return CollectionUtils.isNotEmpty(fileUploadDataToMethodReturn) ?
                fileUploadDataToMethodReturn : CollectionUtils.EMPTY_COLLECTION;
    }


    @Override
    @Cacheable(value = "file_model", key = "#id")
    public FileUploadData findLogById(long id) {
        FileUploadData fileUploadData = new FileUploadData();
        Optional<FileModel> fileModel = fileUploadRepository.findById(id);
        if (fileModel.isPresent()) {
            fileUploadPopulator.populate(fileModel.get(), fileUploadData);
            return fileUploadData;
        }
        throw new RuntimeException("Log not Found !");
    }


    @Override
    public void edit(FileUploadData fileUploadData) {

        if (Objects.nonNull(fileUploadData)) {
            Optional<FileModel> fileModel = fileUploadRepository.findById(fileUploadData.getId());
            if (fileModel.isPresent()) {
                fileUploadReversePopulator.populate(fileUploadData, fileModel.get());
                fileUploadRepository.save(fileModel.get());
            } else {
                throw new RuntimeException("Log not Found !");
            }
        }
    }
}
