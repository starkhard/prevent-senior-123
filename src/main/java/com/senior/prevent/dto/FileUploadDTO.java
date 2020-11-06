package com.senior.prevent.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileUploadDTO implements Serializable {

    private long id;
    private String date;
    private String ip;
    private String request;
    private int status;
    private String userAgent;

}
