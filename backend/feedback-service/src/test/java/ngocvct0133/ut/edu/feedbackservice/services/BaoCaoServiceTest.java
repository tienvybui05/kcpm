package ngocvct0133.ut.edu.feedbackservice.services;

import ngocvct0133.ut.edu.feedbackservice.modules.BaoCao;
import ngocvct0133.ut.edu.feedbackservice.repositories.IBaoCaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BaoCaoService - Whitebox Tests")
class BaoCaoServiceTest {

    @Mock
    private IBaoCaoRepository baoCaoRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AdminTokenService adminTokenService;

    @InjectMocks
    private BaoCaoService baoCaoService;

    private BaoCao sampleBaoCao;

    @BeforeEach
    void setUp() {
        sampleBaoCao = new BaoCao();
        sampleBaoCao.setMaBaoCao(1L);
        sampleBaoCao.setTieuDe("Trạm sạc hỏng");
        sampleBaoCao.setNoiDung("Trạm ABC bị hỏng cổng số 3");
        sampleBaoCao.setMaTaiXe(100L);
        sampleBaoCao.setTrangThaiXuLy("Chờ xử lý");
    }

    // =========================================================================
    // themBaoCao
    // =========================================================================
    @Nested
    @DisplayName("themBaoCao()")
    class ThemBaoCao {

        @Test
        @DisplayName("TC01 - Thêm báo cáo thành công + gửi notification admin")
        void themBaoCao_thanhCong_guiNotifyAdmin() {
            // Arrange
            when(baoCaoRepository.save(any(BaoCao.class))).thenReturn(sampleBaoCao);
            when(adminTokenService.layTokenAdmin()).thenReturn(List.of("token1", "token2"));

            // Act
            BaoCao result = baoCaoService.themBaoCao(sampleBaoCao);

            // Assert
            assertNotNull(result);
            assertEquals("Chờ xử lý", result.getTrangThaiXuLy());
            assertEquals(1L, result.getMaBaoCao());

            // Verify nhánh: setTrangThaiXuLy được gọi trước save
            verify(baoCaoRepository).save(sampleBaoCao);
            // Verify nhánh: gửi notification cho từng token admin
            verify(adminTokenService).layTokenAdmin();
            verify(notificationService, times(2)).sendNotification(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("TC02 - Thêm báo cáo thành công nhưng notification fail → vẫn trả kết quả")
        void themBaoCao_thanhCong_notifyFail() {
            // Arrange – nhánh catch Exception khi gửi notification
            when(baoCaoRepository.save(any(BaoCao.class))).thenReturn(sampleBaoCao);
            when(adminTokenService.layTokenAdmin()).thenThrow(new RuntimeException("Connection refused"));

            // Act
            BaoCao result = baoCaoService.themBaoCao(sampleBaoCao);

            // Assert – báo cáo vẫn được lưu thành công dù notification fail
            assertNotNull(result);
            assertEquals(1L, result.getMaBaoCao());
            verify(baoCaoRepository).save(sampleBaoCao);
            // notification không được gọi vì exception xảy ra trước forEach
            verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("TC03 - Thêm báo cáo, admin token rỗng → không gửi notification")
        void themBaoCao_adminTokenRong() {
            // Arrange – adminTokenService trả về list rỗng
            when(baoCaoRepository.save(any(BaoCao.class))).thenReturn(sampleBaoCao);
            when(adminTokenService.layTokenAdmin()).thenReturn(Collections.emptyList());

            // Act
            BaoCao result = baoCaoService.themBaoCao(sampleBaoCao);

            // Assert
            assertNotNull(result);
            verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
        }
    }

    // =========================================================================
    // xoaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("xoaBaoCao()")
    class XoaBaoCao {

        @Test
        @DisplayName("TC04 - Xóa báo cáo tồn tại → return true")
        void xoaBaoCao_tonTai_returnTrue() {
            // Arrange – nhánh existsById = true
            when(baoCaoRepository.existsById(1L)).thenReturn(true);

            // Act
            boolean result = baoCaoService.xoaBaoCao(1L);

            // Assert
            assertTrue(result);
            verify(baoCaoRepository).existsById(1L);
            verify(baoCaoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("TC05 - Xóa báo cáo không tồn tại → return false")
        void xoaBaoCao_khongTonTai_returnFalse() {
            // Arrange – nhánh existsById = false
            when(baoCaoRepository.existsById(99L)).thenReturn(false);

            // Act
            boolean result = baoCaoService.xoaBaoCao(99L);

            // Assert
            assertFalse(result);
            verify(baoCaoRepository).existsById(99L);
            verify(baoCaoRepository, never()).deleteById(anyLong());
        }
    }

    // =========================================================================
    // suaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("suaBaoCao()")
    class SuaBaoCao {

        @Test
        @DisplayName("TC06 - Sửa báo cáo tồn tại → cập nhật 4 field thành công")
        void suaBaoCao_tonTai_capNhatThanhCong() {
            // Arrange – nhánh findById trả Optional có giá trị
            BaoCao existing = new BaoCao();
            existing.setMaBaoCao(1L);
            existing.setNoiDung("Cũ");
            existing.setTieuDe("Tiêu đề cũ");

            BaoCao capNhat = new BaoCao();
            capNhat.setNoiDung("Nội dung mới");
            capNhat.setTieuDe("Tiêu đề mới");
            capNhat.setTrangThaiXuLy("Đang xử lý");
            capNhat.setPhanHoi("Đã nhận");

            when(baoCaoRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(baoCaoRepository.save(any(BaoCao.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            BaoCao result = baoCaoService.suaBaoCao(1L, capNhat);

            // Assert – kiểm tra tất cả 4 field đã cập nhật
            assertEquals("Nội dung mới", result.getNoiDung());
            assertEquals("Tiêu đề mới", result.getTieuDe());
            assertEquals("Đang xử lý", result.getTrangThaiXuLy());
            assertEquals("Đã nhận", result.getPhanHoi());
            verify(baoCaoRepository).findById(1L);
            verify(baoCaoRepository).save(existing);
        }

        @Test
        @DisplayName("TC07 - Sửa báo cáo không tồn tại → throw RuntimeException")
        void suaBaoCao_khongTonTai_throwException() {
            // Arrange – nhánh findById trả Optional.empty
            when(baoCaoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> baoCaoService.suaBaoCao(99L, sampleBaoCao));
            assertTrue(ex.getMessage().contains("Không tìm thấy báo cáo"));
            verify(baoCaoRepository, never()).save(any());
        }
    }

    // =========================================================================
    // layBaoCao
    // =========================================================================
    @Nested
    @DisplayName("layBaoCao()")
    class LayBaoCao {

        @Test
        @DisplayName("TC08 - Lấy báo cáo tồn tại → trả về đối tượng")
        void layBaoCao_tonTai() {
            // Arrange – nhánh Optional có giá trị
            when(baoCaoRepository.findById(1L)).thenReturn(Optional.of(sampleBaoCao));

            // Act
            BaoCao result = baoCaoService.layBaoCao(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getMaBaoCao());
            assertEquals("Trạm sạc hỏng", result.getTieuDe());
        }

        @Test
        @DisplayName("TC09 - Lấy báo cáo không tồn tại → throw RuntimeException")
        void layBaoCao_khongTonTai_throwException() {
            // Arrange – nhánh Optional.empty
            when(baoCaoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> baoCaoService.layBaoCao(99L));
            assertTrue(ex.getMessage().contains("Không tìm thấy báo cáo"));
        }
    }

    // =========================================================================
    // layTatCaBaoCao
    // =========================================================================
    @Nested
    @DisplayName("layTatCaBaoCao()")
    class LayTatCaBaoCao {

        @Test
        @DisplayName("TC10 - Lấy tất cả báo cáo → trả về danh sách")
        void layTatCaBaoCao_traVeDanhSach() {
            // Arrange
            BaoCao bc2 = new BaoCao();
            bc2.setMaBaoCao(2L);
            when(baoCaoRepository.findAll()).thenReturn(List.of(sampleBaoCao, bc2));

            // Act
            List<BaoCao> result = baoCaoService.layTatCaBaoCao();

            // Assert
            assertEquals(2, result.size());
            verify(baoCaoRepository).findAll();
        }

        @Test
        @DisplayName("TC11 - Không có báo cáo → trả về danh sách rỗng")
        void layTatCaBaoCao_rong() {
            when(baoCaoRepository.findAll()).thenReturn(Collections.emptyList());

            List<BaoCao> result = baoCaoService.layTatCaBaoCao();

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================================
    // phanHoiBaoCao
    // =========================================================================
    @Nested
    @DisplayName("phanHoiBaoCao()")
    class PhanHoiBaoCao {

        @Test
        @DisplayName("TC12 - Phản hồi thành công + gửi notification tài xế")
        void phanHoiBaoCao_thanhCong_guiNotifyTaiXe() {
            // Arrange – nhánh findById tìm thấy
            BaoCao bc = new BaoCao();
            bc.setMaBaoCao(1L);
            bc.setTieuDe("Trạm sạc hỏng");
            bc.setMaTaiXe(100L);

            when(baoCaoRepository.findById(1L)).thenReturn(Optional.of(bc));
            when(baoCaoRepository.save(any(BaoCao.class))).thenAnswer(inv -> inv.getArgument(0));
            when(adminTokenService.layTokenTaiXe(100L)).thenReturn(List.of("driverToken1"));

            // Act
            BaoCao result = baoCaoService.phanHoiBaoCao(1L, "Đã xử lý xong");

            // Assert – kiểm tra field phanHoi + trangThaiXuLy đã cập nhật
            assertEquals("Đã xử lý xong", result.getPhanHoi());
            assertEquals("Đã phản hồi", result.getTrangThaiXuLy());

            // Verify gửi notification cho tài xế
            verify(adminTokenService).layTokenTaiXe(100L);
            verify(notificationService).sendNotification(
                    eq("driverToken1"),
                    eq("📩 Phản hồi từ Admin"),
                    contains("Trạm sạc hỏng")
            );
        }

        @Test
        @DisplayName("TC13 - Phản hồi báo cáo không tồn tại → throw RuntimeException")
        void phanHoiBaoCao_khongTonTai_throwException() {
            // Arrange – nhánh findById trả Optional.empty
            when(baoCaoRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> baoCaoService.phanHoiBaoCao(99L, "Test"));
            assertTrue(ex.getMessage().contains("Không tìm thấy báo cáo"));
            verify(baoCaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC14 - Phản hồi thành công nhưng notification fail → vẫn trả kết quả")
        void phanHoiBaoCao_notifyFail_vanTraKetQua() {
            // Arrange – nhánh catch Exception khi gửi notification tài xế
            BaoCao bc = new BaoCao();
            bc.setMaBaoCao(1L);
            bc.setTieuDe("Báo cáo test");
            bc.setMaTaiXe(100L);

            when(baoCaoRepository.findById(1L)).thenReturn(Optional.of(bc));
            when(baoCaoRepository.save(any(BaoCao.class))).thenAnswer(inv -> inv.getArgument(0));
            when(adminTokenService.layTokenTaiXe(100L)).thenThrow(new RuntimeException("Network error"));

            // Act
            BaoCao result = baoCaoService.phanHoiBaoCao(1L, "Phản hồi");

            // Assert – dữ liệu vẫn được cập nhật
            assertEquals("Phản hồi", result.getPhanHoi());
            assertEquals("Đã phản hồi", result.getTrangThaiXuLy());
            // Notification không gửi được nhưng không ảnh hưởng kết quả
            verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("TC15 - Phản hồi, tài xế token rỗng → không gửi notification")
        void phanHoiBaoCao_tokenTaiXeRong() {
            // Arrange
            BaoCao bc = new BaoCao();
            bc.setMaBaoCao(1L);
            bc.setTieuDe("Test");
            bc.setMaTaiXe(100L);

            when(baoCaoRepository.findById(1L)).thenReturn(Optional.of(bc));
            when(baoCaoRepository.save(any(BaoCao.class))).thenAnswer(inv -> inv.getArgument(0));
            when(adminTokenService.layTokenTaiXe(100L)).thenReturn(Collections.emptyList());

            // Act
            BaoCao result = baoCaoService.phanHoiBaoCao(1L, "OK");

            // Assert
            assertEquals("Đã phản hồi", result.getTrangThaiXuLy());
            verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
        }
    }
}
