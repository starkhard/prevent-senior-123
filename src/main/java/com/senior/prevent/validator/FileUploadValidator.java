package com.senior.prevent.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("fileUploadValidator")
@Slf4j
public class FileUploadValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.getClass().isInstance(MultipartFile.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (supports(o.getClass())) {

            MultipartFile multipartFile[] = (MultipartFile[]) o;

            List<MultipartFile> files = Arrays.stream(multipartFile).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(files))
                errors.reject("file", "Files Cannot be Empty !");

            for (MultipartFile file : multipartFile) {
                if (file.getSize() <= 0)
                    errors.reject("file", "Request to upload files is null or empty ");
            }
        }
    }
}
