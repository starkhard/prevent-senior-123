package com.senior.prevent.populator.impl;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.data.StatusData;
import com.senior.prevent.exception.FileUploadRequestException;
import com.senior.prevent.model.FileModel;
import com.senior.prevent.populator.UploadPopulator;
import com.senior.prevent.utils.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service(value = "fileUploadReversePopulator")
public class FileUploadReversePopulator implements UploadPopulator<FileUploadData,FileModel > {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public void populate(FileUploadData fileUploadData,FileModel fileModel) {
        fileModel.setUserAgent(fileUploadData.getUserAgent());
        fileModel.setDate(DateFormatUtils.now());
        fileModel.setRequest(fileUploadData.getRequest());
        fileModel.setIp(fileUploadData.getIp());
        if(!StatusData.is(fileUploadData.getStatus()))
            throw new FileUploadRequestException("Status Not Allowed to be updated ");
        fileModel.setStatus(StatusData.of(fileUploadData.getStatus()).getStatus());
    }
}
