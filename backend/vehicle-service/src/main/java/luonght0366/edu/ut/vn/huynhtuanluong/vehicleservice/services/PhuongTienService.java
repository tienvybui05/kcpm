package luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.services;

import jakarta.transaction.Transactional;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.dtos.PhuongTienDTO;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.modules.PhuongTien;
import luonght0366.edu.ut.vn.huynhtuanluong.vehicleservice.repositories.IPhuongTienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PhuongTienService implements IPhuongTienService {

    @Autowired
    private IPhuongTienRepository phuongTienRepository;

    private static final String LICENSE_PLATE_REGEX = "^\\d{2}[A-Z]-?\\d{3,4}(\\.\\d{2,4}|\\d{1,4})$";

    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9a-hj-npr-z]{17}$";

    @Transactional
    @Override
    public PhuongTien themPhuongTien(PhuongTienDTO dto) {
        validateCreate(dto);

        if (phuongTienRepository.existsByVin(dto.getVin().trim())) {
            throw new IllegalArgumentException("VIN đã tồn tại");
        }

        if (phuongTienRepository.existsByBienSo(dto.getBienSo().trim())) {
            throw new IllegalArgumentException("Biển số đã tồn tại");
        }

        PhuongTien v = new PhuongTien();
        v.setVin(dto.getVin().trim());
        v.setBienSo(dto.getBienSo().trim().toUpperCase());
        v.setLoaiXe(dto.getLoaiXe().trim());
        v.setMaTaiXe(dto.getMaTaiXe());

        // Theo test Postman: tạo xe chưa được gắn pin.
        // Muốn gắn pin thì dùng API /link-pin/{pinId}
        v.setMaPin(dto.getMaPin());

        return phuongTienRepository.save(v);
    }

    @Override
    public List<PhuongTien> danhSachPhuongTien() {
        return phuongTienRepository.findAll();
    }

    @Override
    public PhuongTien layPhuongTienTheoId(Long id) {
        return phuongTienRepository.findById(id).orElse(null);
    }

    @Override
    public boolean xoaPhuongTien(Long id) {
        if (!phuongTienRepository.existsById(id)) {
            return false;
        }

        phuongTienRepository.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public PhuongTien suaPhuongTien(Long id, PhuongTienDTO dto) {
        PhuongTien v = phuongTienRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phương tiện"));

        validateUpdate(dto);

        if (dto.getVin() != null && !dto.getVin().trim().equals(v.getVin())) {
            String newVin = dto.getVin().trim();

            if (phuongTienRepository.existsByVinAndMaPhuongTienNot(newVin, id)) {
                throw new IllegalArgumentException("VIN đã tồn tại");
            }

            v.setVin(newVin);
        }

        if (dto.getBienSo() != null && !dto.getBienSo().trim().equalsIgnoreCase(v.getBienSo())) {
            String newBienSo = dto.getBienSo().trim().toUpperCase();

            if (phuongTienRepository.existsByBienSoAndMaPhuongTienNot(newBienSo, id)) {
                throw new IllegalArgumentException("Biển số đã tồn tại");
            }

            v.setBienSo(newBienSo);
        }

        if (dto.getLoaiXe() != null) {
            if (isBlank(dto.getLoaiXe())) {
                throw new IllegalArgumentException("Loại xe không được để trống");
            }
            v.setLoaiXe(dto.getLoaiXe().trim());
        }

        if (dto.getMaTaiXe() != null) {
            v.setMaTaiXe(dto.getMaTaiXe());
        }

        // Không update maPin ở API update vehicle.
        // maPin chỉ xử lý ở link-pin / unlink-pin.

        return phuongTienRepository.save(v);
    }

    @Override
    public List<PhuongTien> danhSachTheoTaiXe(Long maTaiXe) {
        return phuongTienRepository.findAllByMaTaiXe(maTaiXe);
    }

    @Transactional
    @Override
    public PhuongTien lienKetPin(Long maPhuongTien, Long maPin) {
        PhuongTien v = phuongTienRepository.findById(maPhuongTien)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phương tiện"));

        if (maPin == null) {
            throw new IllegalArgumentException("Mã pin không được để trống");
        }

        v.setMaPin(maPin);
        return phuongTienRepository.save(v);
    }

    @Transactional
    @Override
    public PhuongTien huyLienKetPin(Long maPhuongTien) {
        PhuongTien v = phuongTienRepository.findById(maPhuongTien)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phương tiện"));

        v.setMaPin(null);
        return phuongTienRepository.save(v);
    }

    private void validateCreate(PhuongTienDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ");
        }

        if (isBlank(dto.getVin())) {
            throw new IllegalArgumentException("VIN không được để trống");
        }

        // Thêm đoạn check VIN ở đây
        validateVin(dto.getVin());

        if (isBlank(dto.getBienSo())) {
            throw new IllegalArgumentException("Biển số không được để trống");
        }

        // 1. Phải check null trước tiên
        if (dto.getMaTaiXe() == null) {
            throw new IllegalArgumentException("Mã tài xế không được để trống");
        }

        // 2. Chắc chắn nó hết null rồi thì mới đem đi so sánh số học an toàn
        if (dto.getMaTaiXe() <= 0) {
            throw new IllegalArgumentException("Mã tài xế phải lớn hơn 0");
        }

        if (isBlank(dto.getLoaiXe())) {
            throw new IllegalArgumentException("Loại xe không được để trống");
        }

        String loaiXeTrim = dto.getLoaiXe().trim();
        if (loaiXeTrim.length() < 2) {
            throw new IllegalArgumentException("Tên loại xe quá ngắn");
        }

        if (loaiXeTrim.length() > 50) {
            throw new IllegalArgumentException("Tên loại xe không được vượt quá 50 ký tự");
        }

        if (dto.getMaTaiXe() == null) {
            throw new IllegalArgumentException("Mã tài xế không được để trống");
        }

        if (dto.getMaPin() != null && dto.getMaPin() <= 0) {
            throw new IllegalArgumentException("Mã Pin phải lớn hơn 0");
        }

        validateLicensePlate(dto.getBienSo());
    }

    private void validateUpdate(PhuongTienDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ");
        }

        if (dto.getVin() != null) {
            if (isBlank(dto.getVin())) {
                throw new IllegalArgumentException("VIN không được để trống");
            }
            validateVin(dto.getVin());
        }

        if (dto.getMaTaiXe() != null) {
            // Bổ sung bắt lỗi khi update
            if (dto.getMaTaiXe() <= 0) {
                throw new IllegalArgumentException("Mã tài xế phải lớn hơn 0");
            }
        }

        if (dto.getMaPin() != null) {
            if (dto.getMaPin() <= 0) { // <-- Đổi MaTaiXe thành MaPin
                throw new IllegalArgumentException("Mã Pin phải lớn hơn 0");
            }
        }

        if (dto.getBienSo() != null) {
            if (isBlank(dto.getBienSo())) {
                throw new IllegalArgumentException("Biển số không được để trống");
            }
            validateLicensePlate(dto.getBienSo());
        }

        if (dto.getLoaiXe() != null) {
            if (dto.getLoaiXe().trim().isEmpty()) {
                throw new IllegalArgumentException("Loại xe không được để trống");
            }
            if (dto.getLoaiXe().trim().length() < 2) {
                throw new IllegalArgumentException("Tên loại xe quá ngắn");
            }
        }
    }

    // Viết thêm hàm helper validateVin này ở dưới cùng file Service
    private void validateVin(String vin) {
        String value = vin.trim();

        // Check độ dài trước để ăn khớp với câu báo lỗi của Postman test
        if (value.length() != 17) {
            throw new IllegalArgumentException("Mã VIN phải có độ dài đúng 17 ký tự");
        }

        // Check ký tự hợp lệ (Không chứa I, O, Q)
        if (!value.matches(VIN_REGEX)) {
            throw new IllegalArgumentException("Mã VIN không đúng định dạng chuẩn quốc tế");
        }
    }

    private void validateLicensePlate(String bienSo) {
        if (bienSo == null || bienSo.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số không được để trống");
        }

        String value = bienSo.trim().toUpperCase();

        if (value.length() < 7) {
            throw new IllegalArgumentException("Biển số chưa đủ độ dài quy định");
        }

        // 1. Kiểm tra giới hạn độ dài trước (7 - 12 ký tự) để pass đúng case lỗi độ dài
        if (value.length() > 12) {
            throw new IllegalArgumentException("Biển số vượt quá độ dài quy định");
        }

        // 2. Kiểm tra định dạng Regex (Lúc này Regex không cần cụm Lookahead độ dài nữa vì đã check ở trên)
        // Regex tinh gọn lại:
        String tinhGonRegex = "^\\d{2}[A-Z0-9]{1,2}[-.]?\\d{3,5}(?:\\.\\d{2,4})?$";
        if (!value.matches(tinhGonRegex)) {
            throw new IllegalArgumentException("Dữ liệu JSON sai định dạng hoặc biển số không hợp lệ");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}