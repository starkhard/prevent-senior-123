package com.senior.prevent.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class MoveFilesWithSuccessStatus implements Tasklet {


    private String RESOURCE_SUCCESS_PATH;
    private String RESOURCE_UPLOAD_PATH;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Collection<StepExecution> jobExecution = chunkContext.getStepContext()
                .getStepExecution().getJobExecution()
                .getStepExecutions();


        for (StepExecution step : jobExecution) {
            if (ExitStatus.COMPLETED.equals(step.getExitStatus())) {

                File file = Paths.get(RESOURCE_UPLOAD_PATH).toFile().getAbsoluteFile();
                Path currentPath = Paths.get(RESOURCE_UPLOAD_PATH).toAbsolutePath();
                Path newPathName = Paths.get(RESOURCE_SUCCESS_PATH).toAbsolutePath();
                String path[] = file.list();

                Arrays.stream(path).forEach(f -> {

                    try {
                        FileUtils.moveFileToDirectory(FileUtils.getFile(currentPath.toString() + "/" + f), FileUtils.getFile(newPathName.toString() + "/"), false);
                    } catch (IOException e) {
                        log.warn("problem to move files !" + e.getMessage());
                    }
                });

            }
        }
        return RepeatStatus.FINISHED;
    }

    public void setRESOURCE_SUCCESS_PATH(String RESOURCE_SUCCESS_PATH) {
        this.RESOURCE_SUCCESS_PATH = RESOURCE_SUCCESS_PATH;
    }

    public String getRESOURCE_SUCCESS_PATH() {
        return RESOURCE_SUCCESS_PATH;
    }

    public void setRESOURCE_UPLOAD_PATH(String RESOURCE_UPLOAD_PATH) {
        this.RESOURCE_UPLOAD_PATH = RESOURCE_UPLOAD_PATH;
    }

    public String getRESOURCE_UPLOAD_PATH() {
        return RESOURCE_UPLOAD_PATH;
    }
}
