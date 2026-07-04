package datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.services;

import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.modules.GoiDichVu;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.repositories.IGoiDichVuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoiDichVuServiceTest {

    @Mock
    private IGoiDichVuRepository goiDichVuRepository;

    @Mock
    private ILichSuDangKyGoiService lichSuDangKyGoiService;

    @InjectMocks
    private GoiDichVuService goiDichVuService;

    // ========== TCS: Thêm gói dịch vụ ==========

    @Test
    void TC_GOI_001_ThemGoi_Success() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, 30, 10);
        Mockito.when(goiDichVuRepository.existsByTenGoi(goi.getTenGoi())).thenReturn(false);
        Mockito.when(goiDichVuRepository.save(goi)).thenReturn(goi);

        GoiDichVu result = goiDichVuService.themGoi(goi);
        assertNotNull(result);
        assertEquals("Gói cơ bản", result.getTenGoi());
    }

    @Test
    void TC_GOI_002_ThemGoi_ThrowsWhenGoiNull() {
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(null));
    }

    @Test
    void TC_GOI_003_ThemGoi_ThrowsWhenTenGoiEmpty() {
        GoiDichVu goi = new GoiDichVu(null, "", "Mô tả", 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_004_ThemGoi_ThrowsWhenGiaNull() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", null, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_005_ThemGoi_ThrowsWhenGiaLeZero() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 0.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_006_ThemGoi_ThrowsWhenThoiGianDungNull() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, null, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_007_ThemGoi_ThrowsWhenThoiGianDungLeZero() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, 0, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_008_ThemGoi_ThrowsWhenSoLanDoiNull() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, 30, null);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_009_ThemGoi_ThrowsWhenSoLanDoiLeZero() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, 30, 0);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_010_ThemGoi_ThrowsWhenTenGoiExists() {
        GoiDichVu goi = new GoiDichVu(null, "Gói cơ bản", "Mô tả", 100.0, 30, 10);
        Mockito.when(goiDichVuRepository.existsByTenGoi(goi.getTenGoi())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    // ========== Bổ sung: kiểm tra tenGoi null và blank ==========

    @Test
    void TC_GOI_020_ThemGoi_TenNull() {
        GoiDichVu goi = new GoiDichVu(null, null, "Mô tả", 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_021_ThemGoi_TenBlank() {
        GoiDichVu goi = new GoiDichVu(null, "      ", "Mô tả", 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    // ========== TCS: Sửa gói dịch vụ ==========

    @Test
    void TC_GOI_011_SuaGoi_Success() {
        GoiDichVu existing = new GoiDichVu(1L, "Gói cũ", "Mô tả cũ", 50.0, 15, 5);
        GoiDichVu updatedData = new GoiDichVu(null, "Gói mới", "Mô tả mới", 200.0, 60, 20);

        Mockito.when(goiDichVuRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(goiDichVuRepository.existsByTenGoi(updatedData.getTenGoi())).thenReturn(false);
        Mockito.when(goiDichVuRepository.save(existing)).thenReturn(existing);

        GoiDichVu result = goiDichVuService.suaGoi(1L, updatedData);
        assertNotNull(result);
        assertEquals("Gói mới", result.getTenGoi());
        assertEquals(200.0, result.getGia());
    }

    @Test
    void TC_GOI_012_SuaGoi_ThrowsWhenNotFound() {
        GoiDichVu updatedData = new GoiDichVu(null, "Gói mới", "Mô tả", 100.0, 30, 10);
        Mockito.when(goiDichVuRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> goiDichVuService.suaGoi(99L, updatedData));
    }

    @Test
    void TC_GOI_013_SuaGoi_ThrowsWhenTenGoiDuplicated() {
        GoiDichVu existing = new GoiDichVu(1L, "Gói cũ", "Mô tả cũ", 50.0, 15, 5);
        GoiDichVu updatedData = new GoiDichVu(null, "Gói trùng", "Mô tả", 100.0, 30, 10);

        Mockito.when(goiDichVuRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(goiDichVuRepository.existsByTenGoi(updatedData.getTenGoi())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> goiDichVuService.suaGoi(1L, updatedData));
    }

    // ========== Bổ sung: sửa nhưng không đổi tên ==========

    @Test
    void TC_GOI_022_SuaGoi_KhongDoiTen() {
        GoiDichVu old = new GoiDichVu(1L, "VIP", "Mô tả", 100.0, 30, 10);
        GoiDichVu dto = new GoiDichVu(null, "VIP", "Mô tả mới", 150.0, 40, 20);

        Mockito.when(goiDichVuRepository.findById(1L)).thenReturn(Optional.of(old));
        Mockito.when(goiDichVuRepository.save(old)).thenReturn(old);

        GoiDichVu result = goiDichVuService.suaGoi(1L, dto);

        assertEquals("VIP", result.getTenGoi());
        assertEquals(150.0, result.getGia());
        assertEquals(40, result.getThoiGianDung());
        assertEquals(20, result.getSoLanDoi());

        // Không gọi existsByTenGoi vì tên không thay đổi
        Mockito.verify(goiDichVuRepository, Mockito.never()).existsByTenGoi(Mockito.any());
    }

    // ========== TCS: Xóa gói dịch vụ ==========

    @Test
    void TC_GOI_014_XoaGoi_Success() {
        Mockito.when(goiDichVuRepository.existsById(1L)).thenReturn(true);
        Mockito.when(lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(1L)).thenReturn(false);

        boolean result = goiDichVuService.xoaGoi(1L);
        assertTrue(result);
        Mockito.verify(goiDichVuRepository).deleteById(1L);
    }

    @Test
    void TC_GOI_015_XoaGoi_NotFound() {
        Mockito.when(goiDichVuRepository.existsById(2L)).thenReturn(false);
        assertFalse(goiDichVuService.xoaGoi(2L));
    }

    @Test
    void TC_GOI_016_XoaGoi_ThrowsWhenDangDuocSuDung() {
        Mockito.when(goiDichVuRepository.existsById(1L)).thenReturn(true);
        Mockito.when(lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> goiDichVuService.xoaGoi(1L));
    }

    // ========== TCS: Lấy danh sách và lấy theo ID ==========

    @Test
    void TC_GOI_017_DanhSachGoi_ReturnsList() {
        List<GoiDichVu> list = Arrays.asList(new GoiDichVu(), new GoiDichVu());
        Mockito.when(goiDichVuRepository.findAll()).thenReturn(list);

        List<GoiDichVu> result = goiDichVuService.danhSachGoi();
        assertEquals(2, result.size());
    }

    @Test
    void TC_GOI_018_LayGoiTheoId_Found() {
        GoiDichVu goi = new GoiDichVu(1L, "Gói", "Mô tả", 100.0, 30, 10);
        Mockito.when(goiDichVuRepository.findById(1L)).thenReturn(Optional.of(goi));

        GoiDichVu result = goiDichVuService.layGoiTheoId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getMaGoi());
    }

    @Test
    void TC_GOI_019_LayGoiTheoId_NotFound() {
        Mockito.when(goiDichVuRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(goiDichVuService.layGoiTheoId(99L));
    }

    @Test
    void TC_GOI_023_ThemGoi_TenGoiTooLong() {
        GoiDichVu goi = new GoiDichVu(null, "A".repeat(256), "Mo ta", 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_024_ThemGoi_MoTaNull() {
        GoiDichVu goi = new GoiDichVu(null, "Goi test", null, 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }

    @Test
    void TC_GOI_025_ThemGoi_MoTaTooLong() {
        GoiDichVu goi = new GoiDichVu(null, "Goi test", "A".repeat(256), 100.0, 30, 10);
        assertThrows(RuntimeException.class, () -> goiDichVuService.themGoi(goi));
    }
}
