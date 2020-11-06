package com.senior.prevent.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@ApiModel
public class FileUploadData implements Serializable {

    @ApiModelProperty(position = 1, notes = "It is not necessary to fill in this field")
    private long id;
    @ApiModelProperty(position = 2, notes = "the expected date format is:  yyyy-MM-dd HH:mm:ss.SSS",required = true)
    private String date;
    @ApiModelProperty(position = 3, notes = "Respect the IP mask formats", required = true)
    private String ip;
    @ApiModelProperty(position = 4, notes = "Send the correct request ", required = true)
    private String request;
    @ApiModelProperty(position = 5, notes = "Send the numeric codes according to the HttpStatus Enumeration", required = true)
    private int status;
    @ApiModelProperty(position = 6, notes = "Send the correct agents ", required = true)
    private String userAgent;
}
