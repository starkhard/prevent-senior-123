package com.senior.prevent.populator.impl;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.data.StatusData;
import com.senior.prevent.model.FileModel;
import com.senior.prevent.populator.UploadPopulator;
import com.senior.prevent.utils.DateFormatUtils;
import org.springframework.stereotype.Service;

@Service
public class FileUploadPopulator implements UploadPopulator<FileModel, FileUploadData> {

    @Override
    public void populate(FileModel fileModel, FileUploadData fileUploadData) {

        fileUploadData.setId(fileModel.getId());
        fileUploadData.setIp(fileModel.getIp());
        fileUploadData.setRequest(fileModel.getRequest());
        fileUploadData.setUserAgent(fileModel.getUserAgent());
        fileUploadData.setStatus(StatusData.of(fileModel.getStatus()).getStatus());
        fileUploadData.setDate(DateFormatUtils.formatDateTime(fileModel.getDate().toString()));
    }
}
