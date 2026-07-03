package ngocvct0133.ut.edu.feedbackservice.services;

import ngocvct0133.ut.edu.feedbackservice.modules.FcmToken;
import ngocvct0133.ut.edu.feedbackservice.repositories.IFcmTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FcmTokenService - Whitebox Tests")
class FcmTokenServiceTest {

    @Mock
    private IFcmTokenRepository repo;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    // =========================================================================
    // saveToken
    // =========================================================================
    @Nested
    @DisplayName("saveToken()")
    class SaveToken {

        @Test
        @DisplayName("TC01 - Token đã tồn tại → cập nhật token + role + createdAt")
        void saveToken_daTonTai_capNhat() {
            // Arrange – nhánh existing.isPresent() = true
            FcmToken existingToken = new FcmToken(1L, "old-token", "TAIXE");
            existingToken.setId(10L);
            existingToken.setCreatedAt(1000L);

            when(repo.findFirstByMaNguoiDung(1L)).thenReturn(Optional.of(existingToken));
            when(repo.save(any(FcmToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            FcmToken result = fcmTokenService.saveToken(1L, "ADMIN", "new-token");

            // Assert – kiểm tra token và role đã được cập nhật
            assertEquals("new-token", result.getToken());
            assertEquals("ADMIN", result.getUserRole());
            assertNotEquals(1000L, result.getCreatedAt()); // createdAt đã được đổi
            assertEquals(10L, result.getId()); // ID giữ nguyên

            verify(repo).findFirstByMaNguoiDung(1L);
            verify(repo).save(existingToken); // save đối tượng existing, không phải new
        }

        @Test
        @DisplayName("TC02 - Token chưa tồn tại → tạo mới FcmToken")
        void saveToken_chuaTonTai_taoMoi() {
            // Arrange – nhánh existing.isPresent() = false
            when(repo.findFirstByMaNguoiDung(2L)).thenReturn(Optional.empty());

            FcmToken newToken = new FcmToken(2L, "brand-new-token", "TAIXE");
            when(repo.save(any(FcmToken.class))).thenReturn(newToken);

            // Act
            FcmToken result = fcmTokenService.saveToken(2L, "TAIXE", "brand-new-token");

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.getMaNguoiDung());
            assertEquals("brand-new-token", result.getToken());
            assertEquals("TAIXE", result.getUserRole());

            verify(repo).findFirstByMaNguoiDung(2L);
            // Verify save được gọi với một FcmToken mới (constructor)
            verify(repo).save(argThat(token ->
                token.getMaNguoiDung().equals(2L) &&
                token.getToken().equals("brand-new-token") &&
                token.getUserRole().equals("TAIXE")
            ));
        }
    }

    // =========================================================================
    // getTokenByMaNguoiDung
    // =========================================================================
    @Nested
    @DisplayName("getTokenByMaNguoiDung()")
    class GetTokenByMaNguoiDung {

        @Test
        @DisplayName("TC03 - Tìm thấy token → trả về FcmToken")
        void getToken_timThay() {
            // Arrange – nhánh Optional có giá trị → orElse không kích hoạt
            FcmToken token = new FcmToken(1L, "test-token", "ADMIN");
            when(repo.findFirstByMaNguoiDung(1L)).thenReturn(Optional.of(token));

            // Act
            FcmToken result = fcmTokenService.getTokenByMaNguoiDung(1L);

            // Assert
            assertNotNull(result);
            assertEquals("test-token", result.getToken());
            assertEquals("ADMIN", result.getUserRole());
        }

        @Test
        @DisplayName("TC04 - Không tìm thấy token → return null")
        void getToken_khongTimThay_returnNull() {
            // Arrange – nhánh Optional.empty → orElse(null) trả null
            when(repo.findFirstByMaNguoiDung(99L)).thenReturn(Optional.empty());

            // Act
            FcmToken result = fcmTokenService.getTokenByMaNguoiDung(99L);

            // Assert
            assertNull(result);
        }
    }
}
