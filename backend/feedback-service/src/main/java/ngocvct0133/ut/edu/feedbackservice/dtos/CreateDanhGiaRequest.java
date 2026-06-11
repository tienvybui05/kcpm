package ngocvct0133.ut.edu.feedbackservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDate;

public class CreateDanhGiaRequest {

    @NotBlank(message = "noiDung is required")
    private String noiDung;

    @NotNull(message = "soSao is required")
    @Min(value = 1, message = "soSao must be at least 1")
    @Max(value = 5, message = "soSao must not exceed 5")
    private Integer soSao;

    private LocalDate ngayDanhGia;

    private Long maLichDat;

    @NotNull(message = "maTram is required")
    private Long maTram;

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Integer getSoSao() {
        return soSao;
    }

    public void setSoSao(Integer soSao) {
        this.soSao = soSao;
    }

    public LocalDate getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(LocalDate ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }

    public Long getMaLichDat() {
        return maLichDat;
    }

    public void setMaLichDat(Long maLichDat) {
        this.maLichDat = maLichDat;
    }

    public Long getMaTram() {
        return maTram;
    }

    public void setMaTram(Long maTram) {
        this.maTram = maTram;
    }
}
