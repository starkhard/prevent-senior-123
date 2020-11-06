package com.senior.prevent.validator;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.data.StatusData;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("fileUploaDataValidator")
public class FileUploadDataValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.getClass().isInstance(FileUploadData.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (supports(o.getClass())) {
            FileUploadData data = (FileUploadData) o;
            if (!StatusData.is(data.getStatus()))
                errors.rejectValue("status", "This status is not part of the implementation");
            if (StringUtils.isBlank(data.getIp()))
                errors.rejectValue("ip", "The IP field cannot be null");
            if (StringUtils.isBlank(data.getRequest()))
                errors.rejectValue("request", "The request field cannot be null");
            if (StringUtils.isBlank(data.getUserAgent()))
                errors.rejectValue("userAgent", "The userAgent field cannot be null");

        }

    }
}
