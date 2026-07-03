package edu.itc.cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByOwnerAndFolderOrderByNameAsc(User owner, Folder folder);

    List<FileEntity> findByOwnerOrderByNameAsc(User owner);
}
