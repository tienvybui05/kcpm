package ngocvct0133.ut.edu.feedbackservice.modules;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BaoCao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maBaoCao;
    private String phanHoi;
    private String noiDung;
    private String tieuDe;
    private String loaiPhanHoi;
    private String trangThaiXuLy = "MOI";


        // 👥 Quan hệ vai trò
    private Long maTaiXe;     // người gửi / người nhận (tài xế)
    private Long maNhanVien;  // nhân viên phản hồi hoặc xác nhận lịch
    private Long maAdmin;     // admin phản hồi báo cáo

        // 🏭 Nơi gửi đến

    private String destinationType; // "ADMIN" hoặc "TRAM"

    private Long maTram;            // trạm liên quan (nếu có)

    private LocalDateTime ngayTao = LocalDateTime.now();


    public BaoCao() {
    }

  public BaoCao(String tieuDe, String noiDung, String loaiPhanHoi, Long maTaiXe) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loaiPhanHoi = loaiPhanHoi;
        this.maTaiXe = maTaiXe;
    }

    // 🧩 Getter & Setter
    public Long getMaBaoCao() { return maBaoCao; }
    public void setMaBaoCao(Long maBaoCao) { this.maBaoCao = maBaoCao; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getLoaiPhanHoi() { return loaiPhanHoi; }
    public void setLoaiPhanHoi(String loaiPhanHoi) { this.loaiPhanHoi = loaiPhanHoi; }
    public String getTrangThaiXuLy() { return trangThaiXuLy; }
    public void setTrangThaiXuLy(String trangThaiXuLy) { this.trangThaiXuLy = trangThaiXuLy; }
    public String getPhanHoi() { return phanHoi; }
    public void setPhanHoi(String phanHoi) { this.phanHoi = phanHoi; }
    public Long getMaTaiXe() { return maTaiXe; }
    public void setMaTaiXe(Long maTaiXe) { this.maTaiXe = maTaiXe; }
    public Long getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Long maNhanVien) { this.maNhanVien = maNhanVien; }
    public Long getMaAdmin() { return maAdmin; }
    public void setMaAdmin(Long maAdmin) { this.maAdmin = maAdmin; }
    public String getDestinationType() { return destinationType; }
    public void setDestinationType(String destinationType) { this.destinationType = destinationType; }
    public Long getMaTram() { return maTram; }
    public void setMaTram(Long maTram) { this.maTram = maTram; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
