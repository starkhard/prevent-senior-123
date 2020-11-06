package com.senior.prevent.job;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.service.FileIndexationService;
import com.senior.prevent.service.FileUploadService;
import com.senior.prevent.solr.LogsIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class PreventSeniorLogIndexationJob {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileIndexationService fileIndexationService;


    @Scheduled(fixedRate = 1000000,initialDelay = 1000)
    @Async
    public void synchronizeAllLogsIndexationJob() {

        log.info("#### SOLR INDEXATION LOGS ##########");

        Collection<FileUploadData> allLogsToIndexationJob = fileUploadService.loadAllFiles();
        List<LogsIndex> allDocumentsToIndex = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(allLogsToIndexationJob)) {
            fileIndexationService.deleteIndex();

            allLogsToIndexationJob.forEach(logs -> {

                LogsIndex toIndex = LogsIndex.builder()
                        .ip(logs.getIp())
                        .id(logs.getId())
                        .request(logs.getRequest())
                        .userAgent(logs.getUserAgent())
                        .status(String.valueOf(logs.getStatus()))
                        .date(logs.getDate()).build();

                allDocumentsToIndex.add(toIndex);
            });
            log.info("Size of documents " + allDocumentsToIndex.size());
            fileIndexationService.indexationSolr(allDocumentsToIndex);
        }
    }


    public void synchronizeDeleteAllLogsIndexationJob() {
        log.info("#### SOLR INDEXATION  DELETE LOGS ##########");
        fileIndexationService.deleteIndex();
    }

}
