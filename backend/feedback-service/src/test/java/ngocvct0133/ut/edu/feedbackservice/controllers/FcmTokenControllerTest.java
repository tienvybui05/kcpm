package ngocvct0133.ut.edu.feedbackservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ngocvct0133.ut.edu.feedbackservice.dtos.UpdateFcmTokenRequest;
import ngocvct0133.ut.edu.feedbackservice.modules.FcmToken;
import ngocvct0133.ut.edu.feedbackservice.services.IFcmTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FcmTokenController.class)
@DisplayName("FcmTokenController - Whitebox Tests")
class FcmTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IFcmTokenService tokenService;

    private FcmToken sampleToken;

    @BeforeEach
    void setUp() {
        sampleToken = new FcmToken(1L, "fcm-token-123", "TAIXE");
        sampleToken.setId(10L);
    }

    // =========================================================================
    // updateToken
    // =========================================================================
    @Nested
    @DisplayName("POST /api/feedback-service/fcm/update")
    class UpdateToken {

        @Test
        @DisplayName("TC01 - Request body hợp lệ → Cập nhật thành công (200 OK)")
        void updateToken_hopLe() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setMaNguoiDung(1L);
            request.setRole("TAIXE");
            request.setToken("fcm-token-123");

            when(tokenService.saveToken(1L, "TAIXE", "fcm-token-123")).thenReturn(sampleToken);

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.token").value("fcm-token-123"));
        }

        @Test
        @DisplayName("TC02 - Request body null → return 400 Bad Request")
        void updateToken_bodyNull() throws Exception {
            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Request body không được để trống"));
        }

        @Test
        @DisplayName("TC03 - maNguoiDung null → return 400 Bad Request")
        void updateToken_maNguoiDungNull() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setRole("TAIXE");
            request.setToken("fcm-token-123");

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("maNguoiDung không được để trống"));
        }

        @Test
        @DisplayName("TC04 - role null → return 400 Bad Request")
        void updateToken_roleNull() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setMaNguoiDung(1L);
            request.setToken("fcm-token-123");

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("role không được để trống"));
        }

        @Test
        @DisplayName("TC05 - role trống → return 400 Bad Request")
        void updateToken_roleTrong() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setMaNguoiDung(1L);
            request.setRole("  "); // space
            request.setToken("fcm-token-123");

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("role không được để trống"));
        }

        @Test
        @DisplayName("TC06 - token null → return 400 Bad Request")
        void updateToken_tokenNull() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setMaNguoiDung(1L);
            request.setRole("TAIXE");

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("token không được để trống"));
        }

        @Test
        @DisplayName("TC07 - token trống → return 400 Bad Request")
        void updateToken_tokenTrong() throws Exception {
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest();
            request.setMaNguoiDung(1L);
            request.setRole("TAIXE");
            request.setToken(""); // empty

            mockMvc.perform(post("/api/feedback-service/fcm/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("token không được để trống"));
        }
    }

    // =========================================================================
    // getToken
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/fcm/{id}")
    class GetToken {

        @Test
        @DisplayName("TC08 - Tìm thấy token → return 200 OK")
        void getToken_tonTai() throws Exception {
            when(tokenService.getTokenByMaNguoiDung(1L)).thenReturn(sampleToken);

            mockMvc.perform(get("/api/feedback-service/fcm/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maNguoiDung").value(1L))
                    .andExpect(jsonPath("$.token").value("fcm-token-123"));
        }
    }
}
