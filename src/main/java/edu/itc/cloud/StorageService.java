package edu.itc.cloud;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StorageService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

    public StorageService(UserRepository userRepository, FolderRepository folderRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    @Transactional
    public User register(String email, String password, String displayName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User(email, password, displayName);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public long usedBytes(User user) {
        return fileRepository.findByOwnerOrderByNameAsc(user).stream().mapToLong(FileEntity::getSizeBytes).sum();
    }

    @Transactional(readOnly = true)
    public long freeBytes(User user) {
        return user.getQuotaBytes() - usedBytes(user);
    }

    @Transactional
    public FileEntity uploadFile(User user, String name, Folder folder, byte[] content) {
        if (content.length > freeBytes(user)) {
            throw new QuotaExceededException("Quota exceeded");
        }
        FileEntity file = new FileEntity(user, folder, name, content);
        return fileRepository.save(file);
    }

    @Transactional(readOnly = true)
    public List<FileEntity> listFiles(User user, Folder folder) {
        return fileRepository.findByOwnerAndFolderOrderByNameAsc(user, folder);
    }

    @Transactional(readOnly = true)
    public List<Folder> listFolders(User user, Folder folder) {
        List<Folder> folders = folderRepository.findByOwnerAndParentFolderOrderByNameAsc(user, folder);
        folders.sort(Comparator.comparing(Folder::getName));
        return folders;
    }

    @Transactional
    public Folder createFolder(User user, String name, Folder parentFolder) {
        Optional<Folder> existing = folderRepository.findByOwnerAndParentFolderAndName(user, parentFolder, name);
        if (existing.isPresent()) {
            return existing.get();
        }
        return folderRepository.save(new Folder(user, name, parentFolder));
    }

    @Transactional(readOnly = true)
    public Folder findFolderByIdAndOwner(Long id, User owner) {
        return folderRepository.findById(id)
                .filter(folder -> folder.getOwner().equals(owner))
                .orElse(null);
    }

    @Transactional
    public User updateProfile(User user, String displayName, String password) {
        if (displayName != null && !displayName.isBlank()) {
            user.setDisplayName(displayName);
        }
        if (password != null && !password.isBlank()) {
            user.setPasswordHash(password);
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public FileEntity getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteUser(User user) {
        fileRepository.deleteAll(fileRepository.findByOwnerOrderByNameAsc(user));
        folderRepository.deleteAll(folderRepository.findByOwnerAndParentFolderOrderByNameAsc(user, null));
        userRepository.delete(user);
    }
}
