package ngocvct0133.ut.edu.feedbackservice.services;

import ngocvct0133.ut.edu.feedbackservice.modules.DanhGia;
import ngocvct0133.ut.edu.feedbackservice.repositories.IDanhGiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DanhGiaService - Whitebox Tests")
class DanhGiaServiceTest {

    @Mock
    private IDanhGiaRepository danhGiaRepository;

    @InjectMocks
    private DanhGiaService danhGiaService;

    private DanhGia sampleDanhGia;

    @BeforeEach
    void setUp() {
        sampleDanhGia = new DanhGia();
        sampleDanhGia.setMaDanhGia(1L);
        sampleDanhGia.setNoiDung("Trạm sạc tốt");
        sampleDanhGia.setSoSao(5);
        sampleDanhGia.setNgayDanhGia(LocalDate.now());
        sampleDanhGia.setMaTram(10L);
    }

    // =========================================================================
    // themDanhGia
    // =========================================================================
    @Nested
    @DisplayName("themDanhGia()")
    class ThemDanhGia {

        @Test
        @DisplayName("TC01 - Thêm đánh giá thành công → trả về đối tượng đã lưu")
        void themDanhGia_thanhCong() {
            // Arrange
            when(danhGiaRepository.save(any(DanhGia.class))).thenReturn(sampleDanhGia);

            // Act
            DanhGia result = danhGiaService.themDanhGia(sampleDanhGia);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getMaDanhGia());
            assertEquals("Trạm sạc tốt", result.getNoiDung());
            assertEquals(5, result.getSoSao());
            verify(danhGiaRepository, times(1)).save(sampleDanhGia);
        }
    }

    // =========================================================================
    // xoaDanhGia
    // =========================================================================
    @Nested
    @DisplayName("xoaDanhGia()")
    class XoaDanhGia {

        @Test
        @DisplayName("TC02 - Xóa đánh giá tồn tại → return true")
        void xoaDanhGia_tonTai_returnTrue() {
            // Arrange – nhánh existsById = true
            when(danhGiaRepository.existsById(1L)).thenReturn(true);

            // Act
            boolean result = danhGiaService.xoaDanhGia(1L);

            // Assert
            assertTrue(result);
            verify(danhGiaRepository, times(1)).existsById(1L);
            verify(danhGiaRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("TC03 - Xóa đánh giá không tồn tại → return false")
        void xoaDanhGia_khongTonTai_returnFalse() {
            // Arrange – nhánh existsById = false
            when(danhGiaRepository.existsById(99L)).thenReturn(false);

            // Act
            boolean result = danhGiaService.xoaDanhGia(99L);

            // Assert
            assertFalse(result);
            verify(danhGiaRepository, times(1)).existsById(99L);
            verify(danhGiaRepository, never()).deleteById(anyLong());
        }
    }

    // =========================================================================
    // suaDanhGia
    // =========================================================================
    @Nested
    @DisplayName("suaDanhGia()")
    class SuaDanhGia {

        @Test
        @DisplayName("TC04 - Sửa đánh giá tồn tại → cập nhật thành công")
        void suaDanhGia_tonTai_capNhatThanhCong() {
            // Arrange – nhánh findById trả Optional có giá trị
            DanhGia existing = new DanhGia();
            existing.setMaDanhGia(1L);
            existing.setNoiDung("Cũ");
            existing.setSoSao(3);

            DanhGia updated = new DanhGia();
            updated.setNoiDung("Mới");
            updated.setSoSao(4);
            updated.setNgayDanhGia(LocalDate.of(2026, 7, 1));
            updated.setMaTram(20L);

            when(danhGiaRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(danhGiaRepository.save(any(DanhGia.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            DanhGia result = danhGiaService.suaDanhGia(1L, updated);

            // Assert – kiểm tra tất cả field đã được cập nhật
            assertEquals("Mới", result.getNoiDung());
            assertEquals(4, result.getSoSao());
            assertEquals(LocalDate.of(2026, 7, 1), result.getNgayDanhGia());
            assertEquals(20L, result.getMaTram());
            verify(danhGiaRepository).findById(1L);
            verify(danhGiaRepository).save(existing);
        }

        @Test
        @DisplayName("TC05 - Sửa đánh giá không tồn tại → throw RuntimeException")
        void suaDanhGia_khongTonTai_throwException() {
            // Arrange – nhánh findById trả Optional.empty
            when(danhGiaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> danhGiaService.suaDanhGia(99L, sampleDanhGia));
            assertEquals("Không tìm thấy đánh giá", ex.getMessage());
            verify(danhGiaRepository, never()).save(any());
        }
    }

    // =========================================================================
    // layDanhGia
    // =========================================================================
    @Nested
    @DisplayName("layDanhGia()")
    class LayDanhGia {

        @Test
        @DisplayName("TC06 - Lấy đánh giá tồn tại → trả về đối tượng")
        void layDanhGia_tonTai() {
            // Arrange – nhánh Optional có giá trị
            when(danhGiaRepository.findById(1L)).thenReturn(Optional.of(sampleDanhGia));

            // Act
            DanhGia result = danhGiaService.layDanhGia(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getMaDanhGia());
            verify(danhGiaRepository).findById(1L);
        }

        @Test
        @DisplayName("TC07 - Lấy đánh giá không tồn tại → throw RuntimeException")
        void layDanhGia_khongTonTai_throwException() {
            // Arrange – nhánh Optional.empty
            when(danhGiaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> danhGiaService.layDanhGia(99L));
            assertEquals("Không tìm thấy đánh giá", ex.getMessage());
        }
    }

    // =========================================================================
    // layTatCaDanhSach
    // =========================================================================
    @Nested
    @DisplayName("layTatCaDanhSach()")
    class LayTatCaDanhSach {

        @Test
        @DisplayName("TC08 - Lấy tất cả đánh giá → trả về danh sách")
        void layTatCaDanhSach_traVeDanhSach() {
            // Arrange
            DanhGia dg2 = new DanhGia();
            dg2.setMaDanhGia(2L);
            when(danhGiaRepository.findAll()).thenReturn(List.of(sampleDanhGia, dg2));

            // Act
            List<DanhGia> result = danhGiaService.layTatCaDanhSach();

            // Assert
            assertEquals(2, result.size());
            verify(danhGiaRepository).findAll();
        }

        @Test
        @DisplayName("TC09 - Không có đánh giá → trả về danh sách rỗng")
        void layTatCaDanhSach_rong() {
            // Arrange
            when(danhGiaRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<DanhGia> result = danhGiaService.layTatCaDanhSach();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    // =========================================================================
    // tinhTrungBinhSaoTheoTram
    // =========================================================================
    @Nested
    @DisplayName("tinhTrungBinhSaoTheoTram()")
    class TinhTrungBinhSaoTheoTram {

        @Test
        @DisplayName("TC10 - Trạm có đánh giá → trả về trung bình sao")
        void tinhTrungBinh_coDuLieu() {
            // Arrange – nhánh list không rỗng
            DanhGia dg1 = new DanhGia();
            dg1.setSoSao(4);
            DanhGia dg2 = new DanhGia();
            dg2.setSoSao(2);
            DanhGia dg3 = new DanhGia();
            dg3.setSoSao(3);

            when(danhGiaRepository.findByMaTram(10L)).thenReturn(List.of(dg1, dg2, dg3));

            // Act
            double result = danhGiaService.tinhTrungBinhSaoTheoTram(10L);

            // Assert – (4+2+3)/3 = 3.0
            assertEquals(3.0, result, 0.001);
            verify(danhGiaRepository).findByMaTram(10L);
        }

        @Test
        @DisplayName("TC11 - Trạm không có đánh giá → return 0")
        void tinhTrungBinh_khongCoDuLieu_return0() {
            // Arrange – nhánh list rỗng
            when(danhGiaRepository.findByMaTram(99L)).thenReturn(Collections.emptyList());

            // Act
            double result = danhGiaService.tinhTrungBinhSaoTheoTram(99L);

            // Assert
            assertEquals(0.0, result, 0.001);
        }
    }

    // =========================================================================
    // tinhTrungBinhSaoToanHeThong
    // =========================================================================
    @Nested
    @DisplayName("tinhTrungBinhSaoToanHeThong()")
    class TinhTrungBinhSaoToanHeThong {

        @Test
        @DisplayName("TC12 - Hệ thống có đánh giá → trả về trung bình sao")
        void tinhTrungBinhToanHeThong_coDuLieu() {
            // Arrange – nhánh list không rỗng
            DanhGia dg1 = new DanhGia();
            dg1.setSoSao(5);
            DanhGia dg2 = new DanhGia();
            dg2.setSoSao(3);

            when(danhGiaRepository.findAll()).thenReturn(List.of(dg1, dg2));

            // Act
            double result = danhGiaService.tinhTrungBinhSaoToanHeThong();

            // Assert – (5+3)/2 = 4.0
            assertEquals(4.0, result, 0.001);
        }

        @Test
        @DisplayName("TC13 - Hệ thống không có đánh giá → return 0")
        void tinhTrungBinhToanHeThong_khongCoDuLieu() {
            // Arrange – nhánh list rỗng
            when(danhGiaRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            double result = danhGiaService.tinhTrungBinhSaoToanHeThong();

            // Assert
            assertEquals(0.0, result, 0.001);
        }
    }
}
