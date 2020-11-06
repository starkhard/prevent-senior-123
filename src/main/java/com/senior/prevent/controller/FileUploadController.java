package com.senior.prevent.controller;

import com.senior.prevent.constants.FileUploadConstants;
import com.senior.prevent.data.FileUploadData;
import com.senior.prevent.data.StatusData;
import com.senior.prevent.exception.FileUploadRequestException;
import com.senior.prevent.service.FileIndexationService;
import com.senior.prevent.service.FileUploadService;
import com.senior.prevent.utils.DateFormatUtils;
import com.senior.prevent.validator.FileUploadValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/upload")
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileUploadValidator fileUploadValidator;
    @Autowired
    private FileIndexationService fileIndexationService;


    @GetMapping
    public ModelAndView initLoad(Model model) {
        model.addAttribute("files", fileIndexationService.searchAllLogs(300));
        return new ModelAndView(FileUploadConstants.File.Upload.Page.uploadPageForm);
    }

    @GetMapping("/{page}")
    public ModelAndView fileUploadPage(Model model,@PathVariable int page) {
        model.addAttribute("files", fileIndexationService.searchAllLogs(page));
        return new ModelAndView(FileUploadConstants.File.Upload.Page.uploadPageForm);
    }

    @PostMapping("/saveFiles")
    public String fileUpload(@RequestParam("file") MultipartFile file) {

        if (file.getSize() <= 0)
            throw new FileUploadRequestException();

        fileUploadService.saveFile(file);
        return FileUploadConstants.File.Upload.Page.redirectOriginalPage;
    }

    @GetMapping("/save-manually")
    public String getFormManually(Model model) {
        model.addAttribute("date", DateFormatUtils.format());
        model.addAttribute("allStatus", StatusData.values());
        model.addAttribute("form", new FileUploadData());
        return FileUploadConstants.File.Upload.Page.formManually;
    }

    @PostMapping("/save-manually")
    public String saveManuallyFile(FileUploadData fileUploadData) {
        fileUploadService.persist(fileUploadData);
        return FileUploadConstants.File.Upload.Page.redirectOriginalPage;
    }

    @GetMapping("/edit/{id}")
    public String editFileUpload(Model model, @PathVariable long id) {
        model.addAttribute("editFile", fileUploadService.findLogById(id));
        return FileUploadConstants.File.Upload.Page.redirectToEditPage;
    }

    @PostMapping("/confirm")
    public String confirmEdit(FileUploadData fileUploadData) {
        fileUploadService.persist(fileUploadData);
        return FileUploadConstants.File.Upload.Page.redirectOriginalPage;
    }

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable String id) {
        fileUploadService.removeFileById(Long.valueOf(id));
        return FileUploadConstants.File.Upload.Page.redirectOriginalPage;
    }
}
