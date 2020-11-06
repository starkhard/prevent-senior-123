package com.senior.prevent.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.NoOpResponseParser;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(basePackages =
        "com.senior.prevent.repository")
@ComponentScan
public class SolrSearhcConfiguration {

    @Value("${spring.data.solr}")
    private final String SOLR_URL ="";

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.
                Builder("http://localhost:8983/solr")
                .withBaseSolrUrl("http://localhost:8983/solr")
                .withResponseParser(new XMLResponseParser())
                .allowCompression(true)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient) {
        return new SolrTemplate(solrClient);
    }
}
