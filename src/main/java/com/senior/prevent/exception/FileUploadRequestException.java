package com.senior.prevent.exception;

public class FileUploadRequestException extends RuntimeException {

    public FileUploadRequestException() {
        super();
    }

    public FileUploadRequestException(String s) {
        super(s);
    }
}
