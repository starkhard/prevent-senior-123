package com.senior.prevent.service;

import com.senior.prevent.solr.LogsIndex;

import java.util.List;

public interface FileIndexationService {

    void indexationSolr(List<LogsIndex> index);

    void deleteIndex();

    List<LogsIndex> searchByParameterIp(String ip);

    List<LogsIndex> searchByParameterAgent(String userAgent);

    List<LogsIndex> searchByParameterRequest(String request);

    List<LogsIndex> searchByParameterStatus(String status);

    List<LogsIndex> searchAllLogs(int indexPage);
}
