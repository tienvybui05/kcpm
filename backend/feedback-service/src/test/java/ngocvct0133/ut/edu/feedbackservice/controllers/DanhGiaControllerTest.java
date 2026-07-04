package ngocvct0133.ut.edu.feedbackservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ngocvct0133.ut.edu.feedbackservice.dtos.CreateDanhGiaRequest;
import ngocvct0133.ut.edu.feedbackservice.modules.DanhGia;
import ngocvct0133.ut.edu.feedbackservice.services.IDanhGiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DanhGiaController.class)
@DisplayName("DanhGiaController - Whitebox Tests")
class DanhGiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IDanhGiaService danhGiaService;

    private DanhGia sampleDanhGia;

    @BeforeEach
    void setUp() {
        sampleDanhGia = new DanhGia();
        sampleDanhGia.setMaDanhGia(1L);
        sampleDanhGia.setNoiDung("Rất hài lòng");
        sampleDanhGia.setSoSao(5);
        sampleDanhGia.setNgayDanhGia(LocalDate.now());
        sampleDanhGia.setMaTram(100L);
    }

    // =========================================================================
    // layTatCaDanhSach
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/danhgia")
    class LayTatCaDanhSach {

        @Test
        @DisplayName("TC01 - Lấy tất cả đánh giá thành công")
        void layTatCa_thanhCong() throws Exception {
            when(danhGiaService.layTatCaDanhSach()).thenReturn(List.of(sampleDanhGia));

            mockMvc.perform(get("/api/feedback-service/danhgia"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].maDanhGia").value(1L))
                    .andExpect(jsonPath("$[0].soSao").value(5));
        }
    }

    // =========================================================================
    // layDanhGia
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/danhgia/{id}")
    class LayDanhGia {

        @Test
        @DisplayName("TC02 - Tìm thấy đánh giá theo ID → return 200 OK")
        void layDanhGia_tonTai() throws Exception {
            when(danhGiaService.layDanhGia(1L)).thenReturn(sampleDanhGia);

            mockMvc.perform(get("/api/feedback-service/danhgia/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maDanhGia").value(1L))
                    .andExpect(jsonPath("$.soSao").value(5));
        }
    }

    // =========================================================================
    // themDanhGia
    // =========================================================================
    @Nested
    @DisplayName("POST /api/feedback-service/danhgia")
    class ThemDanhGia {

        @Test
        @DisplayName("TC03 - Request hợp lệ → Tạo đánh giá thành công (201 Created)")
        void themDanhGia_hopLe() throws Exception {
            CreateDanhGiaRequest request = new CreateDanhGiaRequest();
            request.setNoiDung("Tốt");
            request.setSoSao(4);
            request.setMaTram(100L);
            request.setNgayDanhGia(LocalDate.now());

            DanhGia created = new DanhGia();
            created.setMaDanhGia(2L);
            created.setNoiDung("Tốt");
            created.setSoSao(4);

            when(danhGiaService.themDanhGia(any(DanhGia.class))).thenReturn(created);

            mockMvc.perform(post("/api/feedback-service/danhgia")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.maDanhGia").value(2L))
                    .andExpect(jsonPath("$.soSao").value(4));
        }

        @Test
        @DisplayName("TC04 - Số sao ngoài khoảng 1-5 (soSao = 6) → Validation fail (400 Bad Request)")
        void themDanhGia_soSaoQuaLon() throws Exception {
            CreateDanhGiaRequest request = new CreateDanhGiaRequest();
            request.setNoiDung("Tốt");
            request.setSoSao(6); // Sai range
            request.setMaTram(100L);

            mockMvc.perform(post("/api/feedback-service/danhgia")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC05 - Số sao nhỏ hơn 1 (soSao = 0) → Validation fail (400 Bad Request)")
        void themDanhGia_soSaoQuaNho() throws Exception {
            CreateDanhGiaRequest request = new CreateDanhGiaRequest();
            request.setNoiDung("Tốt");
            request.setSoSao(0); // Sai range
            request.setMaTram(100L);

            mockMvc.perform(post("/api/feedback-service/danhgia")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================================
    // suaDanhGia
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/feedback-service/danhgia/{id}")
    class SuaDanhGia {

        @Test
        @DisplayName("TC06 - Sửa đánh giá thành công")
        void suaDanhGia_thanhCong() throws Exception {
            DanhGia updated = new DanhGia();
            updated.setMaDanhGia(1L);
            updated.setNoiDung("Rất tốt");
            updated.setSoSao(5);

            when(danhGiaService.suaDanhGia(eq(1L), any(DanhGia.class))).thenReturn(updated);

            mockMvc.perform(put("/api/feedback-service/danhgia/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.noiDung").value("Rất tốt"))
                    .andExpect(jsonPath("$.soSao").value(5));
        }
    }

    // =========================================================================
    // xoaDanhGia
    // =========================================================================
    @Nested
    @DisplayName("DELETE /api/feedback-service/danhgia/{id}")
    class XoaDanhGia {

        @Test
        @DisplayName("TC07 - Đánh giá tồn tại → Xóa thành công (204 No Content)")
        void xoaDanhGia_tonTai() throws Exception {
            when(danhGiaService.xoaDanhGia(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/feedback-service/danhgia/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("TC08 - Đánh giá không tồn tại → return 404 Not Found")
        void xoaDanhGia_khongTonTai() throws Exception {
            when(danhGiaService.xoaDanhGia(99L)).thenReturn(false);

            mockMvc.perform(delete("/api/feedback-service/danhgia/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // tbTheoTram
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/danhgia/tram/{maTram}/trung-binh-sao")
    class TbTheoTram {

        @Test
        @DisplayName("TC09 - Tính trung bình sao theo trạm thành công")
        void tbTheoTram_thanhCong() throws Exception {
            when(danhGiaService.tinhTrungBinhSaoTheoTram(100L)).thenReturn(4.5);

            mockMvc.perform(get("/api/feedback-service/danhgia/tram/100/trung-binh-sao"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("4.5"));
        }
    }

    // =========================================================================
    // tbHeThong
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/danhgia/trung-binh-sao")
    class TbHeThong {

        @Test
        @DisplayName("TC10 - Tính trung bình sao toàn hệ thống thành công")
        void tbHeThong_thanhCong() throws Exception {
            when(danhGiaService.tinhTrungBinhSaoToanHeThong()).thenReturn(4.2);

            mockMvc.perform(get("/api/feedback-service/danhgia/trung-binh-sao"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("4.2"));
        }
    }
}
