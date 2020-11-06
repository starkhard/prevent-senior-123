package com.senior.prevent.controller;


import com.senior.prevent.constants.FileUploadConstants;
import com.senior.prevent.exception.FileUploadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class FileUploadExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FileUploadRequestException.class)
    public String handleStorageFileNotFound(FileUploadRequestException exc) {
        log.error("ex-01" +exc.getMessage());
        return FileUploadConstants.File.Upload.Page.errorPage;
    }

}
