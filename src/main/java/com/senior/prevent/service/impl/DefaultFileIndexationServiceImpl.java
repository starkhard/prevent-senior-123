package com.senior.prevent.service.impl;

import com.senior.prevent.repository.IndexationRepository;
import com.senior.prevent.service.FileIndexationService;
import com.senior.prevent.solr.LogsIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class DefaultFileIndexationServiceImpl implements FileIndexationService {


    @Autowired
    private IndexationRepository indexationRepository;

    @Override
    public void indexationSolr(List<LogsIndex> index) {

        try {
            indexationRepository.saveAll(index, Duration.ofSeconds(1000));
        } catch (Exception cause) {
            log.error("failed to index logsIndex" + cause.getMessage());
        }
    }

    @Override
    public void deleteIndex() {
        indexationRepository.deleteAll();
    }

    @Override
    public List<LogsIndex> searchByParameterIp(String ip) {
        List<LogsIndex> documents = indexationRepository.searchByParameterIp(ip);
        log.info("Find logs by IP " + documents);
        return documents;
    }

    @Override
    public List<LogsIndex> searchByParameterAgent(String userAgent) {
        List<LogsIndex> documents = indexationRepository.searchByParameterAgent(userAgent);
        log.info("Find logs by userAgent " + documents);
        return documents;
    }

    @Override
    public List<LogsIndex> searchByParameterRequest(String request) {
        List<LogsIndex> documents = indexationRepository.searchByParameterRequest(request);
        log.info("Find logs by request " + documents);
        return documents;
    }

    @Override
    public List<LogsIndex> searchByParameterStatus(String status) {
        List<LogsIndex> documents = indexationRepository.searchByParameterStatus(status);
        log.info("Find logs by STATUS " + documents);
        return documents;
    }

    @Override
    public List<LogsIndex> searchAllLogs(int indexPage) {
        List<LogsIndex> allDocuments = indexationRepository.searchAllLogsPageable(PageRequest.of(indexPage, 50)).getContent();
        log.info("find all documents paginated");
        return allDocuments;
    }

    public void setIndexationRepository(IndexationRepository indexationRepository) {
        this.indexationRepository = indexationRepository;
    }
}
