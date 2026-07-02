package datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.services;

import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.dtos.LichSuDangKyGoiDTO;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.modules.GoiDichVu;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.modules.LichSuDangKyGoi;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.repositories.IGoiDichVuRepository;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.repositories.ILichSuDangKyGoiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LichSuDangKyGoiServiceTest {

    @Mock
    private ILichSuDangKyGoiRepository lichSuDangKyGoiRepository;

    @Mock
    private IGoiDichVuRepository goiDichVuRepository;

    @InjectMocks
    private LichSuDangKyGoiService lichSuDangKyGoiService;

    private GoiDichVu taoGoi(Long maGoi, int thoiGianDung, int soLanDoi) {
        return new GoiDichVu(maGoi, "Gói test", "Mô tả", 100.0, thoiGianDung, soLanDoi);
    }

    private LichSuDangKyGoi taoLichSu(Long id, Long maTaiXe, GoiDichVu goi,
                                      LocalDate ngayBatDau, LocalDate ngayKetThuc,
                                      Integer soLanConLai, String trangThai) {
        LichSuDangKyGoi ls = new LichSuDangKyGoi();
        ls.setMaLichSuDangKyGoi(id);
        ls.setMaTaiXe(maTaiXe);
        ls.setGoiDichVu(goi);
        ls.setNgayBatDau(ngayBatDau);
        ls.setNgayKetThuc(ngayKetThuc);
        ls.setSoLanConLai(soLanConLai);
        ls.setTrangThai(trangThai);
        return ls;
    }

    // ============================================================
    // 1. Thêm đăng ký gói
    // ============================================================

    @Test
    void TC_LICHSU_001_ThemDangKyGoi_Success() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(100L);
        dto.setMaGoi(1L);

        Mockito.when(goiDichVuRepository.findById(1L)).thenReturn(Optional.of(goi));
        Mockito.when(lichSuDangKyGoiRepository.save(Mockito.any(LichSuDangKyGoi.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LichSuDangKyGoi result = lichSuDangKyGoiService.themDangKyGoi(dto);
        assertNotNull(result);
        assertEquals(100L, result.getMaTaiXe());
        assertEquals(10, result.getSoLanConLai());
        assertEquals("CON_HAN", result.getTrangThai());
        assertNotNull(result.getNgayBatDau());
        assertNotNull(result.getNgayKetThuc());
    }

    @Test
    void TC_LICHSU_002_ThemDangKyGoi_ThrowsWhenGoiNotFound() {
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(100L);
        dto.setMaGoi(99L);

        Mockito.when(goiDichVuRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.themDangKyGoi(dto));
    }

    // ============================================================
    // 2. Sửa đăng ký gói
    // ============================================================

    @Test
    void TC_LICHSU_003_SuaDangKyGoi_Success() {
        GoiDichVu oldGoi = taoGoi(1L, 30, 10);
        GoiDichVu newGoi = taoGoi(2L, 60, 20);
        LichSuDangKyGoi existing = taoLichSu(1L, 100L, oldGoi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 8, "CON_HAN");

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(200L);
        dto.setMaGoi(2L);

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(goiDichVuRepository.findById(2L)).thenReturn(Optional.of(newGoi));
        Mockito.when(lichSuDangKyGoiRepository.save(existing)).thenReturn(existing);

        LichSuDangKyGoi result = lichSuDangKyGoiService.suaDangKyGoi(1L, dto);
        assertNotNull(result);
        assertEquals(200L, result.getMaTaiXe());
        assertEquals(20, result.getSoLanConLai());
        assertEquals(newGoi.getMaGoi(), result.getGoiDichVu().getMaGoi());
    }

    @Test
    void TC_LICHSU_004_SuaDangKyGoi_ThrowsWhenNotFound() {
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        Mockito.when(lichSuDangKyGoiRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaDangKyGoi(99L, dto));
    }

    @Test
    void TC_LICHSU_005_SuaDangKyGoi_MaTaiXeNull() {
        GoiDichVu oldGoi = taoGoi(1L, 30, 10);
        GoiDichVu newGoi = taoGoi(2L, 60, 20);
        LichSuDangKyGoi existing = taoLichSu(1L, 100L, oldGoi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 8, "CON_HAN");

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(null);
        dto.setMaGoi(2L);

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(goiDichVuRepository.findById(2L)).thenReturn(Optional.of(newGoi));
        Mockito.when(lichSuDangKyGoiRepository.save(existing)).thenReturn(existing);

        LichSuDangKyGoi result = lichSuDangKyGoiService.suaDangKyGoi(1L, dto);
        assertNotNull(result);
        assertEquals(100L, result.getMaTaiXe());
        assertEquals(20, result.getSoLanConLai());
        assertEquals(newGoi.getMaGoi(), result.getGoiDichVu().getMaGoi());
    }

    @Test
    void TC_LICHSU_006_SuaDangKyGoi_MaGoiNull() {
        GoiDichVu oldGoi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi existing = taoLichSu(1L, 100L, oldGoi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 8, "CON_HAN");

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(200L);
        dto.setMaGoi(null);

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(lichSuDangKyGoiRepository.save(existing)).thenReturn(existing);

        LichSuDangKyGoi result = lichSuDangKyGoiService.suaDangKyGoi(1L, dto);
        assertNotNull(result);
        assertEquals(200L, result.getMaTaiXe());
        assertEquals(oldGoi.getMaGoi(), result.getGoiDichVu().getMaGoi());
        assertEquals(8, result.getSoLanConLai());
    }

    @Test
    void TC_LICHSU_037_SuaDangKyGoi_GoiKhongTonTai() {
        GoiDichVu oldGoi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi existing = taoLichSu(1L, 100L, oldGoi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 8, "CON_HAN");

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setMaTaiXe(200L);
        dto.setMaGoi(99L);

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(goiDichVuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaDangKyGoi(1L, dto));
    }

    // ============================================================
    // 3. Cập nhật số lần còn lại
    // ============================================================

    @Test
    void TC_LICHSU_007_SuaSoLanConLai_Success() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi valid = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(valid));
        Mockito.when(lichSuDangKyGoiRepository.save(valid)).thenReturn(valid);

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());
        LichSuDangKyGoi result = lichSuDangKyGoiService.suaSoLanConLai(100L, dto);

        assertNotNull(result);
        assertEquals(4, result.getSoLanConLai());
        assertEquals("CON_HAN", result.getTrangThai());
    }

    @Test
    void TC_LICHSU_008_SuaSoLanConLai_ThrowsWhenNoValidPackage() {
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList());
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    @Test
    void TC_LICHSU_009_SuaSoLanConLai_NgayKetThucNull() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi invalid = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(5), null, 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(invalid));
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    @Test
    void TC_LICHSU_010_SuaSoLanConLai_SoLanConLaiNull() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi invalid = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), null, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(invalid));
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    @Test
    void TC_LICHSU_011_SuaSoLanConLai_GiaoDichSauNgayHetHan() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi expired = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "HET_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(expired));
        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    @Test
    void TC_LICHSU_030_SuaSoLanConLai_NgayGiaoDichNull() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(20), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(null);

        LichSuDangKyGoi result = lichSuDangKyGoiService.suaSoLanConLai(100L, dto);
        assertEquals(4, result.getSoLanConLai());
        assertEquals("CON_HAN", result.getTrangThai());
    }

    @Test
    void TC_LICHSU_031_SuaSoLanConLai_SoLanConLaiBang0() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(20), 0, "HET_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    @Test
    void TC_LICHSU_038_SuaSoLanConLai_FilterHetHan() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1), 5, "HET_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    // ========== Bổ sung: đi vào nhánh xacDinhTrangThai() != CON_HAN ==========
    @Test
    void TC_LICHSU_043_SuaSoLanConLai_FilterKhongQuaXacDinhTrangThai() {
        // Kịch bản:
        // - today = 2026-07-02
        // - dto.ngayGiaoDich = 2026-07-01 (yesterday)
        // - ngayKetThuc = 2026-07-01 (yesterday)
        // - soLanConLai = 5 (không null, >0)
        // -> Filter: !ngayGiaoDich.isAfter(ngayKetThuc) = true (1/7 không sau 1/7)
        // -> xacDinhTrangThai: today (2/7) isAfter(1/7) => HET_HAN
        // -> equals("CON_HAN") = false => filter fail
        // -> throw RuntimeException

        // Fix cứng ngày để test ổn định
        LocalDate fixedToday = LocalDate.of(2026, 7, 2);
        LocalDate yesterday = LocalDate.of(2026, 7, 1);

        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                fixedToday.minusDays(10), yesterday, 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(yesterday);

        assertThrows(RuntimeException.class, () -> lichSuDangKyGoiService.suaSoLanConLai(100L, dto));
    }

    // ========== Bổ sung: cover comparator sorted() với 2 phần tử ==========
    @Test
    void TC_LICHSU_046_SuaSoLanConLai_SapXepTheoNgayBatDau() {
        // Mock 2 gói hợp lệ với ngày bắt đầu khác nhau
        // Gói có ngày bắt đầu gần nhất sẽ được chọn (sorted descending)
        GoiDichVu goi = taoGoi(1L, 30, 10);

        LichSuDangKyGoi ls1 = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), 5, "CON_HAN");
        LichSuDangKyGoi ls2 = taoLichSu(2L, 100L, goi,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(28), 8, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L))
                .thenReturn(Arrays.asList(ls1, ls2));

        Mockito.when(lichSuDangKyGoiRepository.save(ls2)).thenReturn(ls2);

        LichSuDangKyGoiDTO dto = new LichSuDangKyGoiDTO();
        dto.setNgayGiaoDich(LocalDate.now());

        LichSuDangKyGoi result = lichSuDangKyGoiService.suaSoLanConLai(100L, dto);

        // Phải lấy ls2 (ngày bắt đầu gần nhất) chứ không phải ls1
        assertEquals(7, result.getSoLanConLai());
        assertEquals(2L, result.getMaLichSuDangKyGoi());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls2);
    }

    // ============================================================
    // 4. Danh sách và lấy theo ID
    // ============================================================

    @Test
    void TC_LICHSU_012_DanhSachDangKyGoi_ReturnsList() {
        List<LichSuDangKyGoi> list = Arrays.asList(new LichSuDangKyGoi(), new LichSuDangKyGoi());
        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(list);
        Mockito.when(lichSuDangKyGoiRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.danhSachDangKyGoi();
        assertEquals(2, result.size());
    }

    @Test
    void TC_LICHSU_013_DanhSachDangKyGoi_TriggerSaveWhenTrangThaiChanged() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.danhSachDangKyGoi();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HET_HAN", result.get(0).getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    // ========== Bổ sung: danhSachDangKyGoi không gọi save ==========
    @Test
    void TC_LICHSU_044_DanhSachDangKyGoi_KhongCapNhatTrangThai() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(List.of(ls));

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.danhSachDangKyGoi();
        assertEquals(1, result.size());
        assertEquals("CON_HAN", result.get(0).getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TC_LICHSU_014_LayDangKyGoiTheoId_Found() {
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, taoGoi(1L, 30, 10),
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(ls));

        LichSuDangKyGoi result = lichSuDangKyGoiService.layDangKyGoiTheoId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getMaLichSuDangKyGoi());
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TC_LICHSU_015_LayDangKyGoiTheoId_TriggerSaveWhenTrangThaiChanged() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        LichSuDangKyGoi result = lichSuDangKyGoiService.layDangKyGoiTheoId(1L);
        assertNotNull(result);
        assertEquals("HET_HAN", result.getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    @Test
    void TC_LICHSU_016_LayDangKyGoiTheoId_NotFound() {
        Mockito.when(lichSuDangKyGoiRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(lichSuDangKyGoiService.layDangKyGoiTheoId(99L));
    }

    // ============================================================
    // 5. Xóa
    // ============================================================

    @Test
    void TC_LICHSU_017_XoaDangKyGoi_Success() {
        Mockito.when(lichSuDangKyGoiRepository.existsById(1L)).thenReturn(true);
        boolean result = lichSuDangKyGoiService.xoaDangKyGoi(1L);
        assertTrue(result);
        Mockito.verify(lichSuDangKyGoiRepository).deleteById(1L);
    }

    @Test
    void TC_LICHSU_018_XoaDangKyGoi_NotFound() {
        Mockito.when(lichSuDangKyGoiRepository.existsById(2L)).thenReturn(false);
        assertFalse(lichSuDangKyGoiService.xoaDangKyGoi(2L));
    }

    @Test
    void TC_LICHSU_019_XoaDangKyGoi_NullId() {
        assertFalse(lichSuDangKyGoiService.xoaDangKyGoi(null));
    }

    // ============================================================
    // 6. Kiểm tra tài xế có gói còn hạn
    // ============================================================

    @Test
    void TC_LICHSU_020_KiemTraTaiXeCoGoiConHan_True() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi valid = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(valid));

        boolean result = lichSuDangKyGoiService.kiemTraTaiXeCoGoiConHan(100L);
        assertTrue(result);
    }

    @Test
    void TC_LICHSU_021_KiemTraTaiXeCoGoiConHan_FalseWhenHetHan() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi expired = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), 0, "HET_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(expired));

        boolean result = lichSuDangKyGoiService.kiemTraTaiXeCoGoiConHan(100L);
        assertFalse(result);
    }

    @Test
    void TC_LICHSU_040_KiemTraTaiXeCoGoiConHan_NgayKetThucNull() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), null, 10, "CON_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        boolean result = lichSuDangKyGoiService.kiemTraTaiXeCoGoiConHan(100L);
        assertFalse(result);
    }

    @Test
    void TC_LICHSU_041_KiemTraTaiXeCoGoiConHan_SoLanConLaiNull() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), null, "CON_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        boolean result = lichSuDangKyGoiService.kiemTraTaiXeCoGoiConHan(100L);
        assertFalse(result);
    }

    @Test
    void TC_LICHSU_042_KiemTraTaiXeCoGoiConHan_SoLanConLaiBang0() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 0, "HET_HAN");
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        boolean result = lichSuDangKyGoiService.kiemTraTaiXeCoGoiConHan(100L);
        assertFalse(result);
    }

    // ============================================================
    // 7. Lấy lịch sử theo tài xế
    // ============================================================

    @Test
    void TC_LICHSU_022_LayLichSuTheoTaiXe_ReturnsList() {
        List<LichSuDangKyGoi> list = Arrays.asList(new LichSuDangKyGoi(), new LichSuDangKyGoi());
        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(list);
        Mockito.when(lichSuDangKyGoiRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.layLichSuTheoTaiXe(100L);
        assertEquals(2, result.size());
    }

    @Test
    void TC_LICHSU_023_LayLichSuTheoTaiXe_TriggerSaveWhenTrangThaiChanged() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(Arrays.asList(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.layLichSuTheoTaiXe(100L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HET_HAN", result.get(0).getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    @Test
    void TC_LICHSU_039_LayLichSuTheoTaiXe_KhongCapNhatTrangThai() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findByMaTaiXe(100L)).thenReturn(List.of(ls));

        List<LichSuDangKyGoi> result = lichSuDangKyGoiService.layLichSuTheoTaiXe(100L);
        assertEquals(1, result.size());
        assertEquals("CON_HAN", result.get(0).getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    // ============================================================
    // 8. Thống kê đăng ký theo gói
    // ============================================================

    @Test
    void TC_LICHSU_024_DemSoLuongDangKyTheoGoi_Success() {
        GoiDichVu goi1 = taoGoi(1L, 30, 10);
        GoiDichVu goi2 = taoGoi(2L, 30, 10);

        LichSuDangKyGoi ls1 = taoLichSu(1L, 100L, goi1,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 10, "CON_HAN");
        LichSuDangKyGoi ls2 = taoLichSu(2L, 101L, goi1,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), 8, "CON_HAN");
        LichSuDangKyGoi ls3 = taoLichSu(3L, 102L, goi2,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 0, "HET_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(ls1, ls2, ls3));

        Map<Long, Map<String, Long>> stats = lichSuDangKyGoiService.demSoLuongDangKyTheoGoi();
        assertNotNull(stats);
        assertEquals(2L, stats.get(1L).get("CON_HAN"));
        assertEquals(1L, stats.get(2L).get("HET_HAN"));
    }

    @Test
    void TC_LICHSU_025_DemSoLuongDangKyTheoGoi_TriggerSaveWhenTrangThaiChanged() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        Map<Long, Map<String, Long>> stats = lichSuDangKyGoiService.demSoLuongDangKyTheoGoi();
        assertNotNull(stats);
        assertTrue(stats.containsKey(1L));
        assertEquals(1L, stats.get(1L).get("HET_HAN"));
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    // ========== Bổ sung: demSoLuongDangKyTheoGoi không gọi save ==========
    @Test
    void TC_LICHSU_045_DemSoLuongDangKyTheoGoi_KhongCapNhatTrangThai() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls1 = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");
        LichSuDangKyGoi ls2 = taoLichSu(2L, 101L, goi,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), 8, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(ls1, ls2));

        Map<Long, Map<String, Long>> stats = lichSuDangKyGoiService.demSoLuongDangKyTheoGoi();
        assertNotNull(stats);
        assertEquals(2L, stats.get(1L).get("CON_HAN"));
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    // ============================================================
    // 9. Kiểm tra gói đang được sử dụng
    // ============================================================

    @Test
    void TC_LICHSU_026_KiemTraGoiDangDuocSuDung_True() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi valid = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 10, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(valid));

        boolean result = lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(1L);
        assertTrue(result);
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TC_LICHSU_027_KiemTraGoiDangDuocSuDung_False() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi expired = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 0, "HET_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(Arrays.asList(expired));

        boolean result = lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(1L);
        assertFalse(result);
        Mockito.verify(lichSuDangKyGoiRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TC_LICHSU_036_KiemTraGoiDangDuocSuDung_CapNhatTrangThaiVaGoiSave() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(1), 5, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findAll()).thenReturn(List.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        boolean result = lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(1L);
        assertFalse(result);
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    // ============================================================
    // 10. Kiểm thử private method xacDinhTrangThai
    // ============================================================

    @Test
    void TC_LICHSU_028_XacDinhTrangThai_NullNgayKetThuc() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), null, 10, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        LichSuDangKyGoi result = lichSuDangKyGoiService.layDangKyGoiTheoId(1L);
        assertNotNull(result);
        assertEquals("KHONG_XAC_DINH", result.getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    @Test
    void TC_LICHSU_029_XacDinhTrangThai_NullSoLanConLai() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), null, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        LichSuDangKyGoi result = lichSuDangKyGoiService.layDangKyGoiTheoId(1L);
        assertNotNull(result);
        assertEquals("KHONG_XAC_DINH", result.getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }

    @Test
    void TC_LICHSU_032_XacDinhTrangThai_SoLanConLaiBang0() {
        GoiDichVu goi = taoGoi(1L, 30, 10);
        LichSuDangKyGoi ls = taoLichSu(1L, 100L, goi,
                LocalDate.now(), LocalDate.now().plusDays(30), 0, "CON_HAN");

        Mockito.when(lichSuDangKyGoiRepository.findById(1L)).thenReturn(Optional.of(ls));
        Mockito.when(lichSuDangKyGoiRepository.save(ls)).thenReturn(ls);

        LichSuDangKyGoi result = lichSuDangKyGoiService.layDangKyGoiTheoId(1L);
        assertEquals("HET_HAN", result.getTrangThai());
        Mockito.verify(lichSuDangKyGoiRepository).save(ls);
    }
}