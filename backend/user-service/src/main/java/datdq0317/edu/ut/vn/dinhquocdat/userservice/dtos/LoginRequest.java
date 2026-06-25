package datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "Người dùng chưa nhập số điện thoại")
    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại chỉ được chứa chữ số")
    @Pattern(regexp = "^(0.*)?$", message = "Số điện thoại phải bắt đầu bằng 0")
    @Pattern(regexp = "^(.{10})?$", message = "Số điện thoại phải đúng 10 chữ số")
    private String soDienThoai;

    private String matKhau;

    // Constructor rỗng
    public LoginRequest() {}

    // Constructor có tham số
    public LoginRequest(String soDienThoai, String matKhau) {
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
    }

    // Getters and Setters
    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}