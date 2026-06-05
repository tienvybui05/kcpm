package ngocvct0133.ut.edu.feedbackservice.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ngocvct0133.ut.edu.feedbackservice.services.IFcmTokenService;
import ngocvct0133.ut.edu.feedbackservice.modules.FcmToken;

@RestController
@RequestMapping("/api/feedback-service/fcm")
public class FcmTokenController {

    @Autowired
    private IFcmTokenService tokenService;

    @PostMapping("/update")
    public Object updateToken(@RequestBody Map<String, String> body) {
        Long maNguoiDung = Long.valueOf(body.get("maNguoiDung"));
        String token = body.get("token");
        String role = body.get("role");
        if (role == null) role = body.get("vaiTro"); // chấp nhận cả 2 key

        // Trả về đối tượng đã lưu để tests có thể kiểm tra các trường
        return tokenService.saveToken(maNguoiDung, role, token);
    }

    @GetMapping("/{id}")
    public Object getToken(@PathVariable("id") Long id) {
        // Trả về đối tượng FcmToken (null nếu không tìm thấy) — phù hợp với các test/khách hàng mong đợi
        return tokenService.getTokenByMaNguoiDung(id);
    }
}
