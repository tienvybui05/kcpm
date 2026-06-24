package datdq0317.edu.ut.vn.dinhquocdat.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public class TaiXeDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 1, max = 50, message = "Họ tên phải từ 1-50 ký tự")
    @Pattern(regexp = "^[^0-9]*$", message = "Họ tên chỉ được chứa chữ cái")
    private String hoTen;
    private String email;

    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại chỉ được chứa chữ số")
    @Pattern(regexp = "^(0.*)?$", message = "Số điện thoại phải bắt đầu bằng 0")
    @Pattern(regexp = "^(.{10})?$", message = "Số điện thoại phải đúng 10 chữ số")
    private String soDienThoai;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "^(Nam|Nữ)$", message = "Giới tính phải là 'Nam' hoặc 'Nữ'")
    private String gioiTinh;

    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6-20 ký tự")
    @Pattern(regexp = ".*[A-Z].*", message = "Mật khẩu phải chứa ít nhất 1 chữ hoa")
    @Pattern(regexp = ".*[a-z].*", message = "Mật khẩu phải chứa ít nhất 1 chữ thường")
    @Pattern(regexp = ".*[0-9].*", message = "Mật khẩu phải chứa ít nhất 1 chữ số")
    private String matKhau;

    @Past(message = "Ngày sinh không được là ngày trong tương lai")
    private LocalDate ngaySinh;

    @NotBlank(message = "Bằng lái xe không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Bằng lái xe không hợp lệ")
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