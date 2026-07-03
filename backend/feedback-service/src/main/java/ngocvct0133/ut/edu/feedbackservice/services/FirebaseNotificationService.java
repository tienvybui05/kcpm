package ngocvct0133.ut.edu.feedbackservice.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FirebaseNotificationService {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA..."; // 🔑 Lấy từ Firebase Console > Cloud Messaging

    private OkHttpClient client = new OkHttpClient();

    public void setOkHttpClient(OkHttpClient client) {
        this.client = client;
    }

    private void send(String to, String title, String body) {

        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);

        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("notification", notification);

MediaType JSON = MediaType.get("application/json; charset=utf-8");
RequestBody req = RequestBody.create(message.toString(), JSON);




        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .post(req)
                .build();

        try (Response res = client.newCall(request).execute()) {
            System.out.println("🔥 Gửi FCM tới " + to + " → " + res.code());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🧭 Gửi đến topic của admin
    public void sendToAdmin(String title, String body) {
        send("/topics/admin_notifications", title, body);
    }

    // 🧭 Gửi đến topic của tài xế
    public void sendToDriver(Long maTaiXe, String title, String body) {
        send("/topics/driver_" + maTaiXe, title, body);
    }
    public void sendNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Gửi thông báo thành công: " + response);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }
}
