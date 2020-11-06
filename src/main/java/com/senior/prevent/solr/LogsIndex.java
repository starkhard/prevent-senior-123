package com.senior.prevent.solr;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "logIndex")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LogsIndex {

    @Id
    @Field
    @Indexed
    private Long id;
    @Field
    @Indexed
    private String date;
    @Field
    @Indexed
    private String ip;
    @Field
    @Indexed
    private String request;
    @Field
    @Indexed
    private String status;
    @Field
    @Indexed
    private String userAgent;
}
