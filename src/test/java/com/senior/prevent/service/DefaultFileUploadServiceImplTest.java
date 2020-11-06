package com.senior.prevent.service;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.service.impl.DefaultFileUploadServiceImpl;
import com.senior.prevent.utils.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
public class DefaultFileUploadServiceImplTest {

    @Mock
    private DefaultFileUploadServiceImpl defaultFileUploadService;

    private Collection<FileUploadData> fileUploadDataCollection = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileUploadDataCollection = createCollections(300);
    }

    @Test
    public void testIfBehaviorOfLoadFilesWillReturnACollectionMock() {
        BDDMockito.when(defaultFileUploadService.loadAllFiles()).thenReturn(fileUploadDataCollection);
        Collection<FileUploadData> resultCollection = defaultFileUploadService.loadAllFiles();
        Assert.assertNotNull(resultCollection);
    }

    private Collection<FileUploadData> createCollections(long quantity) {

        Collection<FileUploadData> collections = new ArrayList<>();

        for (long i = 0; i < quantity; i++) {
            FileUploadData data = FileUploadData.builder()
                    .status(200)
                    .userAgent("userAgent")
                    .id(i)
                    .request("request")
                    .date(DateFormatUtils.format())
                    .ip("192168013")
                    .build();

            collections.add(data);
        }
        return collections;
    }

}
