// 📦 Import SDKs
import { initializeApp } from "firebase/app";
// THÊM isSupported VÀO ĐÂY
import { getMessaging, getToken, onMessage, isSupported } from "firebase/messaging";
import { getAnalytics, logEvent } from "firebase/analytics";

// 🚀 Cấu hình Firebase
const firebaseConfig = {
  apiKey: "AIzaSyDq7Em_OzAMvF0Jm1W9cPw40Eb0jREHQco",
  authDomain: "ev-battery-swap-system.firebaseapp.com",
  projectId: "ev-battery-swap-system",
  storageBucket: "ev-battery-swap-system.appspot.com",
  messagingSenderId: "450888971417",
  appId: "1:450888971417:web:4637cb9114cd6882833408",
  measurementId: "G-6H8GFYNXHH"
};

// ⚙️ Khởi tạo Firebase App
const app = initializeApp(firebaseConfig);

// 🔥 Bật Analytics
const analytics = getAnalytics(app);
logEvent(analytics, "page_view");
console.log("✅ Firebase Analytics connected");

// ==========================================
// 🔔 FCM (Thông báo) - ĐÃ FIX LỖI JENKINS
// ==========================================
let messaging = null;

// Kiểm tra xem trình duyệt có hỗ trợ không trước khi gọi getMessaging
isSupported().then((supported) => {
  if (supported) {
    messaging = getMessaging(app);
  } else {
    console.log("⚠️ Trình duyệt (hoặc Jenkins) không hỗ trợ Firebase Messaging. Bỏ qua khởi tạo để chống Crash.");
  }
});

export const requestPermission = async () => {
  // Nếu đang chạy trên Jenkins (messaging = null), thoát luôn không gọi getToken để tránh lỗi
  if (!messaging) return null;

  try {
    const token = await getToken(messaging, {
      vapidKey: "BPNx56zwXbQJbTCjZZad7z6UJr7zC_gmPoAx_JPYO8fNDu8p4akt5D3fDtUdNwrXNA2XecNz3dM1cEnPMmgPxuE"
    });
    console.log("FCM token:", token);
    return token;
  } catch (error) {
    console.error("Permission denied:", error);
  }
};

export const onMessageListener = () =>
    new Promise((resolve) => {
      // Nếu đang chạy trên Jenkins (messaging = null), không làm gì cả
      if (!messaging) return;

      onMessage(messaging, (payload) => {
        resolve(payload);
      });
    });

// 🔄 Xuất để App.js dùng
export { analytics };