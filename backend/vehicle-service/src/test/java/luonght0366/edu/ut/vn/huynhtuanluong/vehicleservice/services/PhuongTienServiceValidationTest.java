package luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.services;

import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.dtos.PhuongTienDTO;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PhuongTienServiceValidationTest extends PhuongTienServiceBaseTest {

    // ==========================================
    // NHÓM KIỂM THỬ VALIDATE KHI TẠO MỚI (validateCreate)
    // ==========================================

    @Test
    void themPhuongTien_DtoNull_NemNgoaiLe() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(null)
        );
        assertEquals("Dữ liệu JSON không hợp lệ", ex.getMessage());
    }

    @Test
    void themPhuongTien_VinNullHoacRong_NemNgoaiLe() {
        validDto.setVin("   ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("VIN không được để trống", ex.getMessage());
    }

    @Test
    void themPhuongTien_VinSaiDoDai_NemNgoaiLe() {
        validDto.setVin("1234567890123456"); // 16 ký tự (Sai mốc biên)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Mã VIN phải có độ dài đúng 17 ký tự", ex.getMessage());
    }

    @Test
    void themPhuongTien_VinChuaKyTuCam_NemNgoaiLe() {
        validDto.setVin("1HGBH41JXMN10918I"); // Chứa ký tự 'I' (Bị cấm trong chuẩn VIN quốc tế)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Mã VIN không đúng định dạng chuẩn quốc tế", ex.getMessage());
    }

    @Test
    void themPhuongTien_BienSoNullHoacRong_NemNgoaiLe() {
        validDto.setBienSo("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Biển số không được để trống", ex.getMessage());
    }

    @Test
    void themPhuongTien_MaTaiXeNhoHonHoacBy0_NemNgoaiLe() {
        validDto.setMaTaiXe(0L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Mã tài xế phải lớn hơn 0", ex.getMessage());
    }

    @Test
    void themPhuongTien_MaTaiXeNull_NemNullPointerException() {
        validDto.setMaTaiXe(null);
        // Kiểm tra lỗi unboxing từ Long sang long primitive ở câu lệnh so sánh điều kiện `<= 0`
        assertThrows(NullPointerException.class, () -> phuongTienService.themPhuongTien(validDto));
    }

    @Test
    void themPhuongTien_MaPinNhoHonHoacBy0_NemNgoaiLe() {
        validDto.setMaPin(-5L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Mã Pin phải lớn hơn 0", ex.getMessage());
    }

    @Test
    void themPhuongTien_LoaiXeNullHoacRong_NemNgoaiLe() {
        validDto.setLoaiXe("   ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Loại xe không được để trống", ex.getMessage());
    }

    @Test
    void themPhuongTien_LoaiXeQuaNgan_NemNgoaiLe() {
        validDto.setLoaiXe("X"); // Chỉ có 1 ký tự công nghiệp
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Tên loại xe quá ngắn", ex.getMessage());
    }

    @Test
    void themPhuongTien_LoaiXeVuotQua50KyTu_NemNgoaiLe() {
        validDto.setLoaiXe("X".repeat(51));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Tên loại xe không được vượt quá 50 ký tự", ex.getMessage());
    }

    @Test
    void themPhuongTien_BienSoVuotQua12KyTu_NemNgoaiLe() {
        validDto.setBienSo("59A-123.456789");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Biển số vượt quá độ dài quy định", ex.getMessage());
    }

    @Test
    void themPhuongTien_BienSoSaiDinhDangRegex_NemNgoaiLe() {
        validDto.setBienSo("BIEN-SO-GIA");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.themPhuongTien(validDto)
        );
        assertEquals("Dữ liệu JSON sai định dạng hoặc biển số không hợp lệ", ex.getMessage());
    }

    // ==========================================
    // NHÓM KIỂM THỬ VALIDATE KHI CẬP NHẬT (validateUpdate)
    // ==========================================

    @Test
    void suaPhuongTien_DtoNull_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, null)
        );
        assertEquals("Dữ liệu JSON không hợp lệ", ex.getMessage());
    }

    @Test
    void suaPhuongTien_UpdateVinRong_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setVin("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, updateDto)
        );
        assertEquals("VIN không được để trống", ex.getMessage());
    }

    @Test
    void suaPhuongTien_UpdateMaTaiXeNhoHonHoacBy0_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setMaTaiXe(0L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, updateDto)
        );
        assertEquals("Mã tài xế phải lớn hơn 0", ex.getMessage());
    }

    @Test
    void suaPhuongTien_UpdateBienSoRong_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setBienSo("  ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, updateDto)
        );
        assertEquals("Biển số không được để trống", ex.getMessage());
    }

    @Test
    void suaPhuongTien_UpdateLoaiXeRong_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setLoaiXe("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, updateDto)
        );
        assertEquals("Loại xe không được để trống", ex.getMessage());
    }

    @Test
    void suaPhuongTien_UpdateLoaiXeQuaNgan_NemNgoaiLe() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO updateDto = new PhuongTienDTO();
        updateDto.setLoaiXe("O");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                phuongTienService.suaPhuongTien(1L, updateDto)
        );
        assertEquals("Tên loại xe quá ngắn", ex.getMessage());
    }

    @Test
    void themPhuongTien_DtoNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.themPhuongTien(null));
    }

    @Test
    void themPhuongTien_MaPinAm_ThrowsException() {
        PhuongTienDTO dto = createValidDTO();
        dto.setMaPin(-1L); // Rơi vào nhánh: dto.getMaPin() != null && dto.getMaPin() <= 0
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.themPhuongTien(dto));
    }

    @Test
    void themPhuongTien_LoaiXeQuaDai_ThrowsException() {
        PhuongTienDTO dto = createValidDTO();
        dto.setLoaiXe("A".repeat(51)); // Rơi vào nhánh: loaiXeTrim.length() > 50
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.themPhuongTien(dto));
    }

    @Test
    void themPhuongTien_BienSoNull_ThrowsException() {
        PhuongTienDTO dto = createValidDTO();
        dto.setBienSo(null); // Rơi vào nhánh: bienSo == null
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.themPhuongTien(dto));
    }

    @Test
    void themPhuongTien_BienSoQuaDai_ThrowsException() {
        PhuongTienDTO dto = createValidDTO();
        dto.setBienSo("59A-123456789"); // Rơi vào nhánh: value.length() > 12
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.themPhuongTien(dto));
    }

    @Test
    void suaPhuongTien_DtoNull_ThrowsException() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.suaPhuongTien(1L, null));
    }

    @Test
    void suaPhuongTien_MaTaiXeAm_ThrowsException() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO dto = new PhuongTienDTO();
        dto.setMaTaiXe(-1L); // Rơi vào nhánh maTaiXe <= 0 khi update
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.suaPhuongTien(1L, dto));
    }

    @Test
    void suaPhuongTien_LoaiXeRong_ThrowsException() {
        when(phuongTienRepository.findById(1L)).thenReturn(Optional.of(phuongTienEntity));
        PhuongTienDTO dto = new PhuongTienDTO();
        dto.setLoaiXe("   "); // Rơi vào nhánh dto.getLoaiXe().trim().isEmpty()
        assertThrows(IllegalArgumentException.class, () -> phuongTienService.suaPhuongTien(1L, dto));
    }

    // Hàm phụ trợ tạo DTO hợp lệ để tái sử dụng trong các test case
    private PhuongTienDTO createValidDTO() {
        PhuongTienDTO dto = new PhuongTienDTO();
        dto.setVin("1HGCM82633A000001"); // VIN 17 ký tự hợp lệ
        dto.setBienSo("59A-123.45");     // Biển số hợp lệ
        dto.setLoaiXe("Sedan");
        dto.setMaTaiXe(1L);
        dto.setMaPin(10L);
        return dto;
    }
}