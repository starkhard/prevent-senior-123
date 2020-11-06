package com.senior.prevent.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.repository.FileUploadRepository;
import com.senior.prevent.service.FileUploadService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FileUploadIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileUploadRepository fileUploadRepository;
    FileUploadData fileUploadData;

    @Before
    public void setUp() {
        fileUploadRepository.deleteAll();
        fileUploadData = FileUploadData.builder()
                .ip("192.168.1.1")
                .status(200)
                .userAgent("userAgent")
                .request("testRequest")
                .build();

    }


    @Test
    public void testIfLogWilBePersistToDataBaseToManuallyWay() throws Exception {
        MvcResult result = mockMvc.perform(post("/prevent/v1/save-logs-manually")
                .content(new ObjectMapper().writeValueAsString(fileUploadData))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
    }

    @Test
    public void testIfDataPersistWillBeRetrieveByGetLogById() throws Exception {

        fileUploadService.persist(fileUploadData);
        Optional<FileUploadData> uploadData = fileUploadService.loadAllFiles().stream().findAny();

        if (uploadData.isPresent()) {
            MvcResult result = mockMvc.perform(get("/prevent/v1/find-logById/" + String.valueOf(uploadData.get().getId()))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("ip").value(fileUploadData.getIp()))
                    .andExpect(jsonPath("request").value(fileUploadData.getRequest()))
                    .andExpect(jsonPath("userAgent").value(fileUploadData.getUserAgent()))
                    .andExpect(jsonPath("status").value(fileUploadData.getStatus()))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();


            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getResponse());
            Assert.assertNotNull(result.getResponse().getContentAsString());

            ObjectMapper obm = new ObjectMapper();
            FileUploadData data = obm.readValue(result.getResponse().getContentAsString(), FileUploadData.class);
            Assert.assertEquals(fileUploadData.getStatus(), data.getStatus());
            Assert.assertEquals(fileUploadData.getIp(), data.getIp());
            Assert.assertEquals(fileUploadData.getUserAgent(), data.getUserAgent());
            Assert.assertEquals(fileUploadData.getRequest(), data.getRequest());

            fileUploadService.removeFileById(data.getId());
        } else {
            throw new RuntimeException("Failed to persist data values ");
        }

    }

    @Test
    public void testIfASampleFileWillBeSavedInAFolderToBeProcessed() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file",
                "access.log",
                "text/plain", "some xml".getBytes());


        MvcResult result = mockMvc.perform(multipart("/prevent/v1/save-log")
                .file(file)
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk()).andReturn();


        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getResponse());
        Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());

    }

    @Test
    public void testIfArrayFilesWillBeSavedInAFolderToBeProcessed() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file",
                "access.log",
                "text/plain", "some xml".getBytes());

        MvcResult result = mockMvc.perform(multipart("/prevent/v1/save-logs")
                .file(file)
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk()).andReturn();


        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getResponse());
        Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());

    }

    @Test
    public void testIfLogWillBeUpdatedWithRequestBody() throws Exception {

        fileUploadService.persist(fileUploadData);
        Optional<FileUploadData> uploadData = fileUploadService.loadAllFiles().stream().findAny();

        if (uploadData.isPresent()) {

            FileUploadData editFileUpload = FileUploadData
                    .builder()
                    .id(uploadData.get().getId())
                    .ip("100.100.100.100")
                    .request("CHROME")
                    .status(500)
                    .userAgent("CHROME AGENT")
                    .build();


            MvcResult result = mockMvc.perform(put("/prevent/v1/edit-log")
                    .content(new ObjectMapper().writeValueAsString(editFileUpload))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();


            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getResponse());
            Assert.assertNotNull(result.getResponse().getContentAsString());

            ObjectMapper mapper = new ObjectMapper();
            FileUploadData transformerData = mapper.readValue(result.getResponse().getContentAsString(), FileUploadData.class);
            Assert.assertNotNull(transformerData);
            Assert.assertNotEquals(fileUploadData.getRequest(), transformerData.getRequest());
            Assert.assertNotEquals(fileUploadData.getIp(), transformerData.getIp());
            Assert.assertNotEquals(fileUploadData.getUserAgent(), transformerData.getUserAgent());
            Assert.assertNotEquals(fileUploadData.getStatus(), transformerData.getStatus());

            fileUploadService.removeFileById(uploadData.get().getId());

        } else {
            throw new RuntimeException("Failed to persist data values ");
        }

    }

    @Test(expected = Exception.class)
    public void testIfLogFileWillBeRemovedFromDatabase() throws Exception {

        fileUploadService.persist(fileUploadData);
        Optional<FileUploadData> uploadData = fileUploadService.loadAllFiles().stream().findAny();

        if (uploadData.isPresent()) {
            mockMvc.perform(delete("/prevent/v1/delete-log/" + uploadData.get().getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            fileUploadService.findLogById(uploadData.get().getId());

        }
    }


    @After
    public void finishUp() {
        fileUploadRepository.deleteAll();
    }
}


