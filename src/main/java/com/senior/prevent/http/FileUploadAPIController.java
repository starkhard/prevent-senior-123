package com.senior.prevent.http;

import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.exception.FileUploadRequestException;
import com.senior.prevent.service.FileIndexationService;
import com.senior.prevent.service.FileUploadService;
import com.senior.prevent.solr.LogsIndex;
import com.senior.prevent.utils.ResponseError;
import com.senior.prevent.validator.FileUploadDataValidator;
import com.senior.prevent.validator.FileUploadValidator;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*") //only for TEST
@RequestMapping("prevent/v1")
@Slf4j
@Api(tags = "Prevent Senior Logs Management API ")
public class FileUploadAPIController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileUploadValidator fileUploadValidator;
    @Autowired
    private FileUploadDataValidator fileUploadDataValidator;

    @Autowired
    private FileIndexationService indexationService;


    @GetMapping(value = "/find-logById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This api will find a specific log by ID registration", response = FileUploadData.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Logs not found")
    })
    @ApiImplicitParam(name = "id", value = "Is required to get information about a specific LOG ", required = true)
    public ResponseEntity getLogsById(@PathVariable String id) {

        HttpStatus status = null;
        List<ResponseError> errors = new ArrayList<>();
        try {
            FileUploadData data = fileUploadService.findLogById(Long.valueOf(id));
            return ResponseEntity.ok(data);
        } catch (Exception cause) {
            log.error("Log not found by ID " + cause.getMessage());
            errors.add(new ResponseError("404", "Log not found"));
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping(value = "/save-log", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This endpoint will create  log file in database , " +
            "so in this api you will sent only an file per time , " +
            "in case you try  no sent a file " +
            "a exception will be generated ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    public ResponseEntity fileUpload(@RequestPart("file") MultipartFile file) {

        if (file.getSize() <= 0)
            return ResponseEntity.badRequest().body(new ResponseError("400", "File cannot be empty or null"));
        fileUploadService.saveFile(file);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/save-logs", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This endpoint will create  many log files in database , so in this api you will sent many files, " +
            "in case you try  no sent a file a exception will be generated ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    public ResponseEntity fileUpload(@Validated @RequestPart(value = "file") MultipartFile file[]) {
        BindingResult bindingResult = new BeanPropertyBindingResult(file, "file");
        fileUploadValidator.validate(file, bindingResult);
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors()
                    .stream()
                    .map(e -> new ResponseError("400", "Empty Files for processing "))
                    .collect(Collectors.toList()));

        fileUploadService.saveFile(file);
        return ResponseEntity.ok().build();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "This API  have the responsibility to create logs manually, " +
            "so it will be necessary to fill out a form and send us the request according to this documentation, " +
            "the DATE of insertion will be generated internally, dispensing the request for sending.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @PostMapping("/save-logs-manually")
    public ResponseEntity saveManuallyFile(@RequestBody FileUploadData fileUploadData) {

        if (fileUploadData == null)
            ResponseEntity.badRequest().body(new FileUploadRequestException("Bad Request"));

        BindingResult bindingResult = new BeanPropertyBindingResult(fileUploadData, "fileUploadData");
        fileUploadDataValidator.validate(fileUploadData, bindingResult);

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(bindingResult.getFieldErrors().stream()
                    .map(e -> new ResponseError(e.getField(), e.getCode()))
                    .collect(Collectors.toList()));

        fileUploadService.persist(fileUploadData);

        return ResponseEntity.ok().build();
    }


    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "You will have the responsibility to update a log by the ID sent in " +
            "the request along with the other information, " +
            "the date will not be updated, as it is something managed internally ", response = FileUploadData.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @PutMapping("/edit-log")
    public ResponseEntity editFileUpload(@RequestBody FileUploadData request) {
        List<ResponseError> errors = new ArrayList<>();
        HttpStatus status = null;
        try {
            BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
            fileUploadDataValidator.validate(request, bindingResult);

            if (bindingResult.hasErrors())
                return  ResponseEntity.badRequest().body(bindingResult.getFieldErrors().stream().map(error ->
                        new ResponseError(error.getField(), error.getCode()))
                        .collect(Collectors.toList()));

            fileUploadService.edit(request);
            return new ResponseEntity(request, HttpStatus.OK);

        } catch (Exception cause) {
            status = HttpStatus.BAD_REQUEST;
            log.error("Error to edit the file by ID" + status);
            errors.add(new ResponseError("400", "Error to find a log file wih this ID"));
        }
        return new ResponseEntity(errors, status);
    }


    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This endpoint will delete a specific log file in your database with a ID parameter (PK NUMBER )  ")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @DeleteMapping("/delete-log/{id}")
    @CacheEvict(value = "file_model", key = "#id")
    public ResponseEntity deleteLog(@PathVariable String id) {

        HttpStatus status = null;
        List<ResponseError> errors = new ArrayList<>();
        try {
            fileUploadService.removeFileById(Long.valueOf(id));
            return ResponseEntity.ok().body("Log file deleted ");
        } catch (Exception cause) {
            status = HttpStatus.BAD_REQUEST;
            log.info("Error to delete a log file  " + status);
            errors.add(new ResponseError("400", "Error to find a log file wih this ID"));
        }
        return ResponseEntity.badRequest().body(errors);
    }


    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This endpoint will made a search in Solr  with the parameters selected in your front end  side  ")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = " the expected date format is:  yyyy-MM-dd HH:mm:ss.SSS"),
            @ApiImplicitParam(name = "id", value = "It is not necessary to fill in this field"),
            @ApiImplicitParam(name = "ip", value = "Respect the IP mask formats"),
            @ApiImplicitParam(name = "status", value = "Send the numeric codes according to the HttpStatus Enumaration"),
            @ApiImplicitParam(name = "userAgent", value = "Send the correct agents "),
            @ApiImplicitParam(name = "request", value = "Send the correct request ")
    })
    @GetMapping(value = "/search-logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchByLogsByParameters(@RequestParam(required = false) String ip,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String userAgent,
                                                   @RequestParam(required = false) String request) {

        if (StringUtils.isNotBlank(ip))
            return ResponseEntity.ok(indexationService.searchByParameterIp(ip));

        if (StringUtils.isNotBlank(status))
            return ResponseEntity.ok(indexationService.searchByParameterStatus(status));

        if (StringUtils.isNotBlank(userAgent))
            return ResponseEntity.ok(indexationService.searchByParameterAgent(userAgent));

        if (StringUtils.isNotBlank(request))
            return ResponseEntity.ok(indexationService.searchByParameterRequest(request));

        return ResponseEntity.notFound().build();
    }


    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This endpoint will made a search in Solr  to retrieve all logs indexed with pagination condition Max Size (50) ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @GetMapping(value = "/search-all-logs/{page}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchAllLogs(@PathVariable int page) {

        List<ResponseError> errors = new ArrayList<>();

        try {
            List<LogsIndex> allLogs = indexationService.searchAllLogs(page);
            if (CollectionUtils.isNotEmpty(allLogs))
                return ResponseEntity.ok().body(allLogs);
        } catch (Exception cause) {
            log.info("Impossible to retrieve results in solr , verify if this indexation are running !");
            errors.add(new ResponseError(HttpStatus.NOT_FOUND.toString(), "Documents not Indexed to retrieve results"));
        }
        return new ResponseEntity(errors, HttpStatus.NOT_FOUND);
    }

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
