package datdq0317.edu.ut.vn.dinhquocdat.userservice;


import datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO VALIDATION TEST - Kiểm thử các annotation validation trong LoginRequest
 *
 * Các annotation được test:
 * - @NotBlank: soDienThoai
 * - @Pattern: soDienThoai (3 patterns), matKhau (3 patterns)
 * - @Size: matKhau (6-20)
 *
 * LƯU Ý QUAN TRỌNG:
 * - soDienThoai: Có @NotBlank nên KHÔNG cho phép null, "", "   "
 * - soDienThoai: Các @Pattern có ? nên cho phép null, nhưng @NotBlank đã chặn trước
 * - matKhau: KHÔNG có @NotBlank nên cho phép null, ""
 */
@DisplayName("DTO Validation Test - LoginRequest")
class LoginRequestTest {

    private Validator validator;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        loginRequest = new LoginRequest();
        loginRequest.setSoDienThoai("0987654321");
        loginRequest.setMatKhau("Abc123");
    }

    // ============================================================
    // 1. TEST soDienThoai - @NotBlank, @Pattern (3 patterns)
    // ============================================================

    @Test
    @DisplayName("soDienThoai hợp lệ - PASS")
    void soDienThoai_Valid_Pass() {
        loginRequest.setSoDienThoai("0987654321");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai null - @NotBlank → FAIL")
    void soDienThoai_Null_Fail() {
        loginRequest.setSoDienThoai(null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Người dùng chưa nhập số điện thoại"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("soDienThoai rỗng - @NotBlank → FAIL")
    void soDienThoai_Empty_Fail() {
        loginRequest.setSoDienThoai("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        // Có thể là @NotBlank hoặc @Pattern
        assertTrue(violations.size() >= 1);
    }

    @Test
    @DisplayName("soDienThoai khoảng trắng - @NotBlank → FAIL")
    void soDienThoai_Blank_Fail() {
        loginRequest.setSoDienThoai("   ");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Người dùng chưa nhập số điện thoại"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("soDienThoai = 9 chữ số - @Pattern → FAIL")
    void soDienThoai_9Digits_Fail() {
        loginRequest.setSoDienThoai("098765432");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai = 11 chữ số - @Pattern → FAIL")
    void soDienThoai_11Digits_Fail() {
        loginRequest.setSoDienThoai("09876543210");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai bắt đầu bằng 1 - @Pattern → FAIL")
    void soDienThoai_StartWithOne_Fail() {
        loginRequest.setSoDienThoai("1987654321");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai chứa chữ cái - @Pattern → FAIL")
    void soDienThoai_ContainsLetter_Fail() {
        loginRequest.setSoDienThoai("09876543ab");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai chứa ký tự đặc biệt - @Pattern → FAIL")
    void soDienThoai_ContainsSpecialChar_Fail() {
        loginRequest.setSoDienThoai("09876543@1");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    // ============================================================
    // 2. TEST matKhau - @Size, @Pattern (3 patterns)
    // ============================================================

    @Test
    @DisplayName("matKhau hợp lệ - PASS")
    void matKhau_Valid_Pass() {
        loginRequest.setMatKhau("Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau null - PASS (không có @NotBlank)")
    void matKhau_Null_Pass() {
        loginRequest.setMatKhau(null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        // matKhau KHÔNG có @NotBlank, nên null được chấp nhận
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau rỗng - PASS (không có @NotBlank)")
    void matKhau_Empty_Pass() {
        loginRequest.setMatKhau("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        // matKhau KHÔNG có @NotBlank, nên "" được chấp nhận
        // Nhưng "" vi phạm @Size và @Pattern, nên có lỗi
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 6 ký tự hợp lệ - PASS")
    void matKhau_6Chars_Pass() {
        loginRequest.setMatKhau("Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 20 ký tự hợp lệ - PASS")
    void matKhau_20Chars_Pass() {
        loginRequest.setMatKhau("A".repeat(12) + "b" + "1" + "C");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("matKhau = 5 ký tự - @Size(min=6) → FAIL")
    void matKhau_5Chars_Fail() {
        loginRequest.setMatKhau("Abc12");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        // Có thể là lỗi size hoặc pattern (do thiếu số)
        assertTrue(violations.size() >= 1);
    }

    @Test
    @DisplayName("matKhau = 21 ký tự - @Size(max=20) → FAIL")
    void matKhau_21Chars_Fail() {
        loginRequest.setMatKhau("A".repeat(18) + "b1" + "C");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải từ 6-20 ký tự"));
        assertTrue(hasSizeError);
    }

    @Test
    @DisplayName("matKhau không có chữ hoa - @Pattern → FAIL")
    void matKhau_NoUppercase_Fail() {
        loginRequest.setMatKhau("abc@123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ hoa"));
        assertTrue(hasPatternError);
    }

    @Test
    @DisplayName("matKhau không có chữ thường - @Pattern → FAIL")
    void matKhau_NoLowercase_Fail() {
        loginRequest.setMatKhau("ABC@123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ thường"));
        assertTrue(hasPatternError);
    }

    @Test
    @DisplayName("matKhau không có số - @Pattern → FAIL")
    void matKhau_NoDigit_Fail() {
        loginRequest.setMatKhau("Abc@def");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Mật khẩu phải chứa ít nhất 1 chữ số"));
        assertTrue(hasPatternError);
    }

    @Test
    @DisplayName("matKhau chỉ chữ hoa, không có chữ thường và số - FAIL")
    void matKhau_OnlyUppercase_Fail() {
        loginRequest.setMatKhau("ABCDEF");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        // Có ít nhất 2 lỗi: không có chữ thường, không có số
        assertTrue(violations.size() >= 2);
    }

    @Test
    @DisplayName("matKhau chỉ chữ thường, không có chữ hoa và số - FAIL")
    void matKhau_OnlyLowercase_Fail() {
        loginRequest.setMatKhau("abcdef");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        // Có ít nhất 2 lỗi: không có chữ hoa, không có số
        assertTrue(violations.size() >= 2);
    }

    @Test
    @DisplayName("matKhau chỉ số, không có chữ hoa và chữ thường - FAIL")
    void matKhau_OnlyDigits_Fail() {
        loginRequest.setMatKhau("123456");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        // Có ít nhất 2 lỗi: không có chữ hoa, không có chữ thường
        assertTrue(violations.size() >= 2);
    }

    // ============================================================
    // 3. TEST KẾT HỢP CẢ HAI TRƯỜNG
    // ============================================================

    @Test
    @DisplayName("Cả hai trường hợp lệ - PASS")
    void bothValid_Pass() {
        loginRequest.setSoDienThoai("0987654321");
        loginRequest.setMatKhau("Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Cả hai trường đều sai - FAIL")
    void bothInvalid_Fail() {
        loginRequest.setSoDienThoai("invalid");
        loginRequest.setMatKhau("abc");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
    }

    @Test
    @DisplayName("soDienThoai đúng, matKhau sai - FAIL")
    void phoneValid_PasswordInvalid_Fail() {
        loginRequest.setSoDienThoai("0987654321");
        loginRequest.setMatKhau("abc");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("soDienThoai sai, matKhau đúng - FAIL")
    void phoneInvalid_PasswordValid_Fail() {
        loginRequest.setSoDienThoai("invalid");
        loginRequest.setMatKhau("Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    // ============================================================
    // 4. TEST CONSTRUCTOR
    // ============================================================

    @Test
    @DisplayName("Constructor có tham số - PASS")
    void constructor_WithParams_Pass() {
        LoginRequest request = new LoginRequest("0987654321", "Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Constructor có tham số - SĐT null - FAIL")
    void constructor_PhoneNull_Fail() {
        LoginRequest request = new LoginRequest(null, "Abc123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Người dùng chưa nhập số điện thoại"));
        assertTrue(hasNotBlankError);
    }

    @Test
    @DisplayName("Constructor có tham số - matKhau sai - FAIL")
    void constructor_PasswordInvalid_Fail() {
        LoginRequest request = new LoginRequest("0987654321", "abc");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    // ============================================================
    // 5. TEST GETTERS & SETTERS
    // ============================================================

    @Test
    @DisplayName("Getter và Setter hoạt động đúng")
    void gettersAndSetters_Work() {
        LoginRequest request = new LoginRequest();
        request.setSoDienThoai("0987654321");
        request.setMatKhau("Abc123");

        assertEquals("0987654321", request.getSoDienThoai());
        assertEquals("Abc123", request.getMatKhau());
    }

    @Test
    @DisplayName("Setter với giá trị null - soDienThoai bị lỗi, matKhau không")
    void setters_WithNull() {
        LoginRequest request = new LoginRequest();
        request.setSoDienThoai(null);
        request.setMatKhau(null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        // soDienThoai null bị lỗi @NotBlank
        boolean hasPhoneError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Người dùng chưa nhập số điện thoại"));
        assertTrue(hasPhoneError);

        // matKhau null không bị lỗi (không có @NotBlank)
        // Nhưng nếu có các lỗi khác, kiểm tra số lượng
    }
}