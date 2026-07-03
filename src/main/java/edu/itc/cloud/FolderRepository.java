package edu.itc.cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwnerAndParentFolderOrderByNameAsc(User owner, Folder parentFolder);

    Optional<Folder> findByOwnerAndParentFolderAndName(User owner, Folder parentFolder, String name);
}
