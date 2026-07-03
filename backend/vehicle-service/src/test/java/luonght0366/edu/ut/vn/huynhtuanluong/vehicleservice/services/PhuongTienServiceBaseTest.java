package luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.services;

import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.dtos.PhuongTienDTO;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.modules.PhuongTien;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.repositories.IPhuongTienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class PhuongTienServiceBaseTest {

    @Mock
    protected IPhuongTienRepository phuongTienRepository;

    @InjectMocks
    protected PhuongTienService phuongTienService;

    protected PhuongTienDTO validDto;
    protected PhuongTien phuongTienEntity;

    @BeforeEach
    protected void setUp() {
        // Khởi tạo DTO hợp lệ chuẩn 17 ký tự VIN và Biển số đúng định dạng regex
        validDto = new PhuongTienDTO();
        validDto.setVin("1HGBH41JXMN109186");
        validDto.setBienSo("59A-123.45");
        validDto.setLoaiXe("Xe dien VinFast");
        validDto.setMaTaiXe(1L);
        validDto.setMaPin(10L);

        // Khởi tạo Entity tương ứng trong Database mẫu
        phuongTienEntity = new PhuongTien();
        phuongTienEntity.setMaPhuongTien(1L);
        phuongTienEntity.setVin("1HGBH41JXMN109186");
        phuongTienEntity.setBienSo("59A-123.45");
        phuongTienEntity.setLoaiXe("Xe dien VinFast");
        phuongTienEntity.setMaTaiXe(1L);
        phuongTienEntity.setMaPin(10L);
    }
}