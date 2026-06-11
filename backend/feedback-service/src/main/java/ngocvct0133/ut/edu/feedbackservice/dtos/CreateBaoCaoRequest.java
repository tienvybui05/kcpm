package ngocvct0133.ut.edu.feedbackservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateBaoCaoRequest {

    @NotBlank(message = "tieuDe is required")
    private String tieuDe;

    @NotBlank(message = "noiDung is required")
    private String noiDung;

    private String loaiPhanHoi;

    @NotNull(message = "maTaiXe is required")
    private Long maTaiXe;

    @NotBlank(message = "destinationType is required")
    @Pattern(regexp = "^(ADMIN|TRAM)$", message = "destinationType must be either ADMIN or TRAM")
    private String destinationType;

    private Long maTram;

    private String phanHoi;

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getLoaiPhanHoi() {
        return loaiPhanHoi;
    }

    public void setLoaiPhanHoi(String loaiPhanHoi) {
        this.loaiPhanHoi = loaiPhanHoi;
    }

    public Long getMaTaiXe() {
        return maTaiXe;
    }

    public void setMaTaiXe(Long maTaiXe) {
        this.maTaiXe = maTaiXe;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public Long getMaTram() {
        return maTram;
    }

    public void setMaTram(Long maTram) {
        this.maTram = maTram;
    }

    public String getPhanHoi() {
        return phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }
}
