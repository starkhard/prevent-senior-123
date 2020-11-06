package com.senior.prevent.repository;

import com.senior.prevent.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileModel,Long> {
}
