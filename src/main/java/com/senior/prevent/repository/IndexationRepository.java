package com.senior.prevent.repository;

import com.senior.prevent.solr.LogsIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexationRepository extends SolrCrudRepository<LogsIndex, String> {


    @Query("ip:*?0*")
    List<LogsIndex> searchByParameterIp(String ip);

    @Query("userAgent:*?0*")
    List<LogsIndex> searchByParameterAgent(String userAgent);

    @Query("request:*?0*")
    List<LogsIndex> searchByParameterRequest(String request);

    @Query("status:?0")
    List<LogsIndex> searchByParameterStatus(String status);

    @Query("*:*")
    Page<LogsIndex> searchAllLogsPageable(Pageable pageable);



}
