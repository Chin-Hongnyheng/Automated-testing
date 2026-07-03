package edu.itc.cloud;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);

    private final UserRepository userRepository;
    private final StorageService storageService;
    private final UserService userService;

    public StorageController(UserRepository userRepository, StorageService storageService, UserService userService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = currentUser();
        model.addAttribute("user", user);
        model.addAttribute("quotaMb", user.getQuotaBytes() / (1024 * 1024));
        model.addAttribute("usedMb", storageService.usedBytes(user) / (1024 * 1024));
        model.addAttribute("folders", storageService.listFolders(user, null));
        model.addAttribute("files", storageService.listFiles(user, null));
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerForm(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String displayName,
                               HttpServletRequest request) {
        User user = userService.register(email, password, displayName);
        authenticateUser(request, user);
        return "redirect:/dashboard";
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerApi(@RequestBody Map<String, String> body, HttpServletRequest request) {
        User user = userService.register(body.get("email"), body.get("password"), body.getOrDefault("displayName", "User"));
        authenticateUser(request, user);
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("displayName", user.getDisplayName());
        response.put("quotaBytes", user.getQuotaBytes());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginApi(@RequestBody Map<String, String> body, HttpServletRequest request) {
        return userRepository.findByEmail(body.get("email"))
                .map(user -> {
                    authenticateUser(request, user);
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("email", user.getEmail());
                    response.put("displayName", user.getDisplayName());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials")));
    }

    @PostMapping("/api/auth/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAccount(HttpServletRequest request) {
        User user = currentUser();
        storageService.deleteUser(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @GetMapping("/api/me")
    @ResponseBody
    public Map<String, Object> profile() {
        User user = currentUser();
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("displayName", user.getDisplayName());
        response.put("quotaBytes", user.getQuotaBytes());
        response.put("usedBytes", storageService.usedBytes(user));
        response.put("folders", List.of());
        return response;
    }

    @PostMapping("/folders")
    public String createFolderForm(@RequestParam String name) {
        User user = currentUser();
        storageService.createFolder(user, name, null);
        return "redirect:/dashboard";
    }

    @PostMapping("/api/folders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createFolder(@RequestParam String name) {
        User user = currentUser();
        Folder folder = storageService.createFolder(user, name, null);
        Map<String, Object> response = new HashMap<>();
        response.put("id", folder.getId());
        response.put("name", folder.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public String uploadFileForm(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "folderId", required = false) String folderId) {
        User user = currentUser();
        Folder folder = null;
        if (folderId != null && !folderId.isBlank()) {
            try {
                folder = storageService.findFolderByIdAndOwner(Long.valueOf(folderId), user);
            } catch (NumberFormatException ex) {
                folder = null;
            }
        }
        try {
            storageService.uploadFile(user, file.getOriginalFilename(), folder, file.getBytes());
        } catch (QuotaExceededException ex) {
            return "redirect:/dashboard";
        } catch (Exception ex) {
            return "redirect:/dashboard";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/delete-account")
    public String deleteAccountForm(HttpServletRequest request) {
        User user = currentUser();
        storageService.deleteUser(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @PostMapping("/api/files")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "folderId", required = false) String folderId) {
        User user = currentUser();
        log.info("API upload requested by {} file={} folderIdParam={}", user.getEmail(), file.getOriginalFilename(), folderId);
        Folder folder = null;
        if (folderId != null && !folderId.isBlank()) {
            try {
                folder = storageService.findFolderByIdAndOwner(Long.valueOf(folderId), user);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid folder id"));
            }
            if (folder == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid folder"));
            }
        }
        try {
            byte[] content = file.getBytes();
            FileEntity entity = storageService.uploadFile(user, file.getOriginalFilename(), folder, content);
            log.info("Saved file id={} folderId={}", entity.getId(), entity.getFolder() == null ? null : entity.getFolder().getId());
            Map<String, Object> response = new HashMap<>();
            response.put("id", entity.getId());
            response.put("name", entity.getName());
            response.put("sizeBytes", entity.getSizeBytes());
            return ResponseEntity.ok(response);
        } catch (QuotaExceededException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/api/files")
    @ResponseBody
    public List<Map<String, Object>> listFilesApi(@RequestParam(value = "folderId", required = false) Long folderId) {
        User user = currentUser();
        Folder folder = null;
        if (folderId != null) {
            folder = storageService.findFolderByIdAndOwner(folderId, user);
            if (folder == null) {
                return List.of();
            }
        }
        return storageService.listFiles(user, folder).stream().map(file -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", file.getId());
            item.put("name", file.getName());
            item.put("sizeBytes", file.getSizeBytes());
            item.put("folderId", file.getFolder() == null ? null : file.getFolder().getId());
            return item;
        }).toList();
    }

    @GetMapping("/api/files/{id}/download")
    @ResponseBody
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        FileEntity fileEntity = storageService.getFileById(id);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"")
                .body(fileEntity.getContent());
    }

    private void authenticateUser(HttpServletRequest request, User user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}
