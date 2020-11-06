package com.senior.prevent.service;


import com.senior.prevent.data.FileUploadData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
@Slf4j
public class FileFieldMapper implements FieldSetMapper<FileUploadData> {

    @Override
    public FileUploadData mapFieldSet(FieldSet fieldSet) throws BindException {

        FileUploadData fileUploadData = new FileUploadData();
        fileUploadData.setIp(fieldSet.readString("ip"));
        fileUploadData.setRequest(fieldSet.readString("request"));
        fileUploadData.setDate(fieldSet.readString("date"));
        fileUploadData.setUserAgent(fieldSet.readString("userAgent"));
        fileUploadData.setStatus(fieldSet.readInt("status"));

        return fileUploadData;
    }
}
