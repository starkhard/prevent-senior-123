package com.senior.prevent.constants;

public interface FileUploadConstants {

    interface File {
        interface Upload {
            interface Page {
                String redirectOriginalPage = "redirect:/upload";
                String uploadPageForm = "uploadForm";
                String redirectToEditPage = "editfile";
                String formManually = "formManually";
                String errorPage = "errorPage";
            }

            interface Variables {
                String SPACE = " ";
                String TRACE = "-";
                String COMMA = ",";
                String PIPE = "|";
                String PREVENT_FILE_NAME = "prevent-file";
                String FILE_ENDS_WITH = ".log";
                String SLASH = "/";
                String FILE_ENDS_WITH_FILTER = "*.log";
            }
        }
    }
}
