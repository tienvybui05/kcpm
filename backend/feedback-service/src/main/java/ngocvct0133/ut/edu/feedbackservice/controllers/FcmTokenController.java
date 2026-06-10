package ngocvct0133.ut.edu.feedbackservice.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ngocvct0133.ut.edu.feedbackservice.dtos.UpdateFcmTokenRequest;
import ngocvct0133.ut.edu.feedbackservice.services.IFcmTokenService;

@RestController
@RequestMapping("/api/feedback-service/fcm")
public class FcmTokenController {

    @Autowired
    private IFcmTokenService tokenService;

    @PostMapping("/update")
    public ResponseEntity<?> updateToken(@RequestBody(required = false) UpdateFcmTokenRequest body) {

        if (body == null) {
            return badRequest("Request body không được để trống");
        }

        if (body.getMaNguoiDung() == null) {
            return badRequest("maNguoiDung không được để trống");
        }

        if (body.getRole() == null || body.getRole().trim().isEmpty()) {
            return badRequest("role không được để trống");
        }

        if (body.getToken() == null || body.getToken().trim().isEmpty()) {
            return badRequest("token không được để trống");
        }

        return ResponseEntity.ok(
            tokenService.saveToken(
                body.getMaNguoiDung(),
                body.getRole(),
                body.getToken()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getToken(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tokenService.getTokenByMaNguoiDung(id));
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return ResponseEntity.badRequest().body(error);
    }
}