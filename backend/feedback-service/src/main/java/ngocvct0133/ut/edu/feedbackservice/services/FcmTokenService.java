package ngocvct0133.ut.edu.feedbackservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ngocvct0133.ut.edu.feedbackservice.modules.FcmToken;
import ngocvct0133.ut.edu.feedbackservice.repositories.IFcmTokenRepository;

@Service
public class FcmTokenService implements IFcmTokenService {

    @Autowired
    private IFcmTokenRepository repo;

    @Override
    public FcmToken saveToken(Long maNguoiDung, String vaiTro, String token) {
        Optional<FcmToken> existing = repo.findFirstByMaNguoiDung(maNguoiDung);

        if (existing.isPresent()) {
            // 🔄 Nếu có thì cập nhật token mới
            FcmToken t = existing.get();
            t.setToken(token);
            t.setUserRole(vaiTro);
            t.setCreatedAt(System.currentTimeMillis());
            return repo.save(t);
        } else {
            // 🆕 Nếu chưa có thì thêm mới — chú ý thứ tự tham số: (maNguoiDung, token, userRole)
            FcmToken created = new FcmToken(maNguoiDung, token, vaiTro);
            return repo.save(created);
        }
    }

    @Override
    public FcmToken getTokenByMaNguoiDung(Long maNguoiDung) {
        return repo.findFirstByMaNguoiDung(maNguoiDung).orElse(null);
    }
}
