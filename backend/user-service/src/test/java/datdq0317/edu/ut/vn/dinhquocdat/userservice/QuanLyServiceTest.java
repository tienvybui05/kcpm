package datdq0317.edu.ut.vn.dinhquocdat.userservice;

import datdq0317.edu.ut.vn.dinhquocdat.userservice.models.NguoiDung;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.INguoiDungRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.repositories.IQuanLyRepository;
import datdq0317.edu.ut.vn.dinhquocdat.userservice.services.QuanLyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("White-box Testing - QuanLyService")
class QuanLyServiceTest {

    @Mock
    private IQuanLyRepository quanLyRepository;

    @Mock
    private INguoiDungRepository nguoiDungRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private QuanLyService quanLyService;

    private NguoiDung quanLy;

    @BeforeEach
    void setUp() {
        quanLy = new NguoiDung();
        quanLy.setMaNguoiDung(1L);
        quanLy.setHoTen("Admin");
        quanLy.setEmail("admin@test.com");
        quanLy.setSoDienThoai("0988888888");
        quanLy.setMatKhau("123456");
        quanLy.setNgaySinh(LocalDate.of(1990,1,1));
        quanLy.setGioiTinh("Nam");
    }

    //=========================================================
    // THÊM QUẢN LÝ
    //=========================================================

    @Test
    @DisplayName("SC-01: Thêm quản lý thành công")
    void themQuanLy_Success(){

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(quanLyRepository.save(any()))
                .thenReturn(quanLy);

        NguoiDung result = quanLyService.themQuanLy(quanLy);

        assertNotNull(result);

        InOrder order = inOrder(
                nguoiDungRepository,
                passwordEncoder,
                quanLyRepository);

        order.verify(nguoiDungRepository).findByEmail(anyString());
        order.verify(nguoiDungRepository).findBySoDienThoai(anyString());
        order.verify(passwordEncoder).encode(anyString());
        order.verify(quanLyRepository).save(any());
    }

    @Test
    @DisplayName("BC-01: Email đã tồn tại")
    void themQuanLy_EmailExists(){

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(quanLy));

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()->quanLyService.themQuanLy(quanLy));

        assertEquals("Email đã tồn tại!",ex.getMessage());

        verify(quanLyRepository,never()).save(any());
    }

    @Test
    @DisplayName("BC-02: SĐT đã tồn tại")
    void themQuanLy_PhoneExists(){

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.of(quanLy));

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()->quanLyService.themQuanLy(quanLy));

        assertEquals("Số điện thoại đã tồn tại!",ex.getMessage());

        verify(quanLyRepository,never()).save(any());
    }

    //=========================================================
    // DANH SÁCH
    //=========================================================

    @Test
    void danhSachQuanLy(){

        when(quanLyRepository.findByVaiTro("ADMIN"))
                .thenReturn(List.of(quanLy));

        List<NguoiDung> list = quanLyService.danhSachQuanLy();

        assertEquals(1,list.size());

        verify(quanLyRepository).findByVaiTro("ADMIN");
    }

    //=========================================================
    // XÓA
    //=========================================================

    @Test
    void xoaQuanLy_Success(){

        when(quanLyRepository.existsById(1L))
                .thenReturn(true);

        boolean result = quanLyService.xoaQuanLy(1L);

        assertTrue(result);

        verify(quanLyRepository).deleteById(1L);
    }

    @Test
    void xoaQuanLy_NotFound(){

        when(quanLyRepository.existsById(99L))
                .thenReturn(false);

        boolean result = quanLyService.xoaQuanLy(99L);

        assertFalse(result);

        verify(quanLyRepository,never()).deleteById(anyLong());
    }

    @Test
    void xoaQuanLy_Exception(){

        when(quanLyRepository.existsById(1L))
                .thenReturn(true);

        doThrow(new RuntimeException())
                .when(quanLyRepository)
                .deleteById(1L);

        boolean result = quanLyService.xoaQuanLy(1L);

        assertFalse(result);
    }

    //=========================================================
    // LẤY THEO ID
    //=========================================================

    @Test
    void layQuanLyBangId_Found(){

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        NguoiDung result = quanLyService.layQuanLyBangId(1L);

        assertNotNull(result);
    }

    @Test
    void layQuanLyBangId_NotFound(){

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.empty());

        NguoiDung result = quanLyService.layQuanLyBangId(1L);

        assertNull(result);
    }

    //=========================================================
    // SỬA
    //=========================================================

    private NguoiDung createUpdate(){

        NguoiDung nd = new NguoiDung();

        nd.setHoTen("Admin New");
        nd.setEmail("new@test.com");
        nd.setSoDienThoai("0999999999");
        nd.setNgaySinh(LocalDate.of(1995,5,5));
        nd.setGioiTinh("Nữ");
        nd.setMatKhau("newpass");

        return nd;
    }

    @Test
    void suaQuanLy_Success(){

        NguoiDung update = createUpdate();

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(quanLyRepository.save(any()))
                .thenReturn(quanLy);

        NguoiDung result =
                quanLyService.suaThongTinQuanLy(1L,update);

        assertNotNull(result);

        verify(passwordEncoder).encode("newpass");
    }

    @Test
    void suaQuanLy_NotFound(){

        when(quanLyRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()->quanLyService.suaThongTinQuanLy(99L,createUpdate()));

        assertEquals("Không tìm thấy quản lý!",ex.getMessage());
    }

    @Test
    void suaQuanLy_EmailExists(){

        NguoiDung update = createUpdate();

        NguoiDung other = new NguoiDung();
        other.setMaNguoiDung(2L);

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        when(nguoiDungRepository.findByEmail(update.getEmail()))
                .thenReturn(Optional.of(other));

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()->quanLyService.suaThongTinQuanLy(1L,update));

        assertEquals("Email đã được sử dụng bởi người khác!",ex.getMessage());
    }

    @Test
    void suaQuanLy_PhoneExists(){

        NguoiDung update = createUpdate();

        NguoiDung other = new NguoiDung();
        other.setMaNguoiDung(2L);

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        when(nguoiDungRepository.findByEmail(update.getEmail()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(update.getSoDienThoai()))
                .thenReturn(Optional.of(other));

        RuntimeException ex = assertThrows(RuntimeException.class,
                ()->quanLyService.suaThongTinQuanLy(1L,update));

        assertEquals("Số điện thoại đã được sử dụng bởi người khác!",ex.getMessage());
    }

    @Test
    void suaQuanLy_PasswordNull(){

        NguoiDung update = createUpdate();
        update.setMatKhau(null);

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());

        when(quanLyRepository.save(any()))
                .thenReturn(quanLy);

        quanLyService.suaThongTinQuanLy(1L,update);

        verify(passwordEncoder,never()).encode(anyString());
    }

    @Test
    void suaQuanLy_PasswordEmpty(){

        NguoiDung update = createUpdate();
        update.setMatKhau("");

        when(quanLyRepository.findById(1L))
                .thenReturn(Optional.of(quanLy));

        when(nguoiDungRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(nguoiDungRepository.findBySoDienThoai(anyString()))
                .thenReturn(Optional.empty());

        when(quanLyRepository.save(any()))
                .thenReturn(quanLy);

        quanLyService.suaThongTinQuanLy(1L,update);

        verify(passwordEncoder,never()).encode(anyString());
    }
}