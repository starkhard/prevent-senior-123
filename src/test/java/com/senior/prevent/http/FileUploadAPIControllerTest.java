package com.senior.prevent.http;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.dto.FileUploadDTO;
import com.senior.prevent.service.FileUploadService;
import com.senior.prevent.utils.DateFormatUtils;
import com.senior.prevent.validator.FileUploadDataValidator;
import com.senior.prevent.validator.FileUploadValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FileUploadAPIControllerTest {

    @InjectMocks
    private FileUploadAPIController fileUploadAPIController;
    @Mock
    private FileUploadAPIController fileUploadAPIController2;
    @Mock
    private FileUploadDataValidator fileUploadDataValidator;
    @Spy
    private FileUploadService fileUploadService;
    @Spy
    private FileUploadValidator fileUploadValidator;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIfCreateLogsManuallyMockReturnStatus() throws Exception {
        ResponseEntity responseEntity = fileUploadAPIController.saveManuallyFile(dto());
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testIfEditLogWillReturnCorrectResponseStatusMock() {
        ResponseEntity responseEntity = fileUploadAPIController.editFileUpload(dto());
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testIfUploadSingleFileWithTenBytesWillReturnCorrectStatus() {
        MultipartFile multipartFile = new MockMultipartFile("file", new byte[10]);
        ResponseEntity responseEntity = fileUploadAPIController.fileUpload(multipartFile);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testIfUploadSingleFileWithZeroBytesWillReturnCorrectStatus() {
        MultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);
        ResponseEntity responseEntity = fileUploadAPIController.fileUpload(multipartFile);
        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    public void testIfMultiPartFileArraysWillReturnCorrectStatus() {
        MultipartFile fileOne = new MockMultipartFile("file", new byte[10]);
        MultipartFile fileTwo = new MockMultipartFile("file", new byte[20]);
        MultipartFile multipartFile[] = new MultipartFile[]{fileOne, fileTwo};
        ResponseEntity responseEntity = fileUploadAPIController.fileUpload(multipartFile);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testIfMultiPartFileArraysWithZeroBytesWillReturnCorrectStatus() {
        MultipartFile fileOne = new MockMultipartFile("file", new byte[0]);
        MultipartFile fileTwo = new MockMultipartFile("file", new byte[0]);
        MultipartFile multipartFile[] = new MultipartFile[]{fileOne, fileTwo};
        ResponseEntity responseEntity = fileUploadAPIController.fileUpload(multipartFile);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    private ResponseEntity<Collection<FileUploadDTO>> createAllLogsMock() {

        Collection<FileUploadDTO> collectionsMock = new ArrayList<>();
        for (int i = 0; i < 100; i++) {

            FileUploadDTO dto = FileUploadDTO.builder()
                    .date(DateFormatUtils.format())
                    .id(i)
                    .ip("192.168.0.3")
                    .request("TESTE FIREFOX")
                    .userAgent("FIREFOX")
                    .status(200).build();
            collectionsMock.add(dto);
        }
        return ResponseEntity.ok(collectionsMock);
    }

    private FileUploadData dto() {
        FileUploadData data = FileUploadData.builder()
                .ip("192.168.0.1")
                .request("TESTE FIREFOX")
                .userAgent("MOZILLA")
                .status(200)
                .build();

        return data;
    }
}
