package luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.services;

import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.dtos.PhuongTienDTO;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.modules.PhuongTien;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PhuongTienServiceCoreTest extends PhuongTienServiceBaseTest {

    // ==========================================
    // LUỒNG NGHIỆP VỤ: THÊM PHƯƠNG TIỆN
    // ==========================================

    @Test
    void themPhuongTien_ThanhCong() {
        when(phuongTienRepository.existsByVin(anyString())).thenReturn(false);
        when(phuongTienRepository.existsByBienSo(anyString())).thenReturn(false);
        when(phuongTienRepository.save(any(PhuongTien.class))).thenReturn(phuongTienEntity);

        PhuongTien result = phuongTienService.themPhuongTien(validDto);

        assertNotNull(result);
        assertEquals("59A-123.45", result.getBienSo());
        verify(phuongTienRepository, times(1)).save(any(PhuongTien.class));
    }

    @Test
    void themPhuongTien_VinDaTonTai_NemNgoaiLe() {
        when(phuongTienRepository.existsByVin(anyString())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("VIN đã tồn tại", ex.getMessage());
    }

    @Test
    void themPhuongTien_BienSoDaTonTai_NemNgoaiLe() {
        when(phuongTienRepository.existsByVin(anyString())).thenReturn(false);
        when(phuongTienRepository.existsByBienSo(anyString())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Biển số đã tồn tại", ex.getMessage());
    }

    // ==========================================
    // LUỒNG NGHIỆP VỤ: TRUY VẤN & XÓA
    // ==========================================

    @Test
    void danhSachPhuongTien_ThanhCong() {
        when(phuongTienRepository.findAll()).thenReturn(Collections.singletonList(phuongTienEntity));
        List<PhuongTien> list = phuongTienService.danhSachPhuongTien();
        assertEquals(1, list.size());
    }

    @Test
    void layPhuongTienTheoId_TimThay_TraVeEntity() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTien result = phuongTienService.layPhuongTienTheoId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getMaPhuongTien());
    }

    @Test
    void layPhuongTienTheoId_KhongTimThay_TraVeNull() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.empty());
        PhuongTien result = phuongTienService.layPhuongTienTheoId(1L);
        assertNull(result);
    }

    @Test
    void danhSachTheoTaiXe_ThanhCong() {
        when(phuongTienRepository.findAllByMaTaiXe(1L)).thenReturn(Collections.singletonList(phuongTienEntity));
        List<PhuongTien> list = phuongTienService.danhSachTheoTaiXe(1L);
        assertFalse(list.isEmpty());
    }

    @Test
    void xoaPhuongTien_IdTonTai_TraVeTrue() {
        when(phuongTienRepository.existsById(1L)).thenReturn(true);
        doNothing().when(phuongTienRepository).deleteById(1L);

        boolean isDeleted = phuongTienService.xoaPhuongTien(1L);
        assertTrue(isDeleted);
    }

    @Test
    void xoaPhuongTien_IdKhongTonTai_TraVeFalse() {
        when(phuongTienRepository.existsById(1L)).thenReturn(false);
        boolean isDeleted = phuongTienService.xoaPhuongTien(1L);
        assertFalse(isDeleted);
    }

    // ==========================================
    // LUỒNG NGHIỆP VỤ: CẬP NHẬT PHƯƠNG TIỆN (suaPhuongTien)
    // ==========================================

    @Test
    void suaPhuongTien_IdKhongTonTai_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
                phuongTienService.suaPhuongTien(1L, validDto)
        );
        assertEquals("Không tìm thấy phương tiện", ex.getMessage());
    }

    @Test
    void suaPhuongTien_ThayDoiVinTrungXeKhac_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        validDto.setVin("1HGBH41JXMN109187"); // Đổi sang một số VIN mới tinh
        when(phuongTienRepository.existsByVinAndMaPhuongTienNot(anyString(), anyLong())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, validDto)
        );
        assertEquals("VIN đã tồn tại", ex.getMessage());
    }

    @Test
    void suaPhuongTien_ThayDoiBienSoTrungXeKhac_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        validDto.setBienSo("51B-999.99"); // Đổi sang một biển số mới tinh
        when(phuongTienRepository.existsByBienSoAndMaPhuongTienNot(anyString(), anyLong())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, validDto)
        );
        assertEquals("Biển số đã tồn tại", ex.getMessage());
    }

    @Test
    void suaPhuongTien_ThanhCongVoiFullTruongMoi() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));

        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setVin("1HGBH41JXMN109189"); // Cập nhật VIN mới chưa tồn tại
        updateDto.setBienSo("29A-888.88");      // Cập nhật biển số mới chưa tồn tại
        updateDto.setLoaiXe("O to Dien");       // Cập nhật loại xe mới
        updateDto.setMaTaiXe(5L);               // Cập nhật mã tài xế mới

        when(phuongTienRepository.existsByVinAndMaPhuongTienNot(anyString(), anyLong())).thenReturn(false);
        when(phuongTienRepository.existsByBienSoAndMaPhuongTienNot(anyString(), anyLong())).thenReturn(false);
        when(phuongTienRepository.save(any(PhuongTien.class))).thenReturn(phuongTienEntity);

        PhuongTien result = phuongTienService.suaPhuongTien(1L, updateDto);
        assertNotNull(result);
        verify(phuongTienRepository, times(1)).save(any(PhuongTien.class));
    }

    // ==========================================
    // LUỒNG NGHIỆP VỤ: LIÊN KẾT / HỦY PIN (Link-Unlink)
    // ==========================================

    @Test
    void lienKetPin_KhongTimThayXe_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
                phuongTienService.lienKetPin(1L, 100L)
        );
        assertEquals("Không tìm thấy phương tiện", ex.getMessage());
    }

    @Test
    void lienKetPin_MaPinNull_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.lienKetPin(1L, null)
        );
        assertEquals("Mã pin không được để trống", ex.getMessage());
    }

    @Test
    void lienKetPin_ThanhCong() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        when(phuongTienRepository.save(any(PhuongTien.class))).thenReturn(phuongTienEntity);

        PhuongTien result = phuongTienService.lienKetPin(1L, 99L);
        assertNotNull(result);
        assertEquals(99L, result.getMaPin());
    }

    @Test
    void huyLienKetPin_KhongTimThayXe_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
                phuongTienService.huyLienKetPin(1L)
        );
        assertEquals("Không tìm thấy phương tiện", ex.getMessage());
    }

    @Test
    void huyLienKetPin_ThanhCong() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        when(phuongTienRepository.save(any(PhuongTien.class))).thenReturn(phuongTienEntity);

        PhuongTien result = phuongTienService.huyLienKetPin(1L);
        assertNotNull(result);
        assertNull(result.getMaPin()); // Mã pin đã được gán về null thành công
    }

    // --- BỔ SUNG TEST CHO HÀM UPDATE KHI KHÔNG THAY ĐỔI DỮ LIỆU ---

    @Test
    void suaPhuongTien_GiuNguyenThongTin_BoQuaCheckTrung_ThanhCong() {
        // Given: Dữ liệu hiện tại trong Database
        PhuongTien p = new PhuongTien();
        p.setMaPhuongTien(1L);
        p.setVin("1HGCM82633A000001");
        p.setBienSo("59A-123.45");
        p.setLoaiXe("Sedan");
        p.setMaTaiXe(10L);

        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(p));
        when(phuongTienRepository.save(any(PhuongTien.class))).thenReturn(p);

        // Tạo DTO update nhưng VIN và Biển số y hệt cái cũ, các field khác để null
        // Mục đích: Ép code nhảy qua các nhánh (dto.getVin() != null && !dto.getVin().equals(v.getVin()))
        // và (dto.getLoaiXe() != null), (dto.getMaTaiXe() != null) thành FALSE.
        PhuongTienDTO dto = new PhuongTienDTO();
        dto.setVin("1HGCM82633A000001");
        dto.setBienSo("59A-123.45");
        dto.setLoaiXe(null);
        dto.setMaTaiXe(null);

        // When
        PhuongTien updated = phuongTienService.suaPhuongTien(1L, dto);

        // Then
        assertNotNull(updated);
        // Verify rằng repository KHÔNG BỊ GỌI các hàm check trùng (vì nó biết thông tin không đổi)
        verify(phuongTienRepository, never()).existsByVinAndMaPhuongTienNot(anyString(), anyLong());
        verify(phuongTienRepository, never()).existsByBienSoAndMaPhuongTienNot(anyString(), anyLong());
    }
}