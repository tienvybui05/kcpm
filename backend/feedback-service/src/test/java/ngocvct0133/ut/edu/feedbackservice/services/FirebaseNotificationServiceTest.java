package ngocvct0133.ut.edu.feedbackservice.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FirebaseNotificationService - Whitebox Tests")
class FirebaseNotificationServiceTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call mockCall;

    @InjectMocks
    private FirebaseNotificationService firebaseNotificationService;

    @BeforeEach
    void setUp() {
        firebaseNotificationService.setOkHttpClient(okHttpClient);
    }

    // =========================================================================
    // sendNotification (dùng Firebase Admin SDK)
    // =========================================================================
    @Nested
    @DisplayName("sendNotification()")
    class SendNotification {

        @Test
        @DisplayName("TC01 - Gửi notification thành công qua Firebase SDK")
        void sendNotification_thanhCong() throws Exception {
            try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
                mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);
                when(mockMessaging.send(any(Message.class))).thenReturn("projects/test/messages/123");

                assertDoesNotThrow(() ->
                    firebaseNotificationService.sendNotification("testToken", "Title", "Body")
                );

                verify(mockMessaging).send(any(Message.class));
            }
        }

        @Test
        @DisplayName("TC02 - Firebase throw exception → catch và log lỗi")
        void sendNotification_firebaseException() throws Exception {
            try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
                FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
                mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);
                when(mockMessaging.send(any(Message.class)))
                        .thenThrow(new RuntimeException("Firebase error"));

                assertDoesNotThrow(() ->
                    firebaseNotificationService.sendNotification("badToken", "Title", "Body")
                );

                verify(mockMessaging).send(any(Message.class));
            }
        }
    }

    // =========================================================================
    // sendToAdmin (gọi private send() với topic admin)
    // =========================================================================
    @Nested
    @DisplayName("sendToAdmin()")
    class SendToAdmin {

        @Test
        @DisplayName("TC03 - Gửi đến topic admin thành công")
        void sendToAdmin_success() throws Exception {
            // Chuẩn bị response thành công của OkHttp
            Response dummyResponse = new Response.Builder()
                    .request(new Request.Builder().url("https://fcm.googleapis.com/fcm/send").build())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(ResponseBody.create("{}", MediaType.get("application/json")))
                    .build();

            when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(dummyResponse);

            assertDoesNotThrow(() ->
                firebaseNotificationService.sendToAdmin("Test Title", "Test Body")
            );

            verify(okHttpClient).newCall(any(Request.class));
            verify(mockCall).execute();
        }

        @Test
        @DisplayName("TC04 - Gửi đến topic admin gặp lỗi (Exception) → kích hoạt block catch")
        void sendToAdmin_exception() throws Exception {
            // OkHttp ném Exception
            when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
            when(mockCall.execute()).thenThrow(new IOException("Connection failed"));

            assertDoesNotThrow(() ->
                firebaseNotificationService.sendToAdmin("Test Title", "Test Body")
            );

            verify(okHttpClient).newCall(any(Request.class));
            verify(mockCall).execute();
        }
    }

    // =========================================================================
    // sendToDriver (gọi private send() với topic driver)
    // =========================================================================
    @Nested
    @DisplayName("sendToDriver()")
    class SendToDriver {

        @Test
        @DisplayName("TC05 - Gửi đến topic tài xế thành công")
        void sendToDriver_success() throws Exception {
            Response dummyResponse = new Response.Builder()
                    .request(new Request.Builder().url("https://fcm.googleapis.com/fcm/send").build())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(ResponseBody.create("{}", MediaType.get("application/json")))
                    .build();

            when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(dummyResponse);

            assertDoesNotThrow(() ->
                firebaseNotificationService.sendToDriver(100L, "Test Title", "Test Body")
            );

            verify(okHttpClient).newCall(any(Request.class));
            verify(mockCall).execute();
        }
    }
}
