package ngocvct0133.ut.edu.feedbackservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateFcmTokenRequest {

    @NotNull(message = "maNguoiDung is required")
    private Long maNguoiDung;

    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "role is required")
    private String role;

    public Long getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(Long maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
