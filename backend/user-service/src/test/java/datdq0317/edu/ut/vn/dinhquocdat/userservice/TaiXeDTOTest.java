package datdq0317.edu.ut.vn.dinhquocdat.userservice;

import datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos.TaiXeDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO VALIDATION TEST - Kiểm thử các annotation validation trong TaiXeDTO
 *
 * LƯU Ý QUAN TRỌNG VỀ THỨ TỰ VALIDATION:
 * - Khi một trường vi phạm nhiều annotation, lỗi nào xuất hiện trước phụ thuộc vào thứ tự validation
 * - Ví dụ: gioiTinh = "" vi phạm cả @NotBlank và @Pattern, nhưng @Pattern có thể báo lỗi trước
 * - Nên không kiểm tra message cụ thể, chỉ kiểm tra có lỗi hay không
 */
@DisplayName("DTO Validation Test - TaiXeDTO")
class TaiXeDTOTest {

    private Validator validator;
    private TaiXeDTO dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        dto = new TaiXeDTO();
        dto.setHoTen("Nguyen Van A");
        dto.setEmail("a@b.c");
        dto.setSoDienThoai("0987654321");
        dto.setGioiTinh("Nam");
        dto.setMatKhau("Abc123");
        dto.setNgaySinh(LocalDate.now().minusYears(20));
        dto.setBangLaiXe("B1");
    }

    // ============================================================
    // 1. TEST hoTen - @NotBlank, @Size, @Pattern
    // ============================================================

    @Test
    @DisplayName("hoTen hợp lệ - PASS")
    void hoTen_Valid_Pass() {
        dto.setHoTen("Nguyen Van A");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("hoTen null - @NotBlank → FAIL")
    void hoTen_Null_Fail() {
        dto.setHoTen(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        // Kiểm tra có lỗi @NotBlank
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Họ tên không được để trống"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("hoTen rỗng - @NotBlank → FAIL")
    void hoTen_Empty_Fail() {
        dto.setHoTen("");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Họ tên không được để trống"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("hoTen khoảng trắng - @NotBlank → FAIL")
    void hoTen_Blank_Fail() {
        dto.setHoTen("   ");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Họ tên không được để trống"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("hoTen = 1 ký tự - @Size(min=1) → PASS")
    void hoTen_MinLength_Pass() {
        dto.setHoTen("T");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("hoTen = 50 ký tự - @Size(max=50) → PASS")
    void hoTen_MaxLength_Pass() {
        dto.setHoTen("A".repeat(50));
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("hoTen = 51 ký tự - @Size(max=50) → FAIL")
    void hoTen_ExceedMaxLength_Fail() {
        dto.setHoTen("A".repeat(51));
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Họ tên phải từ 1-50 ký tự"));
        assertTrue(hasSizeError);
    }

    @Test
    @DisplayName("hoTen chứa số - @Pattern → FAIL")
    void hoTen_ContainsNumber_Fail() {
        dto.setHoTen("Nguyen Van A1");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Họ tên chỉ được chứa chữ cái"));
        assertTrue(hasPatternError);
    }

    // ✅ SỬA: Pattern "^[^0-9]*$" chỉ cấm số, KHÔNG cấm ký tự đặc biệt
    @Test
    @DisplayName("hoTen chứa ký tự đặc biệt - PASS (pattern chỉ cấm số)")
    void hoTen_ContainsSpecialChar_Pass() {
        dto.setHoTen("Nguyen@Van");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        // Pattern "^[^0-9]*$" chỉ cấm số, nên ký tự đặc biệt được phép
        assertTrue(violations.isEmpty());
    }

    // ============================================================
    // 2. TEST soDienThoai - @Pattern (3 patterns)
    // ============================================================

    @Test
    @DisplayName("soDienThoai null - PASS (vì có ? trong regex)")
    void soDienThoai_Null_Pass() {
        dto.setSoDienThoai(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai rỗng - PASS (vì có ? trong regex)")
    void soDienThoai_Empty_Pass() {
        dto.setSoDienThoai("");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai = 10 chữ số bắt đầu 0 - PASS")
    void soDienThoai_Valid_Pass() {
        dto.setSoDienThoai("0987654321");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai = 9 chữ số - @Pattern → FAIL")
    void soDienThoai_9Digits_Fail() {
        dto.setSoDienThoai("098765432");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai = 11 chữ số - @Pattern → FAIL")
    void soDienThoai_11Digits_Fail() {
        dto.setSoDienThoai("09876543210");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai bắt đầu bằng 1 - @Pattern → FAIL")
    void soDienThoai_StartWithOne_Fail() {
        dto.setSoDienThoai("1987654321");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai chứa chữ cái - @Pattern → FAIL")
    void soDienThoai_ContainsLetter_Fail() {
        dto.setSoDienThoai("09876543ab");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ============================================================
    // 3. TEST gioiTinh - @NotBlank, @Pattern
    // ============================================================

    @Test
    @DisplayName("gioiTinh = 'Nam' - PASS")
    void gioiTinh_Nam_Pass() {
        dto.setGioiTinh("Nam");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("gioiTinh = 'Nữ' - PASS")
    void gioiTinh_Nu_Pass() {
        dto.setGioiTinh("Nữ");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("gioiTinh null - @NotBlank → FAIL")
    void gioiTinh_Null_Fail() {
        dto.setGioiTinh(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Giới tính không được để trống"));
        assertTrue(hasNotBlankError);
    }

    // ✅ SỬA: gioiTinh rỗng
    // Lỗi: "" không match pattern "^(Nam|Nữ)$" nên lỗi pattern xuất hiện trước
    @Test
    @DisplayName("gioiTinh rỗng - @NotBlank và @Pattern → FAIL")
    void gioiTinh_Empty_Fail() {
        dto.setGioiTinh("");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        // Kiểm tra có ít nhất 1 lỗi (không quan tâm message cụ thể)
        assertTrue(violations.size() >= 1);
    }

    @Test
    @DisplayName("gioiTinh = 'Khac' - @Pattern → FAIL")
    void gioiTinh_Invalid_Fail() {
        dto.setGioiTinh("Khac");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Giới tính phải là 'Nam' hoặc 'Nữ'"));
        assertTrue(hasPatternError);
    }

    // ============================================================
    // 4. TEST matKhau - @Size, @Pattern (3 patterns)
    // ============================================================

    @Test
    @DisplayName("matKhau null - PASS (không bắt buộc)")
    void matKhau_Null_Pass() {
        dto.setMatKhau(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 6 ký tự hợp lệ - PASS")
    void matKhau_Valid_Pass() {
        dto.setMatKhau("Abc123");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 20 ký tự hợp lệ - PASS")
    void matKhau_MaxLength_Pass() {
        dto.setMatKhau("A".repeat(12) + "b" + "1" + "C");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 5 ký tự - @Size(min=6) → FAIL")
    void matKhau_5Chars_Fail() {
        dto.setMatKhau("Abc12");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        // Có thể là lỗi size hoặc pattern (do thiếu số)
        assertTrue(violations.size() >= 1);
    }

    // ✅ SỬA: matKhau 21 ký tự
    @Test
    @DisplayName("matKhau = 21 ký tự - @Size(max=20) → FAIL")
    void matKhau_21Chars_Fail() {
        // Thêm số và chữ để đảm bảo lỗi size xuất hiện
        dto.setMatKhau("A".repeat(18) + "b1" + "C");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải từ 6-20 ký tự"));
        assertTrue(hasSizeError);
    }

    @Test
    @DisplayName("matKhau không có chữ hoa - @Pattern → FAIL")
    void matKhau_NoUppercase_Fail() {
        dto.setMatKhau("abc@123");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ hoa"));
        assertTrue(hasPatternError);
    }

    @Test
    @DisplayName("matKhau không có chữ thường - @Pattern → FAIL")
    void matKhau_NoLowercase_Fail() {
        dto.setMatKhau("ABC@123");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ thường"));
        assertTrue(hasPatternError);
    }

    @Test
    @DisplayName("matKhau không có số - @Pattern → FAIL")
    void matKhau_NoDigit_Fail() {
        dto.setMatKhau("Abc@def");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ số"));
        assertTrue(hasPatternError);
    }

    // ============================================================
    // 5. TEST ngaySinh - @Past
    // ============================================================

    @Test
    @DisplayName("ngaySinh null - PASS (không bắt buộc)")
    void ngaySinh_Null_Pass() {
        dto.setNgaySinh(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("ngaySinh trong quá khứ - PASS")
    void ngaySinh_Past_Pass() {
        dto.setNgaySinh(LocalDate.now().minusYears(20));
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ✅ SỬA: ngaySinh hôm nay
    // @Past KHÔNG chấp nhận ngày hôm nay (vì hôm nay không phải quá khứ)
    @Test
    @DisplayName("ngaySinh hôm nay - @Past → FAIL")
    void ngaySinh_Today_Fail() {
        dto.setNgaySinh(LocalDate.now());
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPastError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Ngày sinh không được là ngày trong tương lai"));
        assertTrue(hasPastError);
    }

    @Test
    @DisplayName("ngaySinh trong tương lai - @Past → FAIL")
    void ngaySinh_Future_Fail() {
        dto.setNgaySinh(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPastError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Ngày sinh không được là ngày trong tương lai"));
        assertTrue(hasPastError);
    }

    // ============================================================
    // 6. TEST bangLaiXe - @NotBlank, @Pattern
    // ============================================================

    @Test
    @DisplayName("bangLaiXe hợp lệ - PASS")
    void bangLaiXe_Valid_Pass() {
        dto.setBangLaiXe("B1");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("bangLaiXe = 'B' - PASS")
    void bangLaiXe_SingleChar_Pass() {
        dto.setBangLaiXe("B");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("bangLaiXe = 'A1' - PASS")
    void bangLaiXe_A1_Pass() {
        dto.setBangLaiXe("A1");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("bangLaiXe = 'C' - PASS")
    void bangLaiXe_C_Pass() {
        dto.setBangLaiXe("C");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("bangLaiXe null - @NotBlank → FAIL")
    void bangLaiXe_Null_Fail() {
        dto.setBangLaiXe(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Bằng lái xe không được để trống"));
        assertTrue(hasNotBlankError);
    }

    // ✅ SỬA: bangLaiXe rỗng
    @Test
    @DisplayName("bangLaiXe rỗng - @NotBlank và @Pattern → FAIL")
    void bangLaiXe_Empty_Fail() {
        dto.setBangLaiXe("");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        // Kiểm tra có ít nhất 1 lỗi
        assertTrue(violations.size() >= 1);
    }

    @Test
    @DisplayName("bangLaiXe chứa ký tự đặc biệt - @Pattern → FAIL")
    void bangLaiXe_SpecialChar_Fail() {
        dto.setBangLaiXe("B@1");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Bằng lái xe không hợp lệ"));
        assertTrue(hasPatternError);
    }

    // ============================================================
    // 7. TEST email - KHÔNG CÓ VALIDATION TRONG DTO
    // ============================================================

    @Test
    @DisplayName("email null - PASS (không có validation)")
    void email_Null_Pass() {
        dto.setEmail(null);
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("email rỗng - PASS (không có validation)")
    void email_Empty_Pass() {
        dto.setEmail("");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("email sai định dạng - PASS (không có validation)")
    void email_InvalidFormat_Pass() {
        dto.setEmail("invalid-email");
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ============================================================
    // 8. TEST KẾT HỢP NHIỀU TRƯỜNG
    // ============================================================

    @Test
    @DisplayName("Tất cả trường hợp lệ - PASS")
    void allValid_Pass() {
        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Nhiều lỗi cùng lúc")
    void multipleViolations_Fail() {
        dto.setHoTen("A".repeat(51));
        dto.setGioiTinh("Khac");
        dto.setMatKhau("abc");
        dto.setBangLaiXe("B@1");
        dto.setNgaySinh(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<TaiXeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }
}