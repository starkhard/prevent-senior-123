package com.senior.prevent.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class FileJobRequest {

    private Job job;
    private String filePath = "prevent.senior.store.new.log.file";

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Transformer
    public JobLaunchRequest request(Message<File> files) {
        JobParametersBuilder jobParameter = new JobParametersBuilder();
        jobParameter.addString("prevent-senior", files.getPayload().getAbsolutePath(),false);
        jobParameter.addDate("date", new Date());
        return new JobLaunchRequest(job, jobParameter.toJobParameters());
    }
}
