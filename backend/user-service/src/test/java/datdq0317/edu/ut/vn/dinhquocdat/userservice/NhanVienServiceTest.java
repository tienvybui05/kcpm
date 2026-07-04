package datdq0317.edu.ut.vn.dinhquocdat.userservice;


import datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos.NhanVienDTO;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.models.NguoiDung;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.models.NhanVien;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.INguoiDungRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.INhanVienRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.services.NhanVienService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * WHITE-BOX TESTING - Kiểm thử hộp trắng cho NhanVienService
 *
 * Áp dụng các kỹ thuật theo slide Chương 4:
 * 1. Statement Coverage - Bao phủ câu lệnh (Slide 57-64)
 * 2. Branch/Decision Coverage - Bao phủ nhánh (Slide 65-73)
 * 3. Condition Coverage - Bao phủ điều kiện (Slide 74-77)
 * 4. Path Coverage - Bao phủ đường dẫn (Slide 67-68)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White-box Testing - NhanVienService")
class NhanVienServiceTest {

    // ============================================================
    // MOCK DEPENDENCIES
    // ============================================================
    @Mock
    private INhanVienRepository nhanVienRepository;

    @Mock
    private INguoiDungRepository nguoiDungRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private NhanVienService nhanVienService;

    // ============================================================
    // TEST DATA
    // ============================================================
    private NhanVienDTO validDTO;
    private NguoiDung nguoiDung;
    private NhanVien nhanVien;

    @BeforeEach
    void setUp() {
        validDTO = new NhanVienDTO();
        validDTO.setHoTen("Nguyen Van A");
        validDTO.setEmail("a@b.c");
        validDTO.setSoDienThoai("0987654321");
        validDTO.setGioiTinh("Nam");
        validDTO.setMatKhau("Abc123");
        validDTO.setNgaySinh(LocalDate.now().minusYears(20));
        validDTO.setBangCap("Đại học");
        validDTO.setKinhNghiem("3 năm");
        validDTO.setMaTram(1L);

        nguoiDung = new NguoiDung();
        nguoiDung.setMaNguoiDung(1L);
        nguoiDung.setHoTen("Nguyen Van A");
        nguoiDung.setEmail("a@b.c");
        nguoiDung.setSoDienThoai("0987654321");
        nguoiDung.setMatKhau("encoded");
        nguoiDung.setVaiTro("NHANVIEN");

        nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(1L);
        nhanVien.setBangCap("Đại học");
        nhanVien.setKinhNghiem("3 năm");
        nhanVien.setMaTram(1L);
        nhanVien.setNguoiDung(nguoiDung);
    }

    // ============================================================
    // PHẦN 1: STATEMENT COVERAGE - themNhanVien()
    // ============================================================

    @Test
    @DisplayName("SC-01: Bao phủ 100% câu lệnh - luồng thành công")
    void statementCoverage_ThemNhanVien_AllStatementsExecuted() {
        // Given
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(validDTO.getMatKhau()))
                .thenReturn("encodedPassword");
        when(nguoiDungRepository.save(any(NguoiDung.class)))
                .thenReturn(nguoiDung);
        when(nhanVienRepository.save(any(NhanVien.class)))
                .thenReturn(nhanVien);

        // When
        NhanVien result = nhanVienService.themNhanVien(validDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMaNhanVien());
        assertEquals("Đại học", result.getBangCap());
        assertEquals(1L, result.getMaTram());

        InOrder inOrder = inOrder(
                nguoiDungRepository,
                passwordEncoder,
                nguoiDungRepository,
                nhanVienRepository
        );

        inOrder.verify(nguoiDungRepository).findByEmail(validDTO.getEmail());
        inOrder.verify(nguoiDungRepository).findBySoDienThoai(validDTO.getSoDienThoai());
        inOrder.verify(passwordEncoder).encode(validDTO.getMatKhau());
        inOrder.verify(nguoiDungRepository).save(any(NguoiDung.class));
        inOrder.verify(nhanVienRepository).save(any(NhanVien.class));

        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any(NguoiDung.class));
        verify(nhanVienRepository, times(1)).save(any(NhanVien.class));
    }

    // ============================================================
    // PHẦN 2: BRANCH COVERAGE - themNhanVien()
    // ============================================================

    // --- BRANCH 1: ifPresent (Email tồn tại) ---

    @Test
    @DisplayName("BC-01: Nhánh TRUE - Email đã tồn tại")
    void branchCoverage_EmailExists_TrueBranch() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.of(nguoiDung));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> nhanVienService.themNhanVien(validDTO));
        assertEquals("Email đã tồn tại!", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(nhanVienRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-02: Nhánh FALSE - Email chưa tồn tại")
    void branchCoverage_EmailExists_FalseBranch() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        nhanVienService.themNhanVien(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(nhanVienRepository, times(1)).save(any());
    }

    // --- BRANCH 2: ifPresent (SĐT tồn tại) ---

    @Test
    @DisplayName("BC-03: Nhánh TRUE - SĐT đã tồn tại")
    void branchCoverage_PhoneExists_TrueBranch() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.of(nguoiDung));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> nhanVienService.themNhanVien(validDTO));
        assertEquals("Số điện thoại đã tồn tại!", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(nhanVienRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-04: Nhánh FALSE - SĐT chưa tồn tại")
    void branchCoverage_PhoneExists_FalseBranch() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        nhanVienService.themNhanVien(validDTO);

        verify(nguoiDungRepository, times(1)).save(any());
        verify(nhanVienRepository, times(1)).save(any());
    }

    // ============================================================
    // PHẦN 3: TEST XÓA NHÂN VIÊN
    // ============================================================

    @Test
    @DisplayName("Xóa nhân viên thành công")
    void xoaNhanVien_Success() {
        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        doNothing().when(nhanVienRepository).delete(any(NhanVien.class));
        doNothing().when(nguoiDungRepository).delete(any(NguoiDung.class));

        boolean result = nhanVienService.xoaNhanVien(1L);

        assertTrue(result);
        verify(nhanVienRepository, times(1)).delete(nhanVien);
        verify(nguoiDungRepository, times(1)).delete(nguoiDung);
    }

    @Test
    @DisplayName("Xóa nhân viên - Không tìm thấy")
    void xoaNhanVien_NotFound() {
        when(nhanVienRepository.findById(999L))
                .thenReturn(Optional.empty());

        boolean result = nhanVienService.xoaNhanVien(999L);

        assertFalse(result);
        verify(nhanVienRepository, never()).delete(any());
        verify(nguoiDungRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa nhân viên - Exception xảy ra")
    void xoaNhanVien_Exception() {
        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        doThrow(new RuntimeException("Database error"))
                .when(nhanVienRepository).delete(any(NhanVien.class));

        boolean result = nhanVienService.xoaNhanVien(1L);

        assertFalse(result);
    }

    // ============================================================
    // PHẦN 4: TEST SỬA NHÂN VIÊN
    // ============================================================

    // 🔥 SỬA LỖI: Thêm mock findById cho tất cả test case sửa nhân viên

    // --- BRANCH: if (dto.getNgaySinh() == null) ---

    @Test
    @DisplayName("Sửa nhân viên - Ngày sinh null → throw IllegalArgumentException")
    void suaNhanVien_NgaySinhNull_ThrowException() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setNgaySinh(null);

        // ✅ QUAN TRỌNG: Phải mock findById trước
        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> nhanVienService.suaNhanVien(1L, dto));
        assertEquals("Ngày sinh không hợp lệ", exception.getMessage());
    }

    // --- BRANCH: if (!nd.getEmail().equals(dto.getEmail())) ---

    @Test
    @DisplayName("Sửa nhân viên - Email thay đổi và đã tồn tại")
    void suaNhanVien_EmailChanged_Exists_ThrowException() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setEmail("other@email.com");

        NguoiDung otherUser = new NguoiDung();
        otherUser.setMaNguoiDung(2L);

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(otherUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> nhanVienService.suaNhanVien(1L, dto));
        assertEquals("Email đã được sử dụng bởi người khác!", exception.getMessage());
    }

    @Test
    @DisplayName("Sửa nhân viên - Email thay đổi và chưa tồn tại")
    void suaNhanVien_EmailChanged_NotExists_Success() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setEmail("new@email.com");

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        // When
        NhanVien result = nhanVienService.suaNhanVien(1L, dto);

        // Then
        assertNotNull(result);
        verify(nguoiDungRepository, times(1)).save(any());
        verify(nhanVienRepository, times(1)).save(any());
    }

    // --- BRANCH: if (!nd.getSoDienThoai().equals(dto.getSoDienThoai())) ---

    @Test
    @DisplayName("Sửa nhân viên - SĐT thay đổi và đã tồn tại")
    void suaNhanVien_PhoneChanged_Exists_ThrowException() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setSoDienThoai("0999999999");

        NguoiDung otherUser = new NguoiDung();
        otherUser.setMaNguoiDung(2L);

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.of(otherUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> nhanVienService.suaNhanVien(1L, dto));
        assertEquals("Số điện thoại đã được sử dụng bởi người khác!", exception.getMessage());
    }

    @Test
    @DisplayName("Sửa nhân viên - SĐT thay đổi và chưa tồn tại")
    void suaNhanVien_PhoneChanged_NotExists_Success() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setSoDienThoai("0999999999");

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        // When
        NhanVien result = nhanVienService.suaNhanVien(1L, dto);

        // Then
        assertNotNull(result);
        verify(nguoiDungRepository, times(1)).save(any());
        verify(nhanVienRepository, times(1)).save(any());
    }

    // --- BRANCH: if (dto.getMatKhau() != null && !dto.getMatKhau().trim().isEmpty()) ---

    @Test
    @DisplayName("Sửa nhân viên - Mật khẩu được cập nhật")
    void suaNhanVien_PasswordUpdated_Success() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setMatKhau("NewPass123");

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("NewPass123")).thenReturn("newEncoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        // When
        nhanVienService.suaNhanVien(1L, dto);

        // Then
        verify(passwordEncoder, times(1)).encode("NewPass123");
    }

    @Test
    @DisplayName("Sửa nhân viên - Mật khẩu null → không encode")
    void suaNhanVien_PasswordNull_NotEncode() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setMatKhau(null);

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        // When
        nhanVienService.suaNhanVien(1L, dto);

        // Then
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Sửa nhân viên - Mật khẩu rỗng → không encode")
    void suaNhanVien_PasswordEmpty_NotEncode() {
        // Given
        NhanVienDTO dto = createUpdateDTO();
        dto.setMatKhau("");

        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));
        when(nguoiDungRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(dto.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        // When
        nhanVienService.suaNhanVien(1L, dto);

        // Then
        verify(passwordEncoder, never()).encode(anyString());
    }

    // --- BRANCH: if (nhanVienRepository.findById(id).orElseThrow(...)) ---

    @Test
    @DisplayName("Sửa nhân viên - Không tìm thấy → throw RuntimeException")
    void suaNhanVien_NotFound_ThrowException() {
        // Given
        NhanVienDTO dto = createUpdateDTO();

        when(nhanVienRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> nhanVienService.suaNhanVien(999L, dto));
        assertEquals("Không tìm thấy nhân viên", exception.getMessage());
    }

    // ============================================================
    // PHẦN 5: TEST CÁC PHƯƠNG THỨC KHÁC
    // ============================================================

    @Test
    @DisplayName("layNhanVienTheoMaNguoiDung - Tìm thấy")
    void layNhanVienTheoMaNguoiDung_Found() {
        when(nhanVienRepository.findByNguoiDung_MaNguoiDung(1L))
                .thenReturn(nhanVien);

        NhanVien result = nhanVienService.layNhanVienTheoMaNguoiDung(1L);

        assertNotNull(result);
        assertEquals(1L, result.getMaNhanVien());
    }

    @Test
    @DisplayName("layNhanVienTheoMaNguoiDung - Không tìm thấy")
    void layNhanVienTheoMaNguoiDung_NotFound() {
        when(nhanVienRepository.findByNguoiDung_MaNguoiDung(999L))
                .thenReturn(null);

        NhanVien result = nhanVienService.layNhanVienTheoMaNguoiDung(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("danhSachNhanVien - Trả về danh sách")
    void danhSachNhanVien_Success() {
        when(nhanVienRepository.findAll())
                .thenReturn(List.of(nhanVien));

        List<NhanVien> result = nhanVienService.danhSachNhanVien();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("danhSachNhanVienTheoTram - Tìm theo mã trạm")
    void danhSachNhanVienTheoTram_Success() {
        when(nhanVienRepository.findByMaTram(1L))
                .thenReturn(List.of(nhanVien));

        List<NhanVien> result = nhanVienService.danhSachNhanVienTheoTram(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMaTram());
    }

    @Test
    @DisplayName("layNhanVienTheoId - Tìm thấy")
    void layNhanVienTheoId_Found() {
        when(nhanVienRepository.findById(1L))
                .thenReturn(Optional.of(nhanVien));

        NhanVien result = nhanVienService.layNhanVienTheoId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getMaNhanVien());
    }

    @Test
    @DisplayName("layNhanVienTheoId - Không tìm thấy")
    void layNhanVienTheoId_NotFound() {
        when(nhanVienRepository.findById(999L))
                .thenReturn(Optional.empty());

        NhanVien result = nhanVienService.layNhanVienTheoId(999L);

        assertNull(result);
    }

    // ============================================================
    // PHẦN 6: PATH COVERAGE - Bao phủ đường dẫn
    // ============================================================

    @Test
    @DisplayName("PC-01: Path - Email exists → Exception")
    void pathCoverage_EmailExists_Exception() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.of(nguoiDung));

        assertThrows(RuntimeException.class,
                () -> nhanVienService.themNhanVien(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(nhanVienRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-02: Path - Email OK → Phone exists → Exception")
    void pathCoverage_EmailOk_PhoneExists_Exception() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.of(nguoiDung));

        assertThrows(RuntimeException.class,
                () -> nhanVienService.themNhanVien(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(nhanVienRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-03: Path - All valid → Create success")
    void pathCoverage_AllValid_CreateSuccess() {
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(nhanVienRepository.save(any())).thenReturn(nhanVien);

        NhanVien result = nhanVienService.themNhanVien(validDTO);

        assertNotNull(result);
        verify(nguoiDungRepository, times(1)).save(any());
        verify(nhanVienRepository, times(1)).save(any());
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private NhanVienDTO createUpdateDTO() {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setHoTen("Nguyen Van B");
        dto.setEmail("new@email.com");
        dto.setSoDienThoai("0977777777");
        dto.setGioiTinh("Nữ");
        dto.setMatKhau("NewPass123");
        dto.setNgaySinh(LocalDate.of(1995, 5, 5));
        dto.setBangCap("Thạc sĩ");
        dto.setKinhNghiem("5 năm");
        dto.setMaTram(2L);
        return dto;
    }
}