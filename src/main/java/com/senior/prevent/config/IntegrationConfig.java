package com.senior.prevent.config;

import com.senior.prevent.constants.FileUploadConstants;
import com.senior.prevent.job.FileJobRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@IntegrationComponentScan
public class IntegrationConfig {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Value("${prevent.senior.store.new.log.file}")
    private String PATH;


    protected DirectChannel input() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow flow() {
        return IntegrationFlows.from(fileSourceMessage(),
                f -> f.poller(Pollers.fixedDelay(5000)))
                .transform(request())
                .handle(handler())
                .get();


    }

    @Bean
    public MessageSource<File> fileSourceMessage() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        File file = Paths.get(PATH).toFile().getAbsoluteFile();
        source.setDirectory(file);
        source.setFilter(new SimplePatternFileListFilter(FileUploadConstants.File.Upload.Variables.FILE_ENDS_WITH_FILTER));
        source.setUseWatchService(true);
        source.setWatchEvents(FileReadingMessageSource.WatchEventType.CREATE);
        return source;
    }

    @Bean
    public FileJobRequest request() {
        File file = Paths.get(PATH).toFile().getAbsoluteFile();
        FileJobRequest jobRequestTransform = new FileJobRequest();
        jobRequestTransform.setJob(job);
        jobRequestTransform.setFilePath(file.getAbsolutePath());
        return jobRequestTransform;
    }

    @Bean
    public JobLaunchingMessageHandler handler() {
        return new JobLaunchingMessageHandler(jobLauncher);
    }
}
