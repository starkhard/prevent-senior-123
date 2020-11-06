package com.senior.prevent.processor;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.model.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FileItemProcessor implements ItemProcessor<FileUploadData, FileModel> {

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public FileModel process(FileUploadData fileUploadData) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(fileUploadData.getDate(), dateTimeFormatter);

        FileModel fileModel = new FileModel();
        fileModel.setIp(fileUploadData.getIp());
        fileModel.setRequest(fileUploadData.getRequest());
        fileModel.setDate(dateTime);
        fileModel.setStatus(fileUploadData.getStatus());
        fileModel.setUserAgent(fileUploadData.getUserAgent());
        log.info("File Upload Data " + fileUploadData);

        return fileModel;
    }
}
