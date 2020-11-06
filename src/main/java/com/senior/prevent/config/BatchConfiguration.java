package com.senior.prevent.config;

import com.senior.prevent.constants.FileUploadConstants;
import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.exception.FileUploadRequestException;
import com.senior.prevent.model.FileModel;
import com.senior.prevent.processor.FileItemProcessor;
import com.senior.prevent.service.FileFieldMapper;
import com.senior.prevent.service.JobNotificationListener;
import com.senior.prevent.tasklet.MoveFilesWithSuccessStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Value("${prevent.senior.names.map}")
    private String[] NAMES;
    @Autowired
    private FileFieldMapper fileFieldMapper;
    @Autowired
    private FlatFileItemReader<FileUploadData> fileItemReader;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private NamedParameterJdbcTemplate jdbcBatchItemWriter;
    @Autowired
    private DataSource dataSource;

    @Value("${prevent.senior.store.new.log.file}")
    private String PATH_IMPORT_FILE;

    @Value("${prevent.senior.store.new.log.success}")
    private String PATH_IMPORT_FILE_SUCCESS;

    @Value("${prevent.senior.store.new.log.error}")
    private String PATH_IMPORT_FILE_ERROR;

    private static final String FILE_MODEL_QUERY = "INSERT INTO file_model (date,ip,request,status,user_agent) VALUES (:date,:ip,:request,:status,:userAgent)";


    @StepScope
    @Bean("partitioner")
    public Partitioner partitioner() {

        MultiResourcePartitioner multiResourcePartitioner = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new FileSystemXmlApplicationContext();
        List<Resource[]> allResourcesPaths = new ArrayList<>();
        final String SLASH = FileUploadConstants.File.Upload.Variables.SLASH;

        try {

            File file = Paths.get(PATH_IMPORT_FILE).toFile().getAbsoluteFile();
            String path[] = file.list();

            Arrays.stream(path).forEach(f -> {
                try {
                    allResourcesPaths.add(resolver.getResources(SLASH + file.getAbsolutePath() + SLASH + f));
                } catch (IOException cause) {
                    log.error("Failed to find the file! " + cause.getMessage());
                }
            });

        } catch (Exception cause) {
            log.error("unable to find files ");
        }


        Resource[] resources = new Resource[allResourcesPaths.size()];
        for (int i = 0; i < allResourcesPaths.size(); i++) {
            resources[i] = Arrays.stream(allResourcesPaths.get(i)).findAny().get();
        }
        multiResourcePartitioner.setResources(resources);
        multiResourcePartitioner.partition(100);
        return multiResourcePartitioner;
    }

    @Bean
    @StepScope
    @Qualifier("fileItemReader")
    @DependsOn("partitioner")
    public FlatFileItemReader<FileUploadData> fileItemReader(@Value("#{stepExecutionContext['fileName']}") String filename) throws MalformedURLException {
        FlatFileItemReaderBuilder files = new FlatFileItemReaderBuilder();

        files.name(FileUploadConstants.File.Upload.Variables.PREVENT_FILE_NAME + new Date())
                .resource(new UrlResource(filename))
                .targetType(FileUploadData.class)
                .delimited()
                .names(NAMES)
                .lineMapper(linemapers())
                .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                    setTargetType(FileUploadData.class);
                }})
                .build();

        return files.build();
    }

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(40);
        taskExecutor.setCorePoolSize(40);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public LineMapper<FileUploadData> linemapers() {
        final DefaultLineMapper<FileUploadData> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(FileUploadConstants.File.Upload.Variables.PIPE);
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(NAMES);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fileFieldMapper);
        return defaultLineMapper;
    }

    @Bean
    public FileItemProcessor processor() {
        return new FileItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<FileUploadData> writer(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<FileUploadData> uploadData = new JdbcBatchItemWriter<>();
        uploadData.setDataSource(dataSource);
        uploadData.setJdbcTemplate(jdbcTemplate);
        uploadData.setSql(FILE_MODEL_QUERY);
        ItemSqlParameterSourceProvider<FileUploadData> sqlParameterSourceProvider = sqlParameterSourceProvider();
        uploadData.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);
        this.dataSource = dataSource;
        this.jdbcBatchItemWriter = jdbcTemplate;
        return uploadData;
    }

    private ItemSqlParameterSourceProvider<FileUploadData> sqlParameterSourceProvider() {
        return new BeanPropertyItemSqlParameterSourceProvider<>();
    }

    @Bean
    public Job importJobFiles(JobNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importJobFiles")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(masterStep())
                .on("COMPLETED")
                .to(moveFiles())
                .end()
                .build();
    }

    @Bean
    public Step moveFiles() {

        MoveFilesWithSuccessStatus moveFiles = new MoveFilesWithSuccessStatus();
        moveFiles.setRESOURCE_SUCCESS_PATH(PATH_IMPORT_FILE_SUCCESS);
        moveFiles.setRESOURCE_UPLOAD_PATH(PATH_IMPORT_FILE);

        try {

            Path path = Paths.get(PATH_IMPORT_FILE_SUCCESS);
            if (!Files.exists(path))
                Files.createDirectories(path);

        } catch (IOException cause) {
            log.warn("problem to create directory " + cause.getMessage());
        }
        return stepBuilderFactory.get("moveFiles").tasklet(moveFiles).build();
    }


    @Bean
    @Qualifier("masterStep")
    public Step masterStep() {
        return stepBuilderFactory.get("masterStep")
                .partitioner("step1", partitioner())
                .step(step1())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step step1() {

        JdbcBatchItemWriter writer = new JdbcBatchItemWriter();
        writer.setDataSource(dataSource);
        writer.setJdbcTemplate(jdbcBatchItemWriter);
        writer.setSql(FILE_MODEL_QUERY);
        writer.afterPropertiesSet();

        ItemSqlParameterSourceProvider<FileUploadData> sqlParameterSourceProvider = sqlParameterSourceProvider();
        writer.setItemSqlParameterSourceProvider(sqlParameterSourceProvider);

        return stepBuilderFactory.get("step1")
                .<FileUploadData, FileModel>chunk(10)
                .processor(processor())
                .writer(writer)
                .reader(fileItemReader)
                .build();
    }

}
