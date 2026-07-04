package ngocvct0133.ut.edu.feedbackservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ngocvct0133.ut.edu.feedbackservice.dtos.CreateBaoCaoRequest;
import ngocvct0133.ut.edu.feedbackservice.dtos.UpdateBaoCaoPhanHoiRequest;
import ngocvct0133.ut.edu.feedbackservice.modules.BaoCao;
import ngocvct0133.ut.edu.feedbackservice.services.IBaoCaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BaoCaoController.class)
@DisplayName("BaoCaoController - Whitebox Tests")
class BaoCaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IBaoCaoService baoCaoService;

    private BaoCao sampleBaoCao;

    @BeforeEach
    void setUp() {
        sampleBaoCao = new BaoCao();
        sampleBaoCao.setMaBaoCao(1L);
        sampleBaoCao.setTieuDe("Hỏng cáp sạc");
        sampleBaoCao.setNoiDung("Cáp sạc trạm sạc số 2 bị đứt");
        sampleBaoCao.setLoaiPhanHoi("HỎNG_HÓC");
        sampleBaoCao.setMaTaiXe(10L);
        sampleBaoCao.setDestinationType("ADMIN");
    }

    // =========================================================================
    // layTatCaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/baocao")
    class LayTatCaBaoCao {

        @Test
        @DisplayName("TC01 - Lấy tất cả báo cáo thành công")
        void layTatCaBaoCao_thanhCong() throws Exception {
            when(baoCaoService.layTatCaBaoCao()).thenReturn(List.of(sampleBaoCao));

            mockMvc.perform(get("/api/feedback-service/baocao"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].maBaoCao").value(1L))
                    .andExpect(jsonPath("$[0].tieuDe").value("Hỏng cáp sạc"));

            verify(baoCaoService, times(1)).layTatCaBaoCao();
        }
    }

    // =========================================================================
    // layBaoCao
    // =========================================================================
    @Nested
    @DisplayName("GET /api/feedback-service/baocao/{id}")
    class LayBaoCao {

        @Test
        @DisplayName("TC02 - Tìm thấy báo cáo theo ID → return 200 OK")
        void layBaoCao_tonTai() throws Exception {
            when(baoCaoService.layBaoCao(1L)).thenReturn(sampleBaoCao);

            mockMvc.perform(get("/api/feedback-service/baocao/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maBaoCao").value(1L))
                    .andExpect(jsonPath("$.tieuDe").value("Hỏng cáp sạc"));
        }

        @Test
        @DisplayName("TC03 - Không tìm thấy báo cáo → return 404 Not Found")
        void layBaoCao_khongTonTai() throws Exception {
            when(baoCaoService.layBaoCao(99L)).thenThrow(new RuntimeException("Báo cáo không tồn tại"));

            mockMvc.perform(get("/api/feedback-service/baocao/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // themBaoCao
    // =========================================================================
    @Nested
    @DisplayName("POST /api/feedback-service/baocao")
    class ThemBaoCao {

        @Test
        @DisplayName("TC04 - Request hợp lệ → Tạo mới thành công (201 Created)")
        void themBaoCao_hopLe() throws Exception {
            CreateBaoCaoRequest request = new CreateBaoCaoRequest();
            request.setTieuDe("Hỏng thiết bị");
            request.setNoiDung("Cần sửa chữa gấp");
            request.setMaTaiXe(15L);
            request.setDestinationType("ADMIN");

            BaoCao created = new BaoCao();
            created.setMaBaoCao(2L);
            created.setTieuDe("Hỏng thiết bị");
            created.setNoiDung("Cần sửa chữa gấp");
            created.setMaTaiXe(15L);
            created.setDestinationType("ADMIN");

            when(baoCaoService.themBaoCao(any(BaoCao.class))).thenReturn(created);

            mockMvc.perform(post("/api/feedback-service/baocao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.maBaoCao").value(2L))
                    .andExpect(jsonPath("$.tieuDe").value("Hỏng thiết bị"));
        }

        @Test
        @DisplayName("TC05 - Thiếu tieuDe → Validation fail (400 Bad Request)")
        void themBaoCao_thieuTieuDe() throws Exception {
            CreateBaoCaoRequest request = new CreateBaoCaoRequest();
            request.setNoiDung("Nội dung");
            request.setMaTaiXe(15L);
            request.setDestinationType("ADMIN");

            mockMvc.perform(post("/api/feedback-service/baocao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC06 - destinationType không hợp lệ → Validation fail (400 Bad Request)")
        void themBaoCao_destinationTypeSai() throws Exception {
            CreateBaoCaoRequest request = new CreateBaoCaoRequest();
            request.setTieuDe("Tiêu đề");
            request.setNoiDung("Nội dung");
            request.setMaTaiXe(15L);
            request.setDestinationType("DRIVER"); // Chỉ cho phép ADMIN hoặc TRAM

            mockMvc.perform(post("/api/feedback-service/baocao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================================
    // suaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/feedback-service/baocao/{id}")
    class SuaBaoCao {

        @Test
        @DisplayName("TC07 - Sửa báo cáo thành công")
        void suaBaoCao_thanhCong() throws Exception {
            BaoCao updated = new BaoCao();
            updated.setMaBaoCao(1L);
            updated.setTieuDe("Sửa tiêu đề");
            updated.setNoiDung("Sửa nội dung");

            when(baoCaoService.suaBaoCao(eq(1L), any(BaoCao.class))).thenReturn(updated);

            mockMvc.perform(put("/api/feedback-service/baocao/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tieuDe").value("Sửa tiêu đề"))
                    .andExpect(jsonPath("$.noiDung").value("Sửa nội dung"));
        }
    }

    // =========================================================================
    // xoaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("DELETE /api/feedback-service/baocao/{id}")
    class XoaBaoCao {

        @Test
        @DisplayName("TC08 - Báo cáo tồn tại → xóa thành công (204 No Content)")
        void xoaBaoCao_tonTai() throws Exception {
            when(baoCaoService.xoaBaoCao(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/feedback-service/baocao/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("TC09 - Báo cáo không tồn tại → return 404 Not Found")
        void xoaBaoCao_khongTonTai() throws Exception {
            when(baoCaoService.xoaBaoCao(99L)).thenReturn(false);

            mockMvc.perform(delete("/api/feedback-service/baocao/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // phanHoiBaoCao
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/feedback-service/baocao/{id}/phanhoi")
    class PhanHoiBaoCao {

        @Test
        @DisplayName("TC10 - Phản hồi hợp lệ → cập nhật thành công (200 OK)")
        void phanHoiBaoCao_hopLe() throws Exception {
            UpdateBaoCaoPhanHoiRequest request = new UpdateBaoCaoPhanHoiRequest();
            request.setPhanHoi("Đã tiếp nhận và sửa chữa cáp sạc");

            BaoCao updated = new BaoCao();
            updated.setMaBaoCao(1L);
            updated.setPhanHoi("Đã tiếp nhận và sửa chữa cáp sạc");
            updated.setTrangThaiXuLy("Đã phản hồi");

            when(baoCaoService.phanHoiBaoCao(1L, "Đã tiếp nhận và sửa chữa cáp sạc")).thenReturn(updated);

            mockMvc.perform(put("/api/feedback-service/baocao/1/phanhoi")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phanHoi").value("Đã tiếp nhận và sửa chữa cáp sạc"))
                    .andExpect(jsonPath("$.trangThaiXuLy").value("Đã phản hồi"));
        }

        @Test
        @DisplayName("TC11 - Gửi phản hồi trống → Validation fail (400 Bad Request)")
        void phanHoiBaoCao_trong() throws Exception {
            UpdateBaoCaoPhanHoiRequest request = new UpdateBaoCaoPhanHoiRequest();
            request.setPhanHoi(""); // Trống

            mockMvc.perform(put("/api/feedback-service/baocao/1/phanhoi")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
