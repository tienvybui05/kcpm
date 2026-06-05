package ngocvct0133.ut.edu.feedbackservice.services;

import ngocvct0133.ut.edu.feedbackservice.modules.FcmToken;

public interface IFcmTokenService {

    // 🔹 Lưu hoặc cập nhật token (trả về đối tượng đã lưu)
    FcmToken saveToken(Long maNguoiDung, String role, String token);

    // 🔹 Lấy token đầu tiên (nếu cần dùng để gửi thông báo)
    FcmToken getTokenByMaNguoiDung(Long maNguoiDung);
}
