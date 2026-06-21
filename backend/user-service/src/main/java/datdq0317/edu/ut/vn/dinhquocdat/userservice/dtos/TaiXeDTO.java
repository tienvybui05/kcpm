package datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public class TaiXeDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 1, max = 50, message = "Họ tên phải từ 1-50 ký tự")
    @Pattern(regexp = "^[^0-9]*$", message = "Họ tên chỉ được chứa chữ cái")
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String gioiTinh;
    private String matKhau;
    private LocalDate ngaySinh;
    private String bangLaiXe;

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getBangLaiXe() {
        return bangLaiXe;
    }

    public void setBangLaiXe(String bangLaiXe) {
        this.bangLaiXe = bangLaiXe;
    }
}