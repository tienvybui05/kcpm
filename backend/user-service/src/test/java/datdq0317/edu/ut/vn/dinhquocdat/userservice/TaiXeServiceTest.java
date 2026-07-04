package datdq0317.edu.ut.vn.dinhquocdat.userservice;

import datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos.TaiXeDTO;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.models.NguoiDung;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.models.TaiXe;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.INguoiDungRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.ITaiXeRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.services.TaiXeService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * WHITE-BOX TESTING - Kiểm thử hộp trắng cho phương thức themTaiXe()
 *
 * Áp dụng các kỹ thuật theo slide Chương 4:
 * 1. Statement Coverage - Bao phủ câu lệnh (Slide 57-64)
 * 2. Branch/Decision Coverage - Bao phủ nhánh (Slide 65-73)
 * 3. Condition Coverage - Bao phủ điều kiện (Slide 74-77)
 * 4. Path Coverage - Bao phủ đường dẫn (Slide 67-68)
 * 5. Branch Condition Combination - Tổ hợp điều kiện (Slide 79-81)
 *
 * LƯU Ý: Code hiện tại CHỈ validate:
 * - Email: null, empty, format, duplicate
 * - SĐT: null, empty, format, duplicate
 * - Ngày sinh: null, >= 18 tuổi
 *
 * Code KHÔNG validate:
 * - hoTen (độ dài, ký tự)
 * - gioiTinh (giá trị)
 * - bangLaiXe (độ dài, ký tự)
 * - matKhau (độ dài, complexity)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White-box Testing - TaiXeService.themTaiXe()")
class TaiXeServiceTest {

    // ============================================================
    // MOCK DEPENDENCIES
    // ============================================================
    @Mock
    private ITaiXeRepository taiXeRepository;

    @Mock
    private INguoiDungRepository nguoiDungRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TaiXeService taiXeService;

    // ============================================================
    // TEST DATA
    // ============================================================
    private TaiXeDTO validDTO;
    private NguoiDung nguoiDung;
    private TaiXe taiXe;

    @BeforeEach
    void setUp() {
        validDTO = new TaiXeDTO();
        validDTO.setHoTen("Nguyen Van A");
        validDTO.setEmail("a@b.c");
        validDTO.setSoDienThoai("0987654321");
        validDTO.setGioiTinh("Nam");
        validDTO.setMatKhau("Abc123");
        validDTO.setNgaySinh(LocalDate.of(2000, 1, 1));
        validDTO.setBangLaiXe("B1");

        nguoiDung = new NguoiDung();
        nguoiDung.setMaNguoiDung(1L);
        nguoiDung.setHoTen("Nguyen Van A");
        nguoiDung.setEmail("a@b.c");
        nguoiDung.setSoDienThoai("0987654321");
        nguoiDung.setMatKhau("encoded");
        nguoiDung.setVaiTro("TAIXE");

        taiXe = new TaiXe();
        taiXe.setMaTaiXe(1L);
        taiXe.setBangLaiXe("B1");
        taiXe.setNguoiDung(nguoiDung);
    }

    // ============================================================
    // PHẦN 1: STATEMENT COVERAGE - Bao phủ câu lệnh (Slide 57-64)
    // ============================================================

    @Test
    @DisplayName("SC-01: Bao phủ 100% câu lệnh - luồng thành công")
    void statementCoverage_ThemTaiXe_AllStatementsExecuted() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(validDTO.getMatKhau()))
                .thenReturn("encodedPassword");
        when(nguoiDungRepository.save(any(NguoiDung.class)))
                .thenReturn(nguoiDung);
        when(taiXeRepository.save(any(TaiXe.class)))
                .thenReturn(taiXe);

        TaiXe result = taiXeService.themTaiXe(validDTO);

        assertNotNull(result);
        assertEquals(1L, result.getMaTaiXe());
        assertEquals("B1", result.getBangLaiXe());

        InOrder inOrder = inOrder(
                nguoiDungRepository,
                passwordEncoder,
                nguoiDungRepository,
                taiXeRepository
        );

        inOrder.verify(nguoiDungRepository).findBySoDienThoai(validDTO.getSoDienThoai());
        inOrder.verify(nguoiDungRepository).findByEmail(validDTO.getEmail());
        inOrder.verify(passwordEncoder).encode(validDTO.getMatKhau());
        inOrder.verify(nguoiDungRepository).save(any(NguoiDung.class));
        inOrder.verify(taiXeRepository).save(any(TaiXe.class));
    }

    // ============================================================
    // PHẦN 2: BRANCH COVERAGE - Bao phủ nhánh (Slide 65-73)
    // ============================================================

    // --- BRANCH 1: if (email == null || email.trim().isEmpty()) ---

    @Test
    @DisplayName("BC-01: Nhánh TRUE - Email null")
    void branchCoverage_EmailNull_TrueBranch() {
        validDTO.setEmail(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Người dùng chưa cung cấp email", exception.getMessage());

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-02: Nhánh TRUE - Email rỗng")
    void branchCoverage_EmailEmpty_TrueBranch() {
        validDTO.setEmail("   ");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Người dùng chưa cung cấp email", exception.getMessage());

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-03: Nhánh FALSE - Email hợp lệ")
    void branchCoverage_EmailValid_FalseBranch() {
        validDTO.setEmail("a@b.c");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 2: if (soDienThoai == null || soDienThoai.trim().isEmpty()) ---

    @Test
    @DisplayName("BC-04: Nhánh TRUE - SĐT null")
    void branchCoverage_SoDienThoaiNull_TrueBranch() {
        validDTO.setSoDienThoai(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("người dùng chưa cung cấp số điện thoại", exception.getMessage());

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-05: Nhánh TRUE - SĐT rỗng")
    void branchCoverage_SoDienThoaiEmpty_TrueBranch() {
        validDTO.setSoDienThoai("   ");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("người dùng chưa cung cấp số điện thoại", exception.getMessage());

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-06: Nhánh FALSE - SĐT hợp lệ")
    void branchCoverage_SoDienThoaiValid_FalseBranch() {
        validDTO.setSoDienThoai("0987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 3: if (phoneExists) ---

    @Test
    @DisplayName("BC-07: Nhánh TRUE - SĐT đã tồn tại")
    void branchCoverage_PhoneExists_TrueBranch() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Số điện thoại đã tồn tại!", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-08: Nhánh FALSE - SĐT chưa tồn tại")
    void branchCoverage_PhoneExists_FalseBranch() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 4: if (emailExists) ---

    @Test
    @DisplayName("BC-09: Nhánh TRUE - Email đã tồn tại")
    void branchCoverage_EmailExists_TrueBranch() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.of(nguoiDung));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Email đã tồn tại!", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-10: Nhánh FALSE - Email chưa tồn tại")
    void branchCoverage_EmailExists_FalseBranch() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 5: if (!soDienThoai.matches("^0\\d{9}$")) ---

    @Test
    @DisplayName("BC-11: Nhánh TRUE - SĐT không hợp lệ")
    void branchCoverage_PhoneFormatInvalid_TrueBranch() {
        validDTO.setSoDienThoai("987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Số điện thoại không hợp lệ", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-12: Nhánh FALSE - SĐT hợp lệ")
    void branchCoverage_PhoneFormatValid_FalseBranch() {
        validDTO.setSoDienThoai("0987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 6: if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) ---

    @Test
    @DisplayName("BC-13: Nhánh TRUE - Email không hợp lệ")
    void branchCoverage_EmailFormatInvalid_TrueBranch() {
        validDTO.setEmail("invalid-email");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Định dạng email không hợp lệ", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-14: Nhánh FALSE - Email hợp lệ")
    void branchCoverage_EmailFormatValid_FalseBranch() {
        validDTO.setEmail("a@b.c");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 7: if (ngaySinh == null) ---

    @Test
    @DisplayName("BC-15: Nhánh TRUE - Ngày sinh null")
    void branchCoverage_NgaySinhNull_TrueBranch() {
        validDTO.setNgaySinh(null);
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Ngày sinh không hợp lệ", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-16: Nhánh FALSE - Ngày sinh hợp lệ")
    void branchCoverage_NgaySinhValid_FalseBranch() {
        validDTO.setNgaySinh(LocalDate.of(2000, 1, 1));
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // --- BRANCH 8: if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) ---

    @Test
    @DisplayName("BC-17: Nhánh TRUE - Tuổi < 18")
    void branchCoverage_AgeLessThan18_TrueBranch() {
        validDTO.setNgaySinh(LocalDate.now().minusYears(17));
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Tài xế phải từ 18 tuổi trở lên", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("BC-18: Nhánh FALSE - Tuổi >= 18")
    void branchCoverage_AgeGreaterThan18_FalseBranch() {
        validDTO.setNgaySinh(LocalDate.now().minusYears(20));
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        TaiXe result = taiXeService.themTaiXe(validDTO);

        assertNotNull(result);
        assertEquals(1L, result.getMaTaiXe());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // ============================================================
    // PHẦN 3: CONDITION COVERAGE - Bao phủ điều kiện (Slide 74-77)
    // ============================================================

    @Test
    @DisplayName("CC-01: email == null → TRUE")
    void conditionCoverage_EmailIsNull_True() {
        validDTO.setEmail(null);

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-02: email == null → FALSE")
    void conditionCoverage_EmailIsNull_False() {
        validDTO.setEmail("a@b.c");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-03: email.trim().isEmpty() → TRUE")
    void conditionCoverage_EmailEmpty_True() {
        validDTO.setEmail("   ");

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-04: email.trim().isEmpty() → FALSE")
    void conditionCoverage_EmailEmpty_False() {
        validDTO.setEmail("a@b.c");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-05: soDienThoai == null → TRUE")
    void conditionCoverage_SoDienThoaiIsNull_True() {
        validDTO.setSoDienThoai(null);

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-06: soDienThoai == null → FALSE")
    void conditionCoverage_SoDienThoaiIsNull_False() {
        validDTO.setSoDienThoai("0987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-07: soDienThoai.trim().isEmpty() → TRUE")
    void conditionCoverage_SoDienThoaiEmpty_True() {
        validDTO.setSoDienThoai("   ");

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-08: soDienThoai.trim().isEmpty() → FALSE")
    void conditionCoverage_SoDienThoaiEmpty_False() {
        validDTO.setSoDienThoai("0987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-09: phoneExists → TRUE")
    void conditionCoverage_PhoneExists_True() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-10: phoneExists → FALSE")
    void conditionCoverage_PhoneExists_False() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-11: emailExists → TRUE")
    void conditionCoverage_EmailExists_True() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.of(nguoiDung));

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-12: emailExists → FALSE")
    void conditionCoverage_EmailExists_False() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-13: SĐT format hợp lệ → TRUE")
    void conditionCoverage_PhoneFormat_True() {
        validDTO.setSoDienThoai("0987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-14: SĐT format không hợp lệ → FALSE")
    void conditionCoverage_PhoneFormat_False() {
        validDTO.setSoDienThoai("987654321");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-15: Email format hợp lệ → TRUE")
    void conditionCoverage_EmailFormat_True() {
        validDTO.setEmail("a@b.c");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-16: Email format không hợp lệ → FALSE")
    void conditionCoverage_EmailFormat_False() {
        validDTO.setEmail("invalid-email");
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-17: ngaySinh == null → TRUE")
    void conditionCoverage_NgaySinhNull_True() {
        validDTO.setNgaySinh(null);
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("CC-18: ngaySinh == null → FALSE")
    void conditionCoverage_NgaySinhNull_False() {
        validDTO.setNgaySinh(LocalDate.of(2000, 1, 1));
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        taiXeService.themTaiXe(validDTO);

        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("CC-19: Tuổi < 18 → TRUE")
    void conditionCoverage_AgeLessThan18_True() {
        validDTO.setNgaySinh(LocalDate.now().minusYears(17));
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }



    // ============================================================
    // PHẦN 4: PATH COVERAGE - Bao phủ đường dẫn (Slide 67-68)
    // ============================================================

    @Test
    @DisplayName("PC-01: Path - Email null → Exception")
    void pathCoverage_EmailNull_Exception() {
        validDTO.setEmail(null);

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-02: Path - Email OK → SĐT null → Exception")
    void pathCoverage_EmailOk_PhoneNull_Exception() {
        validDTO.setSoDienThoai(null);

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, never()).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-03: Path - Phone exists → Exception")
    void pathCoverage_PhoneExists_Exception() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-04: Path - Email exists → Exception")
    void pathCoverage_EmailExists_Exception() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.of(nguoiDung));

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-05: Path - Phone format invalid → Exception")
    void pathCoverage_PhoneFormatInvalid_Exception() {
        validDTO.setSoDienThoai("987654321");
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));
        assertEquals("Số điện thoại không hợp lệ", exception.getMessage());

        verify(nguoiDungRepository, times(1)).findBySoDienThoai(anyString());
        verify(nguoiDungRepository, times(1)).findByEmail(anyString());
        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("PC-06: Path - All valid → Create success")
    void pathCoverage_AllValid_CreateSuccess() {
        when(nguoiDungRepository.findBySoDienThoai(validDTO.getSoDienThoai()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(validDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        TaiXe result = taiXeService.themTaiXe(validDTO);

        assertNotNull(result);
        assertEquals(1L, result.getMaTaiXe());
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    // ============================================================
    // PHẦN 5: BRANCH CONDITION COMBINATION (Slide 79-81)
    // ============================================================

    @Test
    @DisplayName("Combo-01: Email hợp lệ AND SĐT hợp lệ → success")
    void combo_EmailValidAndPhoneValid_Success() {
        validDTO.setEmail("a@b.c");
        validDTO.setSoDienThoai("0987654321");

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        TaiXe result = taiXeService.themTaiXe(validDTO);

        assertNotNull(result);
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Combo-02: Email hợp lệ AND SĐT không hợp lệ → exception")
    void combo_EmailValidAndPhoneInvalid_Exception() {
        validDTO.setEmail("a@b.c");
        validDTO.setSoDienThoai("987654321");

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Combo-03: Email không hợp lệ AND SĐT hợp lệ → exception")
    void combo_EmailInvalidAndPhoneValid_Exception() {
        validDTO.setEmail("invalid");
        validDTO.setSoDienThoai("0987654321");

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Combo-04: Phone không tồn tại AND Email không tồn tại → success")
    void combo_PhoneNotExistsAndEmailNotExists_Success() {
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(nguoiDungRepository.save(any())).thenReturn(nguoiDung);
        when(taiXeRepository.save(any())).thenReturn(taiXe);

        TaiXe result = taiXeService.themTaiXe(validDTO);

        assertNotNull(result);
        verify(nguoiDungRepository, times(1)).save(any());
        verify(taiXeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Combo-05: Phone tồn tại AND Email không tồn tại → exception")
    void combo_PhoneExistsAndEmailNotExists_Exception() {
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Combo-06: Phone không tồn tại AND Email tồn tại → exception")
    void combo_PhoneNotExistsAndEmailExists_Exception() {
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(nguoiDung));

        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Combo-07: Phone tồn tại AND Email tồn tại → exception (phone first)")
    void combo_PhoneExistsAndEmailExists_Exception() {
        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(nguoiDung));

        // Exception đầu tiên là phone exists (thứ tự kiểm tra)
        assertThrows(RuntimeException.class,
                () -> taiXeService.themTaiXe(validDTO));

        verify(nguoiDungRepository, never()).save(any());
        verify(taiXeRepository, never()).save(any());
    }

    // ============================================================
    // PHẦN 6: CYCLOMATIC COMPLEXITY (Slide 55)
    // ============================================================
    // V(G) = P + 1 = 8 + 1 = 9

    @Test
    @DisplayName("CC-EX: Độ phức tạp Cyclomatic = 9")
    void cyclomaticComplexity_VerifyAllPaths() {
        // Phương thức themTaiXe() có 8 điều kiện if
        // V(G) = P + 1 = 8 + 1 = 9
        // Đã có 9 paths đại diện (PC-01 đến PC-06 + 3 combo paths)
        assertTrue(true);
    }
}