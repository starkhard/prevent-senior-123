package com.senior.prevent.service;

import com.senior.prevent.repository.IndexationRepository;
import com.senior.prevent.service.impl.DefaultFileIndexationServiceImpl;
import com.senior.prevent.solr.LogsIndex;
import com.senior.prevent.utils.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class DefaultFileIndexationServiceImplTest {

    @InjectMocks
    private DefaultFileIndexationServiceImpl defaultFileIndexationService;
    @Mock
    private DefaultFileIndexationServiceImpl defaultFileIndexationService2;
    @Spy
    private IndexationRepository indexationRepository;
    List<LogsIndex> mockedList = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockedList = createMockedList(100);
    }

    @Test
    public void testIfBehaviorOfDeleteIndexSolrIsCalledWithMock() {
        BDDMockito.verify(defaultFileIndexationService2, BDDMockito.atMost(30)).deleteIndex();
    }

    @Test
    public void testIfSearchByIPHasTheCorrectBehaviorMock() {
        BDDMockito.given(defaultFileIndexationService2.searchByParameterIp(Mockito.any())).willReturn(mockedList);
        List<LogsIndex> indexList = defaultFileIndexationService2.searchByParameterIp("Ip test");
        Assert.assertNotNull(indexList);
    }

    @Test
    public void testIfSearchByRequestHasTheCorrectBehaviorMock() {
        BDDMockito.given(defaultFileIndexationService2.searchByParameterRequest(Mockito.any())).willReturn(mockedList);
        List<LogsIndex> indexList = defaultFileIndexationService2.searchByParameterRequest("request");
        Assert.assertNotNull(indexList);
    }

    @Test
    public void testIfSearchByStatusHasTheCorrectBehaviorMock() {
        BDDMockito.given(defaultFileIndexationService2.searchByParameterStatus(Mockito.any())).willReturn(mockedList);
        List<LogsIndex> indexList = defaultFileIndexationService2.searchByParameterStatus("200");
        Assert.assertNotNull(indexList);
    }

    @Test
    public void testIfSearchByUserAgentHasTheCorrectBehaviorMock() {
        BDDMockito.given(defaultFileIndexationService2.searchByParameterAgent(Mockito.any())).willReturn(mockedList);
        List<LogsIndex> indexList = defaultFileIndexationService2.searchByParameterAgent("test");
        Assert.assertNotNull(indexList);
    }

    @Test
    public void testIfMethodOfIndexationSolrWillHasTheCorrectBehaviorMock() {
        BDDMockito.verify(defaultFileIndexationService2, BDDMockito.atMost(30)).indexationSolr(mockedList);
    }

    @Test
    public void testIfPageOfIndexationAreRespectTheCorrectBehaviorMock() {
        BDDMockito.when(defaultFileIndexationService2.searchAllLogs(100)).thenReturn(createMockedList(50));
        List<LogsIndex> lisIndexes = defaultFileIndexationService2.searchAllLogs(100);
        Assert.assertNotNull(lisIndexes);
        Assert.assertTrue(lisIndexes.size() == 50);
    }


    private List<LogsIndex> createMockedList(long quantity) {

        List<LogsIndex> indexList = new ArrayList<>();
        for (long i = 0; i < quantity; i++) {
            LogsIndex toIndex = LogsIndex.builder()
                    .date(DateFormatUtils.format())
                    .userAgent("test agent")
                    .ip("192.168.3.1")
                    .id(i)
                    .request("request test")
                    .status(HttpStatus.OK.name())
                    .build();

            indexList.add(toIndex);
        }

        return indexList;
    }
}
