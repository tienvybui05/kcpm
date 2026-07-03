package ngocvct0133.ut.edu.feedbackservice.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService - Whitebox Tests")
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    // =========================================================================
    // sendNotification
    // =========================================================================
    @Nested
    @DisplayName("sendNotification()")
    class SendNotificationTests {

        @Test
        @DisplayName("TC01 - Gửi notification thành công → Firebase trả response")
        void sendNotification_thanhCong() throws Exception {
            // Arrange – nhánh try thành công
            try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
                mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);
                when(mockMessaging.send(any(Message.class))).thenReturn("projects/test/messages/456");

                // Act
                assertDoesNotThrow(() ->
                    notificationService.sendNotification("validToken", "Tiêu đề", "Nội dung")
                );

                // Assert
                verify(mockMessaging).send(any(Message.class));
            }
        }

        @Test
        @DisplayName("TC02 - Firebase throw exception → catch và log lỗi, không throw ra ngoài")
        void sendNotification_exception_catch() throws Exception {
            // Arrange – nhánh catch Exception
            try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
                mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);
                when(mockMessaging.send(any(Message.class)))
                        .thenThrow(new RuntimeException("Simulated Firebase failure"));

                // Act & Assert – exception được catch bên trong, không throw ra ngoài
                assertDoesNotThrow(() ->
                    notificationService.sendNotification("invalidToken", "Title", "Body")
                );

                verify(mockMessaging).send(any(Message.class));
            }
        }

        @Test
        @DisplayName("TC03 - FirebaseMessaging.getInstance() throw → catch exception")
        void sendNotification_getInstanceFail() {
            // Arrange – nhánh getInstance() throw exception
            try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
                mockedStatic.when(FirebaseMessaging::getInstance)
                        .thenThrow(new IllegalStateException("Firebase not initialized"));

                // Act & Assert – exception được catch bên trong
                assertDoesNotThrow(() ->
                    notificationService.sendNotification("token", "Title", "Body")
                );
            }
        }
    }
}
