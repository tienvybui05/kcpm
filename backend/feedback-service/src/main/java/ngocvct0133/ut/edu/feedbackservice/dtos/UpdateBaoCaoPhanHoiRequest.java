package ngocvct0133.ut.edu.feedbackservice.dtos;

import jakarta.validation.constraints.NotBlank;

public class UpdateBaoCaoPhanHoiRequest {

    @NotBlank(message = "phanHoi is required")
    private String phanHoi;

    public String getPhanHoi() {
        return phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }
}
