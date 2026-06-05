package datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.modules.GoiDichVu;
import datdq0317.edu.ut.vn.dinhquocdat.subscriptionservice.repositories.IGoiDichVuRepository;
import jakarta.transaction.Transactional;

@Service
public class GoiDichVuService implements IGoiDichVuService {

    @Autowired
    private IGoiDichVuRepository goiDichVuRepository;

    @Autowired
    private ILichSuDangKyGoiService lichSuDangKyGoiService;

    private void validateGoiDichVu(GoiDichVu goi) {
        if (goi == null) {
            throw new RuntimeException("Dữ liệu gói dịch vụ không được rỗng!");
        }

        if (goi.getTenGoi() == null || goi.getTenGoi().trim().isEmpty()) {
            throw new RuntimeException("Tên gói dịch vụ không được rỗng!");
        }

        if (goi.getGia() == null || goi.getGia() <= 0) {
            throw new RuntimeException("Giá gói dịch vụ phải lớn hơn 0!");
        }

        if (goi.getThoiGianDung() == null || goi.getThoiGianDung() <= 0) {
            throw new RuntimeException("Thời gian dùng phải lớn hơn 0!");
        }

        if (goi.getSoLanDoi() == null || goi.getSoLanDoi() <= 0) {
            throw new RuntimeException("Số lần đổi pin phải lớn hơn 0!");
        }
    }

    @Transactional
    @Override
    public GoiDichVu themGoi(GoiDichVu goi) {
        validateGoiDichVu(goi);

        if (goiDichVuRepository.existsByTenGoi(goi.getTenGoi())) {
            throw new RuntimeException("Tên gói dịch vụ đã tồn tại!");
        }

        return goiDichVuRepository.save(goi);
    }

    @Transactional
    @Override
    public GoiDichVu suaGoi(Long id, GoiDichVu goi) {
        validateGoiDichVu(goi);

        return goiDichVuRepository.findById(id).map(g -> {
            if (!g.getTenGoi().equals(goi.getTenGoi())
                    && goiDichVuRepository.existsByTenGoi(goi.getTenGoi())) {
                throw new RuntimeException("Tên gói dịch vụ đã được sử dụng!");
            }

            g.setTenGoi(goi.getTenGoi());
            g.setMoTa(goi.getMoTa());
            g.setGia(goi.getGia());
            g.setThoiGianDung(goi.getThoiGianDung());
            g.setSoLanDoi(goi.getSoLanDoi());

            return goiDichVuRepository.save(g);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy gói dịch vụ!"));
    }

    @Override
    public boolean xoaGoi(Long id) {
        if (!goiDichVuRepository.existsById(id)) {
            return false;
        }

        if (lichSuDangKyGoiService.kiemTraGoiDangDuocSuDung(id)) {
            throw new RuntimeException("Không thể xóa gói dịch vụ đang có người sử dụng còn hạn!");
        }

        goiDichVuRepository.deleteById(id);
        return true;
    }

    @Override
    public List<GoiDichVu> danhSachGoi() {
        return goiDichVuRepository.findAll();
    }

    @Override
    public GoiDichVu layGoiTheoId(Long id) {
        return goiDichVuRepository.findById(id).orElse(null);
    }
}