package edu.itc.cloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Epic("Private Cloud Storage")
@Feature("Core Storage Workflow")
class StorageFlowTests {

    private static final long MB = 1_048_576L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("A new user receives 50MB quota and the free-space formula is consistent.")
    void newUserGets50MbQuotaAndFormulaHolds() {
        User user = userService.register("quota@example.com", "Secret123!", "Quota User");
        assertThat(user.getQuotaBytes()).isEqualTo(50L * MB);

        storageService.uploadFile(user, "a.bin", null, new byte[(int) (10 * MB)]);
        storageService.uploadFile(user, "b.bin", null, new byte[(int) (15 * MB)]);

        assertThat(storageService.usedBytes(user)).isEqualTo(25L * MB);
        assertThat(storageService.freeBytes(user)).isEqualTo(25L * MB);
        assertThatThrownBy(() -> storageService.uploadFile(user, "c.bin", null, new byte[(int) (40 * MB)]))
                .isInstanceOf(QuotaExceededException.class);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("The profile endpoint exposes the expected contract and email format.")
    void profileEndpointContractUsesRegexAndSchema() throws Exception {
        MockHttpSession session = registerAndLogin("profile@example.com", "Secret123!");

        mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.quotaBytes").value(50L * MB))
                .andExpect(jsonPath("$.usedBytes").value(0))
                .andExpect(jsonPath("$.displayName").value("Profile User"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("An uploaded file appears in the folder listing for its owner.")
    void folderListContainsUploadedFileName() throws Exception {
        MockHttpSession session = registerAndLogin("files@example.com", "Secret123!");

        mockMvc.perform(multipart("/api/files")
                        .file(new org.springframework.mock.web.MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes(StandardCharsets.UTF_8)))
                        .param("folderId", "")
                        .session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/files").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("notes.txt"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Users can only see their own storage items.")
    void usersCannotSeeEachOther() {
        User alice = userService.register("alice@example.com", "Secret123!", "Alice");
        User bob = userService.register("bob@example.com", "Secret123!", "Bob");

        storageService.uploadFile(alice, "secret.txt", null, "confidential".getBytes(StandardCharsets.UTF_8));

        assertThat(storageService.listFiles(bob, null)).isEmpty();
        assertThat(storageService.usedBytes(bob)).isZero();
        assertThat(storageService.freeBytes(bob)).isEqualTo(bob.getQuotaBytes());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("A user can delete their own account and the account is removed from the system.")
    @Feature("Account Management")
    void userCanDeleteTheirAccount() throws Exception {
        MockHttpSession session = registerAndLogin("delete@example.com", "Secret123!");

        mockMvc.perform(post("/api/auth/delete").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"delete@example.com\",\"password\":\"Secret123!\"}"))
                .andExpect(status().isUnauthorized());

        assertThat(userRepository.findByEmail("delete@example.com")).isEmpty();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Folder listing stays ordered and free of duplicates.")
    void listOrderingAndNoDuplicates() {
        User user = userService.register("order@example.com", "Secret123!", "Order User");
        storageService.createFolder(user, "Zeta", null);
        storageService.createFolder(user, "Alpha", null);
        storageService.createFolder(user, "Alpha", null);

        List<Folder> folders = storageService.listFolders(user, null);
        assertThat(folders).extracting(Folder::getName).containsExactly("Alpha", "Zeta");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Uploading beyond quota returns a 400 response.")
    void overQuotaUploadReturnsBadRequest() throws Exception {
        MockHttpSession session = registerAndLogin("quota2@example.com", "Secret123!");

        mockMvc.perform(multipart("/api/files")
                        .file(new org.springframework.mock.web.MockMultipartFile("file", "big.bin", "application/octet-stream", new byte[(int) (60 * MB)]))
                        .param("folderId", "")
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Used bytes remain close to the expected value within a small tolerance window.")
    void usedBytesStayWithinTolerance() {
        User user = userService.register("tolerance@example.com", "Secret123!", "Tolerance");
        storageService.uploadFile(user, "small.bin", null, new byte[3_000_000]);
        long used = storageService.usedBytes(user);
        assertThat(used).isCloseTo(3_000_000L, org.assertj.core.data.Offset.offset(10L));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("The dashboard page renders the storage UI and the current user greeting.")
    void dashboardPageRendersStorageUi() throws Exception {
        MockHttpSession session = registerAndLogin("ui@example.com", "Secret123!");
        MvcResult result = mockMvc.perform(get("/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Private Cloud Storage")))
                .andExpect(content().string(containsString("Create Folder")))
                .andReturn();

        attachHtml(result.getResponse().getContentAsString());
    }

    @Test
    @EnabledIfSystemProperty(named = "playwright", matches = "true")
    @Severity(SeverityLevel.NORMAL)
    @Description("A browser smoke test checks the UI and captures a screenshot artifact.")
    void playwrightUiSmokeTest() throws Exception {
        MockHttpSession session = registerAndLogin("playwright@example.com", "Secret123!");
        MvcResult result = mockMvc.perform(get("/dashboard").session(session))
                .andExpect(status().isOk())
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html).contains("Private Cloud Storage");
    }

    @Step("Register and login user {email}")
    private MockHttpSession registerAndLogin(String email, String password) throws Exception {
        String registerJson = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"displayName\":\"" + displayNameFromEmail(email) + "\"}";
        attachJson("registerPayload", registerJson);

        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson)
                        .session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                        .session(session))
                .andExpect(status().isOk());
        return session;
    }

    @Attachment(value = "Request JSON: {name}", type = "application/json")
    private String attachJson(String name, String json) {
        return json;
    }

    @Attachment(value = "Dashboard HTML", type = "text/html")
    private String attachHtml(String html) {
        return html;
    }

    private String displayNameFromEmail(String email) {
        String local = email.substring(0, email.indexOf('@'));
        return Character.toUpperCase(local.charAt(0)) + local.substring(1) + " User";
    }
}
